package com.example.moneymanager.presentation.asset

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.state.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAssetScreen(
    navController: NavController,
    viewModel: AddEditAssetViewModel = hiltViewModel()
) {
    val saveState by viewModel.saveState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("CASH") }

    LaunchedEffect(saveState) {
        when (saveState) {
            is Resource.Success -> {
                Toast.makeText(context, "Wallet Berhasil Dibuat!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                navController.popBackStack()
            }
            is Resource.Error -> {
                Toast.makeText(context, "Error: ${(saveState as Resource.Error).message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Wallet Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Wallet (misal: BCA, Dompet)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Tipe Wallet", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("CASH", "BANK", "EWALLET").forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) }
                    )
                }
            }

            OutlinedTextField(
                value = initialBalance,
                onValueChange = { if (it.all { char -> char.isDigit() }) initialBalance = it },
                label = { Text("Saldo Awal (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Akan tercatat sebagai transaksi 'Saldo Awal'") }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveAsset(name, selectedType, initialBalance.toDoubleOrNull() ?: 0.0)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = name.isNotEmpty()
            ) {
                Text("Simpan Wallet")
            }
        }
    }
}