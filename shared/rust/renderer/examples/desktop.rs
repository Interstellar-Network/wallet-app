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

    if args.is_message {
        pollster::block_on(renderer::run(update_texture_data_message, args.is_message));
    } else {
        pollster::block_on(renderer::run(update_texture_data_pinpad, args.is_message));
    }
}
