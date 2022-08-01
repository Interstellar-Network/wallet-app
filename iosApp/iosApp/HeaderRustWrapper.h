//
//  HeaderRustWrapper.h
//  iosApp
//
//  Created by Nathan Prat on 23/05/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

#ifndef HeaderRustWrapper_h
#define HeaderRustWrapper_h

struct ios_view_obj
{
    void *view;
    // CAMetalLayer
    // TODO? void *metal_layer;
};

struct app;

// TODO how to make sure it matches the signature of /.../wallet-app/shared/rust/src/c_wrapper.rs
// and/or /.../wallet-app/shared/rust/src/lib.rs
const char *rust_extrinsic_register_mobile(const char *ws_url);
void rust_call_extrinsic_free(char *);

long long int rust_init_surface(struct ios_view_obj object);
void rust_render(long long int ptr_app);
void rust_cleanup_app(long long int ptr_app);

#endif /* HeaderRustWrapper_h */
