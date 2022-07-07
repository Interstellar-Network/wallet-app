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
        // TODO is there a more elegant way(eg using prost_build?)
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
    // FAIL on aarch64
    //     error: linking with `/home/pratn/Android/Sdk/ndk/24.0.8215888/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android31-clang` failed: exit status: 1
    //   |
    //   = note: ld: error: /home/pratn/Documents/interstellar/wallet-app/shared/rust/target/aarch64-linux-android/debug/deps/libcircuit_evaluate-1c975ac1cb6d0637.rlib(arena.cc.o) is incompatible with aarch64linux
    //           ld: error: /home/pratn/Documents/interstellar/wallet-app/shared/rust/target/aarch64-linux-android/debug/deps/libcircuit_evaluate-1c975ac1cb6d0637.rlib(arenastring.cc.o) is incompatible with aarch64linux
    // Which makes sense b/c "find . -type f -name "*protoc*""
    //     ./target/debug/build/prost-build-7222232c793920d5/out/lib/libprotoc.a
    // ./target/debug/build/prost-build-7222232c793920d5/out/build/libprotoc.a
    // ./target/debug/build/prost-build-7222232c793920d5/out/build/protoc-3.19.4.0
    // ./target/debug/build/prost-build-7222232c793920d5/out/bin/protoc-3.19.4.0
    // -> ONLY HOST, no TARGET lib
    // println!(
    //     "cargo:rustc-link-search=native={}",
    //     prost_build::protoc()
    //         .parent()
    //         .unwrap()
    //         .parent()
    //         .unwrap()
    //         .join("lib")
    //         .display()
    // );
    //
    // eg protoc_include = /home/AAA/.cargo/registry/src/github.com-XXX/prost-build-0.10.4/third-party/include
    let protobuf_src = prost_build::protoc_include()
        .parent()
        .unwrap()
        .join("protobuf")
        .join("cmake");
    // cf https://github.com/tokio-rs/prost/blob/v0.10.4/prost-build/build.rs#L77
    let mut protobuf_cmake_config = cmake::Config::new(protobuf_src);
    protobuf_cmake_config.define("protobuf_BUILD_TESTS", "OFF");

    // we MUST set "cmake -DANDROID_ABI=XXX" when using CMAKE_TOOLCHAIN_FILE else the detected ABI is always:
    // -- ANDROID_PLATFORM not set. Defaulting to minimum supported version 19.
    // -- Android: Targeting API '19' with architecture 'arm', ABI 'armeabi-v7a', and processor 'armv7-a'
    // -- Android: Selected unified Clang toolchain
    // and we get the the error "is incompatible with aarch64linux", cf above
    match std::env::var("CMAKE_TOOLCHAIN_FILE") {
        Ok(_) => match std::env::var("ANDROID_ABI") {
            Ok(android_abi) => {
                println!("ANDROID_ABI env var : {}", android_abi);
                protobuf_cmake_config.define("ANDROID_ABI", android_abi);
            }
            Err(e) => {
                panic!("ANDROID_ABI MUST be set when using env var \"CMAKE_TOOLCHAIN_FILE\"");
            }
        },
        Err(_) => {
            println!("CMAKE_TOOLCHAIN_FILE env var not set");
        }
    }

    let protobuf_cmake = protobuf_cmake_config.build();
    println!(
        "cargo:rustc-link-search=native={}",
        protobuf_cmake.join("build").display()
    );
    println!("cargo:rustc-link-lib=static=protobuf");

    // But careful, we MUST recompile if the .cpp, the .h or any included .h is modified
    // and using rerun-if-changed=src/lib.rs make it NOT do that
    println!("cargo:rerun-if-changed=src/");
    println!("cargo:rerun-if-changed=build.rs");
    println!("cargo:rerun-if-changed=deps/protos/");
}
