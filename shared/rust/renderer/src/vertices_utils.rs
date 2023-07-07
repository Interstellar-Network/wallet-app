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

// NOTE: for consistency we define our Rect(=bounding box) the same way than Android
// https://developer.android.com/reference/android/graphics/Rect#summary
// https://developer.android.com/ndk/reference/struct/a-rect
#[non_exhaustive] // make this struct NON-constructible(ie MUST use Rect::new)
#[derive(Default, Clone, Debug, PartialEq)]
pub struct Rect {
    pub bottom: f32,
    pub left: f32,
    pub right: f32,
    pub top: f32,
}

impl Rect {
    /// Create a new Rect
    /// NOTE: the order of the parameter matches Rect.fromLTRB from Android Compose
    /// @param: MUST be in NDC[-1.0,1.0]
    pub fn new(left: f32, top: f32, right: f32, bottom: f32) -> Self {
        if !(-1.0..=1.0).contains(&left) {
            panic!("left NOT in NDC[-1.0,1.0]: {left}")
        }
        if !(-1.0..=1.0).contains(&top) {
            panic!("top NOT in NDC[-1.0,1.0]: {top}")
        }
        if !(-1.0..=1.0).contains(&right) {
            panic!("right NOT in NDC[-1.0,1.0]: {right}")
        }
        if !(-1.0..=1.0).contains(&bottom) {
            panic!("bottom NOT in NDC[-1.0,1.0]: {bottom}")
        }
        if bottom > top {
            panic!("bottom > top: {bottom} > {top}")
        }
        if left > right {
            panic!("left > right: {left} > {right} ")
        }

        Rect {
            bottom,
            left,
            right,
            top,
        }
    }

    /// new: with conversion from screen space(x: [0;width], y: [0:height])
    /// NOTE: y axis is top = 0, bottom = height to match Android
    pub fn new_to_ndc_android(
        mut left: f32,
        mut top: f32,
        mut right: f32,
        mut bottom: f32,
        width: f32,
        height: f32,
    ) -> Self {
        // IMPORTANT: we HAVE to correct based on BOTH the aspect ration and
        // scaling mode
        //
        // eg: with a vertical window 1080*1920:
        // world_top: 1.0, world_width: 1.125, aspect_ratio: 0.5625, world_left: -0.5625
        //
        // convert to range [0,1.0]; but what we WANT is [-1,1]
        // also MUST invert y axis
        left /= width;
        top = (height - top) / height;
        right /= width;
        bottom = (height - bottom) / height;
        // then convert [0.0,1.0] -> [-1.0,1.0]
        left = left * 2.0 - 1.0;
        top = top * 2.0 - 1.0;
        right = right * 2.0 - 1.0;
        bottom = bottom * 2.0 - 1.0;
        // finally, adjust for aspect ratio and scaling mode
        // eg with ScalingMode::FixedVertical: the y axis is [-1,1]
        // but the horizontal axis is [-1 / (16/9), 1 / (16/9)] == [-0.5625, 0.5625]
        match crate::CAMERA_SCALING_MODE {
            bevy::render::camera::ScalingMode::AutoMin {
                min_width: _,
                min_height: _,
            } => todo!("ScalingMode::AutoMin not yet supported"),
            bevy::render::camera::ScalingMode::AutoMax {
                max_width: _,
                max_height: _,
            } => todo!("ScalingMode::AutoMax not yet supported"),
            bevy::render::camera::ScalingMode::WindowSize(_pixels_for_world_unit) => {
                todo!("ScalingMode::WindowSize not yet supported")
            }
            bevy::render::camera::ScalingMode::FixedVertical(_viewport_height_world) => {
                left *= width / height;
                right *= width / height;
            }
            bevy::render::camera::ScalingMode::FixedHorizontal(_viewport_width_world) => {
                top *= width / height;
                bottom *= width / height;
            }
            bevy::render::camera::ScalingMode::Fixed {
                width: _,
                height: _,
            } => {
                todo!("ScalingMode::Fixed not yet supported")
            }
        }

        Self::new(left, top, right, bottom)
    }

    pub fn center(&self) -> [f32; 2] {
        [
            self.left + self.width() / 2.0,
            self.bottom + self.height() / 2.0,
        ]
    }

    pub fn width(&self) -> f32 {
        self.right - self.left
    }

    pub fn height(&self) -> f32 {
        self.top - self.bottom
    }
}
