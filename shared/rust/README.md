## dev

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

#### FIX: "[2022-06-01T12:39:18Z WARN  wgpu_hal::vulkan::instance] Unable to find layer: VK_LAYER_KHRONOS_validation"

`sudo apt install vulkan-validationlayers`

#### W/A: "[2022-06-01T12:58:49Z ERROR wgpu::backend::direct] Error in Adapter::request_device: Limit 'max_compute_workgroups_per_dimension' value 65535 is better than allowed 0"

set env var `WGPU_BACKEND=vulkan`

NOTE: "WARNING: lavapipe is not a conformant vulkan implementation, testing use only"