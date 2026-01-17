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
import com.example.moneymanager.presentation.ui.screen.DashboardScreen
import com.example.moneymanager.presentation.ui.screen.SettingsScreen

// Type-safe navigation with sealed class
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")

    object Assets : Screen("assets")

    object AddAsset : Screen("add_asset/{assetId}") {
        fun createRoute(assetId: Int = -1) = "add_asset/$assetId"
    }

    object Transactions : Screen("transactions")

    object AddTransaction : Screen("add_transaction?assetId={assetId}") {
        fun createRoute(assetId: Int? = null) = if (assetId != null) {
            "add_transaction?assetId=$assetId"
        } else {
            "add_transaction"
        }
    }

    object Settings : Screen("settings")
    object Budget : Screen("budget")
    object Reports : Screen("reports")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAssets = { navController.navigate(Screen.Assets.route) },
                onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Assets.route) {
            Text(text = stringResource(R.string.coming_soon))
        }

        composable(
            route = Screen.AddAsset.route,
            arguments = listOf(
                navArgument("assetId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getInt("assetId") ?: -1
            Text(text = "Add Asset Screen - AssetId: $assetId")
        }

        composable(Screen.Transactions.route) {
            Text(text = stringResource(R.string.coming_soon))
        }

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument("assetId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getInt("assetId") ?: -1
            Text(text = "Add Transaction Screen - AssetId: $assetId")
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.Budget.route) {
            Text(text = stringResource(R.string.coming_soon))
        }

        composable(Screen.Reports.route) {
            Text(text = stringResource(R.string.coming_soon))
        }
    }
}