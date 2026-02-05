package com.example.moneymanager.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneymanager.navigation.AppNavigation
import com.example.moneymanager.navigation.Screen

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomNavItem(Screen.Dashboard.route, "Home", Icons.Default.Home)
    data object Wallet : BottomNavItem(Screen.Assets.route, "Aset", Icons.Default.AccountBalanceWallet)
    data object Report : BottomNavItem(Screen.Reports.route, "Laporan", Icons.Default.BarChart)
    data object History : BottomNavItem(Screen.History.route, "Riwayat", Icons.Default.History)
    data object Profile : BottomNavItem(Screen.Profiles.route, "Profil", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Wallet,
            BottomNavItem.Report,
            BottomNavItem.History,
            BottomNavItem.Profile
        )
    }

    val isBottomBarVisible = remember(currentDestination) {
        currentDestination?.hierarchy?.any { destination ->
            bottomNavItems.any { it.route == destination.route }
        } == true
    }

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                BottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems
                )
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            innerPadding = innerPadding
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MaterialTheme {
        BottomNavigationBar(
            navController = rememberNavController(),
            items = listOf(
                BottomNavItem.Home,
                BottomNavItem.Wallet,
                BottomNavItem.Report,
                BottomNavItem.History,
                BottomNavItem.Profile
            )
        )
    }
}