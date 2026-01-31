package com.example.moneymanager.presentation.asset

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
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
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.navigation.Screen
import com.example.moneymanager.presentation.asset.components.AssetItemCard
import com.example.moneymanager.presentation.components.ErrorContent
import com.example.moneymanager.presentation.components.LoadingContent

@Composable
fun AssetListScreen(
    navController: NavController,
    viewModel: AssetListViewModel = hiltViewModel()
) {
    val state by viewModel.assetsState.collectAsState()
    val deleteError by viewModel.deleteState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deleteError) {
        deleteError?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorMessageShown()
        }
    }

    AssetListContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onAddAssetClick = { navController.navigate(Screen.AddAsset.createRoute()) },
        onEditAsset = { asset -> navController.navigate(Screen.AddAsset.createRoute(asset.id)) },
        onDeleteAsset = { asset -> viewModel.deleteAsset(asset) },
        onRetry = { viewModel.getAssets() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListContent(
    state: Resource<List<Asset>>,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onAddAssetClick: () -> Unit,
    onEditAsset: (Asset) -> Unit,
    onDeleteAsset: (Asset) -> Unit,
    onRetry: () -> Unit
) {
    var assetToDelete by remember { mutableStateOf<Asset?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Daftar Aset Saya") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAssetClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Aset")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (state) {
                is Resource.Loading -> {
                    LoadingContent()
                }
                is Resource.Error -> {
                    ErrorContent(
                        error = state.message ?: "Terjadi kesalahan memuat aset",
                        onRetry = onRetry
                    )
                }
                is Resource.Success -> {
                    val assets = state.data ?: emptyList()
                    if (assets.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada aset. Tambah sekarang!", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(assets, key = { it.id }) { asset ->
                                AssetItemCard(
                                    asset = asset,
                                    onEdit = onEditAsset,
                                    onDelete = { assetToDelete = asset }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (assetToDelete != null) {
        AlertDialog(
            onDismissRequest = { assetToDelete = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Hapus Aset?") },
            text = { Text("Aset '${assetToDelete?.name}' dan semua riwayat transaksinya akan dihapus permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        assetToDelete?.let { onDeleteAsset(it) }
                        assetToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { assetToDelete = null }) { Text("Batal") }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AssetListScreenPreview() {
    val dummyAssets = listOf(
        Asset(1, "BCA", "BANK", 5000000.0, "IDR", "Rp"),
        Asset(2, "Dompet","CASH", 150000.0, "IDR", "Rp"),
        Asset(3, "Gopay", "EWALLET", 25000.0, "IDR", "Rp")
    )

    MaterialTheme {
        AssetListContent(
            state = Resource.Success(dummyAssets),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onAddAssetClick = {},
            onEditAsset = {},
            onDeleteAsset = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AssetListScreenErrorPreview() {
    MaterialTheme {
        AssetListContent(
            state = Resource.Error("Koneksi database terputus"),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onAddAssetClick = {},
            onEditAsset = {},
            onDeleteAsset = {},
            onRetry = {}
        )
    }
}