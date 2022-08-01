// Copyright 2022 Nathan Prat

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

use std::path::Path;

fn main() {
    // originally from https://github.com/tokio-rs/prost/blob/38ba547df6ce1c64dee2167f18673deb85a0a5fe/prost-build/build.rs
    // but it was removed in prost-build 0.11
    println!("cargo:rerun-if-env-changed=PROTOC");

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

    // What we setup in CI(.github/workflows/android.yml), and what use here SHOULD MATCH!
    // eg CARGO_MANIFEST_DIR = /.../wallet-app/shared/rust/circuit_evaluate
    // cf https://github.com/tokio-rs/prost/blob/v0.10.4/prost-build/build.rs#L77
    let protobuf_git_dir = Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("3rd_party")
        .join("protobuf");

    // BEFORE CMake: that will (among other things) generate rust/cxx.h that
    // is needed to compile src/rust_wrapper.cpp
    // ALTERNATIVELY we could add a git submodule for https://github.com/dtolnay/cxx/tree/master/include
    let mut cxx_config = cxx_build::bridge("src/lib.rs");
    cxx_config
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
        // NOTE: there is "prost_build::protoc_include()" but that only works from inside prost-build crate
        // It works in CI b/c we use protoc pre-built, and use env vars PROTOC/PROTOC_INCLUDE
        // TODO is there a protoc option to avoid adding those includes?
        .include(protobuf_git_dir.join("src"));

    // without this:
    // cargo:warning=src/cpp/serialize_pgc/serialize.h:30:43: error: 'path' is unavailable: introduced in iOS 13.0
    // cargo:warning=                         std::filesystem::path pgarbled_input_path);
    // cargo:warning=src/cpp/serialize_pgc/serialize.cpp:150:49: error: 'generic_string' is unavailable: introduced in iOS 13.0
    // cargo:warning=  std::fstream input_stream(pgarbled_input_path.generic_string(),
    // Mimick CMAKE_OSX_DEPLOYMENT_TARGET; but we are not using CMake...
    #[cfg(any(target_os = "ios", target_os = "macos"))]
    {
        cxx_config.flag(
            format!(
                "-mios-simulator-version-min={}",
                // TODO when defaulting: it SHOULD match IPHONEOS_DEPLOYMENT_TARGET in iosApp/iosApp.xcodeproj/project.pbxproj
                // NOTE: this is only used when using "cargo" on the command line, so using https://github.com/xbase-lab/xcodeproj
                // is a bit over-the-top
                option_env!("IPHONEOS_DEPLOYMENT_TARGET").unwrap_or("14.1")
            )
            .as_str(),
        );
        println!("cargo:rerun-if-env-changed=IPHONEOS_DEPLOYMENT_TARGET");
    }

    cxx_config.compile("circuit-evaluate");

    let mut protobuf_cmake_config = cmake::Config::new(protobuf_git_dir);
    protobuf_cmake_config.define("protobuf_BUILD_TESTS", "OFF");
    protobuf_cmake_config.define("protobuf_BUILD_PROTOC_BINARIES", "OFF");

    // we MUST set "cmake -DANDROID_ABI=XXX" when using CMAKE_TOOLCHAIN_FILE else the detected ABI is always:
    // -- ANDROID_PLATFORM not set. Defaulting to minimum supported version 19.
    // -- Android: Targeting API '19' with architecture 'arm', ABI 'armeabi-v7a', and processor 'armv7-a'
    // -- Android: Selected unified Clang toolchain
    // and we get the the error "is incompatible with aarch64linux", cf above
    match std::env::var("CMAKE_TOOLCHAIN_FILE") {
        Ok(_) => {
            match std::env::var("ANDROID_ABI") {
                Ok(android_abi) => {
                    println!("ANDROID_ABI env var : {}", android_abi);
                    protobuf_cmake_config.define("ANDROID_ABI", android_abi);
                }
                Err(e) => {
                    panic!("ANDROID_ABI MUST be set when using env var \"CMAKE_TOOLCHAIN_FILE\"");
                }
            };

            match std::env::var("ANDROID_PLATFORM") {
                Ok(android_platform) => {
                    println!("ANDROID_PLATFORM env var : {}", android_platform);
                    protobuf_cmake_config.define("ANDROID_PLATFORM", android_platform);
                }
                Err(e) => {
                    panic!("ANDROID_PLATFORM MUST be set when using env var \"CMAKE_TOOLCHAIN_FILE\"[ie pass minSdk]");
                }
            };
        }
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
