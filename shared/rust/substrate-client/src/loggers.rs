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
extern crate android_logger;

#[cfg(target_os = "android")]
use android_logger::{Config, FilterBuilder};
#[cfg(target_os = "android")]
use log::Level;

#[cfg(target_os = "android")]
pub fn init_logger() {
    // WARNING: conflicts with renderer/src/jni_wrapper.rs
    // only the first one called is taken into account
    // https://github.com/Nercury/android_logger-rs#send-rust-logs-to-logcat
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Info)
            .with_tag("interstellar")
            .with_filter(FilterBuilder::new().parse("info,jni::crate=debug").build()),
    );
}

#[cfg(not(target_os = "android"))]
pub fn init_logger() {
    env_logger::init();
}
