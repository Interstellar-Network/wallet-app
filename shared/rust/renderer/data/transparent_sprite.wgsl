// adapted from https://github.com/bevyengine/bevy/blob/v0.10.0/crates/bevy_sprite/src/render/sprite.wgsl
// CHANGE: one line added at the end, just before "return color;"

// TODO? will be prepended in "setup_transparent_shader_for_sprites"
// let BACKGROUND_COLOR: vec4<f32> = vec4<f32>({}, {}, {}, {});
const BACKGROUND_COLOR: vec4<f32> = vec4<f32>(0.0, 0.0, 0.0, 0.0);

#ifdef TONEMAP_IN_SHADER
#import bevy_core_pipeline::tonemapping
#endif

#import bevy_render::view

@group(0) @binding(0)
var<uniform> view: View;

struct VertexOutput {
    @location(0) uv: vec2<f32>,
#ifdef COLORED
    @location(1) color: vec4<f32>,
#endif
    @builtin(position) position: vec4<f32>,
};

@vertex
fn vertex(
    @location(0) vertex_position: vec3<f32>,
    @location(1) vertex_uv: vec2<f32>,
#ifdef COLORED
    @location(2) vertex_color: vec4<f32>,
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

@group(1) @binding(0)
var sprite_texture: texture_2d<f32>;
@group(1) @binding(1)
var sprite_sampler: sampler;

@fragment
fn fragment(in: VertexOutput) -> @location(0) vec4<f32> {
    var color = textureSample(sprite_texture, sprite_sampler, in.uv);

    // At this point "color" would be either red if foreground(because we are using 1-channel R texture), or background
    // But we want "foreground color" instead of red so we convert it
    // ALSO " * 255.0" to convert the current boolean values = (0,1) from the circuit outputs into
    // "texture values" = (0,255)
    color = mix(BACKGROUND_COLOR, vec4<f32>(1.0, 1.0, 1.0, 1.0), color.r) * 255.0;

#ifdef COLORED
    color = in.color * color;
#endif

#ifdef TONEMAP_IN_SHADER
    color = tone_mapping(color);
#endif

    return color;
}