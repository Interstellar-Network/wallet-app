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

use crate::{
    init_app, my_raw_window_handle, update_texture_utils, vertices_utils::Rect, App,
    TextureUpdateCallbackType,
};
use bevy::prelude::Color;
use log::{debug, info, Level};
use raw_window_handle::{RawWindowHandle, UiKitHandle};
use std::ffi::{c_void, CStr, CString};
use std::os::raw::c_char;

#[cfg(target_os = "ios")]
use core_graphics::{base::CGFloat, geometry::CGRect};
#[cfg(target_os = "ios")]
use objc::{runtime::Object, *};

#[cfg(target_os = "ios")]
#[repr(C)]
pub struct IOSViewObj {
    pub view: *mut Object,
    // TODO? pub metal_layer: *mut c_void,
}

#[cfg(not(target_os = "ios"))]
pub struct IOSViewObj;

// TODO replace the "ptr" by a repr(c) struct
// cf https://github.com/jinleili/wgpu-on-app/blob/ba00bcc4eeabb257c6a2ee4da1dc379c0309a5b1/iOS/base/libwgpu_on_app.h

/// cf https://github.com/jinleili/wgpu-on-app/blob/4adf819ed6647a998d3264863b64af417e04a198/app-surface/src/ios.rs
#[cfg(target_os = "ios")]
pub fn get_raw_window_handle(obj: IOSViewObj) -> (RawWindowHandle, f32, f32) {
    let mut handle = UiKitHandle::empty();
    // TODO handle.ui_window = a_native_window as *mut c_void;
    handle.ui_view = obj.view as *mut c_void;
    // TODO? handle.ui_view_controller = a_native_window as *mut c_void;

    let scale_factor = get_scale_factor(obj.view);
    let s: CGRect = unsafe { msg_send![obj.view, frame] };
    let physical = (
        (s.size.width as f32 * scale_factor) as u32,
        (s.size.height as f32 * scale_factor) as u32,
    );

    return (
        RawWindowHandle::UiKit(handle),
        physical.0 as f32,
        physical.1 as f32,
    );
}

#[cfg(target_os = "ios")]
fn get_scale_factor(obj: *mut Object) -> f32 {
    let s: CGFloat = unsafe { msg_send![obj, contentScaleFactor] };
    s as f32
}

#[no_mangle]
pub extern "C" fn rust_init_surface(
    obj: IOSViewObj,
    // TODO cwrapper
    // surface: JObject,
    // TODO cwrapper
    // messageRects: jfloatArray,
    // pinpadRects: jfloatArray,
    // pinpad_nb_cols: usize,
    // pinpad_nb_rows: usize,
    // message_text_color: Color,
    // circle_text_color: Color,
    // circle_color: Color,
    // background_color: Color,
    // TODO cwrapper
    // message_pgarbled_buf: Vec<u8>,
    // message_packmsg_buf: Vec<u8>,
    // pinpad_pgarbled_buf: Vec<u8>,
    // pinpad_packmsg_buf: Vec<u8>,
) -> isize {
    // TODO? "hook up rust logging"
    // NOTE: it MAY crash, cf LogPlugin in lib.rs?
    // #[cfg(target_os = "ios")]
    // env_logger::init();

    #[cfg(target_os = "ios")]
    let (handle, width, height) = get_raw_window_handle(obj);
    #[cfg(not(target_os = "ios"))]
    let width = 0;
    #[cfg(not(target_os = "ios"))]
    let height = 0;

    info!(
        "initSurface after get_raw_window_handle: width = {}, height = {}",
        width, height
    );

    let message_rect = Rect::new_to_ndc_android(
        0.,
        0.,
        width as f32,
        width as f32 * 0.25,
        width as f32,
        height as f32,
    );
    let pinpad_rects = generate_pinpad_rects();

    // TODO?
    // let size = winit::dpi::PhysicalSize::new(width, height);
    // &awindow,
    //     size,
    //     update_texture_data,
    //     vertices,
    //     indices,
    //     texture_base,
    let mut app = App::new();

    log::debug!("before init_app");
    init_app(
        &mut app,
        message_rect,
        pinpad_rects,
        3,
        4,
        bevy::render::color::Color::WHITE,
        bevy::render::color::Color::WHITE,
        bevy::render::color::Color::hex("0080FFFF").unwrap(),
        bevy::render::color::Color::BLACK,
        // TODO cwrapper DEV/DEBUG: offline
        include_bytes!("../examples/data/message_224x96.pgarbled.stripped.pb.bin").to_vec(),
        include_bytes!("../examples/data/message_224x96.packmsg.pb.bin").to_vec(),
        include_bytes!("../examples/data/pinpad_590x50.pgarbled.stripped.pb.bin").to_vec(),
        include_bytes!("../examples/data/pinpad_590x50.packmsg.pb.bin").to_vec(),
        // message_pgarbled_buf,
        // message_packmsg_buf,
        // pinpad_pgarbled_buf,
        // pinpad_packmsg_buf,
    );

    // NOTE: MUST be after init_app(or rather DefaultPlugins) else
    // panic at: "let mut windows = world.get_resource_mut::<Windows>().unwrap();"
    #[cfg(target_os = "ios")]
    crate::init_window(
        &mut app,
        width as u32,
        height as u32,
        my_raw_window_handle::MyRawWindowHandleWrapper::new(handle),
    );

    info!("init_app ok!");

    Box::into_raw(Box::new(app)) as isize
    // TODO static state?
    // 0
}

#[no_mangle]
pub unsafe fn rust_render(ptr_app: isize) {
    // TODO static state?
    let app = &mut *(ptr_app as *mut App);
    // DO NOT use app.run() cf https://github.com/bevyengine/bevy/blob/main/examples/app/custom_loop.rs
    // calling app.run() makes Android display not updating after a few loops.
    // The texture are setup, circuit_evaluate runs a few times and then nothing changes anymore
    // change_texture_message/change_texture_pinpad are NOT called anymore
    // app.run();
    app.update();
}

#[no_mangle]
pub unsafe fn rust_cleanup_app(obj: *mut c_void) {
    let _obj: Box<App> = Box::from_raw(obj as *mut _);
}

// TODO TOREMOVE this is just until we get those dynamically from Swift
pub fn generate_pinpad_rects() -> ndarray::Array2<Rect> {
    const NB_COLS: usize = 3;
    const NB_ROWS: usize = 4;
    let mut pinpad_rects = ndarray::Array2::default((NB_ROWS, NB_COLS));

    // roughly match what we get from Android; this is just for consistency
    // result = {Rect[12]@20528}
    // 0 = {Rect@20764} Rect.fromLTRB(212.0, 670.0, 399.0, 857.0)
    // 1 = {Rect@20765} Rect.fromLTRB(446.0, 670.0, 633.0, 857.0)
    // 2 = {Rect@20766} Rect.fromLTRB(680.0, 670.0, 867.0, 857.0)
    // 3 = {Rect@20767} Rect.fromLTRB(212.0, 872.0, 399.0, 1059.0)
    // 4 = {Rect@20768} Rect.fromLTRB(446.0, 872.0, 633.0, 1059.0)
    // 5 = {Rect@20769} Rect.fromLTRB(680.0, 872.0, 867.0, 1059.0)
    // 6 = {Rect@20770} Rect.fromLTRB(212.0, 1074.0, 399.0, 1261.0)
    // 7 = {Rect@20771} Rect.fromLTRB(446.0, 1074.0, 633.0, 1261.0)
    // 8 = {Rect@20772} Rect.fromLTRB(680.0, 1074.0, 867.0, 1261.0)
    // 9 = {Rect@20773} Rect.fromLTRB(212.0, 1276.0, 399.0, 1463.0)
    // 10 = {Rect@20774} Rect.fromLTRB(446.0, 1276.0, 633.0, 1463.0)
    // 11 = {Rect@20775} Rect.fromLTRB(680.0, 1276.0, 867.0, 1463.0)

    // TODO ios
    // obtained from Simulator
    let temp_rect = Rect::new_to_ndc_android(400.0, 400.0, 450.0, 450.0, 828.0, 828.0);
    pinpad_rects[[0, 0]] = temp_rect.clone();
    pinpad_rects[[0, 1]] = temp_rect.clone();
    pinpad_rects[[0, 2]] = temp_rect.clone();
    pinpad_rects[[1, 0]] = temp_rect.clone();
    pinpad_rects[[1, 1]] = temp_rect.clone();
    pinpad_rects[[1, 2]] = temp_rect.clone();
    pinpad_rects[[2, 0]] = temp_rect.clone();
    pinpad_rects[[2, 1]] = temp_rect.clone();
    pinpad_rects[[2, 2]] = temp_rect.clone();
    pinpad_rects[[3, 0]] = temp_rect.clone();
    pinpad_rects[[3, 1]] = temp_rect.clone();
    pinpad_rects[[3, 2]] = temp_rect.clone();

    /*
    pinpad_rects[[0, 0]] = Rect::new_to_ndc_android(212.0, 670.0, 399.0, 857.0, 1080.0, 1920.0);
    pinpad_rects[[0, 1]] = Rect::new_to_ndc_android(446.0, 670.0, 633.0, 857.0, 1080.0, 1920.0);
    pinpad_rects[[0, 2]] = Rect::new_to_ndc_android(680.0, 670.0, 867.0, 857.0, 1080.0, 1920.0);
    pinpad_rects[[1, 0]] = Rect::new_to_ndc_android(212.0, 872.0, 399.0, 1059.0, 1080.0, 1920.0);
    pinpad_rects[[1, 1]] = Rect::new_to_ndc_android(446.0, 872.0, 633.0, 1059.0, 1080.0, 1920.0);
    pinpad_rects[[1, 2]] = Rect::new_to_ndc_android(680.0, 872.0, 867.0, 1059.0, 1080.0, 1920.0);
    pinpad_rects[[2, 0]] = Rect::new_to_ndc_android(212.0, 1074.0, 399.0, 1261.0, 1080.0, 1920.0);
    pinpad_rects[[2, 1]] = Rect::new_to_ndc_android(446.0, 1074.0, 633.0, 1261.0, 1080.0, 1920.0);
    pinpad_rects[[2, 2]] = Rect::new_to_ndc_android(680.0, 1074.0, 867.0, 1261.0, 1080.0, 1920.0);
    pinpad_rects[[3, 0]] = Rect::new_to_ndc_android(212.0, 1276.0, 399.0, 1463.0, 1080.0, 1920.0);
    pinpad_rects[[3, 1]] = Rect::new_to_ndc_android(446.0, 1276.0, 633.0, 1463.0, 1080.0, 1920.0);
    pinpad_rects[[3, 2]] = Rect::new_to_ndc_android(680.0, 1276.0, 867.0, 1463.0, 1080.0, 1920.0);
    */

    pinpad_rects
}
