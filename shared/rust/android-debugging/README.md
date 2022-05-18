## preparation

cf https://source.android.google.cn/devices/tech/debug/gdb#vscode ?
This is supposed to be when compiling Android from sources, but it is a good starting point.

NOTE: Android Studio DOES NOT support Rust, so we must "Attach" using VSCode lldb

- `cd android-debugging`
- get `lldbclient.py` from https://android.googlesource.com/platform/development/+/refs/heads/android12-release/scripts/
    - or other branch? NOTE: NOT all branches have this script??
    - NO: cf https://gitlab.com/m2crypto/m2crypto/-/blob/master/INSTALL.rst#id1 `sudo apt install python3-dev build-essential libssl-dev swig`
    - NO: `pip install adb` adb version does NOT work
        FAIL: "AttributeError: module 'adb' has no attribute 'DeviceNotFoundError'"
- get https://android.googlesource.com/platform/development/+/master/python-packages/gdbrunner/__init__.py and write as `gdbrunner.py`

NOTE: adb/ and gdbrunner/ where obtained with:
- `wget https://android.googlesource.com/platform/development/+archive/master/python-packages.tar.gz`
- `tar xvzf python-packages.tar.gz`

## run?

- `ANDROID_BUILD_TOP=. ANDROID_PRODUCT_OUT=. TARGET_PRODUCT=. python lldbclient.py --setup-forwarding vscode-lldb -n gg.interstellar.wallet.android`
