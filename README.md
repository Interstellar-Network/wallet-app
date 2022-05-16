# Interstellar Wallet app

## Dev Details

Uses:
- Kotlin multiplatform to share as much code as possible b/w Android and iOs
- Android: Jetpack Compose for easy UI
- [TODO] iOs: SwiftUI for easy UI

### Android

TODO cleanup

use: https://github.com/bbqsrc/cargo-ndk#cargo-ndk---build-rust-code-for-android

- install NDK using Android Studio "SDK Manager"(ie "NDK (Side by side)" latest)
  - FOR NOW MUST BE <r23, cf below
  - https://github.com/rust-lang/rust/pull/85806 and https://github.com/rust-windowing/android-ndk-rs/pull/189
- `cargo install cargo-ndk`
  - MAYBE `cargo install ndk-build` instead: NO, it is a lib, and can only work as part of https://github.com/rust-windowing/android-ndk-rs/tree/master/cargo-apk ?
- `rustup target add aarch64-linux-android armv7-linux-androideabi`
  - maybe later add `x86_64-linux-android i686-linux-android` but that cover maybe 1% of devices?
- CHECK `cargo ndk -t armeabi-v7a -t arm64-v8a -o ./jniLibs build`
  NOTE: is will be called by gradle when needed, but better check in advance
  - `--release`

- install Python
- CHECK:
  - `cd shared\rust`
  - `cargo build --verbose --target=armv7-linux-androideabi` and `cargo build --verbose --target=aarch64-linux-android`
- [in Root=InterstellarWallet] .\gradlew cargoBuild --info

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