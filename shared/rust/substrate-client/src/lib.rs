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

use frame_support::pallet_prelude::*;
use sp_keyring::AccountKeyring;
use codec::{Decode, Encode};

use substrate_api_client::{
    compose_extrinsic, rpc::WsRpcClient, Api, Hash, Pair, UncheckedExtrinsicV4, XtStatus,
};

#[macro_use]
extern crate log;

#[cfg(feature = "with-cwrapper")]
pub mod c_wrapper;
#[cfg(feature = "with-jni")]
pub mod jni_wrapper;

mod loggers;

fn get_api(ws_url: &str) -> Api<sp_core::sr25519::Pair, WsRpcClient> {
    println!("[+] call_extrinsic: {:?}", ws_url);
    let from = AccountKeyring::Alice.pair();
    println!("[+] call_extrinsic: from {:?}", from.public());
    let client = WsRpcClient::new(&ws_url);
    println!("[+] call_extrinsic: client {:?}", client);
    let api = Api::new(client).map(|api| api.set_signer(from)).unwrap();
    println!("[+] call_extrinsic: api {:?}", api.genesis_hash.to_string());

    api
}

// https://github.com/scs/substrate-api-client/blob/master/examples/example_generic_extrinsic.rs
// TODO replace by ocw-garble garbleAndStripSigned(and update params)
fn call_submit_config_display_signed(api: &Api<sp_core::sr25519::Pair, WsRpcClient>, is_message: bool) -> Hash {
    ////////////////////////////////////////////////////////////////////////////
    // // "set the recipient"
    // let to = AccountKeyring::Bob.to_account_id();

    // // "the names are given as strings"
    // #[allow(clippy::redundant_clone)]
    // let xt: UncheckedExtrinsicV4<_> = compose_extrinsic!(
    //     api.clone(),
    //     "Balances",
    //     "transfer",
    //     GenericAddress::Id(to),
    //     Compact(42_u128)
    // );
    ////////////////////////////////////////////////////////////////////////////
    #[allow(clippy::redundant_clone)]
    let xt: UncheckedExtrinsicV4<_> = compose_extrinsic!(
        api.clone(),
        // MUST match the name in /substrate-offchain-worker-demo/runtime/src/lib.rs
        "OcwCircuits",
        // MUST match the call in /substrate-offchain-worker-demo/pallets/ocw-circuits/src/lib.rs
        "submit_config_display_signed",
        is_message
    );

    println!("[+] Composed Extrinsic:\n {:?}\n", xt);

    // "send and watch extrinsic until InBlock"
    let tx_hash = api
        .send_extrinsic(xt.hex_encode(), XtStatus::InBlock)
        .unwrap();
    println!("[+] Transaction got included. Hash: {:?}", tx_hash);

    tx_hash.expect("send_extrinsic failed")
}

// The custom struct that is to be decoded. The user must know the structure for this to work, which can fortunately
// be looked up from the node metadata and printed with the `example_print_metadata`.
// MUST match pallets/ocw-garble/src/lib.rs AccountToPendingCircuitsMap
#[derive(Encode, Decode, Debug, Clone)]
struct StrippedCircuitPackage {
    pgarbled_cid: BoundedVec<u8, ConstU32<64>>,
    packmsg_cid: BoundedVec<u8, ConstU32<64>>,
}
const MAX_NUMBER_PENDING_CIRCUITS_PER_ACCOUNT: u32 = 16;
type PendingCircuitsType = BoundedVec<StrippedCircuitPackage,ConstU32<MAX_NUMBER_PENDING_CIRCUITS_PER_ACCOUNT>,>;

// https://github.com/scs/substrate-api-client/blob/master/examples/example_get_storage.rs
// TODO use get Account form passed "api"?(ie DO NOT hardcode Alice)
fn get_pending_circuits(
    api: &Api<sp_core::sr25519::Pair, WsRpcClient>,
) -> PendingCircuitsType {
    let account = AccountKeyring::Alice.public();
    // let result: AccountInfo = api
    //     .get_storage_map("System", "Account", account, None)
    //     .unwrap()
    //     .or_else(|| Some(AccountInfo::default()))
    //     .unwrap();
    // TODO use _proof?
    let result: PendingCircuitsType = api
        .get_storage_map("OcwGarble", "AccountToPendingCircuitsMap", account, None)
        .unwrap()
        .unwrap();
    println!("[+] pending circuits for account = {:?}", result);

    result
}

#[cfg(test)]
mod tests {
    use crate::loggers;
    use crate::{call_submit_config_display_signed, get_api, get_pending_circuits};
    static INIT: std::sync::Once = std::sync::Once::new();

    fn init() {
        INIT.call_once(|| {
            loggers::init_logger();
        });
    }

    #[test]
    fn call_call_submit_config_display_signed_local_ok() {
        init();
        let api = get_api("ws://127.0.0.1:9944");

        // IMPORTANT this extrinsic requires IPFS!
        // IPFS_PATH=/tmp/ipfs ipfs init -p test
        // IPFS_PATH=/tmp/ipfs ipfs config Addresses.API /ip4/0.0.0.0/tcp/5001
        // IPFS_PATH=/tmp/ipfs ipfs daemon --enable-pubsub-experiment
        //
        // IMPORTANT also requires a running "api_circuits"
        // Seems to be OK with wss eg "wss://polkadot.api.onfinality.io/public-ws"
        // TODO add integration test with SSL
        let tx_hash = call_submit_config_display_signed(&api, false);
        println!("[+] tx_hash: {:02X}", tx_hash);
    }

    #[test]
    fn get_pending_circuits_local_ok() {
        init();
        let api = get_api("ws://127.0.0.1:9944");

        let pending_circuits = get_pending_circuits(&api);
        println!("[+] pending_circuits: {:?}", pending_circuits);
    }
}
