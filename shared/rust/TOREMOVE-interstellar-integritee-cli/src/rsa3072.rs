use serde::{Deserialize, Serialize};
use sgx_types::{SGX_RSA3072_KEY_SIZE, SGX_RSA3072_PUB_EXP_SIZE};

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

#[derive(Debug)]
enum Error {}

/// https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/core-primitives/sgx/crypto/src/rsa3072.rs#L34
// impl ShieldingCryptoEncrypt for Rsa3072PubKey {
//     type Error = Error;

//     fn encrypt(&self, data: &[u8]) -> Result<Vec<u8>> {
//         let mut cipher_buffer = Vec::new();
//         self.encrypt_buffer(data, &mut cipher_buffer)
//             .map_err(|e| Error::Other(format!("{:?}", e).into()))?;
//         Ok(cipher_buffer)
//     }
// }
impl Rsa3072PubKey {
    // type Error = Error;

    pub(crate) fn encrypt(&self, data: &[u8]) -> Result<Vec<u8>, Error> {
        todo!("Rsa3072PubKey encrypt");
        // let mut cipher_buffer = Vec::new();
        // self.encrypt_buffer(data, &mut cipher_buffer)
        //     .map_err(|e| Error::Other(format!("{:?}", e).into()))?;
        // Ok(cipher_buffer)
    }
}
