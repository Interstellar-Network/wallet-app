// Copyright 2022 Nathan Prat

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// cf https://docs.rs/jni/latest/jni/#the-rust-side

use jni::objects::{JClass, JString};
use jni::sys::JNI_VERSION_1_6;
use jni::sys::{jboolean, jint, jstring};
use jni::{JNIEnv, JavaVM};
use std::os::raw::c_void;

use crate::{extrinsic_garble_and_strip_display_circuits_package_signed, get_api};

use crate::loggers;

#[allow(non_snake_case)]
#[no_mangle]
pub extern "system" fn JNI_OnLoad(vm: JavaVM, _: *mut c_void) -> jint {
    let _env = vm.get_env().expect("Cannot get reference to the JNIEnv");

    loggers::init_logger();

    // MUST be 1.6?
    // https://developer.android.com/training/articles/perf-jni#native-libraries
    JNI_VERSION_1_6
}

// "This keeps Rust from "mangling" the name and making it unique for this
// crate."
#[no_mangle]
pub extern "system" fn Java_gg_interstellar_wallet_RustWrapper_ExtrinsicGarbleAndStripDisplayCircuitsPackage(
    env: JNIEnv,
    // "This is the class that owns our static method. It's not going to be used,
    // but still must be present to match the expected signature of a static
    // native method."
    _class: JClass,
    ws_url: JString,
    tx_message: JString,
) -> jstring {
    // "First, we have to get the string out of Java. Check out the `strings`
    // module for more info on how this works."
    let ws_url: String = env
        .get_string(ws_url)
        .expect("Couldn't get java string[url]!")
        .into();

    let tx_message: String = env
        .get_string(tx_message)
        .expect("Couldn't get java string[tx_message]!")
        .into();

    let api = get_api(&ws_url);
    let tx_hash = extrinsic_garble_and_strip_display_circuits_package_signed(&api, &tx_message);
    // TODO error handling: .unwrap()

    // "Then we have to create a new Java string to return. Again, more info
    // in the `strings` module."
    let output = env
        .new_string(tx_hash.to_string())
        .expect("Couldn't create java string!");

    // "Finally, extract the raw pointer to return."
    output.into_inner()
}
