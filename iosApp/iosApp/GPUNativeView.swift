//
//  GPUNativeView.swift
//  iosApp
//
//  Created by Nathan Prat on 04/08/2022.
//  Copyright © 2022 orgName. All rights reserved.
//
//
//  MetalView.swift
//
//  Created by grenlight on 2018/11/23.
//
// https://github.com/jinleili/wgpu-on-app/blob/349c2f5bed7e2233381fc0752fbb220099586a61/iOS/base/MetalView.swift
//

import UIKit
import Foundation
import SwiftUI
import shared

// IMPORTANT: it MUST be a class b/c we pass a pointer to it to Rust
// and "Unmanaged.passRetained" DOES NOT work for Swift struct
final class GPUNativeView: UIView {
    let WS_URL = "ws://127.0.0.1:9944"
    let IPFS_ADDR = "/ip4/127.0.0.1/tcp/5001"
    
    override class var layerClass: AnyClass {
        return CAMetalLayer.self
    }
        
    private func configLayer() {
        guard let layer = self.layer as? CAMetalLayer else {
            return
        }
        layer.pixelFormat = .rgba16Float
        layer.presentsWithTransaction = false
        layer.framebufferOnly = true
        // nativeScale is real physical pixel scale
        // https://tomisacat.xyz/tech/2017/06/17/scale-nativescale-contentsscale.html
        layer.removeAllAnimations()
        self.contentScaleFactor = UIScreen.main.nativeScale
        
        // ?? HDR
        // opt-in EDR
    }
    
    //
    //  ViewController.swift
    //
    //  Created by LiJinlei on 2021/9/10.
    //
    // https://github.com/jinleili/wgpu-on-app/blob/master/iOS/base/ViewController.swift
    
    // var wgpuCanvas: OpaquePointer?
    var ptr_app: Int64?
    
    lazy var displayLink: CADisplayLink = {
        let link = CADisplayLink.init(target: self, selector: #selector(enterFrame))
        return link
    }()
    
    // override func viewDidLoad() {
    override init(frame: CGRect) {
        // TODO swiftui get proper frame? NOTE: even init(frame) -- frame == .zero??
        super.init(frame: UIScreen.main.bounds)
        // super.viewDidLoad()
       
        self.displayLink.add(to: .current, forMode: .default)
        self.displayLink.isPaused = true
        
        // TODO elsewhere? originally in awakeFromNib
        self.configLayer()
        
        // TODO? elsewhere?
        // originally from "override func viewDidAppear(_ animated: Bool)"
        if self.ptr_app == nil {
            // TODO register only if not yet registered
            // TODO use Keychain/Keystore to get the public key(DO NOT override if already exists)
            RustWrapper().ExtrinsicRegisterMobile(ws_url: WS_URL, pub_key: KotlinByteArray(size: 32))
            
            self.ptr_app = RustWrapper().doInitSurface(surface: self, messageRects: KotlinFloatArray(size: 0), pinpadRects: KotlinFloatArray(size: 0), pinpad_nb_cols: 3, pinpad_nb_rows: 4, message_text_color_hex: "", circle_text_color_hex: "", circle_color_hex: "", background_color_hex: "", circuits_package_ptr: 0)
            
            // TODO elsewhere?
            self.displayLink.isPaused = false
        }
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    /*
    // TODO cleanup, MUST call Rust
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        displayLink.isPaused = true
    }
    
     */
    
    @objc func enterFrame() {
        guard let ptr_app = self.ptr_app else {
            return
        }
        // call rust
        RustWrapper().render(rustObj: ptr_app)
    }
     
}


struct GPUNativeViewRepresentable: UIViewRepresentable {
    typealias UIViewType = GPUNativeView
    
    func makeUIView(context: Context) -> GPUNativeView {
        // TODO how to pass frame?
        GPUNativeView()
    }
    
    func updateUIView(_ uiView: GPUNativeView, context: Context) {
        
    }
    
}
