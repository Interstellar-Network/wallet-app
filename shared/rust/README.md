## Android

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