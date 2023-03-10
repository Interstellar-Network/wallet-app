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

///
/// [Android]
/// REALLY simplified version of Bevy's WinitPlugin
/// which basically does nothing except passing a RawHandle usually obtained from JNI with ANativeWindow_fromSurface
/// ie it DOES NOT setup events, etc b/c when using JNI we use NO event loop b/c everything is controlled from Java(SurfaceView)
use bevy::app::App;
use bevy::app::Plugin;
use bevy::ecs::world::World;
use bevy::utils::default;
use bevy::window::RawHandleWrapper;
use bevy::window::WindowMode;
use bevy::window::WindowResolution;
use bevy::winit::Window;
use bevy::winit::WinitWindows;

pub struct WinitPluginRawWindowHandle {
    physical_width: u32,
    physical_height: u32,
    scale_factor: f64,
    handle_wrapper: RawHandleWrapper,
}

/// cf "fn handle_create_window_events"
/// SHOULD be around https://github.com/bevyengine/bevy/blob/289fd1d0f2353353f565989a2296ed1b442e00bc/crates/bevy_winit/src/lib.rs#L55 at hte end of this file
/// when updating to a new bevy version.
/// We can discard pretty much the whole file.
///
/// Essentially the reference contains a "#[cfg(not(any(target_os = "android", target_os = "ios", target_os = "macos")))]"
/// for calling `handle_create_window_events` but we remove it, and simplify it a bit.
impl Plugin for WinitPluginRawWindowHandle {
    // Android mode: use RawWindowHandle on existing Window(ie ANativeWindow_fromSurface)
    // cf WinitPlugin
    // Essentially removed the Runner/Event Loop
    fn build(&self, app: &mut App) {
        app.init_non_send_resource::<WinitWindows>()
            // .init_resource::<WinitSettings>()
            // .set_runner(winit_runner)
            // // exit_on_all_closed only uses the query to determine if the query is empty,
            // // and so doesn't care about ordering relative to changed_window
            // .add_systems(
            //     (
            //         changed_window.ambiguous_with(exit_on_all_closed),
            //         // Update the state of the window before attempting to despawn to ensure consistent event ordering
            //         despawn_window.after(changed_window),
            //     )
            //         .in_base_set(CoreSet::Last),
            // )
            ;

        // app.add_plugin(AccessibilityPlugin);

        // Note that we create a window here "early" because WASM/WebGL requires the window to exist prior to initializing
        // the renderer.
        // And for ios and macos, we should not create window early, all ui related code should be executed inside
        // UIApplicationMain/NSApplicationMain.
        //[interstllar] #[cfg(not(any(target_os = "android", target_os = "ios", target_os = "macos")))]
        // handle_create_window_events(&mut app.world, &event_loop, &mut create_window_reader.0);
        handle_create_window_events(
            &mut app.world, /* &event_loop, &mut create_window_reader.0 */
            self.physical_width,
            self.physical_height,
            self.scale_factor,
            self.handle_wrapper.clone(),
        );
    }
}

#[cfg(target_os = "android")]
impl WinitPluginRawWindowHandle {
    pub fn new(
        physical_width: u32,
        physical_height: u32,
        scale_factor: f64,
        handle_wrapper: RawHandleWrapper,
    ) -> Self {
        Self {
            physical_width,
            physical_height,
            scale_factor,
            handle_wrapper,
        }
    }
}

fn handle_create_window_events(
    world: &mut World,
    // [interstellar] remove arguments
    // event_loop: &EventLoopWindowTarget<()>,
    // create_window_event_reader: &mut ManualEventReader<CreateWindow>,
    physical_width: u32,
    physical_height: u32,
    scale_factor: f64,
    handle_wrapper: RawHandleWrapper,
) {
    let world = world.cell();
    // let mut winit_windows = world.non_send_resource_mut::<WinitWindows>();
    let mut windows = world.non_send_resource_mut::<WinitWindows>();
    // let create_window_events = world.resource::<Events<CreateWindow>>();
    // for create_window_event in create_window_event_reader.iter(&create_window_events) {
    // let window = winit_windows.create_window(
    //     event_loop,
    //     create_window_event.id,
    //     &create_window_event.descriptor,
    // );
    let window = Window {
        mode: WindowMode::Fullscreen,
        resolution: WindowResolution {
            physical_width,
            physical_height,
            scale_factor,
            ..default()
        },
        resizable: false,
        ..default()
    };
    // Some(self.raw_window_handle.get_handle().raw_window_handle()),
    // Some(handle_wrapper),

    // (*windows).create_window(window);
    (*windows).windows.entry(0.into()).insert(window).into_mut();
}
