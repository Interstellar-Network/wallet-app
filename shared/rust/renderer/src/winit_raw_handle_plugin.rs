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

#[cfg(target_os = "android")]
/// [Android]
/// REALLY simplified version of Bevy's WinitPlugin
/// which basically does nothing except passing a RawHandle usually obtained from JNI with ANativeWindow_fromSurface
/// ie it DOES NOT setup events, etc b/c when using JNI we use NO event loop b/c everything is controlled from Java(SurfaceView)
///
use bevy::window::WindowId;
use bevy::{prelude::*, window::WindowMode};
use raw_window_handle::HasRawWindowHandle;

use crate::my_raw_window_handle::MyRawWindowHandleWrapper;

pub struct WinitPluginRawWindowHandle {
    physical_width: u32,
    physical_height: u32,
    scale_factor: f64,
    raw_window_handle: MyRawWindowHandleWrapper,
}

impl Plugin for WinitPluginRawWindowHandle {
    // Android mode: use RawWindowHandle on existing Window(ie ANativeWindow_fromSurface)
    // cf WinitPlugin
    // Essentially removed the Runner/Event Loop
    fn build(&self, app: &mut App) {
        // TODO(android)? #[cfg(feature = "bevy/bevy_winit")]
        // what is this supposed to be doing?
        // app.init_non_send_resource::<bevy::winit::WinitWindows>();

        // REFERENCE cf WinitPlugin::build
        // .init_resource::<WinitSettings>()
        // .set_runner(winit_runner)
        // .add_system_to_stage(CoreStage::PostUpdate, change_window.exclusive_system());
        // let event_loop = EventLoop::new();
        // handle_initial_window_events(&mut app.world, &event_loop);
        // app.insert_non_send_resource(event_loop);

        let world = app.world.cell();
        let mut windows = world.get_resource_mut::<Windows>().unwrap();
        unsafe {
            windows.add(Window::new(
                WindowId::primary(),
                &WindowDescriptor {
                    resizable: false,
                    mode: WindowMode::Fullscreen,
                    ..default()
                },
                self.physical_width,
                self.physical_height,
                self.scale_factor,
                None,
                self.raw_window_handle.get_handle().raw_window_handle(),
            ))
        }
    }
}

impl WinitPluginRawWindowHandle {
    pub fn new(
        physical_width: u32,
        physical_height: u32,
        scale_factor: f64,
        raw_window_handle: MyRawWindowHandleWrapper,
    ) -> Self {
        Self {
            physical_width: physical_width,
            physical_height: physical_height,
            scale_factor: scale_factor,
            raw_window_handle: raw_window_handle,
        }
    }
}
