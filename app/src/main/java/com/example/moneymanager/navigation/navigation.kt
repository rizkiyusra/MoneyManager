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
import com.example.moneymanager.presentation.asset.AddEditAssetScreen
import com.example.moneymanager.presentation.asset.AssetListScreen
import com.example.moneymanager.presentation.dashboard.DashboardScreen
import com.example.moneymanager.presentation.setting.SettingsScreen
import com.example.moneymanager.presentation.transaction.AddTransactionScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddTransaction = { transactionId ->
                    navController.navigate(Screen.AddTransaction.createRoute(transactionId))
                },
                onNavigateToAssets = {
                    navController.navigate(Screen.Assets.route)
                },
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transactions.route)
                },
                onNavigateToAddWallet = {
                    navController.navigate(Screen.AddAsset.route)
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
            AddTransactionScreen(navController = navController)
        }

        composable(
            route = Screen.AddAsset.route,
            arguments = listOf(
                navArgument("assetId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AddEditAssetScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.Assets.route) {
            AssetListScreen(navController = navController)
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