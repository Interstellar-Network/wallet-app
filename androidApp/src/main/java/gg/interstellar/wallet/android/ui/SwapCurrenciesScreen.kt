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

@Preview
@Composable
fun SwapCurrenciesScreen(onClickGo: () -> Unit = {},) {
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
        Text("SWAP", modifier = Modifier.align(Alignment.Center))
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
    IconButton(onClick = onClickGo,
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = "convert currency",tint = Color.Blue)
    }
}