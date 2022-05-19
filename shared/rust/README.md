## dev

NOTE: only compile with nightly toolchain else https://github.com/scs/substrate-api-client/issues/166#issuecomment-975614152
`rustup override set nightly`

- start the node template
- cargo build --features=with-jni
- cargo test --features=with-jni

### Android Debugging with VSCode

You can check how lldb is started in /.../android-studio/plugins/android-ndk/resources/lldb/android/start_lldb_server.sh

To get the current port: `adb shell run-as gg.interstellar.wallet.android cat /data/data/gg.interstellar.wallet.android/lldb/log/gdb-server.log`
(look at the beginning)

AS lldb console: `(lldb) breakpoint set --file src/lib.rs --line 24` OK

useful: `adb shell pm path gg.interstellar.wallet.android`?

- install https://marketplace.visualstudio.com/items?itemName=rioj7.command-variable
- add: `echo -n $PLATFORM_SOCKET > socket` at the end of `start_lldb_server.sh`
    - "-n" = NO newline; this is IMPORTANT else it breaks VSCode parsing down the line
- vscode: create task to grep using eg
```json
"label": "extract_android_lldb_socket",
"command": "mkdir -p /tmp/lldb_android/ && adb shell run-as gg.interstellar.wallet.android cat /data/data/gg.interstellar.wallet.android/lldb/tmp/socket > /tmp/lldb_android/socket",
```

WIP: use `~/.vscode-server/extensions/vadimcn.vscode-lldb-1.7.0/lldb/bin/lldb`

- `adb shell run-as gg.interstellar.wallet.android /data/data/gg.interstellar.wallet.android/lldb/bin/lldb-server platform --listen "*:5039" --server`
    - `adb shell /data/local/tmp/lldb-server platform --listen "*:5039" --server`
- NO! adb forward tcp:5039 tcp:5039
- `platform select remote-android`
- `platform connect unix-abstract-connect://localhost:5039`
- `platform process list`
- `adb shell pidof gg.interstellar.wallet.android`
- (lldb) attach --pid 11135 (--name gg.interstellar.wallet.android ???)
error: attach failed: Operation not permitted
SAME WITH RUN-AS

#### try without --server

adb shell run-as gg.interstellar.wallet.android /data/data/gg.interstellar.wallet.android/lldb/bin/lldb-server gdbserver --attach 11135 "*:5037"

**WARNING**: if you get failed to attach to process 11135: Operation not permittederror: failed to attach to pid 11135: Operation not permitte
it (probably) means there is already a debugger attached; possible Android Studio

- `target create target/x86_64-linux-android/debug/libshared_rs.so`
- `breakpoint set --file lib.rs --line 24`

####

grep lines in gdb-server.log:
```
1652962422.857038975 GDBRemoteCommunicationServerLLGS::Handle_vAttach attempting to attach to pid 10259
1652962422.857058048 GDBRemoteCommunicationServerLLGS::AttachToProcess pid 10259
1652962422.861745119 GDBRemoteCommunicationServerLLGS::InitializeDelegate called with NativeProcessProtocol pid 10259, current state: invalid
```

MATCHES: `adb shell pidof gg.interstellar.wallet.android`

(lldb) attach --pid 10259
error: attach failed: Operation not permitted

####

adb shell /data/local/tmp/lldb-server platform --listen "*:5039" --attach 10259

#### lldb-server more logging

Edit Android Studio -> Run/Debug Config -> Debugger: Logging: Target Channel
- default: `lldb process:gdb-remote packets`
- verbose: `lldb all:gdb-remote all`

This is passed as arg `--log-channels`