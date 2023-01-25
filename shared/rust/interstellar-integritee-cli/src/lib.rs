use codec::{Decode, Encode};
use itp_node_api::api_client::TEEREX;
use itp_node_api::api_client::{ParentchainApi, WsRpcClient};
use itp_stf_primitives::types::ShardIdentifier;
use itp_types::{BlockNumber, DirectRequestStatus, Header, TrustedOperationStatus};
use log::*;
use my_node_runtime::{AccountId, Hash};
use serde::{Deserialize, Serialize};
use sgx_types::{SGX_RSA3072_KEY_SIZE, SGX_RSA3072_PRI_EXP_SIZE, SGX_RSA3072_PUB_EXP_SIZE};
use sp_application_crypto::sr25519;
use sp_core::{crypto::Ss58Codec, Pair};
use sp_core::{sr25519 as sr25519_core, H256};
use std::path::PathBuf;
use std::{
    result::Result as StdResult,
    sync::mpsc::{channel, Receiver},
    time::Instant,
};
use substrate_api_client::{compose_extrinsic, StaticEvent, XtStatus};
use teerex_primitives::Request;

/// https://github.com/apache/incubator-teaclave-sgx-sdk/blob/master/sgx_crypto_helper/src/rsa3072.rs#L14
/// But deprecated by BigArray Trait
// big_array! { BigArray; }
// cf https://docs.rs/serde-big-array/0.4.1/serde_big_array/macro.big_array.html
use serde_big_array::BigArray;

/// Copied from https://github.com/apache/incubator-teaclave-sgx-sdk/blob/master/sgx_crypto_helper/src/rsa3072.rs#L221
#[derive(Serialize, Deserialize, Clone, Copy)]
pub struct Rsa3072PubKey {
    #[serde(with = "BigArray")]
    n: [u8; SGX_RSA3072_KEY_SIZE],
    e: [u8; SGX_RSA3072_PUB_EXP_SIZE],
}

// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/command_utils.rs#L32
pub(crate) const KEYSTORE_PATH: &str = "my_keystore";

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/command_utils.rs#L35
/// Retrieves the public shielding key via the enclave websocket server.
pub(crate) fn get_shielding_key(cli: &Cli) -> Result<Rsa3072PubKey, String> {
    let worker_api_direct = get_worker_api_direct(cli);
    worker_api_direct
        .get_rsa_pubkey()
        .map_err(|e| e.to_string())
}

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

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/trusted_operation.rs#L181
fn read_shard(trusted_args: &TrustedArgs) -> StdResult<ShardIdentifier, codec::Error> {
    match &trusted_args.shard {
        Some(s) => match s.from_base58() {
            Ok(s) => ShardIdentifier::decode(&mut &s[..]),
            _ => panic!("shard argument must be base58 encoded"),
        },
        None => match trusted_args.mrenclave.from_base58() {
            Ok(s) => ShardIdentifier::decode(&mut &s[..]),
            _ => panic!("mrenclave argument must be base58 encoded"),
        },
    }
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

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/trusted_operation.rs#L195
/// and modified to be useable directly instead of via Clap/Cli
fn send_request(
    cli: &Cli,
    trusted_args: &TrustedArgs,
    trusted_operation: &TrustedOperation,
) -> Option<Vec<u8>> {
    let chain_api = get_chain_api(cli);
    let encryption_key = get_shielding_key(cli).unwrap();
    let call_encrypted = encryption_key.encrypt(&trusted_operation.encode()).unwrap();

    let shard = read_shard(trusted_args).unwrap();

    let arg_signer = &trusted_args.xt_signer;
    let signer = get_pair_from_str(arg_signer);
    let _chain_api = chain_api.set_signer(sr25519_core::Pair::from(signer));

    let request = Request {
        shard,
        cyphertext: call_encrypted,
    };
    let xt = compose_extrinsic!(_chain_api, TEEREX, "call_worker", request);

    // send and watch extrinsic until block is executed
    let block_hash = _chain_api
        .send_extrinsic(xt.hex_encode(), XtStatus::InBlock)
        .unwrap()
        .unwrap();

    info!(
		"Trusted call extrinsic sent and successfully included in parentchain block with hash {:?}.",
		block_hash
	);
    info!("Waiting for execution confirmation from enclave...");
    let (events_in, events_out) = channel();
    _chain_api.subscribe_events(events_in).unwrap();

    loop {
        let ret: ProcessedParentchainBlockArgs = _chain_api
            .wait_for_event::<ProcessedParentchainBlockArgs>(&events_out)
            .unwrap();
        info!("Confirmation of ProcessedParentchainBlock received");
        debug!("Expected block Hash: {:?}", block_hash);
        debug!("Confirmed stf block Hash: {:?}", ret.block_hash);
        match _chain_api.get_header::<Header>(Some(block_hash)) {
            Ok(option) => {
                match option {
                    None => {
                        error!("Could not get Block Header");
                        return None;
                    }
                    Some(header) => {
                        let block_number: BlockNumber = header.number;
                        info!("Expected block Number: {:?}", block_number);
                        info!("Confirmed block Number: {:?}", ret.block_number);
                        // The returned block number belongs to a subsequent event. We missed our event and can break the loop.
                        if ret.block_number > block_number {
                            warn!(
                                "Received block number ({:?}) exceeds expected one ({:?}) ",
                                ret.block_number, block_number
                            );
                            return None;
                        }
                        // The block number is correct, but the block hash does not fit.
                        if block_number == ret.block_number && block_hash != ret.block_hash {
                            error!(
								"Block hash for event does not match expected hash. Expected: {:?}, returned: {:?}",
								block_hash, ret.block_hash);
                            return None;
                        }
                    }
                }
            }
            Err(err) => {
                error!("Could not get Block Header, due to error: {:?}", err);
                return None;
            }
        }
        if ret.block_hash == block_hash {
            return Some(ret.block_hash.encode());
        }
    }
}

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/trusted_operation.rs#L339
#[allow(dead_code)]
#[derive(Decode)]
struct ProcessedParentchainBlockArgs {
    signer: AccountId,
    block_hash: H256,
    merkle_root: H256,
    block_number: BlockNumber,
}

impl StaticEvent for ProcessedParentchainBlockArgs {
	const PALLET: &'static str = TEEREX;
	const EVENT: &'static str = "ProcessedParentchainBlock";
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
