// https://github.com/sotrh/learn-wgpu/blob/master/code/beginner/tutorial5-textures/src/lib.rs

use std::iter;

use vertices_utils::get_indices_fullscreen;
use wgpu::util::DeviceExt;
use winit::event::WindowEvent;

#[cfg(target_arch = "wasm32")]
use wasm_bindgen::prelude::*;

#[cfg_attr(target_os = "android", path = "jni_wrapper.rs", allow(non_snake_case))]
mod jni_wrapper;

mod texture;

pub mod update_texture_placeholder;
pub mod vertex;
pub mod vertices_utils;

pub type UpdateTextureDataType = fn(frame_counter: usize) -> Vec<u8>;

pub struct State {
    surface: wgpu::Surface,
    device: wgpu::Device,
    queue: wgpu::Queue,
    config: wgpu::SurfaceConfiguration,
    pub size: winit::dpi::PhysicalSize<u32>,
    render_pipeline: wgpu::RenderPipeline,
    vertex_buffer: wgpu::Buffer,
    index_buffer: wgpu::Buffer,
    num_indices: u32,
    // NEW!
    #[allow(dead_code)]
    texture: texture::Texture,
    diffuse_bind_group: wgpu::BindGroup,
    frame_number: usize,
    update_texture_data: UpdateTextureDataType,
}

impl State {
    pub async fn new<W>(
        window: &W,
        size: winit::dpi::PhysicalSize<u32>,
        update_texture_data: UpdateTextureDataType,
        vertices: Vec<vertex::Vertex>,
        indices: Vec<u16>,
        texture_base: texture::TextureBase,
    ) -> Self
    where
        W: raw_window_handle::HasRawWindowHandle,
    {
        // The instance is a handle to our GPU
        // BackendBit::PRIMARY => Vulkan + Metal + DX12 + Browser WebGPU
        // FIX: allow backend to be configurable via env var
        let backend = wgpu::util::backend_bits_from_env()
            .unwrap_or(wgpu::Backends::from(wgpu::Backends::PRIMARY));
        // TODO on Android(at least AVD) we MUST use GL!
        // crash https://github.com/gfx-rs/wgpu/issues/2384
        // PRIMARY: OK on Linux/wsl2(with wslg), OK on Android Device, broken on Emulator(borked texture)
        // Gl: OK everywhere, but only "Best Effort Support"
        // let backend = wgpu::Backends::from(wgpu::Backends::PRIMARY);
        // let backend = wgpu::Backends::from(wgpu::Backends::GL); // OK?? at least does not crash AVD
        let instance = wgpu::Instance::new(backend);
        let surface = unsafe { instance.create_surface(window) };

        // let adapter = instance
        //     .request_adapter(&wgpu::RequestAdapterOptions {
        //         power_preference: wgpu::PowerPreference::default(),
        //         compatible_surface: Some(&surface),
        //         force_fallback_adapter: false,
        //     })
        //     .await
        //     .unwrap();
        let adapter =
            wgpu::util::initialize_adapter_from_env_or_default(&instance, backend, Some(&surface))
                .await
                .expect("No suitable GPU adapters found on the system!");

        let limits = if cfg!(target_arch = "wasm32") {
            wgpu::Limits::downlevel_webgl2_defaults()
        } else {
            // TODO? defaults?
            wgpu::Limits::downlevel_webgl2_defaults()
        };

        // FIX defaults() failed:
        // [2022-06-01T12:41:24Z ERROR wgpu::backend::direct] Error in Adapter::request_device: Limit 'max_compute_workgroups_per_dimension' value 65535 is better than allowed 0
        // TODO put those behind an env var
        // limits.max_compute_workgroups_per_dimension = 0;
        // limits.max_compute_workgroup_size_z = 0;
        // limits.max_compute_workgroup_size_y = 0;
        // limits.max_compute_workgroup_size_x = 0;
        // limits.max_compute_invocations_per_workgroup = 0;
        // limits.max_compute_workgroup_storage_size = 0;
        // limits.max_storage_buffer_binding_size = 0;
        // limits.max_storage_textures_per_shader_stage = 0;
        // limits.max_storage_buffers_per_shader_stage = 0;
        // limits.max_dynamic_storage_buffers_per_pipeline_layout = 0;

        let (device, queue) = adapter
            .request_device(
                &wgpu::DeviceDescriptor {
                    label: None,
                    features: wgpu::Features::empty(),
                    // WebGL doesn't support all of wgpu's features, so if
                    // we're building for the web we'll have to disable some.
                    limits: limits,
                },
                None, // Trace path
            )
            .await
            .unwrap();

        let config = wgpu::SurfaceConfiguration {
            usage: wgpu::TextureUsages::RENDER_ATTACHMENT,
            format: surface.get_preferred_format(&adapter).unwrap(),
            width: size.width,
            height: size.height,
            present_mode: wgpu::PresentMode::Fifo,
        };
        surface.configure(&device, &config);

        let texture = texture::Texture::new(&device, None, texture_base).unwrap();

        let texture_bind_group_layout =
            device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
                entries: &[
                    wgpu::BindGroupLayoutEntry {
                        binding: 0,
                        visibility: wgpu::ShaderStages::FRAGMENT,
                        ty: wgpu::BindingType::Texture {
                            multisampled: false,
                            view_dimension: wgpu::TextureViewDimension::D2,
                            sample_type: wgpu::TextureSampleType::Float { filterable: true },
                        },
                        count: None,
                    },
                    wgpu::BindGroupLayoutEntry {
                        binding: 1,
                        visibility: wgpu::ShaderStages::FRAGMENT,
                        ty: wgpu::BindingType::Sampler(wgpu::SamplerBindingType::Filtering),
                        count: None,
                    },
                ],
                label: Some("texture_bind_group_layout"),
            });

        let diffuse_bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
            layout: &texture_bind_group_layout,
            entries: &[
                wgpu::BindGroupEntry {
                    binding: 0,
                    resource: wgpu::BindingResource::TextureView(&texture.view),
                },
                wgpu::BindGroupEntry {
                    binding: 1,
                    resource: wgpu::BindingResource::Sampler(&texture.sampler),
                },
            ],
            label: Some("diffuse_bind_group"),
        });

        let shader = device.create_shader_module(&wgpu::ShaderModuleDescriptor {
            label: Some("Shader"),
            source: wgpu::ShaderSource::Wgsl(include_str!("shader.wgsl").into()),
        });

        let render_pipeline_layout =
            device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
                label: Some("Render Pipeline Layout"),
                bind_group_layouts: &[&texture_bind_group_layout],
                push_constant_ranges: &[],
            });

        let render_pipeline = device.create_render_pipeline(&wgpu::RenderPipelineDescriptor {
            label: Some("Render Pipeline"),
            layout: Some(&render_pipeline_layout),
            vertex: wgpu::VertexState {
                module: &shader,
                entry_point: "vs_main",
                buffers: &[vertex::Vertex::desc()],
            },
            fragment: Some(wgpu::FragmentState {
                module: &shader,
                entry_point: "fs_main",
                targets: &[wgpu::ColorTargetState {
                    format: config.format,
                    blend: Some(wgpu::BlendState {
                        color: wgpu::BlendComponent::REPLACE,
                        alpha: wgpu::BlendComponent::REPLACE,
                    }),
                    write_mask: wgpu::ColorWrites::ALL,
                }],
            }),
            primitive: wgpu::PrimitiveState {
                topology: wgpu::PrimitiveTopology::TriangleList,
                strip_index_format: None,
                front_face: wgpu::FrontFace::Ccw,
                cull_mode: Some(wgpu::Face::Back),
                // Setting this to anything other than Fill requires Features::POLYGON_MODE_LINE
                // or Features::POLYGON_MODE_POINT
                polygon_mode: wgpu::PolygonMode::Fill,
                // Requires Features::DEPTH_CLIP_CONTROL
                unclipped_depth: false,
                // Requires Features::CONSERVATIVE_RASTERIZATION
                conservative: false,
            },
            depth_stencil: None,
            multisample: wgpu::MultisampleState {
                count: 1,
                mask: !0,
                alpha_to_coverage_enabled: false,
            },
            // If the pipeline will be used with a multiview render pass, this
            // indicates how many array layers the attachments will have.
            multiview: None,
        });

        let vertex_buffer = device.create_buffer_init(&wgpu::util::BufferInitDescriptor {
            label: Some("Vertex Buffer"),
            contents: bytemuck::cast_slice(&vertices),
            usage: wgpu::BufferUsages::VERTEX,
        });
        let index_buffer = device.create_buffer_init(&wgpu::util::BufferInitDescriptor {
            label: Some("Index Buffer"),
            contents: bytemuck::cast_slice(&indices),
            usage: wgpu::BufferUsages::INDEX,
        });
        let num_indices = indices.len() as u32;
        let frame_number = 0 as usize;

        Self {
            surface,
            device,
            queue,
            config,
            size,
            render_pipeline,
            vertex_buffer,
            index_buffer,
            num_indices,
            texture,
            diffuse_bind_group,
            frame_number,
            update_texture_data,
        }
    }

    pub fn resize(&mut self, new_size: winit::dpi::PhysicalSize<u32>) {
        if new_size.width > 0 && new_size.height > 0 {
            self.size = new_size;
            self.config.width = new_size.width;
            self.config.height = new_size.height;
            self.surface.configure(&self.device, &self.config);
        }
    }

    #[allow(unused_variables)]
    pub fn input(&mut self, event: &WindowEvent) -> bool {
        false
    }

    pub fn update(&mut self) {
        let rgba = (self.update_texture_data)(self.frame_number);
        self.texture.update_data(&self.queue, &rgba);
    }

    pub fn render(&mut self) -> Result<(), wgpu::SurfaceError> {
        let output = self.surface.get_current_texture()?;
        let view = output
            .texture
            .create_view(&wgpu::TextureViewDescriptor::default());

        let mut encoder = self
            .device
            .create_command_encoder(&wgpu::CommandEncoderDescriptor {
                label: Some("Render Encoder"),
            });

        {
            let mut render_pass = encoder.begin_render_pass(&wgpu::RenderPassDescriptor {
                label: Some("Render Pass"),
                color_attachments: &[wgpu::RenderPassColorAttachment {
                    view: &view,
                    resolve_target: None,
                    ops: wgpu::Operations {
                        load: wgpu::LoadOp::Clear(wgpu::Color {
                            r: 0.1,
                            g: 0.2,
                            b: 0.3,
                            a: 1.0,
                        }),
                        store: true,
                    },
                }],
                depth_stencil_attachment: None,
            });

            render_pass.set_pipeline(&self.render_pipeline);
            render_pass.set_bind_group(0, &self.diffuse_bind_group, &[]);
            render_pass.set_vertex_buffer(0, self.vertex_buffer.slice(..));
            render_pass.set_index_buffer(self.index_buffer.slice(..), wgpu::IndexFormat::Uint16);
            render_pass.draw_indexed(0..self.num_indices, 0, 0..1);
        }

        self.queue.submit(iter::once(encoder.finish()));
        output.present();

        self.frame_number += 1;

        Ok(())
    }
}

// NOTE: we NEED the data_dimensions to generate the proper texcoords
// That is b/c we WANT texture with PoT dimensions(256,512,etc) but usually
// the texture data is image-like with dimensions like 224x96
pub fn prepare_texture_vertices_indices(
    is_message: bool,
    texture_data_dimensions: (u32, u32),
) -> (texture::TextureBase, Vec<vertex::Vertex>, Vec<u16>) {
    let texture_base = texture::TextureBase::new(texture_data_dimensions);

    let vertices = if is_message {
        vertices_utils::get_vertices_fullscreen_from_texture_pot(&texture_base)
    } else {
        let mut vertices = vec![];
        let rect = vertices_utils::Rect::new(0.70, -0.90, -0.70, 0.90);
        vertices_utils::get_vertices_pinpad_quad(rect, &texture_base, &mut vertices);

        vertices
    };

    let indices = if is_message {
        get_indices_fullscreen()
    } else {
        let indices_pinpad = vec![
            1, 0, 2, // top-left triangle: B->A->C
            1, 2, 3, // bottom-right triangle: B->C->D
            /* padding */ 0,
        ];
        indices_pinpad
    };

    (texture_base, vertices, indices)
}
