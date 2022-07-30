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

use common::{DisplayStrippedCircuitsPackageBuffers, PendingCircuitsType};
use core::time::Duration;
use futures_util::TryStreamExt;
use ipfs_api_backend_hyper::{
    BackendWithGlobalOptions, GlobalOptions, IpfsApi, IpfsClient, TryFromUri,
};
use sp_keyring::AccountKeyring;
use substrate_api_client::{
    compose_extrinsic, rpc::WsRpcClient, Api, Hash, Pair, UncheckedExtrinsicV4, XtStatus,
};

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
fn extrinsic_garble_and_strip_display_circuits_package_signed(
    api: &Api<sp_core::sr25519::Pair, WsRpcClient>,
    tx_message: &str,
) -> Hash {
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
        "OcwGarble",
        // MUST match the call in /substrate-offchain-worker-demo/pallets/ocw-circuits/src/lib.rs
        "garble_and_strip_display_circuits_package_signed",
        tx_message.as_bytes().to_vec()
    );

    println!("[+] Composed Extrinsic:\n {:?}\n", xt);

    // "send and watch extrinsic until InBlock"
    let tx_hash = api
        .send_extrinsic(xt.hex_encode(), XtStatus::InBlock)
        .unwrap();
    println!("[+] Transaction got included. Hash: {:?}", tx_hash);

    tx_hash.expect("send_extrinsic failed")
}

pub fn extrinsic_register_mobile(
    api: &Api<sp_core::sr25519::Pair, WsRpcClient>,
    pub_key: Vec<u8>,
) -> Hash {
    #[allow(clippy::redundant_clone)]
    let xt: UncheckedExtrinsicV4<_> = compose_extrinsic!(
        api.clone(),
        // MUST match the name in /substrate-offchain-worker-demo/runtime/src/lib.rs
        "MobileRegistry",
        // MUST match the call in /substrate-offchain-worker-demo/pallets/ocw-circuits/src/lib.rs
        "register_mobile",
        pub_key
    );

    println!("[+] Composed Extrinsic:\n {:?}\n", xt);

    // "send and watch extrinsic until InBlock"
    let tx_hash = api
        .send_extrinsic(xt.hex_encode(), XtStatus::InBlock)
        .unwrap();
    println!("[+] Transaction got included. Hash: {:?}", tx_hash);

    tx_hash.expect("send_extrinsic failed")
}

pub fn extrinsic_check_input(
    api: &Api<sp_core::sr25519::Pair, WsRpcClient>,
    ipfs_cid: Vec<u8>,
    input_digits: Vec<u8>,
) -> Hash {
    #[allow(clippy::redundant_clone)]
    let xt: UncheckedExtrinsicV4<_> = compose_extrinsic!(
        api.clone(),
        // MUST match the name in /substrate-offchain-worker-demo/runtime/src/lib.rs
        "TxValidation",
        // MUST match the call in /substrate-offchain-worker-demo/pallets/ocw-circuits/src/lib.rs
        "check_input",
        ipfs_cid,
        input_digits
    );

    println!("[+] Composed Extrinsic:\n {:?}\n", xt);

    // "send and watch extrinsic until InBlock"
    let tx_hash = api
        .send_extrinsic(xt.hex_encode(), XtStatus::InBlock)
        .unwrap();
    println!("[+] Transaction got included. Hash: {:?}", tx_hash);

    tx_hash.expect("send_extrinsic failed")
}

// https://github.com/scs/substrate-api-client/blob/master/examples/example_get_storage.rs
// TODO use get Account form passed "api"?(ie DO NOT hardcode Alice)
fn get_pending_circuits(api: &Api<sp_core::sr25519::Pair, WsRpcClient>) -> PendingCircuitsType {
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

fn ipfs_client(ipfs_server_multiaddr: &str) -> BackendWithGlobalOptions<IpfsClient> {
    log::info!("ipfs_client: starting with: {}", ipfs_server_multiaddr);
    BackendWithGlobalOptions::new(
        ipfs_api_backend_hyper::IpfsClient::from_multiaddr_str(&ipfs_server_multiaddr).unwrap(),
        GlobalOptions::builder()
            .timeout(Duration::from_millis(5000))
            .build(),
    )
}

/// Get the list of pending circuits using an extrinsic
/// Then download ONE using IPFS
///
/// - ipfs_server_multiaddr: something like "/ip4/127.0.0.1/tcp/5001"
/// - ws_url: address of the WS endpoint of the OCW; something like "ws://127.0.0.1:9944"
pub fn get_one_pending_display_stripped_circuits_package(
    ipfs_server_multiaddr: &str,
    ws_url: &str,
) -> DisplayStrippedCircuitsPackageBuffers {
    // TODO add param for index
    let idx = 0;

    let api = get_api(ws_url);
    let pending_circuits = get_pending_circuits(&api);

    // convert Vec<u8> into str
    let message_pgarbled_cid_str =
        sp_std::str::from_utf8(&pending_circuits[idx].message_pgarbled_cid)
            .expect("message_pgarbled_cid utf8");
    let message_packmsg_cid_str =
        sp_std::str::from_utf8(&pending_circuits[idx].message_packmsg_cid)
            .expect("message_packmsg_cid utf8");
    let pinpad_pgarbled_cid_str =
        sp_std::str::from_utf8(&pending_circuits[idx].pinpad_pgarbled_cid)
            .expect("pinpad_pgarbled_cid utf8");
    let pinpad_packmsg_cid_str = sp_std::str::from_utf8(&pending_circuits[idx].pinpad_packmsg_cid)
        .expect("pinpad_packmsg_cid utf8");

    // allow calling ipfs api(ASYNC) from a sync context
    // TODO can we make jni functions async?
    let rt = tokio::runtime::Runtime::new().unwrap();
    rt.block_on(async {
        // IMPORTANT: stored using ipfs_client().add() so we MUST use cat()
        let message_pgarbled_buf: Vec<u8> = ipfs_client(&ipfs_server_multiaddr)
            .cat(message_pgarbled_cid_str)
            .map_ok(|chunk| chunk.to_vec())
            .try_concat()
            .await
            .unwrap();
        let message_packmsg_buf: Vec<u8> = ipfs_client(&ipfs_server_multiaddr)
            .cat(message_packmsg_cid_str)
            .map_ok(|chunk| chunk.to_vec())
            .try_concat()
            .await
            .unwrap();
        let pinpad_pgarbled_buf: Vec<u8> = ipfs_client(&ipfs_server_multiaddr)
            .cat(pinpad_pgarbled_cid_str)
            .map_ok(|chunk| chunk.to_vec())
            .try_concat()
            .await
            .unwrap();
        let pinpad_packmsg_buf: Vec<u8> = ipfs_client(&ipfs_server_multiaddr)
            .cat(pinpad_packmsg_cid_str)
            .map_ok(|chunk| chunk.to_vec())
            .try_concat()
            .await
            .unwrap();

        log::info!(
            "get_one_pending_display_stripped_circuits_package: got: {},{},{},{},",
            message_pgarbled_buf.len(),
            message_packmsg_buf.len(),
            pinpad_pgarbled_buf.len(),
            pinpad_packmsg_buf.len()
        );

        DisplayStrippedCircuitsPackageBuffers {
            message_pgarbled_buf: message_pgarbled_buf,
            message_packmsg_buf: message_packmsg_buf,
            pinpad_pgarbled_buf: pinpad_pgarbled_buf,
            pinpad_packmsg_buf: pinpad_packmsg_buf,
            message_nb_digits: pending_circuits[idx].message_nb_digits,
        }
    })
}

#[cfg(test)]
mod tests {
    use crate::loggers;
    use crate::{
        extrinsic_check_input, extrinsic_garble_and_strip_display_circuits_package_signed,
        extrinsic_register_mobile, get_api, get_pending_circuits,
    };
    static INIT: std::sync::Once = std::sync::Once::new();

    fn init() {
        INIT.call_once(|| {
            loggers::init_logger();
        });
    }

    // IMPORTANT: use #[serial_test::serial] when testing extrinsics else:
    // "WS Error <Custom(Extrinsic("extrinsic error code 1014: Priority is too low: (35746 vs 19998): The transaction has too low priority to replace another transaction already in the pool."))>"
    #[test]
    #[serial_test::serial]
    fn extrinsic_garble_and_strip_display_circuits_package_signed_local_ok() {
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
        let tx_hash = extrinsic_garble_and_strip_display_circuits_package_signed(&api, "aaa");
        println!("[+] tx_hash: {:02X}", tx_hash);
    }

    #[test]
    fn get_pending_circuits_local_ok() {
        init();
        let api = get_api("ws://127.0.0.1:9944");

        let pending_circuits = get_pending_circuits(&api);
        println!("[+] pending_circuits: {:?}", pending_circuits);
    }

    // IMPORTANT: use #[serial] when testing extrinsics else:
    // "WS Error <Custom(Extrinsic("extrinsic error code 1014: Priority is too low: (35746 vs 19998): The transaction has too low priority to replace another transaction already in the pool."))>"
    #[test]
    #[serial_test::serial]
    fn extrinsic_register_mobile_local_ok() {
        init();
        let api = get_api("ws://127.0.0.1:9944");

        // MUST be at least 32 bytes
        let tx_hash = extrinsic_register_mobile(&api, vec![42; 32]);
        println!("[+] tx_hash: {:02X}", tx_hash);
    }

    // IMPORTANT: use #[serial] when testing extrinsics else:
    // "WS Error <Custom(Extrinsic("extrinsic error code 1014: Priority is too low: (35746 vs 19998): The transaction has too low priority to replace another transaction already in the pool."))>"
    // TODO this requires a setup: ie calling "extrinsic_garble_and_strip_display_circuits_package_signed"
    // #[test]
    // #[serial_test::serial]
    // fn extrinsic_extrinsic_check_input_local_ok() {
    //     init();
    //     let api = get_api("ws://127.0.0.1:9944");

    //     let tx_hash = extrinsic_check_input(&api, vec![0; 32], vec![0, 0]);
    //     println!("[+] tx_hash: {:02X}", tx_hash);
    // }
}
