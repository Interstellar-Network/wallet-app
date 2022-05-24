// TODO into_luma8? ie yes update "TEXTURE_PIXEL_NB_BYTES" and "Image::new_fill"

/**
 * PLACEHOLDER: to be replaced by "circuit evaluation"
 * For now just use a .png instead
 */
pub fn update_texture_data_message() -> Vec<u8> {
    let img = image::load_from_memory_with_format(
        include_bytes!("../examples/data/output_eval_frame0.png"),
        image::ImageFormat::Png,
    )
    .unwrap();
    let rgba = img.into_luma8();
    rgba.into_vec()
}

/**
 * PLACEHOLDER: to be replaced by "circuit evaluation"
 * For now just use a .png instead
 */
pub fn update_texture_data_pinpad() -> Vec<u8> {
    let img = image::load_from_memory_with_format(
        include_bytes!("../examples/data/output_pinpad.png"),
        image::ImageFormat::Png,
    )
    .unwrap();
    let rgba = img.into_luma8();
    rgba.into_vec()
}
