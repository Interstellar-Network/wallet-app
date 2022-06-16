
// Convert r, g, b to normalized vec3
fn rgb(r: f32, g:f32, b:f32) -> vec3<f32> {
	return vec3(r / 255.0, g / 255.0, b / 255.0);
}

// Draw a circle at vec2 `pos` with radius `rad` and
// color `color`.
fn circle(uv: vec2<f32>, pos: vec2<f32>, rad: f32, color: vec3<f32>) -> vec4<f32> {
	var d = length(pos - uv) - rad;
	var t = clamp(d, 0.0, 1.0);
	// return vec4<f32>(color, 1.0 - t);
    return vec4<f32>(color, 1.0);
}

// Vertex shader

struct VertexInput {
    @location(0) position: vec3<f32>,
    @location(1) tex_coords: vec2<f32>,
};

struct VertexOutput {
    @builtin(position) clip_position: vec4<f32>,
    @location(0) tex_coords: vec2<f32>,
};

@vertex
fn vs_main(
    model: VertexInput,
) -> VertexOutput {
    var out: VertexOutput;
    out.tex_coords = model.tex_coords;
    out.clip_position = vec4<f32>(model.position, 1.0);
    return out;
}

// Fragment shader

@group(0) @binding(0)
var t_diffuse: texture_2d<f32>;
@group(0) @binding(1)
var s_diffuse: sampler;

@fragment
fn fs_main(in: VertexOutput) -> @location(0) vec4<f32> {
    return textureSample(t_diffuse, s_diffuse, in.tex_coords);
}