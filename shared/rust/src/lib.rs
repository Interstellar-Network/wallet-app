// use clap::{load_yaml, App};
// use sp_core::crypto::Pair;
use sp_keyring::AccountKeyring;

use substrate_api_client::rpc::WsRpcClient;
use substrate_api_client::Pair;
use substrate_api_client::{
    compose_extrinsic, Api, GenericAddress, UncheckedExtrinsicV4, XtStatus,
};

// TODO TOREMOVE
fn common() -> i32 {
    return 42;
}

// https://github.com/scs/substrate-api-client/blob/master/examples/example_generic_extrinsic.rs
fn call_extrinsic(url: &str) -> Option<sp_core::H256> {
    let from = AccountKeyring::Alice.pair();
    let client = WsRpcClient::new(&url);
    let api = Api::new(client).map(|api| api.set_signer(from)).unwrap();

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

    tx_hash
}

#[cfg(test)]
mod tests {
    use crate::{call_extrinsic, common};

    #[test]
    fn it_works() {
        assert_eq!(common(), 42);
    }

    #[test]
    fn call_extrinsic_local_ok() {
        // IMPORTANT this extrinsic requires IPFS!
        // IPFS_PATH=/tmp/ipfs ipfs init -p test
        // IPFS_PATH=/tmp/ipfs ipfs config Addresses.API /ip4/0.0.0.0/tcp/5001
        // IPFS_PATH=/tmp/ipfs ipfs daemon --enable-pubsub-experiment
        //
        // IMPORTANT also requires a running "api_circuits"
        let tx_hash = call_extrinsic("ws://127.0.0.1:9944");
        // TODO just check has length?
        assert_eq!(tx_hash.expect("tx_hash ok").to_string(), "1213456");
    }
}
