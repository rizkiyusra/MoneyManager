package com.example.moneymanager.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moneymanager.presentation.main.MainScreen
import com.example.moneymanager.presentation.theme.MoneyManagerTheme

@Composable
fun MoneyManagerApp() {
    MoneyManagerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}