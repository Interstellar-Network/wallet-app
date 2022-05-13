# Interstellar Wallet app

## Dev Details

Uses:
- Kotlin multiplatform to share as much code as possible b/w Android and iOs
- Android: Jetpack Compose for easy UI
- [TODO] iOs: SwiftUI for easy UI

Prereq:
- setup Rust: https://www.rust-lang.org/tools/install

- `shared\3rd_party\polkaj> .\gradlew build -x test`
- `.\gradlew syncJars` will put all the .jar into 3rd_party/polkaj/build/libs

TODO?
- [windows only?] `rustup target add x86_64-unknown-linux-gnu`
  - NOTE: MUST match the target used by 3rd_party/substrate-client-java/crypto/build.gradle
    - add to `%USERPROFILE%\.cargo\config.toml`
    ```
    [target.x86_64-unknown-linux-gnu]
  linker = "rust-lld"
    ```
- `.\shared\3rd_party\substrate-client-java\ .\gradlew build -x test`
- FAIL: "rust-lld: error: unable to find library -lc" etc
- FIX? compile using correct target? use https://github.com/bbqsrc/cargo-ndk or https://github.com/mozilla/rust-android-gradle?