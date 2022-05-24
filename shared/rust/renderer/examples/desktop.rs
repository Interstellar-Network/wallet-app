// use sdl2::render::Texture;

extern crate renderer;

// ARCHIVE: SDL2 version
// TODO use "evaluate" instead of png
// fn update_data(
//     texture_buf: &mut [u8],
//     texture_width: u32,
//     texture_height: u32,
//     texture_pitch: usize,
//     frame_counter: usize,
// ) {
//     use std::fs::File;
//     // The decoder is a build for reader and can be used to set various decoding options
//     // via `Transformations`. The default output transformation is `Transformations::IDENTITY`.
//     let decoder = png::Decoder::new(
//         // TODO frame_counter % 4
//         File::open(format!(
//             "renderer/examples/data/output_eval_frame.png",
//             // TODO frame_counter % 4
//         ))
//         .unwrap(),
//     );
//     // TODO?
//     // decoder.set_transformations(png::Transformations::EXPAND);
//     let mut reader = decoder.read_info().unwrap();
//     // Allocate the output buffer.
//     let mut buf = vec![0; reader.output_buffer_size()];
//     // Read the next frame. An APNG might contain multiple frames.
//     let info = reader.next_frame(&mut buf).unwrap();
//     // Grab the bytes of the image.
//     let bytes = &buf[..info.buffer_size()];
//     // Inspect more details of the last read frame.
//     // let in_animation = reader.info().frame_control.is_some();

//     // color strips
//     // texture
//     //     .with_lock(None, |buffer: &mut [u8], pitch: usize| {
//     //         for y in 0..256 {
//     //             for x in 0..256 {
//     //                 let offset = y * pitch + x * 3;
//     //                 buffer[offset] = x as u8;
//     //                 buffer[offset + 1] = y as u8;
//     //                 buffer[offset + 2] = 0;
//     //             }
//     //         }
//     //     })
//     //     .unwrap();

//     // texture
//     // .with_lock(None, |buffer: &mut [u8], pitch: usize| {
//     // convert grayscale(1 byte per color) -> RGBA
//     // gray means R=G=B so we copy 3 channels, and set A to 255

//     // TODO
//     // let alpha = vec![255; texture_buf.len()];
//     // let alpha = vec![255; bytes.len()];

//     // FAIL: we want to "interlace", NOT all R, then all G, then all B, etc
//     // let rgba = [bytes, bytes, bytes, &alpha].concat();
//     // texture_buf.clone_from_slice(&rgba);

//     // let rgba = bytes.iter().zip(bytes.iter()).collect();
//     // buffer.clone_from_slice(&rgba);
//     // })
//     // .unwrap();

//     // missing parts of the png
//     // let mut px = 0;
//     // for i in 0..bytes.len() {
//     //     texture_buf[px] = bytes[i];
//     //     texture_buf[px + 1] = bytes[i];
//     //     texture_buf[px + 2] = bytes[i];
//     //     // alpha
//     //     texture_buf[px + 3] = 255;
//     //     px += 4;
//     // }

//     // for y in 0..info.height as usize {
//     //     for x in 0..info.width as usize {
//     //         // color strip
//     //         // let offset = y * texture_pitch + x * 3;
//     //         // texture_buf[offset] = x as u8;
//     //         // texture_buf[offset + 1] = y as u8;
//     //         // texture_buf[offset + 2] = 0;

//     //         // let offset = y * texture_pitch + x * 3;
//     //         // let px = y * info.width as usize + x;
//     //         // texture_buf[offset] = bytes[px];
//     //         // texture_buf[offset + 1] = bytes[px];
//     //         // texture_buf[offset + 2] = bytes[px];

//     //         // PixelFormatEnum::RGB24
//     //         // texture_pitch = 672
//     //         // let offset = y * texture_pitch + x * 3;
//     //         // texture_buf[offset] = 255;
//     //         // texture_buf[offset + 1] = 0;
//     //         // texture_buf[offset + 2] = 0;
//     //         // (almost) all red; bottom left corner black-ish[b/c of alpha = 0 probably]

//     //         // PixelFormatEnum::RGB24
//     //         // texture_pitch = 672
//     //         // let offset = y * texture_pitch + x * 3;
//     //         // texture_buf[offset] = 0;
//     //         // texture_buf[offset + 1] = 255;
//     //         // texture_buf[offset + 2] = 0;
//     //         // (almost) all green; bottom left corner black-ish[b/c of alpha = 0 probably]

//     //         // // PixelFormatEnum::RGB888
//     //         // let offset = y * texture_pitch + x * 4;
//     //         // texture_buf[offset] = 0;
//     //         // texture_buf[offset + 1] = 255;
//     //         // texture_buf[offset + 2] = 0;
//     //         // color strips

//     //         // PixelFormatEnum::RGB888
//     //         // texture_pitch = 896
//     //         // let offset = y * texture_pitch + x * 4;
//     //         // texture_buf[offset + 0] = 255;
//     //         // -> blue gradient
//     //         // texture_buf[offset + 1] = 255;
//     //         // -> green gradient
//     //         // texture_buf[offset + 2] = 255;
//     //         // -> red gradient

//     //         // PixelFormatEnum::RGB888
//     //         // texture_pitch = 896
//     //         let offset = y * texture_pitch + x * 4;
//     //         let px = y * info.line_size as usize + x * 4;
//     //         // texture_buf[offset + 0] = bytes[px + 0];
//     //         // texture_buf[offset + 1] = bytes[px + 1];
//     //         // texture_buf[offset + 2] = bytes[px + 2];
//     //         // -> wrong color order vs .png
//     //         texture_buf[offset + 0] = bytes[px + 2];
//     //         texture_buf[offset + 1] = bytes[px + 1];
//     //         texture_buf[offset + 2] = bytes[px + 0];
//     //         // OK?
//     //     }
//     // }

//     for y in 0..texture_height as usize {
//         for x in 0..texture_width as usize {
//             let offset = y * texture_pitch + x * 3;
//             texture_buf[offset] = x as u8;
//             texture_buf[offset + 1] = y as u8;
//             texture_buf[offset + 2] = 0;
//         }
//     }
// }

fn update_texture_data(frame_number: usize) -> Vec<u8> {
    let img = image::io::Reader::open(format!(
        "renderer/examples/data/output_eval_frame{}.png",
        frame_number % 5
    ))
    .unwrap()
    .decode()
    .unwrap();
    let rgba = img.to_rgba8();
    rgba.into_vec()
}

fn main() {
    // let thing = renderer::hello_lib(update_data);
    pollster::block_on(renderer::run(update_texture_data));
    // println!("I made a thing: {:?}", thing);
}
