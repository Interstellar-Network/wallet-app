use clap::Parser;

extern crate renderer;

/// Simple program to greet a person
#[derive(Parser, Debug)]
#[clap(author, version, about, long_about = None)]
struct Args {
    /// Message or Pinpad?
    #[clap(short, long)]
    is_message: bool,
}

fn update_texture_data_message(frame_number: usize) -> Vec<u8> {
    let png_path = format!(
        "renderer/examples/data/output_eval_frame{}.png",
        frame_number % 5
    );
    let img = image::io::Reader::open(png_path).unwrap().decode().unwrap();
    let rgba = img.to_rgba8();
    rgba.into_vec()
}

fn update_texture_data_pinpad(frame_number: usize) -> Vec<u8> {
    let png_path = format!("renderer/examples/data/output_pinpad.png");
    let img = image::io::Reader::open(png_path).unwrap().decode().unwrap();
    let rgba = img.to_rgba8();
    rgba.into_vec()
}

fn main() {
    let args = Args::parse();

    let vertices = if args.is_message {
        None
    } else {
        // NOTE: for consistency we define our Rect(=bounding box) the same way than Android
        // https://developer.android.com/reference/android/graphics/Rect#summary
        // https://developer.android.com/ndk/reference/struct/a-rect
        let bottom = 0.70;
        let left = -0.90;
        let right = -0.70;
        let top = 0.90;

        // TODO
        // let texture_height_ratio = texture.data_size.height as f32 / texture.texture_size.height as f32;
        let texture_height_ratio = 59 as f32 / 1024 as f32;
        // let texture_width_ratio = texture.data_size.width as f32 / texture.texture_size.width as f32;
        let texture_width_ratio = 590 as f32 / 1024 as f32;

        // screen:
        // A B
        // C D
        let vertices_pinpad = vec![
            renderer::Vertex {
                position: [left, top, 0.0],
                tex_coords: [0.0, 0.0],
            }, // A
            renderer::Vertex {
                position: [right, top, 0.0],
                tex_coords: [0.1 * texture_width_ratio, 0.0],
            }, // B
            renderer::Vertex {
                position: [left, bottom, 0.0],
                tex_coords: [0.0, texture_height_ratio],
            }, // C
            renderer::Vertex {
                position: [right, bottom, 0.0],
                tex_coords: [0.1 * texture_width_ratio, texture_height_ratio],
            }, // D
        ];
        Some(vertices_pinpad)
    };

    let indices = if args.is_message {
        None
    } else {
        // "full screen" eg for the Message
        // Texture mapped as-is to the screen
        let indices_pinpad = vec![
            1, 0, 2, // top-left triangle: B->A->C
            1, 2, 3, // bottom-right triangle: B->C->D
            /* padding */ 0,
        ];
        Some(indices_pinpad)
    };

    if args.is_message {
        pollster::block_on(renderer::run(
            update_texture_data_message,
            vertices,
            indices,
            // TODO get from png
            (224, 96),
        ));
    } else {
        pollster::block_on(renderer::run(
            update_texture_data_pinpad,
            vertices,
            indices,
            // TODO get from png
            (590, 50),
        ));
    }
}
