use snafu::prelude::*;

pub use circuits_storage_common::DisplayStrippedCircuitsPackage;

/// Struct that match DisplayStrippedCircuitsPackage, but replacing IPFS cid by their content.
///
/// It is used to pass around data b/w substrate-client/src/jni_wrapper.rs GetCircuits and
/// substrate-client/src/jni_wrapper.rs initApp
pub struct DisplayStrippedCircuitsPackageBuffers {
    pub message_pgarbled_buf: Vec<u8>,
    pub message_packmsg_buf: Vec<u8>,
    pub pinpad_pgarbled_buf: Vec<u8>,
    pub pinpad_packmsg_buf: Vec<u8>,
    /// we copy DisplayStrippedCircuitsPackage b/c:
    /// - for UI/UX purposes we need to expose "message_nb_digits"
    /// - the "check_input" extrinsic uses "message_pgarbled_cid" as as ID to know which TX to validate
    ///   for the current account(ie we CAN have multiple pending tx for an account)
    pub package: DisplayStrippedCircuitsPackage,
}

#[derive(Debug, Snafu)]
pub enum InterstellarErrors {
    #[snafu(display("error at get-circuits-package"))]
    GetCircuitsPackage {},
    #[snafu(display("error at garble-and-strip-display-circuits-package-signed"))]
    GarbleAndStrip {},
    #[snafu(display("error at tx-check-input"))]
    TxCheckInput {},
}

#[cfg(test)]
mod tests {}
