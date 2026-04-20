package com.example.moneymanager.presentation.security

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.moneymanager.common.utils.BiometricHelper

@Composable
fun LockScreen(
    onUnlockSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricHelper = remember { activity?.let { BiometricHelper(it) } }

    LaunchedEffect(Unit) {
        biometricHelper?.showBiometricPrompt(
            title = "Aplikasi Terkunci",
            subtitle = "Gunakan sidik jari atau PIN untuk membuka Money Manager",
            onSuccess = { onUnlockSuccess() },
            onError = {  }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Terkunci",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Money Manager Terkunci",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Verifikasi identitas Anda untuk melanjutkan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                biometricHelper?.showBiometricPrompt(
                    title = "Aplikasi Terkunci",
                    subtitle = "Gunakan sidik jari atau PIN untuk membuka Money Manager",
                    onSuccess = { onUnlockSuccess() },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.height(50.dp)
        ) {
            Text("Buka Kunci", style = MaterialTheme.typography.titleMedium)
        }
    }
}