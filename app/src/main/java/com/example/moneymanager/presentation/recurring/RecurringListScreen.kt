package com.example.moneymanager.presentation.recurring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.RecurringTransaction
import com.example.moneymanager.presentation.components.LoadingContent
import com.example.moneymanager.presentation.recurring.components.AddRecurringDialog
import com.example.moneymanager.presentation.recurring.components.RecurringItemCard

@Composable
fun RecurringListScreen(
    navController: NavController,
    viewModel: RecurringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        actionState?.let { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { snackbarHostState.showSnackbar(it) }
                    viewModel.onActionStateHandled()
                }
                is Resource.Error -> {
                    result.message?.let { snackbarHostState.showSnackbar(it) }
                    viewModel.onActionStateHandled()
                }
                is Resource.Loading -> { /* Loading Overlay jika perlu */ }
            }
        }
    }

    RecurringListContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onDeleteClick = { viewModel.deleteRecurring(it) },
        onSaveClick = { amount, note, isIncome, catId, assetId, freq ->
            viewModel.saveRecurringTransaction(amount, note, isIncome, catId, assetId, freq)
        },
        onTypeChanged = { isIncome ->
            viewModel.onTypeChanged(isIncome)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringListContent(
    uiState: RecurringUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onDeleteClick: (RecurringTransaction) -> Unit,
    onSaveClick: (Double, String, Boolean, Int, Int, String) -> Unit,
    onTypeChanged: (Boolean) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<RecurringTransaction?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Rutin (Otomatis)") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Jadwal")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                LoadingContent()
            } else if (uiState.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada jadwal transaksi otomatis.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.transactions) { item ->
                        RecurringItemCard(
                            item = item,
                            onDelete = { itemToDelete = item }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddRecurringDialog(
                categories = uiState.categories,
                assets = uiState.assets,
                onDismiss = { showAddDialog = false },
                onSave = { amount, note, isIncome, catId, assetId, freq ->
                    onSaveClick(amount, note, isIncome, catId, assetId, freq)
                    showAddDialog = false
                },
                onTypeChanged = onTypeChanged
            )
        }

        if (itemToDelete != null) {
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                title = { Text(text = "Hapus Jadwal?") },
                text = {
                    Text("Jadwal '${itemToDelete?.note}' akan dihentikan.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            itemToDelete?.let { onDeleteClick(it) }
                            itemToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecurringListScreenPreview() {
    val dummyTransactions = listOf(
        RecurringTransaction(
            id = 1, amount = 150000.0, note = "WiFi", isIncome = false,
            categoryId = 1, assetId = 1, frequency = "MONTHLY",
            nextRunDate = System.currentTimeMillis(), createdDate = System.currentTimeMillis()
        ),
        RecurringTransaction(
            id = 2, amount = 5000000.0, note = "Gaji", isIncome = true,
            categoryId = 2, assetId = 1, frequency = "MONTHLY",
            nextRunDate = System.currentTimeMillis(), createdDate = System.currentTimeMillis()
        )
    )

    MaterialTheme {
        RecurringListContent(
            uiState = RecurringUiState(
                isLoading = false,
                transactions = dummyTransactions
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onDeleteClick = {},
            onSaveClick = { _, _, _, _, _, _ -> },
            onTypeChanged = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecurringListLoadingPreview() {
    MaterialTheme {
        RecurringListContent(
            uiState = RecurringUiState(isLoading = true),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onDeleteClick = {},
            onSaveClick = { _, _, _, _, _, _ -> },
            onTypeChanged = {}
        )
    }
}