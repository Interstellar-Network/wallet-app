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

fn main() {
    let out_dir = std::env::var("OUT_DIR").unwrap();
    println!("OUT_DIR: {}", out_dir);

    let pb_out_dir = out_dir.clone();

    // PROTOC_NO_VENDOR MUST be set in CI, b/c prost-build does not pass correct defines
    // to CMake(namely ANDROID_PLATFORM)
    // -- ANDROID_PLATFORM not set. Defaulting to minimum supported version 19.
    // -- Android: Targeting API '19' with architecture 'arm', ABI 'armeabi-v7a', and processor 'armv7-a'
    // ..
    // ld: error: cannot open crtbegin_dynamic.o: No such file or directory
    // ld: error: cannot open crtend_android.o: No such file or directory
    // NOTE: we CAN NOT set this env var now, it MUST be done when prost-build is built!
    match std::env::var("CI") {
        Ok(_) => {
            std::env::var("PROTOC_NO_VENDOR")
                .expect("CI MUST set env var PROTOC_NO_VENDOR(for prost-build)");
            std::env::var("PROTOC").expect("CI MUST set env var PROTOC(path of protoc)");
            std::env::var("PROTOC_INCLUDE")
                .expect("CI MUST set env var PROTOC_INCLUDE(path to Protobuf include/)");
        }
        Err(e) => {
            println!("CI not detected... will use prost-build defaults");
        }
    };

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
        // NOTE: there is "prost_build::protoc_include()" but that only works from inside prost-build crate
        // It works in CI b/c we use protoc pre-built, and use env vars PROTOC/PROTOC_INCLUDE
        // TODO is there a protoc option to avoid adding those includes?
        .include(prost_build::protoc_include())
        // For local build, protoc is compiled by prost_build, and we need to point to the downloaded sources
        // NOTE: this is pointing to eg "target/debug/build/prost-build-XXX" NOT our own "OUT_DIR"
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
    // What we download in CI, and what prost_build downloads SHOULD MATCH!
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
