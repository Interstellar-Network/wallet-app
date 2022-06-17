use clap::Parser;
use winit::{
    event::*,
    event_loop::{self, ControlFlow, EventLoop},
    window::Window,
    window::WindowBuilder,
};

extern crate renderer;

/**
 * Only used for desktop
 * Android will directly call State::new etc on the native window via JNI
 * NOTE: Android WILL NOT use an Event loop; everything goes through the View callbacks instead
 */
#[cfg_attr(target_arch = "wasm32", wasm_bindgen(start))]
pub async fn run(
    mut state: renderer::State,
    event_loop: event_loop::EventLoop<()>,
    window: Window,
) {
    cfg_if::cfg_if! {
        if #[cfg(target_arch = "wasm32")] {
            std::panic::set_hook(Box::new(console_error_panic_hook::hook));
            console_log::init_with_level(log::Level::Warn).expect("Could't initialize logger");
        } else {
            env_logger::init();
        }
    }

    #[cfg(target_arch = "wasm32")]
    {
        // Winit prevents sizing with CSS, so we have to set
        // the size manually when on web.
        use winit::dpi::PhysicalSize;
        window.set_inner_size(PhysicalSize::new(450, 400));

        use winit::platform::web::WindowExtWebSys;
        web_sys::window()
            .and_then(|win| win.document())
            .and_then(|doc| {
                let dst = doc.get_element_by_id("wasm-example")?;
                let canvas = web_sys::Element::from(window.canvas());
                dst.append_child(&canvas).ok()?;
                Some(())
            })
            .expect("Couldn't append canvas to document body.");
    }

    event_loop.run(move |event, _, control_flow| {
        *control_flow = ControlFlow::Poll;
        // TODO wrap all into "state.process_event", and remove pub for fields
        match event {
            Event::DeviceEvent {
                event: DeviceEvent::MouseMotion{ delta, },
                .. // We're not using device_id currently
            } => if state.mouse_pressed {
                state.camera_controller.process_mouse(delta.0, delta.1)
            },
            Event::WindowEvent {
                ref event,
                window_id,
            } if window_id == window.id() && !state.input(event) => {
                match event {
                    #[cfg(not(target_arch="wasm32"))]
                    WindowEvent::CloseRequested
                    | WindowEvent::KeyboardInput {
                        input:
                            KeyboardInput {
                                state: ElementState::Pressed,
                                virtual_keycode: Some(VirtualKeyCode::Escape),
                                ..
                            },
                        ..
                    } => *control_flow = ControlFlow::Exit,
                    WindowEvent::Resized(physical_size) => {
                        state.resize(*physical_size);
                    }
                    WindowEvent::ScaleFactorChanged { new_inner_size, .. } => {
                        state.resize(**new_inner_size);
                    }
                    _ => {}
                }
            }
            Event::RedrawRequested(window_id) if window_id == window.id() => {
                state.update();
                match state.render() {
                    Ok(_) => {}
                    // Reconfigure the surface if it's lost or outdated
                    Err(wgpu::SurfaceError::Lost | wgpu::SurfaceError::Outdated) => {
                        state.resize(state.size)
                    }
                    // The system is out of memory, we should probably quit
                    Err(wgpu::SurfaceError::OutOfMemory) => *control_flow = ControlFlow::Exit,
                    // We're ignoring timeouts
                    Err(wgpu::SurfaceError::Timeout) => log::warn!("Surface timeout"),
                }
            }
            Event::MainEventsCleared => {
                // RedrawRequested will only trigger once, unless we manually
                // request it.
                window.request_redraw();
            }
            _ => {}
        }
    });
}

/// Simple program to greet a person
#[derive(Parser, Debug)]
#[clap(author, version, about, long_about = None)]
struct Args {
    /// Message or Pinpad?
    #[clap(short, long)]
    is_message: bool,
}

fn main() {
    let args = Args::parse();

    // TODO this is a PLACEHOLDER
    // In prod we should open the protobuf PGC, and get width/height from it
    let texture_data_dimensions = if args.is_message {
        (224, 96)
    } else {
        (590, 50)
    };

    let (texture_base, vertices, indices) =
        renderer::prepare_texture_vertices_indices(args.is_message, texture_data_dimensions);

    let event_loop = EventLoop::new();
    let window = WindowBuilder::new()
        .with_transparent(true)
        .build(&event_loop)
        .unwrap();

    // State::new uses async code, so we're going to wait for it to finish
    let size = window.inner_size();
    let state = pollster::block_on(renderer::State::new(
        &window,
        size,
        if args.is_message {
            renderer::update_texture_placeholder::update_texture_data_message
        } else {
            renderer::update_texture_placeholder::update_texture_data_pinpad
        },
        vertices,
        indices,
        texture_base,
    ));

    pollster::block_on(run(state, event_loop, window));
}
