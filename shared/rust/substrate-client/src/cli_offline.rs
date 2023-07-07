use common::DisplayStrippedCircuitsPackage;
use common::DisplayStrippedCircuitsPackageBuffers;
use log::info;
use sp_core::bounded_vec::BoundedVec;

use crate::InterstellarIntegriteeWorkerCliTrait;

pub struct InterstellarIntegriteeWorkerCliOffline {}

impl InterstellarIntegriteeWorkerCliTrait for InterstellarIntegriteeWorkerCliOffline {
    fn new(ws_url: &str, node_url: &str) -> Self {
        Self {}
    }

    fn extrinsic_garble_and_strip_display_circuits_package_signed(
        &self,
        tx_message: &str,
    ) -> Result<(), common::InterstellarErrors> {
        info!("offline_demo: extrinsic_garble_and_strip_display_circuits_package_signed");
        Ok(())
    }

    fn extrinsic_register_mobile(&self, _pub_key: Vec<u8>) {
        info!("offline_demo: extrinsic_register_mobile");
    }

    fn extrinsic_check_input(
        &self,
        ipfs_cid: &[u8],
        input_digits: &[u8],
    ) -> Result<(), common::InterstellarErrors> {
        info!("offline_demo: extrinsic_check_input");
        Ok(())
    }

    fn get_latest_pending_display_stripped_circuits_package(
        &self,
        ipfs_server_multiaddr: &str,
    ) -> Result<common::DisplayStrippedCircuitsPackageBuffers, common::InterstellarErrors> {
        let message_bytes = include_bytes!(
            "../../circuit_evaluate/tests/data/display_message_640x360_2digits.garbled.pb.bin"
        );
        let message_vec: Vec<u8> = (*message_bytes).into();

        let pinpad_bytes = include_bytes!(
            "../../circuit_evaluate/tests/data/display_pinpad_590x50.garbled.pb.bin"
        );
        let pinpad_vec: Vec<u8> = (*pinpad_bytes).into();

        Ok(DisplayStrippedCircuitsPackageBuffers {
            message_pgarbled_buf: message_vec,
            message_packmsg_buf: b"TODO TOREMOVE".to_vec(),
            pinpad_pgarbled_buf: pinpad_vec,
            pinpad_packmsg_buf: b"TODO TOREMOVE".to_vec(),
            package: DisplayStrippedCircuitsPackage {
                message_pgarbled_cid: "TODO message_pgarbled_cid?"
                    .as_bytes()
                    .to_vec()
                    .try_into()
                    .unwrap(),
                pinpad_pgarbled_cid: "TODO pinpad_pgarbled_cid?"
                    .as_bytes()
                    .to_vec()
                    .try_into()
                    .unwrap(),
                message_nb_digits: 2,
            },
        })
    }
}
