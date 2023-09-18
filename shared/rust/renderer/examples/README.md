## desktop

Basic implementation of the "renderer" lib useful to dev the lib.

### run

- CHECK if opengl is OK: use eg `glxgears`
- `cargo run --example desktop --features=with-jni,offline_demo,bevy/x11`

#### DEPRECATED(sdl2) FIX: "Couldn't find matching render driver"

https://github.com/Rust-SDL2/rust-sdl2#bundled-feature

`sudo apt-get install libsdl2-dev`