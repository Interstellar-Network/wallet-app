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

#[no_mangle]
pub extern "C" fn rust_call_extrinsic(c_ptr_url: *const c_char) -> *mut c_char {
    let c_url = unsafe { CStr::from_ptr(c_ptr_url) };
    let url = match c_url.to_str() {
        Err(_) => "there",
        Ok(string) => string,
    };

    // let tx_hash = call_extrinsic(&url);
    // CString::new(tx_hash.to_string()).unwrap().into_raw()
    todo!("port c_wrapper to new integritee client")
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
