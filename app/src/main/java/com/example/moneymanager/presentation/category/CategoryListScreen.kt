package com.example.moneymanager.presentation.category

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.navigation.Screen
import com.example.moneymanager.presentation.category.components.CategoryItem
import com.example.moneymanager.presentation.components.ErrorContent
import com.example.moneymanager.presentation.components.LoadingContent

@Composable
fun CategoryListScreen(
    navController: NavController,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val state by viewModel.categoriesState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        actionState?.let { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { snackbarHostState.showSnackbar(it) }
                    viewModel.onActionStateHandled()
                }
                is Resource.Error -> {
                    resource.message?.let { snackbarHostState.showSnackbar("Error: $it") }
                    viewModel.onActionStateHandled()
                }
                is Resource.Loading -> {}
            }
        }
    }

    CategoryListContent(
        state = state,
        selectedFilter = selectedFilter,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onAddClick = {
            navController.navigate(Screen.AddCategory.createRoute(null))
        },
        onFilterSelected = { viewModel.onFilterChanged(it) },
        onEditClick = { category ->
            navController.navigate(Screen.AddCategory.createRoute(category.id))
        },
        onDeleteClick = { viewModel.deleteCategory(it) },
        onRetry = { viewModel.retry() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryListContent(
    state: Resource<List<Category>>,
    selectedFilter: Boolean?,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onFilterSelected: (Boolean?) -> Unit,
    onEditClick: (Category) -> Unit,
    onDeleteClick: (Category) -> Unit,
    onRetry: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    val tabs = listOf("Semua", "Pemasukan", "Pengeluaran")
    val selectedTabIndex = when (selectedFilter) {
        null -> 0
        true -> 1
        false -> 2
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Atur Kategori") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kategori")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            val newFilter = when (index) {
                                0 -> null
                                1 -> true
                                else -> false
                            }
                            onFilterSelected(newFilter)
                        },
                        text = { Text(title) }
                    )
                }
            }

            when (state) {
                is Resource.Loading -> {
                    LoadingContent()
                }
                is Resource.Error -> {
                    ErrorContent(
                        error = state.message ?: "Terjadi kesalahan tidak diketahui",
                        onRetry = onRetry
                    )
                }
                is Resource.Success -> {
                    val categories = state.data ?: emptyList()
                    if (categories.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada kategori", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories, key = { it.id }) { category ->
                                CategoryItem(
                                    category = category,
                                    onEditClick = { onEditClick(category) },
                                    onDeleteClick = {
                                        categoryToDelete = category
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Hapus Kategori?") },
            text = {
                Text("Apakah Anda yakin ingin menghapus '${categoryToDelete?.name}'? Data tidak bisa dikembalikan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        categoryToDelete?.let { onDeleteClick(it) }
                        showDeleteDialog = false
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryListScreenPreview() {
    val dummyCategories = listOf(
        Category(1, "Makan Siang", null, false, Color.Blue.toArgb(), "fastfood"),
        Category(2, "Gaji", null, true, Color.Green.toArgb(), "attach_money"),
        Category(3, "Bensin", null, false, Color.Red.toArgb(), "local_gas_station")
    )

    MaterialTheme {
        CategoryListContent(
            state = Resource.Success(dummyCategories),
            selectedFilter = null,
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onAddClick = {},
            onFilterSelected = {},
            onEditClick = {},
            onDeleteClick = {},
            onRetry = {}
        )
    }
}