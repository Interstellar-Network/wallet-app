package gg.interstellar.wallet.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import io.emeraldpay.polkaj.api.PolkadotApi
import io.emeraldpay.polkaj.api.StandardCommands
import io.emeraldpay.polkaj.apihttp.JavaHttpAdapter
import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.scale.ScaleExtract
import io.emeraldpay.polkaj.scaletypes.MetadataReader
import io.emeraldpay.polkaj.tx.ExtrinsicContext
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.scaletypes.Metadata

@Preview
@Composable
fun SwapCurrenciesScreen(onClickGo: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SwapButtonTop(RoundedCornerShape(33))

        FromToCurrenciesMiddle()

        GoButtonBottom(onClickGo)
    }
}

// First button, at the top
@Composable
private fun SwapButtonTop(shape: Shape){
    Box(
        modifier = Modifier
            .fillMaxWidth(50f)
            .clip(shape)
            .background(Color.Magenta)
    ) {
//        val greeting = Greeting().greeting()
        val msg: ByteArray = byteArrayOf()
        val rdr = ScaleCodecReader(msg)

        /////////////////////////////////////

//        val client = PolkadotApi.newBuilder().rpcCallAdapter(
//            JavaHttpAdapter.newBuilder()
//                .connectTo("wss://cc3-5.kusama.network")
//                .build())
        val adapter = JavaHttpAdapter.newBuilder().connectTo("ws://127.0.0.1:9944").build()
        val client = PolkadotApi.newBuilder().rpcCallAdapter(adapter).build()

        // Build a context for the execution
        val alice = Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY")
        val context: ExtrinsicContext = ExtrinsicContext.newAutoBuilder(alice, client).get().build()

        // Current runtime meta
        val metadata: Metadata = client.execute(
            StandardCommands.getInstance().stateMetadata()
        )
            .thenApply(ScaleExtract.fromBytesData(MetadataReader()))
            .get()

        //////////////////////////////////////


        Text("SWAP $rdr", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun FromToCurrenciesMiddle(){
    var fromTextState by remember { mutableStateOf("From") }
    var toTextState by remember { mutableStateOf("To") }
    BasicTextField(value = fromTextState, onValueChange = {
        fromTextState = it
    })
    BasicTextField(value = toTextState, onValueChange = {
        toTextState = it
    })

    Text("USD")
}

@Composable
private fun GoButtonBottom(onClickGo: () -> Unit){
    IconButton(
        onClick = onClickGo,
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = "convert currency",tint = Color.Blue)
    }
}