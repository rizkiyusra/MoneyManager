package com.example.moneymanager.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moneymanager.navigation.Screen
import com.example.moneymanager.presentation.profile.components.ProfileHeader
import com.example.moneymanager.presentation.profile.components.ProfileMenuItem

@Composable
fun ProfileScreen(
    navController: NavController
) {
    val userName = "User Money Manager"
    val userStatus = "Member Basic"

    ProfileContent(
        userName = userName,
        userStatus = userStatus,
        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
        onSecurityClick = { /* TODO */ },
        onBackupClick = { /* TODO */ },
        onAboutClick = { /* TODO */ }
    )
}

@Composable
private fun ProfileContent(
    userName: String,
    userStatus: String,
    onNavigateToSettings: () -> Unit,
    onSecurityClick: () -> Unit,
    onBackupClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                userName = userName,
                userEmail = userStatus
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Pengaturan",
                subtitle = "Kategori, Tampilan, Notifikasi",
                onClick = onNavigateToSettings
            )

            ProfileMenuItem(
                icon = Icons.Default.Lock,
                title = "Keamanan",
                subtitle = "PIN & Sidik Jari (Segera Hadir)",
                onClick = onSecurityClick
            )

            ProfileMenuItem(
                icon = Icons.Default.Backup,
                title = "Backup & Restore",
                subtitle = "Simpan & Pulihkan Data (Segera Hadir)",
                onClick = onBackupClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "Tentang Aplikasi",
                subtitle = "Versi 1.0.0 (Beta)",
                onClick = onAboutClick
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileContent(
            userName = "Rizki Maulana",
            userStatus = "Premium Member",
            onNavigateToSettings = {},
            onSecurityClick = {},
            onBackupClick = {},
            onAboutClick = {}
        )
    }
}