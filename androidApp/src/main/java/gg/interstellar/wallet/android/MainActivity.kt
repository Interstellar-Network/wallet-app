package gg.interstellar.wallet.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gg.interstellar.wallet.android.ui.SendCurrenciesScreen
import gg.interstellar.wallet.android.ui.TxPinpadScreen
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletApp()
        }
    }

}

@Preview
@Composable
fun WalletApp() {
    InterstellarWalletTheme(darkTheme = isSystemInDarkTheme()) {
        val allScreens = WalletScreen.values().toList()
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = WalletScreen.fromRoute(backstackEntry.value?.destination?.route)

        // TODO cf samples: pass allScreens,currentScreen?
        Scaffold { innerPadding ->
            WalletNavHost(navController, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun WalletNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        // TODO start screen(=landing page) on null
        startDestination = WalletScreen.SendCurrencies.name,
        modifier = modifier
    ) {
        composable(WalletScreen.SendCurrencies.name) {
            SendCurrenciesScreen(
                onClickGo = { navController.navigate(WalletScreen.TxPinpad.name) }
            )
        }
        composable(WalletScreen.TxPinpad.name) {
            // TODO pass params?
            TxPinpadScreen()
        }
    }
}
