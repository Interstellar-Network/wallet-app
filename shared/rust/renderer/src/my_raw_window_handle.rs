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

/// Copy paste of Bevy's bevy_window-0.7.0/src/raw_window_handle.rs
/// needed b/c:
/// - RawWindowHandle can not be used as-is for a custom plugin b/c not Send/Sync
/// - RawWindowHandleWrapper::new is only pub(crate) NOT pub...
use raw_window_handle::{HasRawWindowHandle, RawWindowHandle};

/// This wrapper exist to enable safely passing a [`RawWindowHandle`] across threads. Extracting the handle
/// is still an unsafe operation, so the caller must still validate that using the raw handle is safe for a given context.
#[derive(Debug, Clone)]
pub struct MyRawWindowHandleWrapper(RawWindowHandle);

impl MyRawWindowHandleWrapper {
    pub(crate) fn new(handle: RawWindowHandle) -> Self {
        Self(handle)
    }

    /// # Safety
    /// This returns a [`HasRawWindowHandle`] impl, which exposes [`RawWindowHandle`]. Some platforms
    /// have constraints on where/how this handle can be used. For example, some platforms don't support doing window
    /// operations off of the main thread. The caller must ensure the [`RawWindowHandle`] is only used in valid contexts.
    pub unsafe fn get_handle(&self) -> MyHasRawWindowHandleWrapper {
        MyHasRawWindowHandleWrapper(self.0)
    }
}

// SAFE: RawWindowHandle is just a normal "raw pointer", which doesn't impl Send/Sync. However the pointer is only
// exposed via an unsafe method that forces the user to make a call for a given platform. (ex: some platforms don't
// support doing window operations off of the main thread).
// A recommendation for this pattern (and more context) is available here:
// https://github.com/rust-windowing/raw-window-handle/issues/59
unsafe impl Send for MyRawWindowHandleWrapper {}
unsafe impl Sync for MyRawWindowHandleWrapper {}

pub struct MyHasRawWindowHandleWrapper(RawWindowHandle);

// SAFE: the caller has validated that this is a valid context to get RawWindowHandle
unsafe impl HasRawWindowHandle for MyHasRawWindowHandleWrapper {
    fn raw_window_handle(&self) -> RawWindowHandle {
        self.0
    }
}
