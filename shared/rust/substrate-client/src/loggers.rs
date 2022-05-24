#[cfg(target_os = "android")]
extern crate android_logger;

#[cfg(target_os = "android")]
use android_logger::{Config, FilterBuilder};
#[cfg(target_os = "android")]
use log::Level;

#[cfg(target_os = "android")]
pub fn init_logger() {
    // https://github.com/Nercury/android_logger-rs#send-rust-logs-to-logcat
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Trace)
            .with_tag("interstellar")
            .with_filter(FilterBuilder::new().parse("debug,jni::crate=debug").build()),
    );
}

#[cfg(not(target_os = "android"))]
pub fn init_logger() {
    env_logger::init();
}
