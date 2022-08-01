import SwiftUI
import CoreImage.CIFilterBuiltins

/// - Tag: ContentView
struct ContentView: View {
    var body: some View {
        GPUNativeViewRepresentable().aspectRatio(nil, contentMode: .fill)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
