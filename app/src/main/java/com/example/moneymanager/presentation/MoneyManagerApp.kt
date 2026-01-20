package com.example.moneymanager.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.moneymanager.navigation.AppNavGraph
import com.example.moneymanager.presentation.theme.MoneyManagerTheme

@Composable
fun MoneyManagerApp() {
    MoneyManagerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            AppNavGraph(navController = navController)
        }
    }
}