package com.example.moneymanager.presentation.asset

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.presentation.asset.components.AssetTypeSelector
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditAssetScreen(
    navController: NavController,
    viewModel: AddEditAssetViewModel = hiltViewModel()
) {
    val assetName = viewModel.assetName
    val assetType = viewModel.assetType
    val currentBalance = viewModel.currentBalance
    val assetId = navController.currentBackStackEntry?.arguments?.getInt("assetId") ?: -1
    val isEditMode = assetId != -1
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditAssetViewModel.UiEvent.SaveSuccess -> {
                    navController.popBackStack()
                }
                is AddEditAssetViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    AddEditAssetContent(
        isEditMode = isEditMode,
        name = assetName,
        onNameChange = viewModel::onNameChange,
        selectedType = assetType,
        onTypeChange = viewModel::onTypeChange,
        initialBalance = currentBalance,
        onBalanceChange = viewModel::onBalanceChange,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onSaveClick = { viewModel.onSaveAsset() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAssetContent(
    isEditMode: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    selectedType: String,
    onTypeChange: (String) -> Unit,
    initialBalance: String,
    onBalanceChange: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Wallet" else "Tambah Wallet Baru") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                onValueChange = onNameChange,
                label = { Text("Nama Wallet (misal: BCA, Dompet)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            AssetTypeSelector(
                selectedType = selectedType,
                onTypeSelected = onTypeChange,
            )

            OutlinedTextField(
                value = initialBalance,
                onValueChange = onBalanceChange,
                prefix = { Text("Rp ") },
                label = { Text("Saldo Awal") },
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
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = name.isNotBlank()
            ) {
                Text(if (isEditMode) "Update Wallet" else "Simpan Wallet")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddAssetScreenPreview() {
    MaterialTheme {
        AddEditAssetContent(
            isEditMode = false,
            name = "",
            onNameChange = {},
            selectedType = "CASH",
            onTypeChange = {},
            initialBalance = "",
            onBalanceChange = {},
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditAssetScreenPreview() {
    MaterialTheme {
        AddEditAssetContent(
            isEditMode = true,
            name = "BCA",
            onNameChange = {},
            selectedType = "BANK",
            onTypeChange = {},
            initialBalance = "5.000.000",
            onBalanceChange = {},
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onSaveClick = {}
        )
    }
}