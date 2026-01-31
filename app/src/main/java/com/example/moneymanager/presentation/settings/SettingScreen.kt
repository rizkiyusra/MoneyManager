package com.example.moneymanager.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.navigation.Screen
import com.example.moneymanager.presentation.settings.components.SettingsHeader
import com.example.moneymanager.presentation.settings.components.SettingsItem

@Composable
fun SettingsScreen(navController: NavController) {
    SettingsContent(
        onBackClick = { navController.popBackStack() },
        onCategoryClick = { navController.navigate(Screen.Categories.route) },
        onRecurringClick = { navController.navigate(Screen.Recurring.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    onBackClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onRecurringClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingsHeader(title = "Data Master")

            SettingsItem(
                title = "Atur Kategori",
                icon = Icons.Default.Category,
                onClick = onCategoryClick
            )

            SettingsItem(
                title = "Jadwal Otomatis (Recurring)",
                icon = Icons.Default.Repeat,
                onClick = onRecurringClick
            )

            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsContent(
            onBackClick = {},
            onCategoryClick = {},
            onRecurringClick = {}
        )
    }
}