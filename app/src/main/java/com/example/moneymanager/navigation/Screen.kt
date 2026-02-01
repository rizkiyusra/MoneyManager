package com.example.moneymanager.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Assets : Screen("assets")
    object Transactions : Screen("transactions")
    object Categories : Screen("categories")
    object Profiles: Screen("profile")
    object Settings : Screen("settings")
    object Recurring : Screen("recurring_transactions")
    object Budget : Screen("budget")
    object Reports : Screen("reports")
    object Search : Screen("search")

    object AddCategory : Screen("add_category?categoryId={categoryId}") {
        fun createRoute(categoryId: Int? = null): String {
            return if (categoryId != null) {
                "add_category?categoryId=$categoryId"
            } else {
                "add_category"
            }
        }
    }

    object AddTransaction : Screen("add_transaction?transactionId={transactionId}") {
        fun createRoute(transactionId: Int? = null): String {
            return if (transactionId != null) {
                "add_transaction?transactionId=$transactionId"
            } else {
                "add_transaction"
            }
        }
    }

    object AddAsset : Screen("add_asset?assetId={assetId}") {
        fun createRoute(assetId: Int? = null): String {
            return if (assetId != null) {
                "add_asset?assetId=$assetId"
            } else {
                "add_asset"
            }
        }
    }
}