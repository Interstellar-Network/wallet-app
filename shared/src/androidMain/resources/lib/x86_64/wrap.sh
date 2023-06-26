#!/system/bin/sh

# TODO(bevy0.10) remove??? probably not needed anymore cf https://bevyengine.org/news/bevy-0-10/#enabled-opengl-backend-by-default

# IMPORTANT: https://developer.android.com/ndk/guides/wrap-script#debugging_when_using_wrapsh
# else will be stuck at "Waiting for application to come online"(the app works but the debugger does not)

cmd=$1
shift

os_version=$(getprop ro.build.version.sdk)

if [ "$os_version" -eq "27" ]; then
  cmd="$cmd -Xrunjdwp:transport=dt_android_adb,suspend=n,server=y -Xcompiler-option --debuggable $@"
elif [ "$os_version" -eq "28" ]; then
  cmd="$cmd -XjdwpProvider:adbconnection -XjdwpOptions:suspend=n,server=y -Xcompiler-option --debuggable $@"
else
  cmd="$cmd -XjdwpProvider:adbconnection -XjdwpOptions:suspend=n,server=y $@"
fi

WGPU_BACKEND=vulkan RUST_LOG=debug RUST_BACKTRACE=full exec $cmd