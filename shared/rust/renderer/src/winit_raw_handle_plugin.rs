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

use bevy::a11y::AccessibilityRequested;
///
/// [Android]
/// REALLY simplified version of Bevy's WinitPlugin
/// which basically does nothing except passing a RawHandle usually obtained from JNI with ANativeWindow_fromSurface
/// ie it DOES NOT setup events, etc b/c when using JNI we use NO event loop b/c everything is controlled from Java(SurfaceView)
use bevy::app::App;
use bevy::app::Plugin;
use bevy::ecs::component::Component;
use bevy::ecs::entity::Entity;
use bevy::ecs::event::EventWriter;
use bevy::ecs::query::Added;
use bevy::ecs::system::Commands;
use bevy::ecs::system::NonSendMut;
use bevy::ecs::system::Query;
use bevy::ecs::system::ResMut;
use bevy::ecs::system::SystemState;
use bevy::ecs::world::Mut;
use bevy::prelude::FromWorld;
use bevy::window::RawHandleWrapper;
use bevy::window::Window;
use bevy::window::WindowCreated;
use bevy::winit::WinitSettings;
use bevy::winit::WinitWindows;
use log::info;

pub struct WinitPluginRawWindowHandle {
    pub(super) scale_factor: f64,
    pub(super) handle_wrapper: RawHandleWrapper,
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
            .init_resource::<WinitSettings>();

        #[allow(clippy::type_complexity)]
        #[cfg(not(target_arch = "wasm32"))]
        let mut create_window_system_state: SystemState<(
            Commands,
            Query<(Entity, &mut Window), Added<Window>>,
            EventWriter<WindowCreated>,
            NonSendMut<WinitWindows>,
            ResMut<AccessibilityRequested>,
        )> = SystemState::from_world(&mut app.world);

        // https://github.com/bevyengine/bevy/issues/8658
        // https://github.com/bevyengine/bevy/blob/b88ff154f24314539fc7f03572215bd0283d98ee/crates/bevy_winit/src/lib.rs#L339
        // if !app.ready() {
        //     #[cfg(not(target_arch = "wasm32"))]
        //     bevy::tasks::tick_global_task_pools_on_main_thread();
        // } else {
        //     app.finish();
        //     app.cleanup();
        // }
        info!("app ready? {}", app.ready());
        bevy::tasks::tick_global_task_pools_on_main_thread();
        app.finish();
        app.cleanup();

        #[cfg(not(target_arch = "wasm32"))]
        let (
            commands,
            mut new_windows,
            created_window_writer,
            winit_windows,
            _accessibility_requested,
        ) = create_window_system_state.get_mut(&mut app.world);

        // Responsible for creating new windows
        create_window(
            self.handle_wrapper.clone(),
            self.scale_factor,
            commands,
            new_windows.iter_mut(),
            created_window_writer,
            winit_windows,
            #[cfg(target_arch = "wasm32")]
            canvas_parent_resize_channel,
        );

        create_window_system_state.apply(&mut app.world);
    }
}

/// NOTE: `create_window` not pub, so cf https://github.com/bevyengine/bevy/blob/v0.10.1/crates/bevy_winit/src/system.rs#L35
#[allow(clippy::too_many_arguments)]
fn create_window<'a>(
    handle_wrapper: RawHandleWrapper,
    scale_factor: f64,
    mut commands: Commands,
    created_windows: impl Iterator<Item = (Entity, Mut<'a, Window>)>,
    mut event_writer: EventWriter<WindowCreated>,
    winit_windows: NonSendMut<WinitWindows>,
    #[cfg(target_arch = "wasm32")] event_channel: ResMut<CanvasParentResizeEventChannel>,
) {
    for (entity, mut window) in created_windows {
        if winit_windows.get_window(entity).is_some() {
            continue;
        }

        log::info!(
            "Creating new window {:?} ({:?})",
            window.title.as_str(),
            entity
        );

        // window
        //     .resolution
        //     .set_scale_factor(winit_window.scale_factor());
        window.resolution.set_scale_factor(scale_factor);

        // commands
        //     .entity(entity)
        //     .insert(RawHandleWrapper {
        //         window_handle: winit_window.raw_window_handle(),
        //         display_handle: winit_window.raw_display_handle(),
        //     })
        //     .insert(CachedWindow {
        //         window: window.clone(),
        //     });
        commands
            .entity(entity)
            .insert(RawHandleWrapper {
                window_handle: handle_wrapper.get_window_handle(),
                display_handle: handle_wrapper.get_display_handle(),
            })
            .insert(CachedWindow {
                window: window.clone(),
            });

        #[cfg(target_arch = "wasm32")]
        {
            if window.fit_canvas_to_parent {
                let selector = if let Some(selector) = &window.canvas {
                    selector
                } else {
                    WINIT_CANVAS_SELECTOR
                };
                event_channel.listen_to_selector(entity, selector);
            }
        }

        event_writer.send(WindowCreated { window: entity });
    }
}

/// The cached state of the window so we can check which properties were changed from within the app.
#[derive(Debug, Clone, Component)]
pub struct CachedWindow {
    pub window: Window,
}

// cf impl WinitWindows {pub fn create_window
// FAIL, cf `EventLoopBuilder::new().build` below
// fn winit_windows_create_window<'a>(
//     winit_windows: &'a mut WinitWindows,
//     // event_loop: &winit::event_loop::EventLoopWindowTarget<()>,
//     entity: Entity,
//     window: &'a Window,
//     // adapters: &'a mut AccessKitAdapters,
//     // handlers: &'a mut WinitActionHandlers,
//     accessibility_requested: &'a mut AccessibilityRequested,
// ) -> &'a winit::window::Window {
//     let mut winit_window_builder = winit::window::WindowBuilder::new();

//     // FAIL: "thread '<unnamed>' panicked at 'An `AndroidApp` as passed to android_main() is required to create an `EventLoop` on Android', /home/pratn/.cargo/registry/src/github.com-1ecc6299db9ec823/winit-0.28.6/src/platform_impl/android/mod.rs:331:59
//     // 2023-06-27 12:23:02.454  7364-7364  wrap.sh                 logwrapper                           I  "
//     let event_loop = winit::event_loop::EventLoopBuilder::new().build();

//     let winit_window = winit::window::Window::new(&event_loop).unwrap();

//     winit_windows
//         .windows
//         .entry(winit_window.id())
//         .insert(winit_window)
//         .into_mut()
// }
