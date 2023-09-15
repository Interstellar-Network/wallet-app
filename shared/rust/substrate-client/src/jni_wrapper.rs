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

use crate::loggers;
use crate::InterstellarIntegriteeWorkerCli;
use crate::InterstellarIntegriteeWorkerCliTrait;
use common::{DisplayStrippedCircuitsPackage, DisplayStrippedCircuitsPackageBuffers};
use jni::objects::{JByteArray, JClass, JString};
use jni::sys::JNI_VERSION_1_6;
use jni::sys::{jint, jlong, jstring};
use jni::{JNIEnv, JavaVM};
use jni_fn::jni_fn;
use std::os::raw::c_void;

static RUNTIME_EXCEPTION_CLASS: &str = "java/lang/RuntimeException";

#[allow(non_snake_case)]
#[no_mangle]
pub extern "system" fn JNI_OnLoad(vm: JavaVM, _: *mut c_void) -> jint {
    let _env = vm.get_env().expect("Cannot get reference to the JNIEnv");

    loggers::init_logger();

    // MUST be 1.6?
    // https://developer.android.com/training/articles/perf-jni#native-libraries
    JNI_VERSION_1_6
}

/// IMPORTANT: this Extrinsic is async!
/// The circuit generation is started immediatelely(ish), but the results
/// are not available in IPFS until after at least a few seconds.
/// ie calling GetCircuits immediately usually fails!
///
// "This keeps Rust from "mangling" the name and making it unique for this
// crate."
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub fn ExtrinsicGarbleAndStripDisplayCircuitsPackage(
    mut env: JNIEnv,
    // "This is the class that owns our static method. It's not going to be used,
    // but still must be present to match the expected signature of a static
    // native method."
    _class: JClass,
    ws_url: JString,
    node_url: JString,
    tx_message: JString,
) -> jstring {
    // "First, we have to get the string out of Java. Check out the `strings`
    // module for more info on how this works."
    let ws_url: String = env
        .get_string(&ws_url)
        .expect("Couldn't get java string[ws_url]!")
        .into();
    let node_url: String = env
        .get_string(&node_url)
        .expect("Couldn't get java string[node_url]!")
        .into();

    let tx_message: String = env
        .get_string(&tx_message)
        .expect("Couldn't get java string[tx_message]!")
        .into();

    let worker_cli = InterstellarIntegriteeWorkerCli::new(&ws_url, &node_url);
    worker_cli
        .extrinsic_garble_and_strip_display_circuits_package_signed(&tx_message)
        .unwrap();
    // TODO error handling: .unwrap()

    // "Then we have to create a new Java string to return. Again, more info
    // in the `strings` module."
    let output = env
        .new_string("TODO TOREMOVE string ExtrinsicGarbleAndStripDisplayCircuitsPackage")
        .expect("Couldn't create java string!");

    // "Finally, extract the raw pointer to return."
    output.into_raw()
}

fn convert_jbytearray_to_vec(byte_arr: JByteArray, env: JNIEnv) -> Vec<u8> {
    // FAIL: works on desktop but crash on Android "panicked at 'called `Result::unwrap()` on an `Err` value: TryFromIntError(())', substrate-client/src/jni_wrapper.rs:104:18"
    // let byte_arr_autoarr = env
    //     .get_byte_array_elements(byte_arr, ReleaseMode::NoCopyBack)
    //     .unwrap();
    // assert_ne!(
    //     byte_arr_autoarr.size().unwrap(),
    //     0,
    //     "byte_arr_autoarr is empty!"
    // );

    // let bytearr_size: usize = byte_arr_autoarr.size().unwrap().try_into().unwrap();
    // log::info!(
    //     "byte_arr_autoarr size = {:?}",
    //     byte_arr_autoarr.size().unwrap()
    // );
    // let mut res_vec = Vec::<u8>::with_capacity(bytearr_size);
    // unsafe {
    //     for i in 0..bytearr_size {
    //         log::info!(
    //             "convert_jbytearray_to_vec raw: {} -> {:?}",
    //             i,
    //             (*byte_arr_autoarr.as_ptr().offset(i.try_into().unwrap()))
    //         );
    //         let x: u8 = (*byte_arr_autoarr.as_ptr().offset(i.try_into().unwrap()))
    //             .try_into()
    //             .unwrap();
    //         log::info!("convert_jbytearray_to_vec u8: {} -> {:?}", i, x);
    //         res_vec.push(x);
    //     }
    // }

    let res_vec = env.convert_byte_array(byte_arr).unwrap();

    log::info!("convert_jbytearray_to_vec = {:?}", res_vec);
    res_vec
}

// "This keeps Rust from "mangling" the name and making it unique for this
// crate."
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub fn ExtrinsicRegisterMobile(
    mut env: JNIEnv,
    // "This is the class that owns our static method. It's not going to be used,
    // but still must be present to match the expected signature of a static
    // native method."
    _class: JClass,
    ws_url: JString,
    node_url: JString,
    pub_key: JByteArray,
) -> jstring {
    // "First, we have to get the string out of Java. Check out the `strings`
    // module for more info on how this works."
    let ws_url: String = env
        .get_string(&ws_url)
        .expect("Couldn't get java string[ws_url]!")
        .into();
    let node_url: String = env
        .get_string(&node_url)
        .expect("Couldn't get java string[node_url]!")
        .into();

    let pub_key_vec = unsafe { convert_jbytearray_to_vec(pub_key, env.unsafe_clone()) };

    let worker_cli = InterstellarIntegriteeWorkerCli::new(&ws_url, &node_url);
    worker_cli.extrinsic_register_mobile(pub_key_vec);
    // TODO error handling: .unwrap()

    // "Then we have to create a new Java string to return. Again, more info
    // in the `strings` module."
    let output = env
        .new_string("TODO TOREMOVE string ExtrinsicRegisterMobile")
        .expect("Couldn't create java string!");

    // "Finally, extract the raw pointer to return."
    output.into_raw()
}

/// Get circuits, OR throw if there is no circuit ready!
/// To generate them: use Java_gg_interstellar_wallet_RustWrapper_ExtrinsicGarbleAndStripDisplayCircuitsPackage
///
/// WARNING: returns a POINTER to a Rust struct = common::DisplayStrippedCircuitsPackageBuffers
///
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub fn GetCircuits(
    mut env: JNIEnv,
    // "This is the class that owns our static method. It's not going to be used,
    // but still must be present to match the expected signature of a static
    // native method."
    _class: JClass,
    ws_url: JString,
    node_url: JString,
    ipfs_addr: JString,
) -> jlong {
    // "First, we have to get the string out of Java. Check out the `strings`
    // module for more info on how this works."
    let ws_url: String = env
        .get_string(&ws_url)
        .expect("Couldn't get java string[ws_url]!")
        .into();
    let node_url: String = env
        .get_string(&node_url)
        .expect("Couldn't get java string[node_url]!")
        .into();

    let ipfs_addr: String = env
        .get_string(&ipfs_addr)
        .expect("Couldn't get java string[ipfs_addr]!")
        .into();

    log::debug!("before get_latest_pending_display_stripped_circuits_package");
    let worker_cli = InterstellarIntegriteeWorkerCli::new(&ws_url, &node_url);
    let display_stripped_circuits_package_buffers = worker_cli
        .get_latest_pending_display_stripped_circuits_package(&ipfs_addr)
        .map_err(|_| {
            env.throw_new(RUNTIME_EXCEPTION_CLASS, "NoCircuitAvailableException")
                .unwrap()
        });

    // https://github.com/jni-rs/jni-rs/issues/101
    // sort of works without "new_byte_array", but the Java array is not the correct size so it then crash at
    // IFF the arrays are init with enough space: `ByteArray(10 * 1024 * 1024)`
    // ```
    // pub fn convert_byte_array(&self, array: jbyteArray) -> Result<Vec<u8>> {
    // non_null!(array, "convert_byte_array array argument");
    // let length = jni_non_void_call!(self.internal, GetArrayLength, array);
    // ```
    // FAIL: still not working
    // message_pgarbled_arr = env
    //     .new_byte_array(message_pgarbled_buf.len().try_into().unwrap())
    //     .unwrap();
    // message_packmsg_arr = env
    //     .new_byte_array(message_packmsg_buf.len().try_into().unwrap())
    //     .unwrap();
    // pinpad_pgarbled_arr = env
    //     .new_byte_array(pinpad_pgarbled_buf.len().try_into().unwrap())
    //     .unwrap();
    // pinpad_packmsg_arr = env
    //     .new_byte_array(pinpad_packmsg_buf.len().try_into().unwrap())
    //     .unwrap();

    // env.set_byte_array_region(
    //     message_pgarbled_arr,
    //     0,
    //     bytemuck::cast_slice::<u8, i8>(&*message_pgarbled_buf),
    // )
    // .unwrap();
    // env.set_byte_array_region(
    //     message_packmsg_arr,
    //     0,
    //     bytemuck::cast_slice::<u8, i8>(&*message_packmsg_buf),
    // )
    // .unwrap();
    // env.set_byte_array_region(
    //     pinpad_pgarbled_arr,
    //     0,
    //     bytemuck::cast_slice::<u8, i8>(&*pinpad_pgarbled_buf),
    // )
    // .unwrap();
    // env.set_byte_array_region(
    //     pinpad_packmsg_arr,
    //     0,
    //     bytemuck::cast_slice::<u8, i8>(&*pinpad_packmsg_buf),
    // )
    // .unwrap();

    Box::into_raw(Box::new(display_stripped_circuits_package_buffers)) as jlong
}

#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub fn GetMessageNbDigitsFromPtr(
    _env: JNIEnv,
    _class: JClass,
    circuits_package_ptr: jlong,
) -> jint {
    // DO NOT USE A Box; we DO NOT want to cleanup the memory at this time!
    let display_stripped_circuits_package_buffers =
        unsafe { &mut *(circuits_package_ptr as *mut DisplayStrippedCircuitsPackageBuffers) };

    display_stripped_circuits_package_buffers
        .package
        .message_nb_digits
        .try_into()
        .unwrap()
}

/// IMPORTANT: we MUST "clone" what is needed for ExtrinsicCheckInput b/c
/// renderer/src/jni_wrapper.rs initApp WILL cleanup "circuits_package_ptr"!
/// So here we just clone the whole "package" field, b/c that should be enough to be used a "tx_id"
#[no_mangle]
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub fn GetTxIdPtrFromPtr(_env: JNIEnv, _class: JClass, circuits_package_ptr: jlong) -> jlong {
    // DO NOT USE A Box; we DO NOT want to cleanup the memory at this time!
    let display_stripped_circuits_package_buffers =
        unsafe { &mut *(circuits_package_ptr as *mut DisplayStrippedCircuitsPackageBuffers) };

    Box::into_raw(Box::new(
        display_stripped_circuits_package_buffers.package.clone(),
    )) as jlong
}

/// @param circuits_package_ptr: returned from "GetTxIdPtrFromPtr"
///
/// IMPORTANT: "tx_id_ptr" is NOT valid after calling this function!
///
// "This keeps Rust from "mangling" the name and making it unique for this
// crate."
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub fn ExtrinsicCheckInput(
    mut env: JNIEnv,
    // "This is the class that owns our static method. It's not going to be used,
    // but still must be present to match the expected signature of a static
    // native method."
    _class: JClass,
    ws_url: JString,
    node_url: JString,
    tx_id_ptr: jlong,
    inputs: JByteArray,
) -> jstring {
    // USE a Box, that way the pointer is properly cleaned up when exiting this function
    let package: Box<DisplayStrippedCircuitsPackage> =
        unsafe { Box::from_raw(tx_id_ptr as *mut _) };

    // "First, we have to get the string out of Java. Check out the `strings`
    // module for more info on how this works."
    let ws_url: String = env
        .get_string(&ws_url)
        .expect("Couldn't get java string[ws_url]!")
        .into();
    let node_url: String = env
        .get_string(&node_url)
        .expect("Couldn't get java string[node_url]!")
        .into();

    let inputs_vec = unsafe { convert_jbytearray_to_vec(inputs, env.unsafe_clone()) };

    let worker_cli = InterstellarIntegriteeWorkerCli::new(&ws_url, &node_url);
    worker_cli
        .extrinsic_check_input(&package.message_pgarbled_cid, &inputs_vec)
        .unwrap();
    // TODO error handling: .unwrap()

    // "Then we have to create a new Java string to return. Again, more info
    // in the `strings` module."
    let output = env
        .new_string(
            "TODO TOREMOVE string Java_gg_interstellar_wallet_RustWrapper_ExtrinsicCheckInput",
        )
        .expect("Couldn't create java string!");

    // "Finally, extract the raw pointer to return."
    output.into_raw()
}

// https://github.com/jni-rs/jni-rs/blob/master/tests/util/mod.rs
#[cfg(test)]
#[cfg(target_os = "linux")] // we do not need jni features = ["invocation"] for Android
fn jvm() -> &'static std::sync::Arc<jni::JavaVM> {
    static mut JVM: Option<std::sync::Arc<jni::JavaVM>> = None;
    static INIT: std::sync::Once = std::sync::Once::new();

    INIT.call_once(|| {
        let jvm_args = jni::InitArgsBuilder::new()
            .version(jni::JNIVersion::V8)
            .option("-Xcheck:jni")
            .build()
            .unwrap_or_else(|e| panic!("{:#?}", e));

        let jvm = jni::JavaVM::new(jvm_args).unwrap_or_else(|e| panic!("{:#?}", e));

        unsafe {
            JVM = Some(std::sync::Arc::new(jvm));
        }
    });

    unsafe { JVM.as_ref().unwrap() }
}

#[cfg(test)]
#[cfg(target_os = "linux")] // we do not need jni features = ["invocation"] for Android
#[allow(dead_code)]
pub fn attach_current_thread() -> jni::AttachGuard<'static> {
    jvm()
        .attach_current_thread()
        .expect("failed to attach jvm thread")
}

// cf https://github.com/jni-rs/jni-rs/blob/master/tests/jni_api.rs
#[cfg(target_os = "linux")] // we do not need jni features = ["invocation"] for Android
#[test]
pub fn test_convert_jbytearray_to_vec() {
    let env = attach_current_thread();

    //     result = {Rect[1]@20529}
    //  0 = {Rect@20731} Rect.fromLTRB(0.0, 0.0, 1080.0, 381.0)
    // message_rects_flattened = {ArrayList@20533}  size = 4
    //  0 = {Float@20689} 0.0
    //  1 = {Float@20690} 0.0
    //  2 = {Float@20691} 1080.0
    //  3 = {Float@20692} 381.0
    let buf = vec![0, 42, 12, 42];
    let java_byte_array = env.byte_array_from_slice(&buf).unwrap();

    // Not sure why, but we need to clone everything?
    let vm = env.get_java_vm().unwrap();
    let env = vm.get_env().unwrap();

    let res = convert_jbytearray_to_vec(java_byte_array, env);

    assert_eq!(res, vec![0, 42, 12, 42])
}
