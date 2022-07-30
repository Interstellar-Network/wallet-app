use codec::{Decode, Encode};
use core::time::Duration;
use frame_support::pallet_prelude::*;

/// MUST match pallets/ocw-garble/src/lib.rs "DisplayStrippedCircuitsPackage"(in repo ocw-demo)
///
/// Contrary to "DisplayStrippedCircuitsPackageBuffers" it is NOT used by multiple crates,
/// but we put it in common b/c down the line it SHOULD be put in a separate repo which will be used
/// by this wallet-app and substrate-offchain-worker-demo.
#[derive(Encode, Decode, Debug, Clone)]
pub struct DisplayStrippedCircuitsPackage {
    pub message_pgarbled_cid: BoundedVec<u8, ConstU32<64>>,
    pub message_packmsg_cid: BoundedVec<u8, ConstU32<64>>,
    pub pinpad_pgarbled_cid: BoundedVec<u8, ConstU32<64>>,
    pub pinpad_packmsg_cid: BoundedVec<u8, ConstU32<64>>,
    pub message_nb_digits: u32,
}
/// MUST also match?
/// Probably at least "BoundedVec"; not sure about MAX_NUMBER_PENDING_CIRCUITS_PER_ACCOUNT
const MAX_NUMBER_PENDING_CIRCUITS_PER_ACCOUNT: u32 = 16;
pub type PendingCircuitsType =
    BoundedVec<DisplayStrippedCircuitsPackage, ConstU32<MAX_NUMBER_PENDING_CIRCUITS_PER_ACCOUNT>>;

/// Struct that match DisplayStrippedCircuitsPackage, but replacing IPFS cid by their content.
///
/// It is used to pass around data b/w substrate-client/src/jni_wrapper.rs GetCircuits and
/// substrate-client/src/jni_wrapper.rs initApp
pub struct DisplayStrippedCircuitsPackageBuffers {
    pub message_pgarbled_buf: Vec<u8>,
    pub message_packmsg_buf: Vec<u8>,
    pub pinpad_pgarbled_buf: Vec<u8>,
    pub pinpad_packmsg_buf: Vec<u8>,
    pub message_nb_digits: u32,
}

#[cfg(test)]
mod tests {
    use super::*;
}
