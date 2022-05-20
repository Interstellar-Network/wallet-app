//
//  HeaderRustWrapper.h
//  iosApp
//
//  Created by Nathan Prat on 23/05/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

#ifndef HeaderRustWrapper_h
#define HeaderRustWrapper_h

// TODO how to make sure it matches the signature of /.../wallet-app/shared/rust/src/c_wrapper.rs
// and/or /.../wallet-app/shared/rust/src/lib.rs
const char* rust_call_extrinsic(const char* to);
void rust_call_extrinsic_free(char *);

#endif /* HeaderRustWrapper_h */
