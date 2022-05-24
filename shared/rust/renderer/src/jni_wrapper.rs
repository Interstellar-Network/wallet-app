// https://github.com/jinleili/wgpu-on-app/blob/master/src/android.rs
// and https://github.com/gfx-rs/wgpu/discussions/1487

use android_logger::Config;
use core::ffi::c_void;
use jni::objects::{JClass, JObject};
use jni::sys::jlong;
use jni::JNIEnv;
use jni_fn::jni_fn;
use log::{info, Level};
use raw_window_handle::{AndroidNdkHandle, HasRawWindowHandle, RawWindowHandle};

// #[cfg(target_os = "android")]
use android_logger::FilterBuilder;

use crate::{State, UpdateTextureDataType};

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
// static mut state: Option<State> = None;

impl State {
    async fn new_native<W>(
        window: &W,
        window_size: [u32; 2],
        update_texture_data: UpdateTextureDataType,
    ) -> Self
    where
        W: raw_window_handle::HasRawWindowHandle,
    {
        // TODO?
        let size = winit::dpi::PhysicalSize::new(window_size[0], window_size[1]);
        // let backend = wgpu::util::backend_bits_from_env().unwrap_or_else(wgpu::Backends::all);
        // let instance = wgpu::Instance::new(backend);
        // log::debug!("initializing surface");
        // let surface: wgpu::Surface = unsafe { instance.create_surface(window) };
        // log::debug!("successfully initialize surface: {:?}", surface);

        Self::new(window, size, update_texture_data).await
    }
}

struct AWindow {
    handle: RawWindowHandle,
}

unsafe impl HasRawWindowHandle for AWindow {
    fn raw_window_handle(&self) -> RawWindowHandle {
        return self.handle;
    }
}

fn update_texture_data(frame_number: usize) -> Vec<u8> {
    log::debug!("update_texture_data: frame_number = {}", frame_number,);
    let img = image::load_from_memory_with_format(
        include_bytes!("../examples/data/output_eval_frame0.png"),
        image::ImageFormat::Png,
    )
    .unwrap();
    let rgba = img.to_rgba8();
    rgba.into_vec()
}

///
/// param: surface: SHOULD come from "override fun surfaceCreated(holder: SurfaceHolder)" holder.surface
#[no_mangle]
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub unsafe fn initSurface(env: JNIEnv, _: JClass, surface: JObject) -> jlong {
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
    // AWindow is a wrapper for the handle, which implements HasRawWindowHandle(by directy return the handle)
    let awindow = AWindow { handle };

    info!("initSurface before new_native");

    let mut state = Some(futures::executor::block_on(State::new_native(
        &awindow,
        [width, height],
        update_texture_data,
    )))
    .unwrap();
    log::debug!("get state!");

    info!("initSurface state ok!");

    // TODO? probably not needed
    // state.resize(winit::dpi::PhysicalSize::<u32>::new(width, height));

    Box::into_raw(Box::new(state)) as jlong

    // TODO static state?
    // 0
}

#[no_mangle]
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub unsafe fn render(_env: *mut JNIEnv, _: JClass, obj: jlong) {
    // TODO static state?
    let state = &mut *(obj as *mut State);
    state.render();

    // TODO?
    // match state.render() {
    //     Ok(_) => {}
    //     // Reconfigure the surface if it's lost or outdated
    //     Err(wgpu::SurfaceError::Lost | wgpu::SurfaceError::Outdated) => state.resize(state.size),
    //     // The system is out of memory, we should probably quit
    //     // TODO? this is probably never called anyway
    //     // Err(wgpu::SurfaceError::OutOfMemory) => {*control_flow = ControlFlow::Exit,}
    //     Err(wgpu::SurfaceError::OutOfMemory) => todo!(),
    //     // We're ignoring timeouts
    //     Err(wgpu::SurfaceError::Timeout) => log::warn!("Surface timeout"),
    // }
}

#[no_mangle]
#[jni_fn("gg.interstellar.wallet.RustWrapper")]
pub unsafe fn update(_env: *mut JNIEnv, _: JClass, obj: jlong) {
    // TODO static state?
    let state = &mut *(obj as *mut State);
    state.update();
}

// #[no_mangle]
// #[jni_fn("gg.interstellar.wallet.RustWrapper")]
// pub unsafe fn createWgpuCanvas(env: *mut JNIEnv, _: JClass, surface: jobject, idx: jint) -> jlong {
//     // TODO use loggers.rs(same as substrate-client)
//     android_logger::init_once(
//         Config::default()
//             .with_min_level(Level::Trace)
//             .with_tag("interstellar")
//             .with_filter(FilterBuilder::new().parse("debug,jni::crate=debug").build()),
//     );

//     info!("createWgpuCanvas idx = {}", idx);
//     let canvas = WgpuCanvas::new(AppSurface::new(env as *mut _, surface), idx as i32);
//     info!("WgpuCanvas created!");
//     Box::into_raw(Box::new(canvas)) as jlong
// }

// #[no_mangle]
// #[jni_fn("gg.interstellar.wallet.RustWrapper")]
// pub unsafe fn enterFrame(_env: *mut JNIEnv, _: JClass, obj: jlong) {
//     let obj = &mut *(obj as *mut WgpuCanvas);
//     obj.enter_frame();
// }

// // #[no_mangle]
// // #[jni_fn("gg.interstellar.wallet.RustWrapper")]
// // pub unsafe fn changeExample(_env: *mut JNIEnv, _: JClass, obj: jlong, idx: jint) {
// //     let obj = &mut *(obj as *mut WgpuCanvas);
// //     obj.change_example(idx as i32);
// // }

// #[no_mangle]
// #[jni_fn("gg.interstellar.wallet.RustWrapper")]
// pub unsafe fn dropWgpuCanvas(_env: *mut JNIEnv, _: JClass, obj: jlong) {
//     let _obj: Box<WgpuCanvas> = Box::from_raw(obj as *mut _);
// }
