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
    
    func CallExtrinsic(url: String) -> String? {
        let c_tx_hash = rust_call_extrinsic(url)
        let tx_hash = String(cString: c_tx_hash!)
        rust_call_extrinsic_free(UnsafeMutablePointer(mutating: c_tx_hash))
        return tx_hash
    }
    
}


