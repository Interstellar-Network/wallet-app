import SwiftUI
import shared

struct ContentView: View {
	let greet = Greeting().greeting()

	var body: some View {
	    let txhash = RustWrapper().CallExtrinsic(url: "ws://127.0.0.1:9990")!
		Text(txhash)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
