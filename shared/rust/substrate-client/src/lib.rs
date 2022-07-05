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

use sp_keyring::AccountKeyring;

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

// https://github.com/scs/substrate-api-client/blob/master/examples/example_generic_extrinsic.rs
fn call_extrinsic(url: &str) -> Hash {
    println!("[+] call_extrinsic: {:?}", url); // OK
    let from = AccountKeyring::Alice.pair();
    println!("[+] call_extrinsic: from {:?}", from.public());
    let client = WsRpcClient::new(&url); // OK
    println!("[+] call_extrinsic: client {:?}", client);
    let api = Api::new(client).map(|api| api.set_signer(from)).unwrap();
    println!("[+] call_extrinsic: api {:?}", api.genesis_hash.to_string());

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
        "submit_config_display_signed" // no param
    );

    println!("[+] Composed Extrinsic:\n {:?}\n", xt);

    // "send and watch extrinsic until InBlock"
    let tx_hash = api
        .send_extrinsic(xt.hex_encode(), XtStatus::InBlock)
        .unwrap();
    println!("[+] Transaction got included. Hash: {:?}", tx_hash);

    tx_hash.expect("send_extrinsic failed")
}

#[cfg(test)]
mod tests {
    use crate::call_extrinsic;
    use crate::loggers;

    fn init() {
        loggers::init_logger();
    }

    #[test]
    fn call_extrinsic_local_ok() {
        init();

        // IMPORTANT this extrinsic requires IPFS!
        // IPFS_PATH=/tmp/ipfs ipfs init -p test
        // IPFS_PATH=/tmp/ipfs ipfs config Addresses.API /ip4/0.0.0.0/tcp/5001
        // IPFS_PATH=/tmp/ipfs ipfs daemon --enable-pubsub-experiment
        //
        // IMPORTANT also requires a running "api_circuits"
        // Seems to be OK with wss eg "wss://polkadot.api.onfinality.io/public-ws"
        // TODO add integration test with SSL
        let tx_hash = call_extrinsic("ws://127.0.0.1:9944");
        println!("[+] tx_hash: {:02X}", tx_hash);
    }
}
