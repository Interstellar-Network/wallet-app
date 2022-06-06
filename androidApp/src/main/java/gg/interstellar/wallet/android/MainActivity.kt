package gg.interstellar.wallet.android

import android.os.Bundle
import android.service.autofill.UserData
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import gg.interstellar.wallet.android.ui.SwapCurrenciesScreen
import gg.interstellar.wallet.android.ui.TxPinpadScreen
import gg.interstellar.wallet.android.ui.theme.InterstellarWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletApp()
            // TOREMOVE
//            InterstellarWalletTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//                    InterstellarTxScreen()
//                }
//            }
        }
    }
}

@Preview
@Composable
fun WalletApp() {
    InterstellarWalletTheme(
        darkTheme = true
        //To test dark mode
    ) {
        val allScreens = WalletScreen.values().toList()
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = WalletScreen.fromRoute(backstackEntry.value?.destination?.route)

        // TODO?
        Scaffold(
//            topBar = {
//                TabRow(
//                    allScreens = allScreens,
//                    onTabSelected = { screen ->
//                        navController.navigate(screen.name)
//                    },
//                    currentScreen = currentScreen
//                )
//            }
        ) { innerPadding ->
            WalletNavHost(navController, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun WalletNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        // TODO start screen(=landing page) on null
        //startDestination = WalletScreen.SwapCurrencies.name,

        startDestination = WalletScreen.TxPinpad.name,
        modifier = modifier
    ) {
        composable(WalletScreen.SwapCurrencies.name) {
            SwapCurrenciesScreen(
                onClickGo = {navController.navigate(WalletScreen.TxPinpad.name)}
                // TODO?
//                onClickSeeAllAccounts = { navController.navigate(Accounts.name) },
//                onClickSeeAllBills = { navController.navigate(Bills.name) },
//                onAccountClick = { name ->
//                    navigateToSingleAccount(navController, name)
//                },
            )
        }
        composable(WalletScreen.TxPinpad.name) {
            // TODO pass params?
            TxPinpadScreen()
//            { name ->
//                // TODO?
//                navigateToSingleAccount(navController = navController, accountName = name)
//            }
        }
        // TODO?
//        composable(Bills.name) {
//            BillsBody(bills = UserData.bills)
//        }
        //TODO?
//        val accountsName = Accounts.name
//        composable(
//            route = "$accountsName/{name}",
//            arguments = listOf(
//                navArgument("name") {
//                    type = NavType.StringType
//                }
//            ),
//            deepLinks = listOf(
//                navDeepLink {
//                    uriPattern = "interstellarwallet://$accountsName/{name}"
//                }
//            ),
//        ) { entry ->
//            val accountName = entry.arguments?.getString("name")
//            // TODO?
////            val account = UserData.getAccount(accountName)
////            SingleAccountBody(account = account)
//        }
    }
}

// TODO?
//private fun navigateToSingleAccount(navController: NavHostController, accountName: String) {
//    navController.navigate("${Accounts.name}/$accountName")
//}


//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    InterstellarWalletTheme {
//        InterstellarTxScreen()
//    }
//}