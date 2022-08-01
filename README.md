# Interstellar Wallet app

## Dev Details

NOTE: for now compiling on windows is not supported b/c of poor support of Rust cross-compiling wrt to Android NDK
It can probably be fixed but setting up Windows as dev env is a pain; it requires at least: Perl, Python, Rust, C++ Dev Tools, etc

Uses:
- Kotlin multiplatform to share as much code as possible b/w Android and iOs
- Android: Jetpack Compose for easy UI
- [TODO] iOs: SwiftUI for easy UI

NOTE: the Rust dependencies only compile on **nightly**

### Prereq

- install a C/C++ compiler and build tools
- CMake
  - [Linux] eg `apt-get install cmake`, or install prebuilt binaries
  - [MacOS] `brew install cmake`
- Protobuf(only for `protoc`)
  - [Linux] cf .github/workflows/android.yml (and adapt the paths to your machine)
  - [MacOS] `brew install protobuf` it installs a working `protoc` but we NEED the sources b/c we need
    to compile **lib**protobuf for the current target(ie arm)
    You may install `brew install gnu-tar`, then use `gtar` instead of `tar`
- `rustup toolchain install nightly`
- NOTE: only compile with nightly toolchain else https://github.com/scs/substrate-api-client/issues/166#issuecomment-975614152
  `rustup override set nightly`

#### Desktop

- test with a offline renderer+circuit_evaluate: `cargo run --features=with-jni --example desktop -- [--is-online]`

#### Android

- `rustup target add armv7-linux-androideabi --toolchain nightly`
- `rustup target add aarch64-linux-android --toolchain nightly`
- [needed only for Android Emulator] `rustup target add x86_64-linux-android --toolchain nightly`

#### iOs

cf https://kotlinlang.org/docs/multiplatform-mobile-integrate-in-existing-app.html#connect-the-framework-to-your-ios-project

- `rustup target add aarch64-apple-ios x86_64-apple-ios --toolchain nightly`
- Install Android Studio
- Android Studio: open the project "wallet-app"; this is needed to download the SDK etc else iOs can not compile
  - NOTE: you will have to download eg the NDK using the SDK Manager, and anything else that is required until you can Build on this machine
- Once you have built the Android App successfully once, you can close Android Studio
- Open the folder iOsApp in XCode and dev/compile/whatever
- MAYBE Download a simulator using XCode?
  - else:
  `cargo:warning=xcrun: error: SDK "iphonesimulator" cannot be located`
  `cargo:warning=xcrun: error: unable to lookup item 'Path' in SDK 'iphonesimulator'`
- XCode?
  - `sudo xcode-select --reset`
  - ~~[NO?] `sudo xcode-select --switch /Applications/Xcode.app/Contents/Developer/`~~
  - or `xcode-select --install`
- ~~TODO? add `/Library/Developer/CommandLineTools/SDKs/MacOSX.sdk` to `LIBRARY_PATH`?~~
- [to build on command line] IMPORTANT: if something fails, run the sequence cargo->gradle->XCode to help diag what is wrong;
It could a PATH, LD_LIBRARY_PATH/LIBRARY_PATH, etc
  - [for Simulator][Rust only] `.../wallet-app/shared$ cargo +nightly build --target=x86_64-apple-ios --features=with-cwrapper`
  - [for Simulator][Rust via Gradle] `.../wallet-app$ PLATFORM_NAME=iphonesimulator PLATFORM_PREFERRED_ARCH=x86_64 IPHONEOS_DEPLOYMENT_TARGET=14.1 ./gradlew --info :shared:cargoBuildIosSimulator`
  - [for Simulator][Full build] `.../wallet-app/iosApp$ xcodebuild -scheme iosApp -sdk iphonesimulator build`
  - [for real device] `.../wallet-app/iosApp$ xcodebuild -scheme iosApp build CODE_SIGN_IDENTITY=''`
    NOTE: gradle task "embedAndSignAppleFrameworkForXcode" will fail if signing not setup; but at least that allows testing Rust arm64 cross-compiling

NOTE: below SHOULD already be set, but double check the value:
- Project settings -> click "+" add "new User defined": `JAVA_HOME`
  - eg `/Applications/Android Studio.app/Contents/jre/Contents/Home/`(no need for double quotes or escaping)
  - ALTERNATIVE: add `export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jre/Contents/Home` before ./gradlew in `Build Phase -> Run Script` -> works but ugly

### IMPORTANT: Known issues/Various

#### Build issues(with Android Studio or XCode, or cargo directly)

If you have build issue, first try to build directly on the command line using eg `cargo +nightly build --target=REPLACEME_target`

That way you will know if the issue is on the Rust/Cargo side, or if the IDE is messing up some PATHS.

NOTE: if you get weird errors especially when building a dep which uses CMake/cc: do not hesitate to `cargo clean`!
That is b/c some crate have a CMake dep, and those have a tendency to be cached around; which means if you add a cargo target,
or change env var, etc it will NOT be detected and you will still get the same error when you try to rebuild.

#### XCode: cargo fails

```
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':shared:cargoBuildIosSimulator'.
> A problem occurred starting process 'command 'cargo''
```
But no error/stacktrace/etc

- `./gradlew --stop`
- `xcodebuild` cf [for Simulator][Full build]

#### Android/XCode: fails to to run cargo

If you get:
```
Showing All Messages
> A problem occurred starting process 'command 'cargo''
```
or
```
`cargo` No such file or directory at `exec`
```

and/or `'cargo': no such file or directory` when building with eg **but** `cargo +nightly build --target=x86_64-apple-ios --features=with-cwrapper` is working fine: you can try to kill the gradle daemon: `./gradlew --stop`

Apparently the gradle daemon can get a wrong env(`PATH`) in some cases??
cf https://github.com/gradle/gradle/issues/5631

That is b/c ~/.profile will NOT be sourced for GUI apps, so if the "first build after machine startup" is started from XCode, the daemon PATH
will not contain eg `cargo`...

#### VSCode

see https://github.com/bevyengine/bevy/issues/86#issuecomment-766100761

- if you have modified `rustflags` or `linker` in eg `~/.cargo/config.toml` make sure the desktop one and the on used for Android Emulator match
- else it will recompile from scratch when switching target...
- the easiest way to do it is to NOT set `rustflags` or `linker`
    - if you use Mold; set it as global linker by symlinking to `ld`
    - same for CCache

#### FIX: "error while loading shared libraries: libjvm.so: cannot open shared object file: No such file or directory"

- install JDK eg `sudo apt-get install openjdk-8-jdk`(cf .github/workflows/android.yml for a known-good VERSION)
    - NOTE: this is needed only to **run** some tests, NOT for compiling
- `export LD_LIBRARY_PATH=/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/server/`

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

### ARCHIVE

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