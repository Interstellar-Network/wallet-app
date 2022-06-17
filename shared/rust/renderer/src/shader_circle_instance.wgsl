// https://github.com/sotrh/learn-wgpu/blob/master/code/beginner/tutorial7-instancing/src/shader.wgsl

// Vertex shader

struct Camera {
    view_proj: mat4x4<f32>,
};

@group(1) @binding(0)
var<uniform> camera: Camera;

struct VertexInput {
    @location(0) position: vec3<f32>,
    @location(1) tex_coords: vec2<f32>,
};
struct InstanceInput {
    @location(5) model_matrix_0: vec4<f32>,
    @location(6) model_matrix_1: vec4<f32>,
    @location(7) model_matrix_2: vec4<f32>,
    @location(8) model_matrix_3: vec4<f32>,
};

struct VertexOutput {
    @builtin(position) clip_position: vec4<f32>,
    @location(0) tex_coords: vec2<f32>,
};

@vertex
fn vs_main(
    model: VertexInput,
    instance: InstanceInput,
) -> VertexOutput {
    let model_matrix = mat4x4<f32>(
        instance.model_matrix_0,
        instance.model_matrix_1,
        instance.model_matrix_2,
        instance.model_matrix_3,
    );
    var out: VertexOutput;
    out.tex_coords = model.tex_coords;
    out.clip_position = camera.view_proj * model_matrix * vec4<f32>(model.position, 1.0);
    return out;
}

// Fragment shader

@group(0) @binding(0)
var t_diffuse: texture_2d<f32>;
@group(0) @binding(1)
var s_diffuse: sampler;

// @fragment
// fn fs_main(in: VertexOutput) -> @location(0) vec4<f32> {
//     return textureSample(t_diffuse, s_diffuse, in.tex_coords);
// }

// Convert r, g, b to normalized vec3
fn rgb(r: f32, g:f32, b:f32) -> vec3<f32> {
	return vec3(r / 255.0, g / 255.0, b / 255.0);
}

// https://thebookofshaders.com/07/
fn circle2(st: vec2<f32>, radius: f32, center: vec2<f32>) -> f32 {
    // var dist: vec2<f32> = st-vec2(0.5);
    var dist: vec2<f32> = st-center;
	return 1.-smoothstep(radius-(radius*0.01),
                         radius+(radius*0.01),
                         dot(dist,dist)*4.0);
}

// @fragment
// fn fs_main(in: VertexOutput) -> @location(0) vec4<f32> {
//     // TODO param
//     var u_resolution = vec2<f32>(800.0, 600.0);

//     // "Normalized pixel coordinates (from 0 to 1)"
//     var st: vec2<f32> = in.clip_position.xy / u_resolution.xy;
//     st = st - 0.5;
//     st.x = st.x * (u_resolution.x / u_resolution.y);

//     var center: vec2<f32> = vec2(0.5);

// 	return vec4(rgb(225.0, 95.0, 60.0), circle2(st,0.25,center));
// }

@fragment
fn fs_main(in: VertexOutput) -> @location(0) vec4<f32> {
    return textureSample(t_diffuse, s_diffuse, in.tex_coords);
}