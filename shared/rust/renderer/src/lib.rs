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
use bevy::render::camera::ScalingMode;
use bevy::{asset::LoadState, prelude::*};
use ndarray::Array2;

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
use setup::setup_camera;

pub mod my_raw_window_handle;
pub mod vertices_utils;

mod setup;
mod update_texture_utils;
#[cfg(target_os = "android")]
mod winit_raw_handle_plugin;

// #[cfg_attr(target_os = "android", path = "jni_wrapper.rs", allow(non_snake_case))]
mod jni_wrapper;

/// IMPORTANT: if you change it, adjust renderer/src/vertices_utils.rs else it will
/// not position the message/pinpad correctly
pub const CameraScalingMode: ScalingMode = ScalingMode::FixedVertical(1.0);

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
    background_color: Color,
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

/// Init the Window with winit
/// Only needed for Android; this replaces "WinitPlugin"
#[cfg(target_os = "android")]
#[cfg(target_os = "android")]
pub fn init_window(
    app: &mut App,
    physical_width: u32,
    physical_height: u32,
    raw_window_handle: my_raw_window_handle::MyRawWindowHandleWrapper,
) {
    app.add_plugin(winit_raw_handle_plugin::WinitPluginRawWindowHandle::new(
        physical_width,
        physical_height,
        1.0,
        raw_window_handle,
    ));
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
/// WARNING: apparently using WHITE(which is Sprite's default) for text colors breaks the shader
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
    plugin_skip_window: bool,
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

    ////////////////////////////////////////////////////////////////////////////
    /// circuits init, via crate ../circuit_evaluate
    let message_evaluate_wrapper = circuit_evaluate::EvaluateWrapper::new(message_pgc_buf);
    let pinpad_evaluate_wrapper = circuit_evaluate::EvaluateWrapper::new(pinpad_pgc_buf);

    // TODO? #[cfg(target_os = "android")]
    // default runner crash at app.run on Android
    // app.set_runner(my_runner);

    // TODO CHECK history for why "app.add_plugins_with(DefaultPlugins, |group| group.disable::<ImagePlugin>());"

    // TODO? for Android: https://github.com/bevyengine/bevy/blob/main/examples/app/without_winit.rs

    // DEFAULT: /.../bevy_internal-0.9.1/src/default_plugins.rs
    // group = group
    // .add(bevy_log::LogPlugin::default())
    // .add(bevy_core::CorePlugin::default())
    // .add(bevy_time::TimePlugin::default())
    // .add(bevy_transform::TransformPlugin::default())
    // .add(bevy_hierarchy::HierarchyPlugin::default())
    // .add(bevy_diagnostic::DiagnosticsPlugin::default())
    // .add(bevy_input::InputPlugin::default())
    // .add(bevy_window::WindowPlugin::default());

    // WARNING: order matters!
    #[cfg(not(target_os = "android"))]
    app.add_plugin(bevy::log::LogPlugin { ..default() });
    app.add_plugin(bevy::core::CorePlugin { ..default() });
    app.add_plugin(bevy::time::TimePlugin { ..default() });
    app.add_plugin(bevy::transform::TransformPlugin { ..default() });
    app.add_plugin(bevy::hierarchy::HierarchyPlugin { ..default() });
    app.add_plugin(bevy::diagnostic::DiagnosticsPlugin { ..default() });
    app.add_plugin(bevy::input::InputPlugin { ..default() });
    // if !plugin_skip_window {
    //     app.add_plugin(bevy::window::WindowPlugin { ..default() });
    // }
    app.add_plugin(WindowPlugin {
        window: WindowDescriptor {
            title: "renderer demo".to_string(),
            width: 1920. / 2.,
            height: 1080. / 2.,
            // TODO?
            // present_mode: PresentMode::AutoVsync,
            ..default()
        },
        // MUST set ELSE: "thread 'main' panicked at 'Requested resource bevy_window::windows::Windows does not exist in the `World`."
        add_primary_window: true,
        ..default()
    });
    // #[cfg(feature = "bevy_asset")]
    app.add_plugin(bevy::asset::AssetPlugin { ..default() });
    // #[cfg(feature = "bevy_scene")]
    app.add_plugin(bevy::scene::ScenePlugin { ..default() });
    // the two next are feature gated behind #[cfg(feature = "bevy_render")]
    app.add_plugin(bevy::render::RenderPlugin { ..default() });
    app.add_plugin(bevy::render::texture::ImagePlugin { ..default() });
    app.add_plugin(bevy::winit::WinitPlugin { ..default() });
    // #[cfg(feature = "bevy_core_pipeline")]
    app.add_plugin(bevy::core_pipeline::CorePipelinePlugin { ..default() });
    // #[cfg(feature = "bevy_sprite")]
    app.add_plugin(bevy::sprite::SpritePlugin { ..default() });

    // TODO
    // app.add_plugins_with(DefaultPlugins, |group| {
    //     #[cfg(target_os = "android")]
    //     {
    //         // NOTE: this is in case we re-add "bevy_winit" for all arch later by mystake
    //         // Yes, to DISABLE WinitPlugin we need to enable "bevy_winit"...
    //         // REALLY IMPORTANT: else ndk-glue is used and we end up aborting at
    //         // pub fn native_activity() -> &'static NativeActivity {
    //         //     unsafe { NATIVE_ACTIVITY.as_ref().unwrap() }
    //         // }
    //         // TODO(android) #[cfg(feature = "bevy/bevy_winit")]
    //         // group.disable::<bevy::winit::WinitPlugin>();

    //         // crash: does not exist?? group.disable::<ImagePlugin>();
    //         // TODO FIX?: this crashes on Android see also android_logger::init_once in jni_wrapper.rs
    //         group.disable::<bevy::log::LogPlugin>()
    //     }
    // });

    // TODO how much msaa?
    app.insert_resource(Msaa { samples: 4 });
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
        background_color: background_color,
        circuit_dimension: [
            message_evaluate_wrapper.GetWidth().try_into().unwrap(),
            message_evaluate_wrapper.GetHeight().try_into().unwrap(),
        ],
    });
    app.add_startup_system(setup::setup_message_texture);
    // and same the pinpad
    app.insert_resource(RectsPinpad {
        rects: rects_pinpad,
        nb_cols: pinpad_nb_cols,
        nb_rows: pinpad_nb_rows,
        text_color: circle_text_color,
        circle_color: circle_color,
        circuit_dimension: [
            pinpad_evaluate_wrapper.GetWidth().try_into().unwrap(),
            pinpad_evaluate_wrapper.GetHeight().try_into().unwrap(),
        ],
    });
    app.add_startup_system(setup::setup_pinpad_textures);
    app.insert_resource(CircuitMessage {
        wrapper: message_evaluate_wrapper,
    });
    app.insert_resource(CircuitPinpad {
        wrapper: pinpad_evaluate_wrapper,
    });

    // TODO only when Debug?
    #[cfg(debug_assertions)]
    {
        app.add_plugin(LogDiagnosticsPlugin::default());
        app.add_plugin(FrameTimeDiagnosticsPlugin::default());
    }
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

        if let Some(mut image) = opt_handle.map(|handle| images.get_mut(handle)).flatten() {
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
        if let Some(texture_atlas) = opt_handle
            .map(|handle| texture_atlas.get_mut(handle))
            .flatten()
        {
            log::debug!("change_texture_pinpad texture_atlas OK");
            if let Some(mut atlas_image) = images.get_mut(&texture_atlas.texture) {
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
