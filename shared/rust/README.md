## dev

install the Android toolchains(with emulator if needed): `rustup target add --toolchain=nightly x86_64-unknown-linux-gnu armv7-linux-androideabi aarch64-linux-android`

NOTE: only compile with nightly toolchain else https://github.com/scs/substrate-api-client/issues/166#issuecomment-975614152
`rustup override set nightly`

- start the node template
- cargo build --features=with-jni
- cargo test --features=with-jni

### cross compiling

NOTE: adjust `CC_` and `AR_` depending on `--target`
- [adjust to match your machine] `export NDK_ROOT=~/Android/Sdk/ndk/24.0.8215888`
- `export CC_x86_64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android31-clang`
- `export AR_x86_64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar`
- TODO? `export SDL2_TOOLCHAIN=$NDK_ROOT/build/cmake/android.toolchain.cmake`
- TODO? `export CXX_x86_64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android31-clang++`
- `cargo +nightly build --features=with-jni --features=with-cwrapper --target=x86_64-linux-android`

### Debugging with VSCode

cf "android-debugging"

### DEV

MAYBE: `apt remove vulkan-tools libvulkan-dev vulkan-validationlayers-dev spirv-tools`
https://vulkan-tutorial.com/Development_environment#page_Vulkan-Packages
NOT needed to dev/compile, but can be useful to debug? Maybe needed for the shaders? vulkan validation?

#### VSCode

see https://github.com/bevyengine/bevy/issues/86#issuecomment-766100761

- if you have modified `rustflags` or `linker` in eg `~/.cargo/config.toml` make sure the desktop one and the on used for Android Emulator match
    - else it will recompile from scratch when switching target...
    - the easiest way to do it is to NOT set `rustflags` or `linker`
        - if you use mold; set it as global linker by symlinking to `ld`

#### IMPORTANT

#### FIX: "error while loading shared libraries: libjvm.so: cannot open shared object file: No such file or directory"

- install JDK eg `sudo apt-get install openjdk-8-jdk`
    - NOTE: this is needed only to **run** some tests, NOT for compiling

`export LD_LIBRARY_PATH=/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/server/`

#### FIX: "[2022-06-01T12:39:18Z WARN  wgpu_hal::vulkan::instance] Unable to find layer: VK_LAYER_KHRONOS_validation"

`sudo apt install vulkan-validationlayers`

#### W/A: "[2022-06-01T12:58:49Z ERROR wgpu::backend::direct] Error in Adapter::request_device: Limit 'max_compute_workgroups_per_dimension' value 65535 is better than allowed 0"

set env var `WGPU_BACKEND=vulkan`

NOTE: "WARNING: lavapipe is not a conformant vulkan implementation, testing use only"

#### W/A crash at startup; "Segmentation fault"

NOTE: even a breakpoint at "Rust Panic" is not reached

FIX: install vulkan drivers eg `sudo apt-get install mesa-vulkan-drivers`
cf https://github.com/bevyengine/bevy/blob/main/docs/linux_dependencies.md#installing-linux-dependencies
MAYBE see https://github.com/bevyengine/bevy/issues/2661