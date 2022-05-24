// copy-pasted from https://github.com/bevyengine/bevy/blob/v0.7.0/crates/bevy_sprite/src/lib.rs
// CHANGE: one line added at the end, just before "return color;"

// TODO? will be prepended in "setup_transparent_shader_for_sprites"
// let BACKGROUND_COLOR: vec4<f32> = vec4<f32>({}, {}, {}, {});
let BACKGROUND_COLOR: vec4<f32> = vec4<f32>(0.0, 0.0, 0.0, 0.0);

struct View {
    view_proj: mat4x4<f32>;
    world_position: vec3<f32>;
};
[[group(0), binding(0)]]
var<uniform> view: View;

struct VertexOutput {
    [[location(0)]] uv: vec2<f32>;
#ifdef COLORED
    [[location(1)]] color: vec4<f32>;
#endif
    [[builtin(position)]] position: vec4<f32>;
};

[[stage(vertex)]]
fn vertex(
    [[location(0)]] vertex_position: vec3<f32>,
    [[location(1)]] vertex_uv: vec2<f32>,
#ifdef COLORED
    [[location(2)]] vertex_color: vec4<f32>,
#endif
) -> VertexOutput {
    var out: VertexOutput;
    out.uv = vertex_uv;
    out.position = view.view_proj * vec4<f32>(vertex_position, 1.0);
#ifdef COLORED
    out.color = vertex_color;
#endif
    return out;
}

[[group(1), binding(0)]]
var sprite_texture: texture_2d<f32>;
[[group(1), binding(1)]]
var sprite_sampler: sampler;

[[stage(fragment)]]
fn fragment(in: VertexOutput) -> [[location(0)]] vec4<f32> {
    var color = textureSample(sprite_texture, sprite_sampler, in.uv);
// #ifdef COLORED
    // color = in.color * color;
// #endif
    // if RED is set, we use the texture's color
    // else we make it transparent
    // NOTE: RED channel b/c we use wgpu::TextureFormat::R8Unorm, but adjust if necessary
    color = mix(BACKGROUND_COLOR, in.color, color.r);
    return color;
}