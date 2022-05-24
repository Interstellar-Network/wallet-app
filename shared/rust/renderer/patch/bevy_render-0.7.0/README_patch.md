## PATCH

- Cargo.toml: replace all eg `bevy_app = { path = "../bevy_app, version = "0.7.0" }` by `bevy_app = { version = "0.7.0" }`
- renderer/patch/bevy_render-0.7.0/src/texture/mod.rs: `impl BevyDefault for wgpu::TextureFormat` cf renderer/src/lib.rs