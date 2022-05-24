extern crate sdl2;

use sdl2::event::Event;
use sdl2::keyboard::Keycode;
use sdl2::pixels::Color;
use sdl2::pixels::PixelFormatEnum;
use sdl2::render::Texture;
use sdl2::render::TextureCreator;
use sdl2::video::WindowContext;
use std::time::Duration;

fn init_texture(
    width: u32,
    height: u32,
    texture_creator: &TextureCreator<WindowContext>,
) -> Texture {
    sdl2::log::set_output_function(|priority, category, msg| {
        println!("[{:#?},{:#?}] {}", priority, category, msg);
    });
    unsafe {
        sdl2::sys::SDL_LogSetAllPriority(sdl2::sys::SDL_LogPriority::SDL_LOG_PRIORITY_ERROR);
    }

    // TODO? texture format?
    let texture = texture_creator
        .create_texture(
            // TODO format?
            // texture_creator.default_pixel_format(),
            // PixelFormatEnum::RGBA8888,
            PixelFormatEnum::RGB24,
            // PixelFormatEnum::RGB888,
            sdl2::render::TextureAccess::Streaming,
            width,
            height,
        )
        .unwrap();

    texture
}

// TODO refactor: update_data: param [u8]? and return [u8]? or inplace?
// DO NOT pass the texture, we WANT to separate the "render loop" and the "logic update"
pub fn hello_lib(
    update_data: fn(
        &mut [u8],
        texture_width: u32,
        texture_height: u32,
        texture_pitch: usize,
        frame_counter: usize,
    ),
) {
    let sdl_context = sdl2::init().unwrap();
    let video_subsystem = sdl_context.video().unwrap();

    let window = video_subsystem
        .window("rust-sdl2 demo", 800, 600)
        .position_centered()
        // TODO opengl?
        // .opengl()
        .build()
        .unwrap();

    let mut canvas = window.into_canvas().build().unwrap();

    let texture_creator = canvas.texture_creator();
    // TODO param width,height
    let width = 256;
    let height = 256;
    let mut texture = init_texture(width, height, &texture_creator);
    canvas.present();
    // TODO?
    // let mut texture_buf: Vec<u8> = Vec::with_capacity(width as usize * height as usize);
    // let mut texture_buf = vec![0; width as usize * height as usize];

    // canvas.set_draw_color(Color::RGB(0, 0, 0));
    // canvas.clear();
    // canvas.present();

    let mut event_pump = sdl_context.event_pump().unwrap();
    let mut i = 0; // TODO remove
    let mut frame_counter = 0 as usize;
    'running: loop {
        i = (i + 1) % 255;

        // TODO remove
        // canvas.set_draw_color(Color::RGB(i % 255, 64, 255 - i));
        // let texture_buf_size_before = texture_buf.len();
        let format = texture.query().format;
        let texture_width = texture.query().width;
        let texture_height = texture.query().height;
        texture
            .with_lock(
                sdl2::rect::Rect::new(0, 0, width, height),
                |buffer: &mut [u8], pitch: usize| {
                    update_data(buffer, texture_width, texture_height, pitch, frame_counter);
                },
            )
            .unwrap();

        canvas.clear();

        // TODO REMOVE, use as-is copy(cf just below)
        // canvas
        //     .copy(
        //         &texture,
        //         None,
        //         Some(sdl2::rect::Rect::new(100, 100, width, height)),
        //     )
        //     .unwrap();
        canvas.copy(&texture, None, None).unwrap();
        // canvas
        //     .copy(
        //         &texture,
        //         sdl2::rect::Rect::new(0, 0, width, height),
        //         sdl2::rect::Rect::new(100, 100, width, height),
        //     )
        //     .unwrap();
        // TODO flip and/or rotate? or rather do it in shader if needed?
        // canvas
        //     .copy_ex(
        //         &texture,
        //         None,
        //         Some(Rect::new(450, 100, 256, 256)),
        //         30.0,
        //         None,
        //         false,
        //         false,
        //     )
        //     .unwrap();

        for event in event_pump.poll_iter() {
            match event {
                Event::Quit { .. }
                | Event::KeyDown {
                    keycode: Some(Keycode::Escape),
                    ..
                } => break 'running,
                // Event::KeyDown {
                //     keycode: Some(Keycode::Space),
                //     ..
                // } => {
                //     canvas
                //         .copy(
                //             &texture,
                //             None,
                //             Some(sdl2::rect::Rect::new(100, 100, width, height)),
                //         )
                //         .unwrap();
                // }
                _ => {}
            }
        }
        // The rest of the game loop goes here...

        canvas.present();

        frame_counter += 1;
        ::std::thread::sleep(Duration::new(0, 1_000_000_000u32 / 60));
    }
}

#[cfg(test)]
mod tests {
    #[test]
    fn it_works() {
        let result = 2 + 2;
        assert_eq!(result, 4);
    }
}
