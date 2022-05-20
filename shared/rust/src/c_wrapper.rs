use std::ffi::{CStr, CString};
use std::os::raw::c_char;

use crate::call_extrinsic;

#[no_mangle]
pub extern "C" fn rust_call_extrinsic(c_ptr_url: *const c_char) -> *mut c_char {
    let c_url = unsafe { CStr::from_ptr(c_ptr_url) };
    let url = match c_url.to_str() {
        Err(_) => "there",
        Ok(string) => string,
    };

    let tx_hash = call_extrinsic(&url);

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
