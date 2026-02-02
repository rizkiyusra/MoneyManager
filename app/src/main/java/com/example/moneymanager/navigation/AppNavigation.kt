package com.example.moneymanager.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moneymanager.presentation.asset.AddEditAssetScreen
import com.example.moneymanager.presentation.asset.AssetListScreen
import com.example.moneymanager.presentation.budget.BudgetScreen
import com.example.moneymanager.presentation.category.AddEditCategoryScreen
import com.example.moneymanager.presentation.category.CategoryListScreen
import com.example.moneymanager.presentation.dashboard.DashboardScreen
import com.example.moneymanager.presentation.history.HistoryScreen
import com.example.moneymanager.presentation.profile.ProfileScreen
import com.example.moneymanager.presentation.recurring.RecurringListScreen
import com.example.moneymanager.presentation.search.SearchScreen
import com.example.moneymanager.presentation.settings.SettingsScreen
import com.example.moneymanager.presentation.transaction.AddEditTransactionScreen

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
                    navController.navigate(Screen.AddTransaction.createRoute(transactionId))
                },
                onNavigateToBudget = {
                    navController.navigate(Screen.Budget.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                navController = navController,
                onNavigateToEdit = { transactionId ->
                    navController.navigate(
                        Screen.AddTransaction.createRoute(transactionId)
                    )
                }
            )
        }

        composable(Screen.Budget.route) {
            BudgetScreen(navController = navController)
        }

        composable(Screen.Assets.route) {
            AssetListScreen(navController = navController)
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

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            AddEditTransactionScreen(navController = navController)
        }

        composable(Screen.Categories.route) {
            CategoryListScreen(navController = navController)
        }

        composable(
            route = Screen.AddCategory.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AddEditCategoryScreen(navController = navController)
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToEdit = { transactionId ->
                    navController.navigate(Screen.AddTransaction.createRoute(transactionId))
                }
            )
        }

        composable(Screen.Profiles.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable (Screen.Recurring.route) {
            RecurringListScreen(navController = navController)
        }
    }
}