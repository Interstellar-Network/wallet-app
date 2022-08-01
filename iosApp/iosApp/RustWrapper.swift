//
//  RustWrapper.swift
//  iosApp
//
//  Created by Nathan Prat on 20/05/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

class RustWrapper: RustInterface {
    func ExtrinsicCheckInput(ws_url: String, package_ptr: Int64, inputs: KotlinByteArray){

    }

    func ExtrinsicGarbleAndStripDisplayCircuitsPackage(ws_url: String, tx_message: String) -> String? {
        return nil
    }

    func ExtrinsicRegisterMobile(ws_url: String, pub_key: KotlinByteArray) -> String? {
        let c_tx_hash = rust_extrinsic_register_mobile(ws_url)
        let tx_hash = String(cString: c_tx_hash!)
        rust_call_extrinsic_free(UnsafeMutablePointer(mutating: c_tx_hash))
        return tx_hash
    }

    func GetCircuits(ws_url: String, ipfs_addr: String) -> Int64 {
        return 0
    }

    func GetMessageNbDigitsFromPtr(circuits_package_ptr: Int64) -> Int32 {
        return 0
    }

    func GetTxIdPtrFromPtr(circuits_package_ptr: Int64) -> Int64 {
        return 0
    }

    func cleanup(rustObj: Int64) {
        rust_cleanup_app(rustObj)
    }

    func getMobilePublicKey() -> KotlinByteArray {
        return KotlinByteArray(size: 32)
    }

    /**
            surface: a MetalView
     */
    func doInitSurface(surface: Any?, messageRects: KotlinFloatArray, pinpadRects: KotlinFloatArray, pinpad_nb_cols: Int32, pinpad_nb_rows: Int32, message_text_color_hex: String, circle_text_color_hex: String, circle_color_hex: String, background_color_hex: String, circuits_package_ptr: Int64) -> Int64 {
        let metalview = surface as! GPUNativeView
        
        // let viewPointer = UnsafeMutableRawPointer(metalview)
        let viewPointer = UnsafeMutableRawPointer(Unmanaged.passRetained(metalview).toOpaque())
        // TODO? let metalLayer = UnsafeMutableRawPointer(Unmanaged.passRetained(metalview.layer).toOpaque())

        let viewObj = ios_view_obj(view: viewPointer)
        
        let ptr = rust_init_surface(viewObj)
        return ptr
    }

    func render(rustObj: Int64) {
        rust_render(rustObj)
    }

}
