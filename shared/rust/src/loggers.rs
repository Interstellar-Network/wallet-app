// #[macro_use]
extern crate android_logger;

use android_logger::{Config, FilterBuilder};
use log::Level;

pub fn init_android_logger() {
    // https://github.com/Nercury/android_logger-rs#send-rust-logs-to-logcat
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Trace)
            .with_tag("interstellar")
            .with_filter(FilterBuilder::new().parse("debug,jni::crate=debug").build()),
    );
}
