// https://github.com/jinleili/wgpu-on-app/blob/master/src/android.rs
// and https://github.com/gfx-rs/wgpu/discussions/1487

use android_logger::Config;
use bevy::prelude::Color;
use core::ffi::c_void;
use jni::objects::{AutoArray, JClass, JObject, JString, ReleaseMode};
use jni::sys::{jfloat, jfloatArray, jint, jlong, jstring};
use jni::JNIEnv;
use jni_fn::jni_fn;
use log::{debug, info, Level};
use raw_window_handle::{AndroidNdkHandle, RawWindowHandle};

// #[cfg(target_os = "android")]
use android_logger::FilterBuilder;

use crate::{
    init_app, my_raw_window_handle, update_texture_utils, vertices_utils::Rect, App,
    TextureUpdateCallbackType,
};

extern "C" {
    pub fn ANativeWindow_fromSurface(env: JNIEnv, surface: JObject) -> usize;
    // TODO maybe use:ANativeWindow_getFormat?
    pub fn ANativeWindow_getHeight(window_ptr: usize) -> u32;
    pub fn ANativeWindow_getWidth(window_ptr: usize) -> u32;
}

pub fn get_raw_window_handle(env: JNIEnv, surface: JObject) -> (RawWindowHandle, u32, u32) {
    let a_native_window = unsafe { ANativeWindow_fromSurface(env, surface) };
    let mut handle = AndroidNdkHandle::empty();
    handle.a_native_window = a_native_window as *mut c_void;

    let width = unsafe { ANativeWindow_getWidth(a_native_window) };
    let height = unsafe { ANativeWindow_getHeight(a_native_window) };

    return (RawWindowHandle::AndroidNdk(handle), width, height);
}

// TODO static state? or return Box<State> in initSurface and store as "long" in Kotlin?
// static mut state: Option<State> = None;size

fn init_surface(
    env: JNIEnv,
    surface: JObject,
    messageRects: jfloatArray,
    pinpadRects: jfloatArray,
    pinpad_nb_cols: usize,
    pinpad_nb_rows: usize,
    message_text_color: Color,
    circle_text_color: Color,
    circle_color: Color,
    background_color: Color,
) -> jlong {
    // TODO use loggers.rs(same as substrate-client)
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Trace)
            .with_tag("interstellar")
            .with_filter(
                FilterBuilder::new()
                    .parse("debug,jni::crate=debug,wgpu_hal=debug")
                    .build(),
            ),
    );

    let (handle, width, height) = get_raw_window_handle(env, surface);
    log::debug!(
        "initSurface: got handle! width = {}, height = {}",
        width,
        height
    );
    info!("initSurface before new_native");

    let mut message_rects_vec = unsafe {
        convert_rect_floatArr_to_vec_rect(env, messageRects, width as f32, height as f32)
    };
    let mut pinpad_rects_vec =
        unsafe { convert_rect_floatArr_to_vec_rect(env, pinpadRects, width as f32, height as f32) };
    assert!(
        message_rects_vec.len() == 1,
        "should have only ONE message_rects!",
    );
    assert!(
        pinpad_rects_vec.len() == pinpad_nb_cols * pinpad_nb_rows,
        "pinpadRects length MUST = pinpad_nb_cols * pinpad_nb_rows!"
    );
    // get the only Rect from "message_rects"; owned
    let message_rect = message_rects_vec.swap_remove(0);
    debug!("init_surface: message_rect: {:?}", message_rect);
    // pinpad: convert the Vec<> into a 2D matrix
    let mut pinpad_rects = ndarray::Array2::<Rect>::default((pinpad_nb_rows, pinpad_nb_cols));
    for row in 0..pinpad_nb_rows {
        for col in 0..pinpad_nb_cols {
            let index = col + row * pinpad_nb_cols;
            debug!(
                "init_surface: col: {:?}, row: {:?}, index: {}",
                col, row, index
            );
            pinpad_rects[[row, col]] = pinpad_rects_vec.get(index).unwrap().clone();
            // swap_remove takes the first(0 in this case), so no need to compute "let index = col + row * pinpad_nb_cols;"
            // pinpad_rects[[row, col]] = pinpad_rects_vec.swap_remove(0);
            // FAIL: the order ends up messed up, which means the "cancel" and "go" button are not in the right place
        }
    }

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
        pinpad_nb_cols,
        pinpad_nb_rows,
        message_text_color,
        circle_text_color,
        circle_color,
        background_color,
        include_bytes!("../examples/data/message_224x96.pgarbled.stripped.pb.bin").to_vec(),
        include_bytes!("../examples/data/message_224x96.packmsg.pb.bin").to_vec(),
        include_bytes!("../examples/data/pinpad_590x50.pgarbled.stripped.pb.bin").to_vec(),
        include_bytes!("../examples/data/pinpad_590x50.packmsg.pb.bin").to_vec(),
    );

    // NOTE: MUST be after init_app(or rather DefaultPlugins) else
    // panic at: "let mut windows = world.get_resource_mut::<Windows>().unwrap();"
    #[cfg(target_os = "android")]
    crate::init_window(
        &mut app,
        width,
        height,
        my_raw_window_handle::MyRawWindowHandleWrapper::new(handle),
    );

    info!("init_app ok!");

    Box::into_raw(Box::new(app)) as jlong
    // TODO static state?
    // 0
}

/// IMPORTANT: pinpadRects is assumed to be given from top->bottom, left->right
/// ie pinpadRects[0] is top left, pinpadRects[12] is bottom right
///
/// param: surface: SHOULD come from "override fun surfaceCreated(holder: SurfaceHolder)" holder.surface
#[no_mangle]
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub unsafe fn initSurface(
    env: JNIEnv,
    _: JClass,
    surface: JObject,
    messageRects: jfloatArray,
    pinpadRects: jfloatArray,
    pinpad_nb_cols: jint,
    pinpad_nb_rows: jint,
    message_text_color_hex: JString,
    circle_text_color_hex: JString,
    circle_color_hex: JString,
    background_color_hex: JString,
) -> jlong {
    init_surface(
        env,
        surface,
        messageRects,
        pinpadRects,
        pinpad_nb_cols.try_into().unwrap(),
        pinpad_nb_rows.try_into().unwrap(),
        Color::hex::<String>(
            env.get_string(message_text_color_hex)
                .expect("Couldn't get java string message_text_color_hex!")
                .into(),
        )
        .unwrap(),
        Color::hex::<String>(
            env.get_string(circle_text_color_hex)
                .expect("Couldn't get java string circle_text_color_hex!")
                .into(),
        )
        .unwrap(),
        Color::hex::<String>(
            env.get_string(circle_color_hex)
                .expect("Couldn't get java string circle_color_hex!")
                .into(),
        )
        .unwrap(),
        Color::hex::<String>(
            env.get_string(background_color_hex)
                .expect("Couldn't get java string background_color_hex!")
                .into(),
        )
        .unwrap(),
    )
}

#[no_mangle]
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub unsafe fn render(_env: *mut JNIEnv, _: JClass, obj: jlong) {
    // TODO static state?
    let app = &mut *(obj as *mut App);
    // NO! Conflicts with app.update and CRASH on Android
    // "app.run" ends up calling "app.update"
    app.run();
}

#[no_mangle]
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub unsafe fn update(_env: *mut JNIEnv, _: JClass, _obj: jlong) {
    // TODO static state?
    // let app = &mut *(obj as *mut App);
    // NO! Conflicts with app.update and CRASH on Android
    // "app.run" ends up calling "app.update"
    // app.update();
}

#[no_mangle]
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub unsafe fn cleanup(_env: *mut JNIEnv, _: JClass, obj: jlong) {
    let _obj: Box<App> = Box::from_raw(obj as *mut _);
}

/// Convert a floatArray like [left0, top0, right0, bottom0, left1, top2, right1, bottom1, ...]
/// into vec[Rect(left0, top0, right0, bottom0),Rect(left1, top2, right1, bottom1),...]
///
/// NOTE: will also convert the Coords to match Bevy
/// eg a Rect on the top of screen, full width:
//  0 = {Rect@20731} Rect.fromLTRB(0.0, 0.0, 1080.0, 381.0)
//  message_rects_flattened = {ArrayList@20533}  size = 4
//   0 = {Float@20689} 0.0
//   1 = {Float@20690} 0.0
//   2 = {Float@20691} 1080.0
//   3 = {Float@20692} 381.0
// will be converted to:
// Rect(left:0.0, top: height - 0.0, right: 1080, bottom: height - 381.0)
unsafe fn convert_rect_floatArr_to_vec_rect(
    env: JNIEnv,
    rectsFloatArray: jfloatArray,
    width: f32,
    height: f32,
) -> Vec<Rect> {
    let rects_floatarr = env
        .get_float_array_elements(rectsFloatArray, ReleaseMode::NoCopyBack)
        .unwrap();
    assert_ne!(
        rects_floatarr.size().unwrap(),
        0,
        "rects_floatarr is empty!"
    );
    assert_eq!(
        rects_floatarr.size().unwrap() % 4,
        0,
        "rects_floatarr MUST be % 4!"
    );

    let mut rects_vec =
        Vec::<Rect>::with_capacity((rects_floatarr.size().unwrap() / 4).try_into().unwrap());
    let mut idx = 0;
    for i in (0..rects_floatarr.size().unwrap()).step_by(4) {
        rects_vec.insert(
            idx,
            Rect::new_to_ndc_android(
                // message_rects_jlist.get(i).unwrap().unwrap().into(),
                // message_rects_jlist.get(i + 1).unwrap().unwrap().into(),
                // message_rects_jlist.get(i + 2).unwrap().unwrap().into(),
                // message_rects_jlist.get(i + 3).unwrap().unwrap().into(),
                *rects_floatarr.as_ptr().offset(i.try_into().unwrap()),
                *rects_floatarr.as_ptr().offset((i + 1).try_into().unwrap()),
                *rects_floatarr.as_ptr().offset((i + 2).try_into().unwrap()),
                *rects_floatarr.as_ptr().offset((i + 3).try_into().unwrap()),
                width,
                height,
            ),
        );
        idx += 1;
    }

    rects_vec
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
pub fn test_convert_rect_floatArr_to_vec_rect() {
    let env = attach_current_thread();

    //     result = {Rect[1]@20529}
    //  0 = {Rect@20731} Rect.fromLTRB(0.0, 0.0, 1080.0, 381.0)
    // message_rects_flattened = {ArrayList@20533}  size = 4
    //  0 = {Float@20689} 0.0
    //  1 = {Float@20690} 0.0
    //  2 = {Float@20691} 1080.0
    //  3 = {Float@20692} 381.0
    let buf: &[jfloat] = &[
        0.0 as jfloat,
        0.0 as jfloat,
        1080.0 as jfloat,
        381.0 as jfloat,
    ];
    let java_array = env
        .new_float_array(4)
        .expect("JNIEnv#new_float_array must create a Java jfloat array with given size");

    // Insert array elements
    let _ = env.set_float_array_region(java_array, 0, buf);

    let res = unsafe { convert_rect_floatArr_to_vec_rect(*env, java_array, 1080., 1920.) };

    assert_eq!(res[0], Rect::new(-0.5625, 1.0, 0.5625, 0.603125))
}
