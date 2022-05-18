// cf https://docs.rs/jni/latest/jni/#the-rust-side

use jni::objects::{JClass, JString};
use jni::sys::JNI_VERSION_1_6;
use jni::sys::{jint, jstring};
use jni::{JNIEnv, JavaVM};
use std::os::raw::c_void;

use crate::call_extrinsic;

#[cfg(target_os = "android")]
use crate::loggers;

#[allow(non_snake_case)]
#[no_mangle]
pub extern "system" fn JNI_OnLoad(vm: JavaVM, _: *mut c_void) -> jint {
    let env = vm.get_env().expect("Cannot get reference to the JNIEnv");

    loggers::init_android_logger();

    // MUST be 1.6?
    // https://developer.android.com/training/articles/perf-jni#native-libraries
    JNI_VERSION_1_6
}

// "This keeps Rust from "mangling" the name and making it unique for this
// crate."
#[no_mangle]
pub extern "system" fn Java_gg_interstellar_wallet_RustWrapper_CallExtrinsic(
    env: JNIEnv,
    // "This is the class that owns our static method. It's not going to be used,
    // but still must be present to match the expected signature of a static
    // native method."
    class: JClass,
    url: JString,
) -> jstring {
    // "First, we have to get the string out of Java. Check out the `strings`
    // module for more info on how this works."
    let url: String = env
        .get_string(url)
        .expect("Couldn't get java string!")
        .into();

    let tx_hash = call_extrinsic(&url);
    // TODO error handling: .unwrap()

    // "Then we have to create a new Java string to return. Again, more info
    // in the `strings` module."
    let output = env
        .new_string(tx_hash.to_string())
        .expect("Couldn't create java string!");

    // "Finally, extract the raw pointer to return."
    output.into_inner()
}
