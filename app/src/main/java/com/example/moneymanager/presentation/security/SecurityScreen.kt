package com.example.moneymanager.presentation.security

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.utils.BiometricHelper

@Composable
fun SecurityScreen(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isLockEnabled by viewModel.isAppLockEnabled.collectAsState()

    val activity = context as? FragmentActivity
    val biometricHelper = remember { activity?.let { BiometricHelper(it) } }

    SecurityContent(
        isLockEnabled = isLockEnabled,
        onBackClick = { navController.popBackStack() },
        onToggleLock = { newState ->
            if (biometricHelper == null) {
                Toast.makeText(context, "Aktivitas tidak valid untuk biometrik", Toast.LENGTH_SHORT).show()
                return@SecurityContent
            }

            biometricHelper.showBiometricPrompt(
                title = if (newState) "Aktifkan Kunci Aplikasi" else "Matikan Kunci Aplikasi",
                subtitle = "Verifikasi identitas Anda",
                onSuccess = {
                    viewModel.setAppLock(newState)
                    Toast.makeText(
                        context,
                        if (newState) "Kunci Aplikasi Aktif" else "Kunci Aplikasi Dimatikan",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onError = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityContent(
    modifier: Modifier = Modifier,
    isLockEnabled: Boolean,
    onBackClick: () -> Unit,
    onToggleLock: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keamanan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Kunci Aplikasi",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Gunakan Sidik Jari / PIN") },
                supportingContent = { Text("Kunci aplikasi Money Manager saat dibuka") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Ikon Sidik Jari",
                        tint = if (isLockEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    Switch(
                        checked = isLockEnabled,
                        onCheckedChange = { newState -> onToggleLock(newState) }
                    )
                }
            )

            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecurityScreenPreview() {
    MaterialTheme {
        SecurityContent(
            isLockEnabled = true,
            onBackClick = {},
            onToggleLock = {}
        )
    }
}