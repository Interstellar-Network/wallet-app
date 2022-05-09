package gg.interstellar.wallet.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import gg.interstellar.wallet.android.WalletScreen

//@Composable
//fun WalletTabRow(
//    allScreens: List<WalletScreen>,
//    onTabSelected: (WalletScreen) -> Unit,
//    currentScreen: WalletScreen
//) {
//    Surface(
//        Modifier
//            .height(TabHeight)
//            .fillMaxWidth()
//    ) {
//        Row(Modifier.selectableGroup()) {
//            allScreens.forEach { screen ->
//                // TODO sample RallyTab? https://github.com/googlecodelabs/android-compose-codelabs/blob/end/NavigationCodelab/app/src/main/java/com/example/compose/rally/ui/components/TabRow.kt
//                Tab(
//                    text = screen.name,
//                    icon = screen.icon,
//                    onSelected = { onTabSelected(screen) },
//                    selected = currentScreen == screen
//                )
//            }
//        }
//    }
//}
