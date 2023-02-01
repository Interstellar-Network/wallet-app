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

use clap::Parser;
use common::DisplayStrippedCircuitsPackageBuffers;
use common::InterstellarErrors;
use core::time::Duration;
use futures_util::TryStreamExt;
use integritee_cli::{
    commands, Cli, CliResult, CliResultOk, PalletOcwGarbleDisplayStrippedCircuitsPackage,
};
use ipfs_api_backend_hyper::{
    BackendWithGlobalOptions, GlobalOptions, IpfsApi, IpfsClient, TryFromUri,
};
use log::*;
use sp_core::crypto::Pair;
use sp_keyring::AccountKeyring;
use url::Url;

#[cfg(feature = "with-cwrapper")]
pub mod c_wrapper;
#[cfg(feature = "with-jni")]
pub mod jni_wrapper;

mod loggers;

/// Return a client for the SUBSTRATE/INTEGRITEE NODE
// fn get_node_api(
//     ws_url: &str,
// ) -> Api<sp_core::sr25519::Pair, WsRpcClient, BaseExtrinsicParams<AssetTip>> {
//     println!("[+] call_extrinsic: {:?}", ws_url);
//     let from = AccountKeyring::Alice.pair();
//     println!("[+] call_extrinsic: from {:?}", from.public());
//     let client = WsRpcClient::new(&ws_url);
//     println!("[+] call_extrinsic: client {:?}", client);
//     let api = Api::new(client).map(|api| api.set_signer(from)).unwrap();
//     println!("[+] call_extrinsic: api {:?}", api.genesis_hash.to_string());

//     api
// }

pub struct InterstellarIntegriteeWorkerCli {
    worker_url: String,
    worker_port: u16,
    node_url: String,
    node_port: u16,
    // WARNING: cf "get_account"
    // account: sp_core::sr25519::Pair,
    mrenclave: Option<String>,
}

impl InterstellarIntegriteeWorkerCli {
    /// Return a client for the INTEGRITEE WORKER
    /// NOTE: it is a bit ugly but `integritee-cli` is NOT made to be a lib; and it only exposes a clap Parse...
    ///
    /// param: ws_url: default to "wss://127.0.0.1:2000"
    pub fn new(ws_url: &str, node_url: &str) -> InterstellarIntegriteeWorkerCli {
        let ws_url = Url::parse(ws_url).unwrap();
        let node_url = Url::parse(node_url).unwrap();

        // Two steps init:
        // - First we parse the url etc
        // - Then we send a query to get the mrenclave
        let mut worker_cli = InterstellarIntegriteeWorkerCli {
            worker_url: format!("{}://{}", ws_url.scheme(), ws_url.host_str().unwrap()),
            worker_port: ws_url.port().unwrap(),
            node_url: format!("{}://{}", node_url.scheme(), node_url.host_str().unwrap()),
            node_port: node_url.port().unwrap(),
            // WARNING: cf "get_account"
            // account: AccountKeyring::Alice.pair(),
            mrenclave: None,
        };

        // TODO DRY with "run_trusted_direct"
        let worker_port_str = worker_cli.worker_port.to_string();
        let worker_url_str = worker_cli.worker_url.to_string();
        let node_port_str = worker_cli.node_port.to_string();
        let node_url_str = worker_cli.node_url.to_string();
        let cli = Cli::parse_from([
            // we MUST replace the binary name
            // else we end up with eg "error: Found argument '2090' which wasn't expected, or isn't valid in this context"
            // https://stackoverflow.com/questions/74465951/how-to-parse-custom-string-with-clap-derive
            "",
            "--trusted-worker-port",
            &worker_port_str,
            "--worker-url",
            &worker_url_str,
            "--node-port",
            &node_port_str,
            "--node-url",
            &node_url_str,
            "list-workers",
        ]);
        let res = commands::match_command(&cli);
        match res {
            Ok(CliResultOk::MrEnclaveBase58 { mr_enclaves }) => {
                // TODO which enclave to choose if more than one? Probably random to distribute the clients?
                worker_cli.mrenclave = Some(mr_enclaves.first().unwrap().to_string());
            }
            _ => todo!("InterstellarIntegriteeWorkerCli::new list-workers failed"),
        }

        worker_cli
    }

    /// Wrap: integritee-cli trusted [OPTIONS] --mrenclave <MRENCLAVE> <SUBCOMMAND>
    fn run_trusted_direct(&self, trusted_subcommand: &[&str]) -> CliResult {
        let port_str = self.worker_port.to_string();
        let mrenclave_str = self
            .mrenclave
            .clone()
            .expect("run_trusted_direct called but mrenclave not set!");

        let mut args = vec![
            // we MUST replace the binary name
            // else we end up with eg "error: Found argument '2090' which wasn't expected, or isn't valid in this context"
            // https://stackoverflow.com/questions/74465951/how-to-parse-custom-string-with-clap-derive
            "",
            "--trusted-worker-port",
            &port_str,
            "--worker-url",
            &self.worker_url,
            "trusted",
            "--mrenclave",
            &mrenclave_str,
            "--direct",
        ];
        args.extend_from_slice(trusted_subcommand);

        let cli = Cli::parse_from(args);
        commands::match_command(&cli)
    }

    /// CAREFUL! The result of this is indirectly used by "get_pair_from_str" in /xxx/.cargo/git/checkouts/integritee-worker-4df232146e8c8d35/0c9d7cf/cli/src/trusted_command_utils.rs
    /// When NOT using a hardcoded key(eg //ALICE), it ends up using the filesystem via "TRUSTED_KEYSTORE_PATH"
    /// which is not really ideal for Android...
    fn get_account(&self) -> String {
        "//Alice".to_string()
    }

    /// cf /integritee-worker/cli/demo_interstellar.sh for how to call "garble-and-strip-display-circuits-package-signed"
    /// eg:
    /// ${CLIENT} trusted --mrenclave "${MRENCLAVE}" --direct garble-and-strip-display-circuits-package-signed "${PLAYER1}" "REPLACEME tx msg"
    pub fn extrinsic_garble_and_strip_display_circuits_package_signed(
        &self,
        tx_message: &str,
    ) -> Result<(), InterstellarErrors> {
        Ok(self
            .run_trusted_direct(&[
                "garble-and-strip-display-circuits-package-signed",
                &self.get_account(),
                tx_message,
            ])
            .map_err(|err| InterstellarErrors::GarbleAndStrip {})
            .map(|_| ())?)
    }

    pub fn extrinsic_register_mobile(&self, pub_key: Vec<u8>) {
        todo!("TODO extrinsic_register_mobile")
    }

    /// ${CLIENT} trusted --mrenclave "${MRENCLAVE}" --direct tx-check-input "${PLAYER1}" "${IPFS_CID}" ${USER_INPUTS}
    pub fn extrinsic_check_input(
        &self,
        ipfs_cid: &[u8],
        input_digits: Vec<u8>,
    ) -> Result<(), InterstellarErrors> {
        // NOTE: the cli expects a list of SPACE-separated integer b/w [0-9]
        let inputs_prepared = input_digits
            .iter()
            .map(|digit| digit.to_string())
            .collect::<Vec<String>>()
            .join(" ");

        let res = self
            .run_trusted_direct(&[
                "tx-check-input",
                &self.get_account(),
                std::str::from_utf8(ipfs_cid).unwrap(),
                &inputs_prepared,
            ])
            .map_err(|err| InterstellarErrors::TxCheckInput {})?;

        match res {
            CliResultOk::TrustedOpRes { res } => Ok(()),
            _ => panic!("called tx-check-input but got an unexpected enum variant"),
        }
    }

    /// Get the list of pending circuits using an extrinsic
    /// Then download ONE using IPFS
    ///
    /// - ipfs_server_multiaddr: something like "/ip4/127.0.0.1/tcp/5001"
    /// - ws_url: address of the WS endpoint of the OCW; something like "ws://127.0.0.1:9990"
    pub fn get_latest_pending_display_stripped_circuits_package(
        &self,
        ipfs_server_multiaddr: &str,
    ) -> Result<DisplayStrippedCircuitsPackageBuffers, InterstellarErrors> {
        let circuit = self.get_most_recent_circuit()?;

        // convert Vec<u8> into str
        let message_pgarbled_cid_str = sp_std::str::from_utf8(&circuit.message_pgarbled_cid)
            .expect("message_pgarbled_cid utf8");
        let pinpad_pgarbled_cid_str =
            sp_std::str::from_utf8(&circuit.pinpad_pgarbled_cid).expect("pinpad_pgarbled_cid utf8");

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
            let pinpad_pgarbled_buf: Vec<u8> = ipfs_client(&ipfs_server_multiaddr)
                .cat(pinpad_pgarbled_cid_str)
                .map_ok(|chunk| chunk.to_vec())
                .try_concat()
                .await
                .unwrap();

            info!(
                "get_one_pending_display_stripped_circuits_package: got: {},{}",
                message_pgarbled_buf.len(),
                pinpad_pgarbled_buf.len(),
            );

            Ok(DisplayStrippedCircuitsPackageBuffers {
                message_pgarbled_buf: message_pgarbled_buf,
                message_packmsg_buf: b"TODO TOREMOVE".to_vec(),
                pinpad_pgarbled_buf: pinpad_pgarbled_buf,
                pinpad_packmsg_buf: b"TODO TOREMOVE".to_vec(),
                package: circuit.clone(),
            })
        })
    }

    /// RESULT=$(${CLIENT} trusted --mrenclave "${MRENCLAVE}" --direct get-circuits-package "${PLAYER1}" | xargs)
    /// NOTE: the name is bad: "get-circuits-package" return the MOST recent Circuit(SINGULAR == 1 Circuit)
    fn get_most_recent_circuit(
        &self,
    ) -> Result<PalletOcwGarbleDisplayStrippedCircuitsPackage, InterstellarErrors> {
        let res = self
            .run_trusted_direct(&["get-circuits-package", &self.get_account()])
            .map_err(|err| InterstellarErrors::GetCircuitsPackage {})?;

        match res {
            CliResultOk::DisplayStrippedCircuitsPackage { circuit } => Ok(circuit),
            _ => panic!("called get-circuits-package but got an unexpected enum variant"),
        }
    }
}

fn ipfs_client(ipfs_server_multiaddr: &str) -> BackendWithGlobalOptions<IpfsClient> {
    info!("ipfs_client: starting with: {}", ipfs_server_multiaddr);
    BackendWithGlobalOptions::new(
        ipfs_api_backend_hyper::IpfsClient::from_multiaddr_str(&ipfs_server_multiaddr).unwrap(),
        GlobalOptions::builder()
            .timeout(Duration::from_millis(5000))
            .build(),
    )
}

#[cfg(test)]
mod tests {
    use super::*;
    static INIT: std::sync::Once = std::sync::Once::new();

    fn init() -> InterstellarIntegriteeWorkerCli {
        INIT.call_once(|| {
            loggers::init_logger();
        });

        InterstellarIntegriteeWorkerCli::new("wss://127.0.0.1:2090", "ws://127.0.0.1:9990")
    }

    #[test]
    fn can_build_integritee_client_ok() {
        let worker_cli = init();
        assert!(worker_cli.mrenclave.is_some());
    }

    // IMPORTANT: use #[serial_test::serial] when testing extrinsics else:
    // "WS Error <Custom(Extrinsic("extrinsic error code 1014: Priority is too low: (35746 vs 19998): The transaction has too low priority to replace another transaction already in the pool."))>"
    #[test]
    #[serial_test::serial]
    fn extrinsic_garble_and_strip_display_circuits_package_signed_local_ok() {
        let worker_cli = init();

        // NOTE: we use this tx message b/c that way we can easily compare signatures etc vs /integritee-worker/cli/demo_interstellar.sh
        // NOTE: when comparing: you MUST restart the worker else the nonce will not match
        let res = worker_cli
            .extrinsic_garble_and_strip_display_circuits_package_signed("REPLACEME tx msg");
        assert!(res.is_ok());
    }

    #[test]
    #[serial_test::serial]
    fn get_pending_circuits_local_ok() {
        let worker_cli = init();

        let circuit = worker_cli.get_most_recent_circuit().unwrap();
        assert!(
            circuit.message_pgarbled_cid.len()
                == "QmQMRdg8eCu8bzaBQqdtW26G87XCnX6CAi3oa2vfVx67Lb".len()
        );
    }

    // IMPORTANT: use #[serial] when testing extrinsics else:
    // "WS Error <Custom(Extrinsic("extrinsic error code 1014: Priority is too low: (35746 vs 19998): The transaction has too low priority to replace another transaction already in the pool."))>"
    // #[test]
    // #[serial_test::serial]
    // fn extrinsic_register_mobile_local_ok() {
    //     init();
    //     let worker_cli =
    //         InterstellarIntegriteeWorkerCli::new("wss://127.0.0.1".to_string(), "2090".to_string());

    //     // MUST be at least 32 bytes
    //     worker_cli.extrinsic_register_mobile(vec![42; 32]);
    // }

    // IMPORTANT: use #[serial] when testing extrinsics else:
    // "WS Error <Custom(Extrinsic("extrinsic error code 1014: Priority is too low: (35746 vs 19998): The transaction has too low priority to replace another transaction already in the pool."))>"
    // TODO this requires a setup: ie calling "extrinsic_garble_and_strip_display_circuits_package_signed"
    // #[test]
    // #[serial_test::serial]
    // fn extrinsic_extrinsic_check_input_local_ok() {
    //     init();
    //     let worker_cli =
    // InterstellarIntegriteeWorkerCli::new("wss://127.0.0.1".to_string(), "2090".to_string());

    //     let tx_hash = extrinsic_check_input(&api, vec![0; 32], vec![0, 0]);
    //     println!("[+] tx_hash: {:02X}", tx_hash);
    // }
}
