use crate::cli::Cli;
use crate::command_utils::get_chain_api;
use crate::command_utils::get_pair_from_str;
use crate::command_utils::get_shielding_key;
use crate::command_utils::get_worker_api_direct;
use crate::direct_client::DirectApi;
use crate::rsa3072::Rsa3072PubKey;
use crate::trusted_commands::TrustedArgs;
use codec::{Decode, Encode};
use itp_node_api::api_client::TEEREX;
// use itp_sgx_crypto::ShieldingCryptoEncrypt;
use base58::FromBase58;
use ita_stf::TrustedOperation;
use itp_rpc::{RpcRequest, RpcResponse, RpcReturnValue};
use itp_stf_primitives::types::ShardIdentifier;
use itp_types::{BlockNumber, DirectRequestStatus, Header, TrustedOperationStatus};
use itp_utils::{FromHexPrefixed, ToHexPrefixed};
use log::*;
use my_node_runtime::{AccountId, Hash};
use sp_core::{sr25519 as sr25519_core, H256};
use std::{result::Result as StdResult, sync::mpsc::channel};
use substrate_api_client::{compose_extrinsic, StaticEvent, XtStatus};
use teerex_primitives::Request;

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

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/trusted_operation.rs#L105
/// and modified to be useable directly instead of via Clap/Cli
pub fn send_request(
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

/// Copied from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/trusted_operation.rs#L195
/// and modified to be useable directly instead of via Clap/Cli
/// sends a rpc watch request to the worker api server
pub fn send_direct_request(
    cli: &Cli,
    trusted_args: &TrustedArgs,
    operation_call: &TrustedOperation,
) -> Option<Vec<u8>> {
    let encryption_key = get_shielding_key(cli).unwrap();
    let shard = read_shard(trusted_args).unwrap();
    let jsonrpc_call: String = get_json_request(shard, operation_call, encryption_key);

    debug!("get direct api");
    let direct_api = get_worker_api_direct(cli);

    debug!("setup sender and receiver");
    let (sender, receiver) = channel();
    direct_api.watch(jsonrpc_call, sender);

    debug!("waiting for rpc response");
    loop {
        match receiver.recv() {
            Ok(response) => {
                debug!("received response");
                let response: RpcResponse = serde_json::from_str(&response).unwrap();
                if let Ok(return_value) = RpcReturnValue::from_hex(&response.result) {
                    debug!("successfully decoded rpc response: {:?}", return_value);
                    match return_value.status {
                        DirectRequestStatus::Error => {
                            debug!("request status is error");
                            if let Ok(value) = String::decode(&mut return_value.value.as_slice()) {
                                println!("[Error] {}", value);
                            }
                            direct_api.close().unwrap();
                            return None;
                        }
                        DirectRequestStatus::TrustedOperationStatus(status) => {
                            debug!("request status is: {:?}", status);
                            if let Ok(value) = Hash::decode(&mut return_value.value.as_slice()) {
                                println!("Trusted call {:?} is {:?}", value, status);
                            }
                            if connection_can_be_closed(status) {
                                direct_api.close().unwrap();
                            }
                        }
                        _ => {
                            debug!("request status is ignored");
                            direct_api.close().unwrap();
                            return None;
                        }
                    }
                    if !return_value.do_watch {
                        debug!("do watch is false, closing connection");
                        direct_api.close().unwrap();
                        return None;
                    }
                };
            }
            Err(e) => {
                error!("failed to receive rpc response: {:?}", e);
                direct_api.close().unwrap();
                return None;
            }
        };
    }
}

pub(crate) fn get_json_request(
    shard: ShardIdentifier,
    operation_call: &TrustedOperation,
    shielding_pubkey: Rsa3072PubKey,
) -> String {
    let operation_call_encrypted = shielding_pubkey.encrypt(&operation_call.encode()).unwrap();

    // compose jsonrpc call
    let request = Request {
        shard,
        cyphertext: operation_call_encrypted,
    };
    RpcRequest::compose_jsonrpc_call(
        "author_submitAndWatchExtrinsic".to_string(),
        vec![request.to_hex()],
    )
    .unwrap()
}

/// https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/trusted_operation.rs#L329
fn connection_can_be_closed(top_status: TrustedOperationStatus) -> bool {
    !matches!(
        top_status,
        TrustedOperationStatus::Submitted
            | TrustedOperationStatus::Future
            | TrustedOperationStatus::Ready
            | TrustedOperationStatus::Broadcast
    )
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
    use crate::trusted_commands::TrustedCommands;
    use ita_stf::TrustedCall;
    use itp_stf_primitives::types::KeyPair;
    use sp_keyring::AccountKeyring;

    #[test]
    fn it_works() {
        // cf test "verify_signature_works"
        let nonce = 21;
        let mrenclave = [0u8; 32];
        let shard = ShardIdentifier::default();

        let call = TrustedCall::garble_and_strip_display_circuits_package_signed(
            AccountKeyring::Alice.public().into(),
            vec![],
        );
        let signed_call = call.sign(
            &KeyPair::Sr25519(Box::new(AccountKeyring::Alice.pair())),
            nonce,
            &mrenclave,
            &shard,
        );

        let result = send_direct_request(
            &Cli {
                node_url: "TODO node_url".to_string(),
                node_port: "TODO node_port".to_string(),
                worker_url: "TODO worker_url".to_string(),
                trusted_worker_port: "TODO trusted_worker_port".to_string(),
            },
            &TrustedArgs {
                mrenclave: "TODO mrenclave".to_string(),
                shard: None,
                xt_signer: "TODO xt_signer".to_string(),
                direct: true,
                command: TrustedCommands::TOREMOVE,
            },
            &TrustedOperation::direct_call(signed_call),
        )
        .unwrap();
        assert_eq!(result, vec![42, 42]);
    }
}
