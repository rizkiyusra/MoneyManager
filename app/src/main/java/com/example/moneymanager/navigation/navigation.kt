package com.example.moneymanager.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moneymanager.R
import com.example.moneymanager.presentation.dashboard.DashboardScreen
import com.example.moneymanager.presentation.transaction.AddTransactionScreen
import com.example.moneymanager.presentation.setting.SettingsScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Assets : Screen("assets")
    object Transactions : Screen("transactions")
    object Settings : Screen("settings")
    object Budget : Screen("budget")
    object Reports : Screen("reports")

    object AddTransaction : Screen("add_transaction?transactionId={transactionId}") {
        fun createRoute(transactionId: Int? = null): String {
            return if (transactionId != null) {
                "add_transaction?transactionId=$transactionId"
            } else {
                "add_transaction"
            }
        }
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.createRoute(null))
                },
                onNavigateToAssets = {
                    navController.navigate(Screen.Assets.route)
                },
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transactions.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AddTransactionScreen(
                navController = navController
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.Assets.route) {
            Text(text = "Halaman Aset (Coming Soon)")
        }

        composable(Screen.Transactions.route) {
            Text(text = "Halaman List Transaksi (Coming Soon)")
        }

        composable(Screen.Budget.route) {
            Text(text = stringResource(R.string.coming_soon))
        }
        composable(Screen.Reports.route) {
            Text(text = stringResource(R.string.coming_soon))
        }
    }
}