## dev

NOTE: only compile with nightly toolchain else https://github.com/scs/substrate-api-client/issues/166#issuecomment-975614152
`rustup override set nightly`

- start the node template
- cargo build --features=with-jni
- cargo test --features=with-jni

### Debugging with VSCode

cf "android-debugging"
