# circuit evaluate

IMPORTANT: most of the code in `src/cpp/` is copy-pasted from https://github.com/Interstellar-Network/lib_garble
and then simplified

eg:
- remove all functions needed only server-side
- replace AES SSE by a Rust version

TODO we SHOULD move the common in a separate repo! Or better, rewrite in Rust on use it from "lib_garble"(needed only for tests?)

TODO replace GPL by Apache