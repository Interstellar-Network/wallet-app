# Interstellar Wallet app

## Dev Details

NOTE: for now compiling on windows is not supported b/c of poor support of Rust cross-compiling wrt to Android NDK
It can probably be fixed but setting up Windows as dev env is a pain; it requires at least: Perl, Python, Rust, C++ Dev Tools, etc

Uses:
- Kotlin multiplatform to share as much code as possible b/w Android and iOs
- Android: Jetpack Compose for easy UI
- [TODO] iOs: SwiftUI for easy UI

NOTE: the Rust dependencies only compile on **nightly**

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

### Android

TODO cleanup/rewrite below
- <del>?</del> install Python
- <del>?</del> install Perl?
  -[windows] FAIL: "This perl implementation doesn't produce Unix like paths"
    Probably b/c https://github.com/alexcrichton/openssl-src-rs/blob/main/src/lib.rs#L226 hardcoded all Android NDK to match Linux...
  -[windows] W/A: alias "perl=wsl perl" `${env:PERL} = 'wsl perl'`
    eg `Set-Alias -Name perl -Value 'C:\Users\nat\Documents\programs\wsl_perl.bat'`
    with wsl_perl.bat: `wsl perl %*`
    CHECK: `perl -v`
- [FAIL for now] `rustup target add armv7-linux-androideabi`
- `rustup target add aarch64-linux-android`
- [FAIL for now] `rustup target add x86_64-linux-android`
- CHECK if it cross-compiles directly(ie without gradle):
  - NOTE: you can check for the correct env vars in [CI of other projects eg](https://github.com/Interstellar-Network/lib-garble-rs/blob/initial/.github/workflows/rust.yml#L88)
  - `cd shared/rust`
  - `export NDK_ROOT=~/Android/Sdk/ndk/25.1.8937393`
  -
    ```bash
    export CC_armv7_linux_androideabi=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi33-clang
    export CXX_armv7_linux_androideabi=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi33-clang++
    export AR_armv7_linux_androideabi=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar
    export CARGO_TARGET_ARMV7_LINUX_ANDROIDEABI_LINKER=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi33-clang

    export CC_aarch64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android33-clang
    export CXX_aarch64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android33-clang++
    export AR_aarch64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar
    export CARGO_TARGET_AARCH64_LINUX_ANDROID_LINKER=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android33-clang

    export CC_x86_64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android33-clang
    export CXX_x86_64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android33-clang++
    export AR_x86_64_linux_android=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar
    export CARGO_TARGET_X86_64_LINUX_ANDROID_LINKER=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android33-clang

    export CMAKE_TOOLCHAIN_FILE=$NDK_ROOT/build/cmake/android.toolchain.cmake
    export ANDROID_PLATFORM=21
    ```
  - `cargo build --release --target=aarch64-linux-android --features=with-jni` and/or `cargo build --release --target=x86_64-linux-android --features=with-jni`
- [cd to Root of the repo] `./gradlew cargoBuild --info`

NOTE: see https://github.com/mozilla/rust-android-gradle#specifying-local-targets to compile only for the emulator
NOTE: to debug Rust code: Run -> Edit Configurations -> Debugger: Debug Type = Dual

- `adb reverse tcp:5001 tcp:5001` and `adb reverse tcp:9990 tcp:9990` and `adb reverse tcp:2090 tcp:2090`
- CHECK by opening [a front-end](https://substrate-developer-hub.github.io/substrate-front-end-template/?rpc=ws://localhost:9990) on the Device or Emulator

#### About release builds

You CAN build the `Release` flavors from Android studio, but to deploy/test them you MUST use `Build -> "Generate Signed Bundled / APK"` and then eg `adb install path/to/app.apk`.
That is because the CI is directly signing with an [action](https://github.com/ilharp/sign-android-release) and we want to avoid messing with keys etc from inside the build scripts.

### iOs

cf https://kotlinlang.org/docs/multiplatform-mobile-integrate-in-existing-app.html#connect-the-framework-to-your-ios-project

- `rustup target add aarch64-apple-ios x86_64-apple-ios`
- Install Android Studio
- Android Studio: open the project "wallet-app"; this is needed to download the SDK etc else iOs can not compile
  - NOTE: you will have to download eg the NDK using the SDK Manager, and anything else that is required until you can Build on this machine
- Once you have built the Android App successfully once, you can close Android Studio
- Open the folder iOsApp in XCode and dev/compile/whatever
- MAYBE Download a simulator using XCode?
  - else:
  `cargo:warning=xcrun: error: SDK "iphonesimulator" cannot be located`
  `cargo:warning=xcrun: error: unable to lookup item 'Path' in SDK 'iphonesimulator'`
- `sudo xcode-select --switch /Applications/Xcode.app/Contents/Developer/`
- [to build on command line] `.../wallet-app/iosApp$ xcodebuild -scheme iosApp -sdk iphonesimulator build`
  - [for real device] `xcodebuild -scheme iosApp build CODE_SIGN_IDENTITY=''`
    NOTE: gradle task "embedAndSignAppleFrameworkForXcode" will fail if signing not setup; but at least that allows testing Rust arm64 cross-compiling

NOTE: below SHOULD already be set, but double check the value:
- Project settings -> click "+" add "new User defined": `JAVA_HOME`
  - eg `/Applications/Android Studio.app/Contents/jre/Contents/Home/`(no need for double quotes or escaping)
  - ALTERNATIVE: add `export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jre/Contents/Home` before ./gradlew in `Build Phase -> Run Script` -> works but ugly

#### FIX: UnsatisfiedLinkError
Process: gg.interstellar.wallet.android, PID: 26011
    java.lang.UnsatisfiedLinkError: dlopen failed: library "libshared_substrate_client.so" not found

- CHECK eg `ll shared/rust/target/BUILD_VARIANT/debug/`
- make sure the selected Build Variant is the correct one

#### FIX: -lgcc missing

cf https://github.com/rust-lang/rust/pull/85806#issuecomment-1096266946

TODO check https://github.com/rust-windowing/android-ndk-rs/pull/270 and https://github.com/rust-lang/rust/pull/85806 for alternative

#### WSL2: KVM for the emulator

See https://github.com/microsoft/WSL/issues/7149

```bash
$ cat /etc/wsl.conf
[boot]
command = /bin/bash -c 'chown root:kvm /dev/kvm && chmod 660 /dev/kvm'
```

`sudo usermod -a -G kvm <username>`


#### FIX: "error while loading shared libraries: libjvm.so: cannot open shared object file: No such file or directory"

- install JDK eg `sudo apt-get install openjdk-11-jre-headless`
    - NOTE: this is needed only to **run** some tests, NOT for compiling
- CHECK path with eg `find /usr/lib/jvm/ -type f -name libjvm.so`
- `export LD_LIBRARY_PATH=/usr/lib/jvm/java-11-openjdk-amd64/lib/server/`

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

#### ARCHIVE alternative tested: cargo ndk

- install NDK using Android Studio "SDK Manager"(ie "NDK (Side by side)" latest)
  - FOR NOW MUST BE <r23, cf below
  - https://github.com/rust-lang/rust/pull/85806 and https://github.com/rust-windowing/android-ndk-rs/pull/189
- NO: `cargo install cargo-ndk`
  - MAYBE `cargo install ndk-build` instead: NO, it is a lib, and can only work as part of https://github.com/rust-windowing/android-ndk-rs/tree/master/cargo-apk ?
- `rustup target add aarch64-linux-android armv7-linux-androideabi`
  - maybe later add `x86_64-linux-android i686-linux-android` but that cover maybe 1% of devices?
- CHECK `cargo ndk -t armeabi-v7a -t arm64-v8a -o ./jniLibs build`
  NOTE: is will be called by gradle when needed, but better check in advance
  - `--release`

#### FIX(windows): `error: linker `cc` not found`

**NO!** install Python!
cf https://github.com/mozilla/rust-android-gradle/blob/master/plugin/src/main/resources/com/nishtahir/linker-wrapper.bat

NOTE: this is needed if you want to compile the Rust project directly instead of using Gradle

```
[target.armv7-linux-androideabi]
# default to "cc": "error: linker `cc` not found"
# cf Android\Sdk\ndk\XXX\toolchains\arm-linux-androideabi-4.9\prebuilt\windows-x86_64\arm-linux-androideabi\bin
# "error: incorrect value `armv7a-linux-androideabi21-clang` for codegen option `linker-flavor` -
# one of: em gcc l4-bender ld msvc ptx-linker bpf-linker wasm-ld ld64.lld ld.lld lld-link  was expected"
# rustflags = ["-C", "linker-flavor=lld-link"]
#
#  = note: ld: error: unable to find library -ldl
#          ld: error: unable to find library -llog
#          ld: error: unable to find library -lgcc
#          ld: error: unable to find library -ldl
#          ld: error: unable to find library -lc
#          ld: error: unable to find library -lm
#
# linker = "C:\\Users\\nat\\AppData\\Local\\Android\\Sdk\\ndk\\22.1.7171670\\toolchains\\llvm\\prebuilt\\windows-x86_64\\arm-linux-androideabi\\bin\\ld.exe"
#
#  = note: C:\Users\nat\AppData\Local\Android\Sdk\ndk\22.1.7171670\toolchains\arm-linux-androideabi-4.9\prebuilt\windows-x86_64\bin\arm-linux-android
#eabi-ld.exe: error: cannot find -ldl
#          C:\Users\nat\AppData\Local\Android\Sdk\ndk\22.1.7171670\toolchains\arm-linux-androideabi-4.9\prebuilt\windows-x86_64\bin\arm-linux-android
#eabi-ld.exe: error: cannot find -llog
#          C:\Users\nat\AppData\Local\Android\Sdk\ndk\22.1.7171670\toolchains\arm-linux-androideabi-4.9\prebuilt\windows-x86_64\bin\arm-linux-android
#eabi-ld.exe: error: cannot find -lgcc
#          C:\Users\nat\AppData\Local\Android\Sdk\ndk\22.1.7171670\toolchains\arm-linux-androideabi-4.9\prebuilt\windows-x86_64\bin\arm-linux-android
#eabi-ld.exe: error: cannot find -ldl
#          C:\Users\nat\AppData\Local\Android\Sdk\ndk\22.1.7171670\toolchains\arm-linux-androideabi-4.9\prebuilt\windows-x86_64\bin\arm-linux-android
#eabi-ld.exe: error: cannot find -lc
#          C:\Users\nat\AppData\Local\Android\Sdk\ndk\22.1.7171670\toolchains\arm-linux-androideabi-4.9\prebuilt\windows-x86_64\bin\arm-linux-android
#eabi-ld.exe: error: cannot find -lm
#linker = "C:\\Users\\nat\\AppData\\Local\\Android\\Sdk\\ndk\\22.1.7171670\\toolchains\\arm-linux-androideabi-4.9\\prebuilt\\windows-x86_64\\bin\\arm-linux-androideabi-ld"
# linker = "C:\\Users\\nat\\AppData\\Local\\Android\\Sdk\\ndk\\22.1.7171670\\toolchains\\arm-linux-androideabi-4.9\\prebuilt\\windows-x86_64\\arm-linux-androideabi\\bin\\ld.exe"
# same
# better?
#   = note: lld: error: unknown argument: --version-script=C:\Users\nat\AppData\Local\Temp\rustcepdewB\list
#          lld: error: unknown argument: --as-needed
#          lld: error: unknown argument: --eh-frame-hdr
#          lld: error: unknown argument: -znoexecstack
#          lld: error: unknown argument: -zrelro
#          lld: error: unknown argument: -znow
#          clang: error: linker command failed with exit code 1 (use -v to see invocation)
#linker = "C:\\Users\\nat\\AppData\\Local\\Android\\Sdk\\ndk\\22.1.7171670\\toolchains\\llvm\\prebuilt\\windows-x86_64\\bin\\clang.exe"
#
# OK!
linker = "C:\\Users\\nat\\AppData\\Local\\Android\\Sdk\\ndk\\22.1.7171670\\toolchains\\llvm\\prebuilt\\windows-x86_64\\bin\\armv7a-linux-androideabi30-clang.cmd"

[target.aarch64-linux-android]
linker = "C:\\Users\\nat\\AppData\\Local\\Android\\Sdk\\ndk\\22.1.7171670\\toolchains\\llvm\\prebuilt\\windows-x86_64\\bin\\aarch64-linux-android30-clang.cmd"
```

#### FIX(windows) linker-wrapper.bat
`error: linking with `C:\Users\nat\Documents\workspace\interstellar\InterstellarWallet\build\linker-wrapper\linker-wrapper.bat` failed: exit code: 9009 note: Python was not found;`

- install Python