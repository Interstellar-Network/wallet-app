// Copyright 2022 Nathan Prat

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

pub fn update_texture_data_message(data: &mut Vec<u8>, circuit: &mut crate::EvaluateWrapperType) {
    log::debug!("update_texture_data_message BEGIN");
    circuit.as_mut().unwrap().EvaluateWithPackmsg(data);
    log::debug!("update_texture_data_message END");
}

pub fn update_texture_data_pinpad(data: &mut Vec<u8>, circuit: &mut crate::EvaluateWrapperType) {
    log::debug!("update_texture_data_pinpad BEGIN");
    circuit.as_mut().unwrap().EvaluateWithPackmsg(data);
    log::debug!("update_texture_data_pinpad END");
}

// TODO into_luma8? ie yes update "TEXTURE_PIXEL_NB_BYTES" and "Image::new_fill"

/**
 * PLACEHOLDER: to be replaced by "circuit evaluation"
 * For now just use a .png instead
 */
pub fn update_texture_data_message_placeholder(
    data: &mut Vec<u8>,
    circuit: &mut crate::EvaluateWrapperType,
) {
    let img = image::load_from_memory_with_format(
        include_bytes!("../examples/data/output_eval_frame0.png"),
        image::ImageFormat::Png,
    )
    .unwrap();
    let rgba = img.into_luma8();
    *data = rgba.into_vec();
}

/**
 * PLACEHOLDER: to be replaced by "circuit evaluation"
 * For now just use a .png instead
 */
pub fn update_texture_data_pinpad_placeholder(
    data: &mut Vec<u8>,
    circuit: &mut crate::EvaluateWrapperType,
) {
    let img = image::load_from_memory_with_format(
        include_bytes!("../examples/data/output_pinpad.png"),
        image::ImageFormat::Png,
    )
    .unwrap();
    let rgba = img.into_luma8();
    *data = rgba.into_vec();
}
