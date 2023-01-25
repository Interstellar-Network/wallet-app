use crate::cli::Cli;
use crate::direct_client::DirectClient as DirectWorkerApi;
use crate::rsa3072::Rsa3072PubKey;
use itp_node_api::api_client::{ParentchainApi, WsRpcClient};
use log::*;
use sp_application_crypto::sr25519;
use sp_core::{crypto::Ss58Codec, Pair};
use std::path::PathBuf;
use std::sync::mpsc::Sender;
use substrate_client_keystore::LocalKeystore;

// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/command_utils.rs#L32
pub(crate) const KEYSTORE_PATH: &str = "my_keystore";

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/command_utils.rs#L35
/// Retrieves the public shielding key via the enclave websocket server.
pub(crate) fn get_shielding_key(cli: &Cli) -> Result<Rsa3072PubKey, String> {
    // let worker_api_direct = get_worker_api_direct(cli);
    // worker_api_direct
    //     .get_rsa_pubkey()
    //     .map_err(|e| e.to_string())
    todo!("get_shielding_key + get_worker_api_direct")
}

// #[derive(Debug)]
// enum Error {}

// // TODO TOREMOVE
// struct DirectWorkerApi {}

// impl DirectWorkerApi {
//     fn new(url: String) -> DirectWorkerApi {
//         todo!("DirectWorkerApi::new")
//     }

//     pub(crate) fn close(&self) -> Result<(), Error> {
//         todo!("DirectWorkerApi::close")
//     }

//     pub(crate) fn watch<T>(&self, jsonrpc_call: String, sender: Sender<T>) {
//         todo!("DirectWorkerApi::watch")
//     }

//     fn recv(&self) {
//         todo!("DirectWorkerApi::recv")
//     }
// }

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/command_utils.rs#L54
pub(crate) fn get_worker_api_direct(cli: &Cli) -> DirectWorkerApi {
    let url = format!("{}:{}", cli.worker_url, cli.trusted_worker_port);
    info!("Connecting to integritee-service-direct-port on '{}'", url);
    DirectWorkerApi::new(url)
}

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/command_utils.rs#L40
pub(crate) fn get_chain_api(cli: &Cli) -> ParentchainApi {
    let url = format!("{}:{}", cli.node_url, cli.node_port);
    info!("connecting to {}", url);
    ParentchainApi::new(WsRpcClient::new(&url)).unwrap()
}

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/command_utils.rs#L60
/// get a pair either form keyring (well known keys) or from the store
pub(crate) fn get_pair_from_str(account: &str) -> sr25519::AppPair {
    info!("getting pair for {}", account);
    match &account[..2] {
        "//" => sr25519::AppPair::from_string(account, None).unwrap(),
        _ => {
            info!("fetching from keystore at {}", &KEYSTORE_PATH);
            // open store without password protection
            let store = LocalKeystore::open(PathBuf::from(&KEYSTORE_PATH), None)
                .expect("store should exist");
            info!("store opened");
            let _pair = store
                .key_pair::<sr25519::AppPair>(
                    &sr25519::Public::from_ss58check(account).unwrap().into(),
                )
                .unwrap()
                .unwrap();
            drop(store);
            _pair
        }
    }
}
