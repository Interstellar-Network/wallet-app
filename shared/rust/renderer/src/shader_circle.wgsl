// https://www.shadertoy.com/view/XsjGDt

// Convert r, g, b to normalized vec3
fn rgb(r: f32, g:f32, b:f32) -> vec3<f32> {
	return vec3(r / 255.0, g / 255.0, b / 255.0);
}

// Draw a circle at vec2 `pos` with radius `rad` and
// color `color`.
fn circle(uv: vec2<f32>, pos: vec2<f32>, rad: f32, color: vec3<f32>) -> vec4<f32> {
	var d = length(pos - uv) - rad;
	var t = clamp(d, 0.0, 1.0);
	return vec4<f32>(color, 1.0 - t);
    // return vec4<f32>(color, 1.0);
}

// Vertex shader

struct VertexInput {
    @location(0) position: vec3<f32>,
};

struct VertexOutput {
    @builtin(position) clip_position: vec4<f32>,
};

@vertex
fn vs_main(
    model: VertexInput,
) -> VertexOutput {
    var out: VertexOutput;
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
    // TODO param
    var iResolution = vec2<f32>(800.0,600.0);

    // NOTE: that draws ONE big circle in the center of the screen
    // but this is NOT what we want
    // We want one circle per "input digit"
    //
    // var uv: vec2<f32>  = in.clip_position.xy;
	// var center: vec2<f32> = iResolution.xy * 0.5;
    // // TODO! 0.25
	// var radius: f32 = 0.5 * iResolution.y;

    var uv: vec2<f32>  = in.clip_position.xy;
	var center: vec2<f32> = in.clip_position.xy * 0.5;
    // TODO! 0.25
	var radius: f32 = 100.0;

    // TODO? TOREMOVE? Background layer
	var layer1: vec4<f32> = vec4(rgb(210.0, 222.0, 228.0), 1.0);

	// Circle
	var red: vec3<f32> = rgb(225.0, 95.0, 60.0);
	var layer2: vec4<f32> = circle(uv, center, radius, red);

	// Blend the two
	return mix(layer1, layer2, layer2.a);
}