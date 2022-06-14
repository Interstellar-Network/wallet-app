// https://github.com/sotrh/learn-wgpu/blob/master/code/beginner/tutorial5-textures/src/texture.rs

use std::num::NonZeroU32;

use anyhow::*;

pub struct Texture {
    pub texture: wgpu::Texture,
    pub view: wgpu::TextureView,
    pub sampler: wgpu::Sampler,
    pub texture_size: wgpu::Extent3d, // SHOULD be a POT=Power-Of-Two
    pub data_size: wgpu::Extent3d,
}

impl Texture {
    pub fn new(
        device: &wgpu::Device,
        queue: &wgpu::Queue,
        label: Option<&str>,
        data_dimensions: (u32, u32),
    ) -> Result<Self> {
        // TODO compute "Next Power of Two" from data_dimensions
        let texture_dimensions = (1024, 1024);

        let texture_size = wgpu::Extent3d {
            width: texture_dimensions.0,
            height: texture_dimensions.1,
            depth_or_array_layers: 1,
        };
        let data_size = wgpu::Extent3d {
            width: data_dimensions.0,
            height: data_dimensions.1,
            depth_or_array_layers: 1,
        };
        let texture = device.create_texture(&wgpu::TextureDescriptor {
            label,
            size: texture_size,
            mip_level_count: 1,
            sample_count: 1,
            dimension: wgpu::TextureDimension::D2,
            format: wgpu::TextureFormat::Rgba8UnormSrgb,
            usage: wgpu::TextureUsages::TEXTURE_BINDING | wgpu::TextureUsages::COPY_DST,
        });

        let view = texture.create_view(&wgpu::TextureViewDescriptor::default());
        let sampler = device.create_sampler(&wgpu::SamplerDescriptor {
            address_mode_u: wgpu::AddressMode::ClampToEdge,
            address_mode_v: wgpu::AddressMode::ClampToEdge,
            address_mode_w: wgpu::AddressMode::ClampToEdge,
            mag_filter: wgpu::FilterMode::Linear,
            min_filter: wgpu::FilterMode::Nearest,
            mipmap_filter: wgpu::FilterMode::Nearest,
            ..Default::default()
        });

        Ok(Self {
            texture,
            view,
            sampler,
            texture_size,
            data_size,
        })
    }

    // pub fn from_bytes(
    //     device: &wgpu::Device,
    //     queue: &wgpu::Queue,
    //     bytes: &[u8],
    //     label: &str,
    // ) -> Result<Self> {
    //     let img = image::load_from_memory(bytes)?;
    //     Self::from_image(device, queue, &img, Some(label))
    // }

    // pub fn from_image(
    //     device: &wgpu::Device,
    //     queue: &wgpu::Queue,
    //     img: &image::DynamicImage,
    //     label: Option<&str>,
    // ) -> Result<Self> {
    //     let rgba = img.to_rgba8();
    //     let dimensions = img.dimensions();

    //     let size = wgpu::Extent3d {
    //         width: dimensions.0,
    //         height: dimensions.1,
    //         depth_or_array_layers: 1,
    //     };
    //     let texture = device.create_texture(&wgpu::TextureDescriptor {
    //         label,
    //         size,
    //         mip_level_count: 1,
    //         sample_count: 1,
    //         dimension: wgpu::TextureDimension::D2,
    //         format: wgpu::TextureFormat::Rgba8UnormSrgb,
    //         usage: wgpu::TextureUsages::TEXTURE_BINDING | wgpu::TextureUsages::COPY_DST,
    //     });

    //     queue.write_texture(
    //         wgpu::ImageCopyTexture {
    //             aspect: wgpu::TextureAspect::All,
    //             texture: &texture,
    //             mip_level: 0,
    //             origin: wgpu::Origin3d::ZERO,
    //         },
    //         &rgba,
    //         wgpu::ImageDataLayout {
    //             offset: 0,
    //             bytes_per_row: NonZeroU32::new(4 * dimensions.0),
    //             rows_per_image: NonZeroU32::new(dimensions.1),
    //         },
    //         size,
    //     );

    //     let view = texture.create_view(&wgpu::TextureViewDescriptor::default());
    //     let sampler = device.create_sampler(&wgpu::SamplerDescriptor {
    //         address_mode_u: wgpu::AddressMode::ClampToEdge,
    //         address_mode_v: wgpu::AddressMode::ClampToEdge,
    //         address_mode_w: wgpu::AddressMode::ClampToEdge,
    //         mag_filter: wgpu::FilterMode::Linear,
    //         min_filter: wgpu::FilterMode::Nearest,
    //         mipmap_filter: wgpu::FilterMode::Nearest,
    //         ..Default::default()
    //     });

    //     Ok(Self {
    //         texture,
    //         view,
    //         sampler,
    //         size,
    //     })
    // }

    pub fn update_data(&self, queue: &wgpu::Queue, data: &[u8]) {
        // TODO? better?
        // queue.write_buffer(buffer, offset, data);
        queue.write_texture(
            wgpu::ImageCopyTexture {
                aspect: wgpu::TextureAspect::All,
                texture: &self.texture,
                mip_level: 0,
                origin: wgpu::Origin3d::ZERO,
            },
            &data,
            wgpu::ImageDataLayout {
                offset: 0,
                bytes_per_row: NonZeroU32::new(4 * self.data_size.width),
                rows_per_image: NonZeroU32::new(self.data_size.height),
            },
            self.data_size,
        );
    }
}
