package com.example.moneymanager.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moneymanager.presentation.asset.AddEditAssetScreen
import com.example.moneymanager.presentation.asset.AssetListScreen
import com.example.moneymanager.presentation.dashboard.DashboardScreen
import com.example.moneymanager.presentation.setting.SettingsScreen
import com.example.moneymanager.presentation.transaction.AddTransactionScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddTransaction = { transactionId ->
                    if (transactionId != null) {
                        navController.navigate("add_transaction/$transactionId")
                    } else {
                        navController.navigate(Screen.AddTransaction.route)
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Assets.route) {
            AssetListScreen(navController = navController)
        }

        composable(Screen.AddAsset.route) {
            AddEditAssetScreen(navController = navController)
        }

        composable(
            route = "add_asset/{assetId}",
            arguments = listOf(
                navArgument("assetId") { type = NavType.IntType }
            )
        ) {
            AddEditAssetScreen(navController = navController)
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(navController = navController)
        }

        composable(
            route = "add_transaction/{transactionId}",
            arguments = listOf(
                navArgument("transactionId") { type = NavType.IntType; defaultValue = -1 }
            )
        ) {
            AddTransactionScreen(navController = navController)
        }

        composable("history") {
            PlaceholderScreen(title = "History")
        }

        composable("profile") {
            PlaceholderScreen(title = "Profile")
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(title: String) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Fitur $title Segera Hadir",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}