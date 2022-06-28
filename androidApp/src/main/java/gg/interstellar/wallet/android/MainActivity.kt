package gg.interstellar.wallet.android

import AddressesBody
import SingleAddressBody
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import gg.interstellar.wallet.android.data.Currency
import gg.interstellar.wallet.android.data.UserData
import gg.interstellar.wallet.android.data.UserData.currencies
import gg.interstellar.wallet.android.ui.sendCurrencies.SendCurrenciesBody
import gg.interstellar.wallet.android.ui.TxPinpadScreen
// TEST import gg.interstellar.wallet.android.ui.accounts.AccountsBody
import gg.interstellar.wallet.android.ui.market.CurrenciesBody
import gg.interstellar.wallet.android.ui.market.SingleCurrencyBody
import gg.interstellar.wallet.android.ui.sendCurrencies.SingleCurrencyStatement
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
        var currentScreen = WalletScreen.fromRoute(backstackEntry.value?.destination?.route)

        Scaffold(
            bottomBar  = {
                WalletTabRow(
                    allScreens = allScreens,
                    onTabSelected = { screen ->
                        navController.navigate(screen.name)
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            WalletNavHost(navController, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun WalletNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    val notUsed = remember { mutableStateOf("notUsed") }//TODO makecleaner

    NavHost(
        navController = navController,
        // TODO start screen(=landing page) on null
        startDestination = WalletScreen.SendCurrencies.name,
        modifier = modifier
    ) {
        composable(WalletScreen.SendCurrencies.name) {
            SendCurrenciesBody(currencies = UserData.currencies, addresses = UserData.addresses,
                onClickGo = { navController.navigate(WalletScreen.TxPinpad.name) },
            )
        }

        composable(WalletScreen.Market.name) {
            CurrenciesBody(currencies = UserData.currencies,notUsed,notUsed) { name ->
                navigateToSingleCurrency(navController = navController, currencyName = name)
            }
        }

        composable(WalletScreen.Addresses.name) {
            AddressesBody(addresses = UserData.addresses,notUsed) { name ->
                navigateToSingleAddress(navController = navController, addressName = name)
            }
        }


        composable(WalletScreen.TxPinpad.name) {
            // TODO pass params?
            // TODO Hide the status bar -Themes.xml in vaalues not a viable option
            //TODO use ? https://developer.android.com/training/system-ui/status
            // https://developer.android.com/reference/android/view/WindowInsetsController
            TxPinpadScreen()
        }
        val currenciesName = WalletScreen.Market.name

        composable(
            route = "$currenciesName/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "wallet://$currenciesName/{name}"
                }
            ),
        ) { entry ->
            val currenciesName = entry.arguments?.getString("name")
            val currency = UserData.getCurrency(currenciesName)
            SingleCurrencyBody(currency = currency,notUsed,notUsed)
        }

        val  addressesName = WalletScreen.Addresses.name
        composable(
            route = "$addressesName/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "wallet://$addressesName/{name}"
                }
            ),
        ) { entry ->
            val addressesName = entry.arguments?.getString("name")
            val address = UserData.getAddress(addressesName)
            SingleAddressBody(address = address,notUsed)
        }

    }
}

private fun navigateToSingleCurrency(navController: NavHostController, currencyName: String) {
    navController.navigate("${WalletScreen.Market.name}/$currencyName")
}

private fun navigateToSingleAddress(navController: NavHostController, addressName: String) {
        navController.navigate("${WalletScreen.Addresses.name}/$addressName")
}