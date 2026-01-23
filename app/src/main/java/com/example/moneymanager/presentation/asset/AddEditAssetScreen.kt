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
    val isLoading = saveState is Resource.Loading
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("CASH") }

    var isButtonLocked by remember { mutableStateOf(false) }
    val assetId = navController.currentBackStackEntry?.arguments?.getInt("assetId") ?: -1
    val assetDetailState by viewModel.assetDetailState.collectAsState()
    val isEditMode = assetId != -1

    LaunchedEffect(saveState) {
        when (saveState) {
            is Resource.Success -> {
                val message = if (isEditMode) "Wallet Berhasil Diupdate!" else "Wallet Berhasil Dibuat!"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                isButtonLocked = false
                navController.popBackStack()
            }
            is Resource.Error -> {
                Toast.makeText(context, "Error: ${(saveState as Resource.Error).message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                isButtonLocked = false
            }
            else -> Unit
        }
    }

    LaunchedEffect(key1 = assetId) {
        if (isEditMode) {
             viewModel.getAssetById(assetId)
        }
    }
    LaunchedEffect(assetDetailState) {
         if (assetDetailState is Resource.Success) {
             val asset = (assetDetailState as Resource.Success).data
             if (asset != null) {
                 name = asset.name
                 selectedType = asset.type
                 initialBalance = asset.balance.toInt().toString()
             }
         }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Wallet" else "Tambah Wallet Baru") },
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
                enabled = !isEditMode,
                supportingText = {
                    if (isEditMode) Text("Saldo tidak bisa diedit disini")
                    else Text("Akan tercatat sebagai transaksi 'Saldo Awal'")
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isButtonLocked = true
                    if (isEditMode) {
                        viewModel.updateAsset(assetId, name, selectedType)
                    } else {
                        val balance = initialBalance.toDoubleOrNull() ?: 0.0
                        viewModel.saveAsset(name, selectedType, balance)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = name.isNotEmpty() && !isLoading && !isButtonLocked
            ) {
                if (isLoading || isButtonLocked) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan Wallet")
                }
            }
        }
    }
}