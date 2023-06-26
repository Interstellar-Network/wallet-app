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

use bevy::diagnostic::{FrameTimeDiagnosticsPlugin, LogDiagnosticsPlugin};
use bevy::prelude::*;
use bevy::render::camera::ScalingMode;
use bevy::window::WindowResolution;
use ndarray::Array2;

// TODO(bevy0.10) ??? or remove entirely?
// mod winit2;
// TODO?
// mod winit_raw_handle_plugin;

// eg 4 when ARGB/RGBA, 1 for GRAYSCALE
// MUST have a match b/w wgpu::TextureFormat and "update_texture_data"
const TEXTURE_PIXEL_NB_BYTES: u32 = 1;

/// IMPORTANT
/// The only way to make Bevy work with the Android Emulator(ie OpenGL) is to patch
/// bevy_render-0.7.0/src/texture/mod.rs
///
// impl BevyDefault for wgpu::TextureFormat {
//     fn bevy_default() -> Self {
//         if cfg!(target_os = "android") || cfg!(target_arch = "wasm32") {
//             // Bgra8UnormSrgb texture missing on some Android devices
//             wgpu::TextureFormat::Rgba8Unorm
//         } else {
//             wgpu::TextureFormat::Bgra8UnormSrgb
//         }
//     }
// }
//
// ALSO
// # TODO(android) AT LEAST FOR EMULATOR: "webgl", else ""wgpu::backend::direct: Shader translation error for stage VERTEX: The selected version doesn't support CUBE_TEXTURES_ARRAY""
// # cf https://github.com/bevyengine/bevy/blob/main/crates/bevy_pbr/src/render/mesh.rs
// b/c https://github.com/gfx-rs/naga/pull/1736
// But we do not need lighting/PBR for now so this is acceptable
pub use bevy::prelude::App;

pub mod vertices_utils;

mod setup;
mod update_texture_utils;

// #[cfg_attr(target_os = "android", path = "jni_wrapper.rs", allow(non_snake_case))]
mod jni_wrapper;

/// IMPORTANT: if you change it, adjust renderer/src/vertices_utils.rs else it will
/// not position the message/pinpad correctly
pub const CAMERA_SCALING_MODE: ScalingMode = ScalingMode::FixedVertical(1.0);

type EvaluateWrapperType = circuit_evaluate::EvaluateWrapper;
type TextureUpdateCallbackType =
    Option<Box<dyn FnMut(&mut Vec<u8>, &mut EvaluateWrapperType) + Send + Sync>>;

// TODO? Default, or impl FromWorld? In any case we need Option
// TODO? use a common Trait
#[derive(Default, Resource)]
pub struct TextureUpdateCallbackMessage {
    callback: TextureUpdateCallbackType,
}

#[derive(Default, Resource)]
pub struct TextureUpdateCallbackPinpad {
    callback: TextureUpdateCallbackType,
}

/// Declare the position/size of the message(usually at the top of the window, full width)
/// used via "insert_resource"
#[derive(Resource)]
pub struct RectMessage {
    rect: vertices_utils::Rect,
    text_color: Color,
    // background_color: Color,
    circuit_dimension: [u32; 2],
}

#[derive(Resource)]
pub struct RectsPinpad {
    rects: Array2<vertices_utils::Rect>,
    nb_cols: usize,
    nb_rows: usize,
    text_color: Color,
    circle_color: Color,
    circuit_dimension: [u32; 2],
}

// TODO? Default, or impl FromWorld? In any case we need Option
// TODO? use a common Trait
#[derive(Resource)]
pub struct CircuitMessage {
    wrapper: EvaluateWrapperType,
}

#[derive(Resource)]
pub struct CircuitPinpad {
    wrapper: EvaluateWrapperType,
}

/// param message_text_color: color of the segments on the message
/// param circle_text_color: color of the segments on the pinpad
/// param circle_background_color: color of the circle background(ie the 10 circles digits)
/// param background_color: color used as ClearColor, ie whole screen background
/// PINPAD: A good starting point is to have circle_text_color ~= background_color (eg white for a white theme),
/// and circle_background_color opposite(eg dark for a white theme)
/// MESSAGE: message_text_color ~ circle_background_color is OK
///
/// param message_pgc_buf/pinpad_pgc_buf: buffers containing a STRIPPED circuit.pgarbled.stripped.pb.bin
/// param message_packmsg_buf/pinpad_packmsg_buf: buffers containing the corresponding PACKMSG
///
/// WARNING: apparently using WHITE(which is Sprite's default) for text colors breaks the shader.
#[allow(clippy::too_many_arguments)]
pub fn init_app(
    app: &mut App,
    rect_message: vertices_utils::Rect,
    rects_pinpad: Array2<vertices_utils::Rect>,
    pinpad_nb_cols: usize,
    pinpad_nb_rows: usize,
    mut message_text_color: Color,
    mut circle_text_color: Color,
    circle_color: Color,
    background_color: Color,
    message_pgc_buf: Vec<u8>,
    pinpad_pgc_buf: Vec<u8>,
    #[cfg(target_os = "android")] physical_width: u32,
    #[cfg(target_os = "android")] physical_height: u32,
    #[cfg(target_os = "android")] raw_window_handle: raw_window_handle::RawWindowHandle,
) {
    // cf renderer/data/transparent_sprite.wgsl
    // apparently using WHITE(which is Sprite's default) make COLORED NOT defined and that breaks the shader!
    // That is b/c Color::WHITE is Sprite's default "color"
    // TODO find an elegant way to avoid this ugly workaround
    // NOTE: only need the w/a for colors used as a Sprite(and derived TextureAtlasSprite)'s color field
    // There is still an error in the logs:
    //     2022-07-04T13:16:07.054766Z ERROR bevy_render::render_resource::pipeline_cache: failed to process shader:
    // error: invalid field accessor `color`
    //    ┌─ wgsl:45:38
    //    │
    // 45 │     color = mix(BACKGROUND_COLOR, in.color, color.r);
    //    │                                      ^^^^^ invalid accessor
    // Possibly b/c we override the shader "setup_transparent_shader_for_sprites" so at the start the old one is used?
    if message_text_color == Color::WHITE {
        warn!("WORKAROUND for White color breaking shader; replacing 'message_text_color'");
        message_text_color = Color::rgb(0.99, 0.99, 0.99)
    }
    if circle_text_color == Color::WHITE {
        warn!("WORKAROUND for White color breaking shader; replacing 'circle_text_color'");
        circle_text_color = Color::rgb(0.99, 0.99, 0.99)
    }

    //**************************************************************************
    // circuits init, via crate ../circuit_evaluate
    let message_evaluate_wrapper = circuit_evaluate::EvaluateWrapper::new(message_pgc_buf);
    let pinpad_evaluate_wrapper = circuit_evaluate::EvaluateWrapper::new(pinpad_pgc_buf);

    // TODO? #[cfg(target_os = "android")]
    // default runner crash at app.run on Android
    // app.set_runner(my_runner);

    // TODO CHECK history for why "app.add_plugins_with(DefaultPlugins, |group| group.disable::<ImagePlugin>());"

    // TODO? for Android: https://github.com/bevyengine/bevy/blob/main/examples/app/without_winit.rs

    // DEFAULT: https://github.com/bevyengine/bevy/blob/289fd1d0f2353353f565989a2296ed1b442e00bc/crates/bevy_internal/src/default_plugins.rs#L43

    // WARNING: order matters!
    #[cfg(not(target_os = "android"))]
    app.add_plugin(bevy::log::LogPlugin { ..default() });
    app.add_plugin(bevy::core::TaskPoolPlugin { ..default() });
    app.add_plugin(bevy::core::TypeRegistrationPlugin { ..default() });
    app.add_plugin(bevy::core::FrameCountPlugin { ..default() });
    app.add_plugin(bevy::time::TimePlugin {});
    app.add_plugin(bevy::transform::TransformPlugin {});
    app.add_plugin(bevy::hierarchy::HierarchyPlugin {});
    app.add_plugin(bevy::diagnostic::DiagnosticsPlugin {});
    #[cfg(not(target_os = "android"))]
    app.add_plugin(bevy::input::InputPlugin {});
    app.add_plugin(WindowPlugin {
        primary_window: Some(Window {
            title: "renderer demo".to_string(),
            resolution: WindowResolution::new(1920. / 2., 1080. / 2.),
            // TODO?
            // present_mode: PresentMode::AutoVsync,
            ..default()
        }),
        ..default()
    });
    app.add_plugin(bevy::a11y::AccessibilityPlugin);
    // #[cfg(feature = "bevy_asset")]
    app.add_plugin(bevy::asset::AssetPlugin { ..default() });
    // #[cfg(feature = "bevy_scene")]
    // app.add_plugin(bevy::scene::ScenePlugin { ..default() });
    // the two next are feature gated behind #[cfg(feature = "bevy_render")]
    app.add_plugin(bevy::render::RenderPlugin { ..default() });
    app.add_plugin(bevy::render::texture::ImagePlugin { ..default() });
    // FAIL on Android?
    // thread '<unnamed>' panicked at 'called `Option::unwrap()` on a `None` value', /home/pratn/.cargo/registry/src/github.com-1ecc6299db9ec823/bevy_render-0.10.1/src/pipelined_rendering.rs:135:84
    #[cfg(not(any(target_arch = "wasm32", target_os = "android")))]
    app.add_plugin(bevy::render::pipelined_rendering::PipelinedRenderingPlugin { ..default() });
    // DO NOT use on Android:
    // else: thread '<unnamed>' panicked at 'Bevy must be setup with the #[bevy_main] macro on Android', /home/XXX/.cargo/registry/src/github.com-1ecc6299db9ec823/bevy_winit-0.10.1/src/lib.rs:65:22
    #[cfg(feature = "with_winit")]
    app.add_plugin(bevy::winit::WinitPlugin {});
    // Init the Window with our CUSTOM winit
    // Only needed for Android; this replaces "WinitPlugin"
    //
    // NOTE: MUST be after init_app(or rather DefaultPlugins) else
    // panic at: "let mut windows = world.get_resource_mut::<Windows>().unwrap();"
    #[cfg(all(target_os = "android", feature = "with_winit"))]
    compile_error!("FAIL android+with_winit is NOT supported!");
    // TODO(bevy0.10)
    // #[cfg(target_os = "android")]
    // app.add_plugin(winit_raw_handle_plugin::WinitPluginRawWindowHandle::new(
    //     physical_width,
    //     physical_height,
    //     1.0,
    //     // TODO?raw_window_handle,
    //     // my_raw_window_handle::MyRawWindowHandleWrapper::new(raw_window_handle),
    //     bevy::window::RawHandleWrapper {
    //         window_handle: raw_window_handle,
    //         display_handle: raw_window_handle::RawDisplayHandle::Android(
    //             raw_window_handle::AndroidDisplayHandle::empty(),
    //         ),
    //     },
    // ));
    // #[cfg(feature = "bevy_core_pipeline")]
    app.add_plugin(bevy::core_pipeline::CorePipelinePlugin {});
    // #[cfg(feature = "bevy_sprite")]
    app.add_plugin(bevy::sprite::SpritePlugin {});
    // TODO only when Debug?
    app.add_plugin(LogDiagnosticsPlugin::default());
    // TODO only when Debug?
    app.add_plugin(FrameTimeDiagnosticsPlugin::default());

    // TODO how much msaa?
    // MSAA makes some Android devices panic, this is under investigation
    // https://github.com/bevyengine/bevy/issues/8229
    #[cfg(target_os = "android")]
    app.insert_resource(Msaa::Off);
    #[cfg(not(target_os = "android"))]
    app.insert_resource(Msaa::Sample4);
    // TODO add param, and obtain from Android
    app.insert_resource(ClearColor(background_color));

    app.add_startup_system(setup::setup_camera);
    app.add_startup_system(setup::setup_transparent_shader_for_sprites);

    app.init_resource::<TextureUpdateCallbackMessage>();
    app.init_resource::<TextureUpdateCallbackPinpad>();
    app.add_startup_system(setup::setup_texture_update_systems);
    app.add_system(change_texture_message);
    app.add_system(change_texture_pinpad);

    // setup where and how to draw the message
    app.insert_resource(RectMessage {
        rect: rect_message,
        text_color: message_text_color,
        // background_color,
        circuit_dimension: [
            message_evaluate_wrapper.get_width().try_into().unwrap(),
            message_evaluate_wrapper.get_height().try_into().unwrap(),
        ],
    });
    app.add_startup_system(setup::setup_message_texture);
    // and same the pinpad
    app.insert_resource(RectsPinpad {
        rects: rects_pinpad,
        nb_cols: pinpad_nb_cols,
        nb_rows: pinpad_nb_rows,
        text_color: circle_text_color,
        circle_color,
        circuit_dimension: [
            pinpad_evaluate_wrapper.get_width().try_into().unwrap(),
            pinpad_evaluate_wrapper.get_height().try_into().unwrap(),
        ],
    });
    app.add_startup_system(setup::setup_pinpad_textures);
    app.insert_resource(CircuitMessage {
        wrapper: message_evaluate_wrapper,
    });
    app.insert_resource(CircuitPinpad {
        wrapper: pinpad_evaluate_wrapper,
    });
}

// https://github.com/bevyengine/bevy/pull/3139/files#diff-aded320ea899c7a8c225f19639c8aaab1d9d74c37920f1a415697262d6744d54
// https://bevy-cheatbook.github.io/assets/data.html
// https://github.com/bevyengine/bevy/discussions/3620
// TODO is this the proper way to modify a Texture's underlying data??
// TODO DRY change_texture_message+change_texture_pinpad
fn change_texture_message(
    mut query: Query<(&mut Sprite, &Transform, Option<&Handle<Image>>)>,
    mut images: ResMut<Assets<Image>>,
    mut texture_update_callback: ResMut<TextureUpdateCallbackMessage>,
    mut circuit_message: ResMut<CircuitMessage>,
) {
    // TODO investigate: without "mut sprite" it SOMETIMES update the texture, and sometimes it is just not visible
    log::debug!("change_texture_message BEGIN");
    for (_sprite, _t, opt_handle) in query.iter_mut() {
        log::debug!("change_texture_message query OK");

        // let size = if let Some(custom_size) = sprite.custom_size {
        //     custom_size
        // } else if let Some(image) = opt_handle.map(|handle| images.get(handle)).flatten() {
        //     Vec2::new(
        //         image.texture_descriptor.size.width as f32,
        //         image.texture_descriptor.size.height as f32,
        //     )
        // } else {
        //     Vec2::new(1.0, 1.0)
        // };
        // info!("{:?}", size * t.scale.truncate());

        if let Some(image) = opt_handle.and_then(|handle| images.get_mut(handle)) {
            log::debug!("change_texture_message images OK");

            // IMPORTANT: DO NOT use image.texture_descriptor.size.width/height
            // Eg:
            // - image.data.len(); 86016 -> OK = 224 * 96 * TEXTURE_PIXEL_NB_BYTES
            // - size.x as usize * size.y as usize * TEXTURE_PIXEL_NB_BYTES; = 10000 b/c the rendered texture is 50x50
            let data_len = image.data.len();
            (texture_update_callback.callback.as_mut().unwrap().as_mut())(
                &mut image.data,
                &mut circuit_message.wrapper,
            );
            assert!(
                image.data.len() == data_len,
                "image: modified data len! before: {}, after: {}",
                data_len,
                image.data.len()
            );

            // sprite.color = Color::ALICE_BLUE;
        }
    }
}

///
/// NOTE the TextureAtlas itself has a Handle<Image>, so we also need "ResMut<Assets<Image>>"
// TODO DRY change_texture_message+change_texture_pinpad
fn change_texture_pinpad(
    mut query: Query<(
        &mut TextureAtlasSprite,
        &Transform,
        Option<&Handle<TextureAtlas>>,
    )>,
    mut texture_atlas: ResMut<Assets<TextureAtlas>>,
    mut images: ResMut<Assets<Image>>,
    mut texture_update_callback: ResMut<TextureUpdateCallbackPinpad>,
    mut circuit_pinpad: ResMut<CircuitPinpad>,
) {
    // TODO investigate: without "mut sprite" it SOMETIMES update the texture, and sometimes it is just not visible
    // TODO FIX the query does not work -> texture_update_callback is not called
    log::debug!("change_texture_pinpad BEGIN");
    for (_sprite, _t, opt_handle) in query.iter_mut() {
        log::debug!("change_texture_pinpad query OK");
        if let Some(texture_atlas) = opt_handle.and_then(|handle| texture_atlas.get_mut(handle)) {
            log::debug!("change_texture_pinpad texture_atlas OK");
            if let Some(atlas_image) = images.get_mut(&texture_atlas.texture) {
                log::debug!("change_texture_pinpad texture_atlas.texture OK");
                let data_len = atlas_image.data.len();
                (texture_update_callback.callback.as_mut().unwrap().as_mut())(
                    &mut atlas_image.data,
                    &mut circuit_pinpad.wrapper,
                );
                assert!(
                    atlas_image.data.len() == data_len,
                    "atlas_image: modified data len! before: {}, after: {}",
                    data_len,
                    atlas_image.data.len()
                );
            }
        }
    }
}
