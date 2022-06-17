// https://github.com/sotrh/learn-wgpu/blob/master/code/beginner/tutorial5-textures/src/lib.rs

use std::iter;

use cgmath::prelude::*;
use wgpu::util::DeviceExt;
use winit::event::*;

#[cfg(target_arch = "wasm32")]
use wasm_bindgen::prelude::*;

#[cfg_attr(target_os = "android", path = "jni_wrapper.rs", allow(non_snake_case))]
mod jni_wrapper;

mod camera;
mod instance;
mod light;
mod model;
mod resources;
mod texture;

pub mod update_texture_placeholder;
pub mod vertices_utils;

pub use crate::model::TextureVertex;

use crate::light::LightUniform;
use crate::model::DrawLight;
use crate::model::DrawModel;
use crate::model::ModelVertex;
use crate::model::Vertex;

pub type UpdateTextureDataType = fn(frame_counter: usize) -> Vec<u8>;

pub struct State {
    surface: wgpu::Surface,
    device: wgpu::Device,
    queue: wgpu::Queue,
    config: wgpu::SurfaceConfiguration,
    pub size: winit::dpi::PhysicalSize<u32>,
    render_pipeline: wgpu::RenderPipeline,
    obj_model: model::Model,
    vertex_buffer: wgpu::Buffer,
    index_buffer: wgpu::Buffer,
    num_indices: u32,
    // NEW!
    #[allow(dead_code)]
    // texture: texture::Texture,
    // texture_bind_group: wgpu::BindGroup,
    // texture_bg: texture::Texture,
    // texture_bg_bind_group: wgpu::BindGroup,
    frame_number: usize,
    last_render_time: instant::Instant,
    camera: camera::Camera,
    projection: camera::Projection,
    pub camera_controller: camera::CameraController,
    camera_uniform: camera::CameraUniform,
    camera_buffer: wgpu::Buffer,
    camera_bind_group: wgpu::BindGroup,
    pub mouse_pressed: bool,
    depth_texture: texture::Texture,
    light_uniform: LightUniform,
    light_buffer: wgpu::Buffer,
    light_bind_group: wgpu::BindGroup,
    light_render_pipeline: wgpu::RenderPipeline,
    update_texture_data: UpdateTextureDataType,
    instances_bg: Vec<instance::Instance>,
    instance_bg_buffer: wgpu::Buffer,
}

impl State {
    pub async fn new<W>(
        window: &W,
        size: winit::dpi::PhysicalSize<u32>,
        update_texture_data: UpdateTextureDataType,
        vertices: Vec<TextureVertex>,
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

        let format = surface.get_preferred_format(&adapter).unwrap();
        // TODO TOREMOVE
        // let format = wgpu::TextureFormat::Bgra8UnormSrgb;

        let config = wgpu::SurfaceConfiguration {
            usage: wgpu::TextureUsages::RENDER_ATTACHMENT,
            format: format,
            width: size.width,
            height: size.height,
            present_mode: wgpu::PresentMode::Fifo,
        };
        surface.configure(&device, &config);

        ////////////////////////////////////////////////////////////////////////

        // TODO
        // let texture = texture::Texture::new(&device, None, texture_base, false).unwrap();
        // // The circle .png
        // let circle_img = image::load_from_memory_with_format(
        //     include_bytes!("../data/Red_Circle_full.png"),
        //     image::ImageFormat::Png,
        // )
        // .unwrap();
        // let texture_bg =
        //     texture::Texture::from_image(&device, None, &queue, &circle_img, false).unwrap();

        ////////////////////////////////////////////////////////////////////////

        let texture_bind_group_layout =
            device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
                entries: &[
                    wgpu::BindGroupLayoutEntry {
                        binding: 0,
                        visibility: wgpu::ShaderStages::FRAGMENT,
                        ty: wgpu::BindingType::Texture {
                            multisampled: false,
                            sample_type: wgpu::TextureSampleType::Float { filterable: true },
                            view_dimension: wgpu::TextureViewDimension::D2,
                        },
                        count: None,
                    },
                    wgpu::BindGroupLayoutEntry {
                        binding: 1,
                        visibility: wgpu::ShaderStages::FRAGMENT,
                        ty: wgpu::BindingType::Sampler(wgpu::SamplerBindingType::Filtering),
                        count: None,
                    },
                    // normal map
                    wgpu::BindGroupLayoutEntry {
                        binding: 2,
                        visibility: wgpu::ShaderStages::FRAGMENT,
                        ty: wgpu::BindingType::Texture {
                            multisampled: false,
                            sample_type: wgpu::TextureSampleType::Float { filterable: true },
                            view_dimension: wgpu::TextureViewDimension::D2,
                        },
                        count: None,
                    },
                    wgpu::BindGroupLayoutEntry {
                        binding: 3,
                        visibility: wgpu::ShaderStages::FRAGMENT,
                        ty: wgpu::BindingType::Sampler(wgpu::SamplerBindingType::Filtering),
                        count: None,
                    },
                ],
                label: Some("texture_bind_group_layout"),
            });

        // let texture_bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
        //     layout: &texture_bind_group_layout,
        //     entries: &[
        //         wgpu::BindGroupEntry {
        //             binding: 0,
        //             resource: wgpu::BindingResource::TextureView(&texture.view),
        //         },
        //         wgpu::BindGroupEntry {
        //             binding: 1,
        //             resource: wgpu::BindingResource::Sampler(&texture.sampler),
        //         },
        //     ],
        //     label: Some("texture_bind_group"),
        // });

        // Re-use the same layout for both texture and texture_bg
        // let texture_bg_bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
        //     layout: &texture_bind_group_layout,
        //     entries: &[
        //         wgpu::BindGroupEntry {
        //             binding: 0,
        //             resource: wgpu::BindingResource::TextureView(&texture_bg.view),
        //         },
        //         wgpu::BindGroupEntry {
        //             binding: 1,
        //             resource: wgpu::BindingResource::Sampler(&texture_bg.sampler),
        //         },
        //     ],
        //     label: Some("texture_bg_bind_group"),
        // });

        ////////////////////////////////////////////////////////////////////////

        let camera = camera::Camera::new((0.0, 5.0, 10.0), cgmath::Deg(-90.0), cgmath::Deg(-20.0));
        let projection =
            camera::Projection::new(config.width, config.height, cgmath::Deg(45.0), 0.1, 100.0);
        let camera_controller = camera::CameraController::new(4.0, 0.4);

        let mut camera_uniform = camera::CameraUniform::new();
        camera_uniform.update_view_proj(&camera, &projection);

        let camera_buffer = device.create_buffer_init(&wgpu::util::BufferInitDescriptor {
            label: Some("Camera Buffer"),
            contents: bytemuck::cast_slice(&[camera_uniform]),
            usage: wgpu::BufferUsages::UNIFORM | wgpu::BufferUsages::COPY_DST,
        });

        let camera_bind_group_layout =
            device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
                entries: &[wgpu::BindGroupLayoutEntry {
                    binding: 0,
                    visibility: wgpu::ShaderStages::VERTEX | wgpu::ShaderStages::FRAGMENT,
                    ty: wgpu::BindingType::Buffer {
                        ty: wgpu::BufferBindingType::Uniform,
                        has_dynamic_offset: false,
                        min_binding_size: None,
                    },
                    count: None,
                }],
                label: Some("camera_bind_group_layout"),
            });

        let camera_bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
            layout: &camera_bind_group_layout,
            entries: &[wgpu::BindGroupEntry {
                binding: 0,
                resource: camera_buffer.as_entire_binding(),
            }],
            label: Some("camera_bind_group"),
        });

        ////////////////////////////////////////////////////////////////////////

        let obj_model =
            resources::load_model("cube.obj", &device, &queue, &texture_bind_group_layout)
                .await
                .unwrap();

        let light_uniform = LightUniform {
            position: [2.0, 2.0, 2.0],
            _padding: 0,
            color: [1.0, 1.0, 1.0],
            _padding2: 0,
        };

        let light_buffer = device.create_buffer_init(&wgpu::util::BufferInitDescriptor {
            label: Some("Light VB"),
            contents: bytemuck::cast_slice(&[light_uniform]),
            usage: wgpu::BufferUsages::UNIFORM | wgpu::BufferUsages::COPY_DST,
        });

        let light_bind_group_layout =
            device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
                entries: &[wgpu::BindGroupLayoutEntry {
                    binding: 0,
                    visibility: wgpu::ShaderStages::VERTEX | wgpu::ShaderStages::FRAGMENT,
                    ty: wgpu::BindingType::Buffer {
                        ty: wgpu::BufferBindingType::Uniform,
                        has_dynamic_offset: false,
                        min_binding_size: None,
                    },
                    count: None,
                }],
                label: None,
            });

        let light_bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
            layout: &light_bind_group_layout,
            entries: &[wgpu::BindGroupEntry {
                binding: 0,
                resource: light_buffer.as_entire_binding(),
            }],
            label: None,
        });

        let depth_texture =
            texture::Texture::create_depth_texture(&device, &config, "depth_texture");

        ////////////////////////////////////////////////////////////////////////

        let render_pipeline_layout =
            device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
                label: Some("Render Pipeline Layout"),
                bind_group_layouts: &[
                    &texture_bind_group_layout,
                    &camera_bind_group_layout,
                    &light_bind_group_layout,
                ],
                push_constant_ranges: &[],
            });

        let render_pipeline = {
            let shader = wgpu::ShaderModuleDescriptor {
                label: Some("Normal Shader"),
                //TODO? shader_texture?
                source: wgpu::ShaderSource::Wgsl(include_str!("shader.wgsl").into()),
            };

            create_render_pipeline(
                &device,
                &render_pipeline_layout,
                config.format,
                Some(texture::Texture::DEPTH_FORMAT),
                &[ModelVertex::desc(), instance::InstanceRaw::desc()],
                shader,
            )
        };

        let light_render_pipeline = {
            let layout = device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
                label: Some("Light Pipeline Layout"),
                bind_group_layouts: &[&camera_bind_group_layout, &light_bind_group_layout],
                push_constant_ranges: &[],
            });
            let shader = wgpu::ShaderModuleDescriptor {
                label: Some("Light Shader"),
                source: wgpu::ShaderSource::Wgsl(include_str!("light.wgsl").into()),
            };
            create_render_pipeline(
                &device,
                &layout,
                config.format,
                Some(texture::Texture::DEPTH_FORMAT),
                &[model::ModelVertex::desc()],
                shader,
            )
        };

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

        ////////////////////////////////////////////////////////////////////////
        /// TODO move to instance_utils
        const SPACE_BETWEEN: f32 = 3.0;
        const NUM_INSTANCES_PER_ROW: u32 = 30;
        let instances_bg = (0..NUM_INSTANCES_PER_ROW)
            .flat_map(|z| {
                (0..NUM_INSTANCES_PER_ROW).map(move |x| {
                    let x = SPACE_BETWEEN * (x as f32 - NUM_INSTANCES_PER_ROW as f32 / 2.0);
                    let z = SPACE_BETWEEN * (z as f32 - NUM_INSTANCES_PER_ROW as f32 / 2.0);

                    let position = cgmath::Vector3 { x, y: 0.0, z };

                    let rotation = if position.is_zero() {
                        cgmath::Quaternion::from_axis_angle(
                            cgmath::Vector3::unit_z(),
                            cgmath::Deg(0.0),
                        )
                    } else {
                        cgmath::Quaternion::from_axis_angle(position.normalize(), cgmath::Deg(45.0))
                    };

                    instance::Instance { position, rotation }
                })
            })
            .collect::<Vec<_>>();

        let instance_bg_data = instances_bg
            .iter()
            .map(instance::Instance::to_raw)
            .collect::<Vec<_>>();
        let instance_bg_buffer = device.create_buffer_init(&wgpu::util::BufferInitDescriptor {
            label: Some("Instance Buffer"),
            contents: bytemuck::cast_slice(&instance_bg_data),
            usage: wgpu::BufferUsages::VERTEX,
        });

        ////////////////////////////////////////////////////////////////////////

        Self {
            surface,
            device,
            queue,
            config,
            size,
            render_pipeline,
            obj_model,
            vertex_buffer,
            index_buffer,
            num_indices,
            // texture_bind_group,
            // texture_bg,
            // texture_bg_bind_group,
            frame_number,
            last_render_time: instant::Instant::now(),
            camera,
            projection,
            camera_controller,
            camera_buffer,
            camera_bind_group,
            camera_uniform,
            mouse_pressed: false,
            depth_texture,
            light_uniform,
            light_buffer,
            light_bind_group,
            light_render_pipeline,
            update_texture_data,
            instances_bg,
            instance_bg_buffer,
        }
    }

    pub fn resize(&mut self, new_size: winit::dpi::PhysicalSize<u32>) {
        if new_size.width > 0 && new_size.height > 0 {
            self.projection.resize(new_size.width, new_size.height);
            self.size = new_size;
            self.config.width = new_size.width;
            self.config.height = new_size.height;
            self.surface.configure(&self.device, &self.config);
            self.depth_texture =
                texture::Texture::create_depth_texture(&self.device, &self.config, "depth_texture");
        }
    }

    #[allow(unused_variables)]
    pub fn input(&mut self, event: &WindowEvent) -> bool {
        match event {
            WindowEvent::KeyboardInput {
                input:
                    KeyboardInput {
                        virtual_keycode: Some(key),
                        state,
                        ..
                    },
                ..
            } => self.camera_controller.process_keyboard(*key, *state),
            WindowEvent::MouseWheel { delta, .. } => {
                self.camera_controller.process_scroll(delta);
                true
            }
            WindowEvent::MouseInput {
                button: MouseButton::Left,
                state,
                ..
            } => {
                self.mouse_pressed = *state == ElementState::Pressed;
                true
            }
            _ => false,
        }
    }

    pub fn update(&mut self) {
        let now = instant::Instant::now();
        let dt = now - self.last_render_time;
        self.last_render_time = now;

        self.camera_controller.update_camera(&mut self.camera, dt);
        self.camera_uniform
            .update_view_proj(&self.camera, &self.projection);
        self.queue.write_buffer(
            &self.camera_buffer,
            0,
            bytemuck::cast_slice(&[self.camera_uniform]),
        );

        // Update the light
        let old_position: cgmath::Vector3<_> = self.light_uniform.position.into();
        self.light_uniform.position =
            (cgmath::Quaternion::from_axis_angle((0.0, 1.0, 0.0).into(), cgmath::Deg(1.0))
                * old_position)
                .into();
        self.queue.write_buffer(
            &self.light_buffer,
            0,
            bytemuck::cast_slice(&[self.light_uniform]),
        );

        let rgba = (self.update_texture_data)(self.frame_number);
        // self.texture.update_data(&self.queue, &rgba);
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

        // {
        //     let mut render_pass = encoder.begin_render_pass(&wgpu::RenderPassDescriptor {
        //         label: Some("Render Pass BACKGROUND"),
        //         color_attachments: &[wgpu::RenderPassColorAttachment {
        //             view: &view,
        //             resolve_target: None,
        //             ops: wgpu::Operations {
        //                 load: wgpu::LoadOp::Clear(wgpu::Color {
        //                     r: 0.1,
        //                     g: 0.2,
        //                     b: 0.3,
        //                     // MUST make it transparent b/c are drawing ABOVE the button etc
        //                     // that way the buttons are shown unless explicitly matching a Vertex
        //                     a: 0.5,
        //                 }),
        //                 store: true,
        //             },
        //         }],
        //         depth_stencil_attachment: None,
        //     });

        //     render_pass.set_pipeline(&self.render_pipeline);
        //     render_pass.set_bind_group(0, &self.texture_bg_bind_group, &[]);
        //     render_pass.set_bind_group(1, &self.camera_bind_group, &[]);
        //     render_pass.set_vertex_buffer(0, self.vertex_buffer.slice(..));
        //     render_pass.set_vertex_buffer(1, self.instance_bg_buffer.slice(..));
        //     render_pass.set_index_buffer(self.index_buffer.slice(..), wgpu::IndexFormat::Uint16);
        //     render_pass.draw_indexed(0..self.num_indices, 0, 0..self.instances_bg.len() as _);
        // }

        // {
        //     let mut render_pass = encoder.begin_render_pass(&wgpu::RenderPassDescriptor {
        //         label: Some("Render Pass Texture"),
        //         color_attachments: &[wgpu::RenderPassColorAttachment {
        //             view: &view,
        //             resolve_target: None,
        //             ops: wgpu::Operations {
        //                 load: wgpu::LoadOp::Clear(wgpu::Color {
        //                     r: 0.1,
        //                     g: 0.2,
        //                     b: 0.3,
        //                     // MUST make it transparent b/c are drawing ABOVE the button etc
        //                     // that way the buttons are shown unless explicitly matching a Vertex
        //                     a: 0.5,
        //                 }),
        //                 store: true,
        //             },
        //         }],
        //         depth_stencil_attachment: None,
        //     });

        //     render_pass.set_pipeline(&self.render_pipeline);
        //     render_pass.set_bind_group(0, &self.texture_bind_group, &[]);
        //     render_pass.set_bind_group(1, &self.camera_bind_group, &[]);
        //     render_pass.set_vertex_buffer(0, self.vertex_buffer.slice(..));
        //     // TODO add instance_buffer (ie NO _bg = background)
        //     render_pass.set_vertex_buffer(1, self.instance_bg_buffer.slice(..));
        //     render_pass.set_index_buffer(self.index_buffer.slice(..), wgpu::IndexFormat::Uint16);
        //     render_pass.draw_indexed(0..self.num_indices, 0, 0..1);
        // }

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
                depth_stencil_attachment: Some(wgpu::RenderPassDepthStencilAttachment {
                    view: &self.depth_texture.view,
                    depth_ops: Some(wgpu::Operations {
                        load: wgpu::LoadOp::Clear(1.0),
                        store: true,
                    }),
                    stencil_ops: None,
                }),
            });

            render_pass.set_vertex_buffer(1, self.instance_bg_buffer.slice(..));
            render_pass.set_pipeline(&self.light_render_pipeline);
            render_pass.draw_light_model(
                &self.obj_model,
                &self.camera_bind_group,
                &self.light_bind_group,
            );

            render_pass.set_pipeline(&self.render_pipeline);
            render_pass.draw_model_instanced(
                &self.obj_model,
                0..self.instances_bg.len() as u32,
                &self.camera_bind_group,
                &self.light_bind_group,
            );
        }

        self.queue.submit(iter::once(encoder.finish()));
        output.present();

        self.frame_number += 1;

        Ok(())
    }
}

fn create_render_pipeline(
    device: &wgpu::Device,
    layout: &wgpu::PipelineLayout,
    color_format: wgpu::TextureFormat,
    depth_format: Option<wgpu::TextureFormat>,
    vertex_layouts: &[wgpu::VertexBufferLayout],
    shader: wgpu::ShaderModuleDescriptor,
) -> wgpu::RenderPipeline {
    let shader = device.create_shader_module(&shader);

    device.create_render_pipeline(&wgpu::RenderPipelineDescriptor {
        label: Some(&format!("{:?}", shader)),
        layout: Some(layout),
        vertex: wgpu::VertexState {
            module: &shader,
            entry_point: "vs_main",
            buffers: vertex_layouts,
        },
        fragment: Some(wgpu::FragmentState {
            module: &shader,
            entry_point: "fs_main",
            targets: &[wgpu::ColorTargetState {
                format: color_format,
                blend: Some(wgpu::BlendState {
                    alpha: wgpu::BlendComponent::REPLACE,
                    color: wgpu::BlendComponent::REPLACE,
                }),
                write_mask: wgpu::ColorWrites::ALL,
            }],
        }),
        primitive: wgpu::PrimitiveState {
            topology: wgpu::PrimitiveTopology::TriangleList,
            strip_index_format: None,
            front_face: wgpu::FrontFace::Ccw,
            cull_mode: Some(wgpu::Face::Back),
            // Setting this to anything other than Fill requires Features::NON_FILL_POLYGON_MODE
            polygon_mode: wgpu::PolygonMode::Fill,
            // Requires Features::DEPTH_CLIP_CONTROL
            unclipped_depth: false,
            // Requires Features::CONSERVATIVE_RASTERIZATION
            conservative: false,
        },
        depth_stencil: depth_format.map(|format| wgpu::DepthStencilState {
            format,
            depth_write_enabled: true,
            depth_compare: wgpu::CompareFunction::Less,
            stencil: wgpu::StencilState::default(),
            bias: wgpu::DepthBiasState::default(),
        }),
        multisample: wgpu::MultisampleState {
            count: 1,
            mask: !0,
            alpha_to_coverage_enabled: false,
        },
        // If the pipeline will be used with a multiview render pass, this
        // indicates how many array layers the attachments will have.
        multiview: None,
    })
}

// NOTE: we NEED the data_dimensions to generate the proper texcoords
// That is b/c we WANT texture with PoT dimensions(256,512,etc) but usually
// the texture data is image-like with dimensions like 224x96
pub fn prepare_texture_vertices_indices(
    is_message: bool,
    texture_data_dimensions: (u32, u32),
) -> (texture::TextureBase, Vec<TextureVertex>, Vec<u16>) {
    let texture_base = texture::TextureBase::new(texture_data_dimensions);

    let vertices = if is_message {
        vertices_utils::get_vertices_fullscreen_from_texture_pot(&texture_base)
    } else {
        let mut vertices = vec![];
        // let rect = vertices_utils::Rect::new(0.70, -0.90, -0.70, 0.90);
        // vertices_utils::get_vertices_pinpad_quad(0, rect, &texture_base, &mut vertices);

        // let rect = vertices_utils::Rect::new(0.70, -0.60, -0.40, 0.90);
        // vertices_utils::get_vertices_pinpad_quad(1, rect, &texture_base, &mut vertices);

        // let rect = vertices_utils::Rect::new(0.70, -0.30, -0.10, 0.90);
        // vertices_utils::get_vertices_pinpad_quad(2, rect, &texture_base, &mut vertices);

        let rect = vertices_utils::Rect::new(-1.0, -1.0, 1.0, 1.0);
        vertices_utils::get_vertices_pinpad_quad(0, rect, &texture_base, &mut vertices);

        vertices
    };

    let indices = if is_message {
        vertices_utils::get_indices_fullscreen()
    } else {
        vertices_utils::get_indices_pinpad(&vertices)
    };

    (texture_base, vertices, indices)
}
