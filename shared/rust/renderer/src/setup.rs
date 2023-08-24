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

use bevy::prelude::*;
use bevy::render::camera::{OrthographicProjection, ScalingMode};
use bevy::render::mesh::shape::Circle;
use bevy::render::render_resource::Extent3d;
use bevy::sprite::MaterialMesh2dBundle;

use crate::TEXTURE_PIXEL_NB_BYTES;

/// Init the Camera, with a 2D projection
// NOTE: not sure how to have a "add_startup_system" depends on another, so this is called FROM setup_main, not via "add_startup_system"
pub(super) fn setup_camera(mut commands: Commands) {
    // TODO TOREMOVE
    // // camera
    // let mut camera = OrthographicCameraBundle::new_2d();
    // //     depth_calculation: DepthCalculation::ZDifference,
    // //     ..Default::default()
    // // };
    // camera.orthographic_projection.scale = 1.0;
    // camera.orthographic_projection.scaling_mode = crate::CameraScalingMode;

    // let camera2 = camera.camera.clone();
    // let global_transform = camera.global_transform.clone();

    // TODO? use proj.get_projection_matrix()?
    // TODO proper values
    commands.spawn(Camera2dBundle {
        projection: OrthographicProjection {
            // Defaults to `0.0`
            near: -1.,
            scaling_mode: ScalingMode::FixedVertical(2.0),
            ..default()
        },
        ..default()
    });

    // FOR REFERENCE; useful to get the resolution from the camera
    // If needed in the future: add a resource with eg ResMut<MyCameraData> and pass around
    // let scale = 1.;
    // let (camera, camera_global_transform) = setup::setup_camera(&mut commands, scale);
    // // TODO which one should we use? The one that depends on DPI? (if any)
    // let logical_size = camera.target.get_logical_size(&windows, &images);
    // let physical_size = camera.target.get_physical_size(&windows, &images);
    // debug!(
    //     "logical_size: {:?}, physical_size: {:?}",
    //     logical_size, physical_size
    // );
}

/// draw a pinpad on the bottom part of the screen
///
/// @param sprite_width,sprite_height: dimensions of the TEXTURE inside the Circle
/// @param col_offset: leftmost column
/// @param col_stride: spacing b/w columns
/// @param row_offset: topmost column
/// @param row_stride: spacing b/w rows
pub(super) fn setup_pinpad_textures(
    mut commands: Commands,
    mut images: ResMut<Assets<Image>>,
    mut texture_atlas: ResMut<Assets<TextureAtlas>>,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials_color: ResMut<Assets<ColorMaterial>>,
    rects_pinpad: Res<crate::RectsPinpad>,
) {
    // TODO https://bevy-cheatbook.github.io/features/parent-child.html
    // circle is the parent, Texture is child

    // WARNING it is assumed that the layout is one row of 10 "cases"
    let atlas_width = rects_pinpad.circuit_dimension[0];
    let atlas_height = rects_pinpad.circuit_dimension[1];

    // pinpad
    let atlas_handle = texture_atlas.add(TextureAtlas::from_grid(
        images.add(uv_debug_texture(atlas_width, atlas_height)),
        Vec2::new((atlas_width as f32) / 10., atlas_height as f32),
        10,
        1,
        None,
        None,
    ));
    // draw a sprite from the atlas
    for row in 0..rects_pinpad.nb_rows {
        for mut col in 0..rects_pinpad.nb_cols {
            // on index = 9, we want to draw in the BOTTOM CENTER; which is why we move "col++"
            // TODO proper index directly(ie without "if"): exclude lower left(cancel button) and lower right(done button)
            let index = col + row * rects_pinpad.nb_cols;
            if index == 9 {
                col += 1;
            } else if index >= 10 {
                break;
            }

            let current_rect = &rects_pinpad.rects[[row, col]];

            let center_x = current_rect.center()[0];
            let center_y = current_rect.center()[1];

            commands.spawn(SpriteSheetBundle {
                transform: Transform {
                    translation: Vec3::new(center_x, center_y, 1.0),
                    ..default()
                },
                sprite: TextureAtlasSprite {
                    index,
                    custom_size: Some(Vec2::new(
                        current_rect.width() / 2.0,
                        current_rect.height() / 2.0,
                    )),
                    color: rects_pinpad.text_color,
                    ..default()
                },
                texture_atlas: atlas_handle.clone(),
                ..default()
            });

            // circle_radius: max(width, height), that way it works even if change
            let circle_radius = (current_rect.width() / 2.0).max(current_rect.height() / 2.0);

            commands.spawn(MaterialMesh2dBundle {
                mesh: meshes.add(Circle::new(circle_radius).into()).into(),
                material: materials_color.add(rects_pinpad.circle_color.into()),
                transform: Transform::from_xyz(center_x, center_y, 0.0),
                ..default()
            });
        }
    }
}

/// Will draw the message texture at the given RectMessage
pub(super) fn setup_message_texture(
    mut commands: Commands,
    mut images: ResMut<Assets<Image>>,
    rect_message: Res<crate::RectMessage>,
) {
    // Texture message = foreground
    commands.spawn(SpriteBundle {
        texture: images.add(uv_debug_texture(
            rect_message.circuit_dimension[0],
            rect_message.circuit_dimension[1],
        )),
        sprite: Sprite {
            custom_size: Some(Vec2::new(
                rect_message.rect.width(),
                rect_message.rect.height(),
            )),
            color: rect_message.text_color,
            ..default()
        },
        transform: Transform::from_xyz(
            // Sprite default to Anchor::Center which means x=0.0 will center it; and this also why "rect.height() / 2.0" and ""rect.width() / 2.0""
            rect_message.rect.center()[0],
            rect_message.rect.center()[1],
            1.0,
        ),
        ..default()
    });
}

/// add_startup_system: Init TextureUpdateCallbackMessage/TextureUpdateCallbackPinpad
/// using the mod "update_texture_utils"
// TODO ideally we would want to pass the function all the way from init_app, to completely
// decouple renderer and "circuit update"
// pub fn setup_texture_update_systems(
//     mut texture_update_callback_message: ResMut<TextureUpdateCallbackMessage>,
//     mut texture_update_callback_pinpad: ResMut<TextureUpdateCallbackPinpad>,
// ) {
//     texture_update_callback_message.callback = Some(Box::new(
//         crate::update_texture_utils::update_texture_data_message,
//     ));
//     texture_update_callback_pinpad.callback = Some(Box::new(
//         crate::update_texture_utils::update_texture_data_pinpad,
//     ));
// }

/// NOTE: it will REPLACE the default shader used by all SpriteSheetBundle/SpriteBundle/etc
/// This shader is allows to us to use alpha as a mask
/// - when the channel is set, it will draw the given color(set in Sprite init)
/// - when channel is 0.0, it will be full transparent[rbga 0,0,0,0]
/// -> ie we DO NOT want a background color; we want to "draw only the foreground"
///
/// ARCHIVE/ALTERNATIVE?
/// Based on https://github.com/bevyengine/bevy/blob/v0.7.0/examples/2d/sprite_manual.rs
/// but derive SpritePipeline instead of SpritePipeline
///
/// Allows to use a custom shader, with added uniform for colors
/// SpritePipeline only supports blending ONE color, but we want a behavior like
/// an ALPHA only texture(RED channel only in our case)
/// - if the channel is 1.0: draw the foreground color
/// - if the channel is 0.0: draw the background color
/// We do it this way b/c the "circuit outputs" are binary, so it simpler to have a
/// binary-like texture on the GPU side.
/// We could probably do it with a RGBA texture IFF the layout in memory is RRR...GGG...BBB...AAA
/// else it would means a sub-optimal buffer copy each frame instead of the direct
/// ~ memcopy("circuit outputs", "texture")
///
/// -> TODO? this fails b/c SpritePipeline* fields are private, which means we basically have to copy paste everything
/// in order to access them in "queue_colored_sprites"
//
// TODO can we find a way to override the shader only when needed
// see https://github.com/bevyengine/bevy/blob/main/crates/bevy_sprite/src/render/mod.rs for where SPRITE_SHADER_HANDLE is used
// cf colored_sprite_pipeline.rs
// NOTE: right now we use a DEFINE(let in wgsl) so both message and pinpad sprite WILL have the same text color...
pub(super) fn setup_transparent_shader_for_sprites(
    mut shaders: ResMut<Assets<Shader>>,
    // mut pipeline_cache: ResMut<bevy::render::render_resource::PipelineCache>,
    // mut pipelines: ResMut<
    //     bevy::render::render_resource::SpecializedRenderPipelines<bevy::sprite::SpritePipeline>,
    // >,
    // mut sprite_pipeline: ResMut<bevy::sprite::SpritePipeline>,
    // msaa: Res<Msaa>,
    // theme: Res<crate::Theme>,
) {
    // cf https://github.com/bevyengine/bevy/blob/v0.7.0/crates/bevy_sprite/src/lib.rs
    // can we modify render/sprite.wgsl to do "if background color, set alpha = 0.0, else draw color"
    // TODO is there a way to override the shader for a specific Sprite?

    // let text_color_rgba = theme.text_color.as_rgba_f32();
    // let define_str = format!(
    //     "let BACKGROUND_COLOR: vec4<f32> = vec4<f32>({:.5}, {:.5}, {:.5}, {:.5});",
    //     text_color_rgba[0], text_color_rgba[1], text_color_rgba[2], text_color_rgba[3]
    // );

    let shader_str = include_str!("../data/transparent_sprite.wgsl").to_string();

    let new_sprite_shader = Shader::from_wgsl(shader_str, file!());
    shaders.set_untracked(bevy::sprite::SPRITE_SHADER_HANDLE, new_sprite_shader);

    // TODO?
    // pipeline_cache
    //     .get_render_pipeline_descriptor(bevy::render::render_resource::CachedRenderPipelineId(0))
    //     .fragment
    //     .unwrap()
    //     .shader_defs
    //     .push("other".to_string());
    //
    // cf /.../bevy_sprite-0.7.0/src/render/mod.rs around "let colored_pipeline"
    // let key = bevy::sprite::SpritePipelineKey::from_msaa_samples(msaa.samples);
    // let pipeline = pipelines.specialize(&mut pipeline_cache, &sprite_pipeline, key);
    // let colored_pipeline = pipelines.specialize(
    //     &mut pipeline_cache,
    //     &sprite_pipeline,
    //     key | bevy::sprite::SpritePipelineKey::COLORED,
    // );
}

// Creates a colorful test pattern
// https://github.com/bevyengine/bevy/blob/main/examples/3d/shapes.rs
fn uv_debug_texture(width: u32, height: u32) -> Image {
    // : Vec<u8> = vec!
    // : &[u8; 32] = &
    let palette: Vec<u8> = vec![
        255, 102, 159, 255, 255, 159, 102, 255, 236, 255, 102, 255, 121, 255, 102, 255, 102, 255,
        198, 255, 102, 198, 255, 255, 121, 102, 255, 255, 236, 102, 255, 255,
    ];

    // let mut texture_data = vec![0; (width * height * TEXTURE_PIXEL_NB_BYTES).try_into().unwrap()];
    // for y in 0..height {
    //     let offset = width * y * TEXTURE_PIXEL_NB_BYTES;
    //     texture_data[offset..(offset + width * TEXTURE_PIXEL_NB_BYTES)].copy_from_slice(&palette);
    //     palette.rotate_right(4);
    // }
    //
    // 4 because RGBA(or ARGB)
    let target_size = (width * height * TEXTURE_PIXEL_NB_BYTES) as usize;
    let mut texture_data = Vec::with_capacity(target_size);
    while texture_data.len() < target_size {
        let start = texture_data.len();
        // end:
        // - try to append the whole "palette"(32 bytes)
        // - but DO NOT exceed target_size
        let end = std::cmp::min(target_size, start + palette.len());
        texture_data.extend(&palette[0..(end - start)]);
    }
    assert!(texture_data.len() == target_size);

    Image::new_fill(
        Extent3d {
            width,
            height,
            depth_or_array_layers: 1,
        },
        wgpu::TextureDimension::D2,
        &texture_data,
        // wgpu::TextureFormat::bevy_default(),
        wgpu::TextureFormat::R8Unorm,
    )
}
