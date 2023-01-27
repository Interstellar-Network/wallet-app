# integritee-cli: direct version without SGX dependency

NOTE: ideally we would want to use "integritee-cli" directly to avoid duplicating logic
but we CAN NOT b/c it only works in sgx env
FAIL:
error: could not find native static library `sgx_tcrypto`, perhaps an -L flag is missing?
error: could not compile `sgx_ucrypto` due to previous error

Does NOT work either with "itc-rpc-client" b/c it ALSO has a dep to sgx_ucrypto...
So we duplicate the logic from https://github.com/integritee-network/worker/blob/3cc023423fafa93e806553b4ac0f2408c6a6ddbc/cli/src/trusted_operation.rs
ourselves...