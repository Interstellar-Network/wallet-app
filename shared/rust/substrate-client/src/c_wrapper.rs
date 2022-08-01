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

use std::ffi::{CStr, CString};
use std::os::raw::c_char;

use crate::{
    extrinsic_check_input, extrinsic_garble_and_strip_display_circuits_package_signed,
    extrinsic_register_mobile, get_api, get_latest_pending_display_stripped_circuits_package,
};

#[no_mangle]
pub extern "C" fn rust_extrinsic_register_mobile(c_ptr_ws_url: *const c_char) -> *mut c_char {
    let c_ws_url = unsafe { CStr::from_ptr(c_ptr_ws_url) };
    let ws_url = match c_ws_url.to_str() {
        Err(_) => "there",
        Ok(string) => string,
    };

    let api = get_api(&ws_url);

    // TODO param for "pub_key"
    let tx_hash = extrinsic_register_mobile(&api, vec![0; 32]);

    CString::new(tx_hash.to_string()).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn rust_call_extrinsic_free(s: *mut c_char) {
    unsafe {
        if s.is_null() {
            return;
        }
        CString::from_raw(s)
    };
}
