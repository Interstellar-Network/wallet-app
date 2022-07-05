// api_garble
// Copyright (C) 2O22  Nathan Prat

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

use std::fmt::format;

fn main() {
    let out_dir = std::env::var("OUT_DIR").unwrap();
    println!("OUT_DIR: {}", out_dir);

    let pb_out_dir = out_dir.clone();

    prost_build::Config::new()
        // by default prost-build only generates Rust
        .protoc_arg(format!("--cpp_out={}", pb_out_dir))
        .compile_protos(
            &[
                "deps/protos/circuits/block.proto",
                "deps/protos/circuits/circuit.proto",
                "deps/protos/circuits/packmsg.proto",
            ],
            &["deps/protos/circuits/"],
        )
        .unwrap();

    // BEFORE CMake: that will (among other things) generate rust/cxx.h that
    // is needed to compile src/rust_wrapper.cpp
    // ALTERNATIVELY we could add a git submodule for https://github.com/dtolnay/cxx/tree/master/include
    cxx_build::bridge("src/lib.rs")
        .file("src/cpp/evaluate/evaluate.cpp")
        .file("src/cpp/packmsg/packmsg.cpp")
        .file("src/cpp/parallel_garbled_circuit/parallel_garbled_circuit.cpp")
        .file("src/cpp/serialize_packmsg/serialize.cpp")
        .file("src/cpp/serialize_pgc/serialize.cpp")
        .file("src/rust_wrapper.cpp")
        // MUST compile the generated .cc by Protobuf else:
        // "error: undefined symbol: interstellarpbcircuits::_Block_default_instance_"
        // TODO(cpp) is there a more elegant way(eg using prost_build?)
        .file(format!("{}/block.pb.cc", out_dir.clone()))
        .file(format!("{}/circuit.pb.cc", out_dir.clone()))
        .file(format!("{}/packmsg.pb.cc", out_dir.clone()))
        .flag("-std=c++2a")
        .include("src/cpp")
        .include(pb_out_dir.clone())
        // else "fatal error: google/protobuf/port_def.inc: No such file or directory"
        // NOTE: this is pointing to eg "target/debug/build/prost-build-XXX" NOT "OUT_DIR"
        // NOTE2: there is "prost_build::protoc_include()" but that only works from inside prost-build crate
        // TODO is there a protoc option to avoid generating those includes?
        .include(
            prost_build::protoc()
                .parent()
                .unwrap()
                .parent()
                .unwrap()
                .join("include"),
        )
        .compile("circuit-evaluate");

    // else: "error: undefined symbol: google::protobuf::internal::MapFieldBase::SyncMapWithRepeatedField() const"
    println!(
        "cargo:rustc-link-search=native={}",
        prost_build::protoc()
            .parent()
            .unwrap()
            .parent()
            .unwrap()
            .join("lib")
            .display()
    );
    println!("cargo:rustc-link-lib=static=protobuf");

    // But careful, we MUST recompile if the .cpp, the .h or any included .h is modified
    // and using rerun-if-changed=src/lib.rs make it NOT do that
    println!("cargo:rerun-if-changed=src/");
    println!("cargo:rerun-if-changed=build.rs");
    println!("cargo:rerun-if-changed=deps/protos/");
}
