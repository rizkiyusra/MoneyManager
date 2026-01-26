package com.example.moneymanager.presentation.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.navigation.Screen
import com.example.moneymanager.presentation.component.getCategoryIcon

@Composable
fun CategoryListScreen(
    navController: NavController,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val state by viewModel.categoriesState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    val onRequestDelete = { category: Category ->
        categoryToDelete = category
        showDeleteDialog = true
    }

    val onConfirmDelete = {
        categoryToDelete?.let {
            viewModel.deleteCategory(it)
        }
        showDeleteDialog = false
        categoryToDelete = null
    }

    CategoryListContent(
        state = state,
        selectedFilter = selectedFilter,
        onBackClick = { navController.popBackStack() },
        onAddClick = {
            navController.navigate(Screen.AddCategory.createRoute(null))
        },
        onFilterSelected = { viewModel.onFilterChanged(it) },
        onEditClick = { category ->
            navController.navigate(Screen.AddCategory.createRoute(category.id))
        },
        onDeleteClick = onRequestDelete
    )

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
                    onClick = onConfirmDelete,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListContent(
    state: Resource<List<Category>>,
    selectedFilter: Boolean?,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onFilterSelected: (Boolean?) -> Unit,
    onEditClick: (Category) -> Unit,
    onDeleteClick: (Category) -> Unit
) {
    val tabs = listOf("Semua", "Pemasukan", "Pengeluaran")
    val selectedTabIndex = when (selectedFilter) {
        null -> 0
        true -> 1
        false -> 2
    }

    Scaffold(
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message ?: "Terjadi kesalahan", color = Color.Red)
                    }
                }
                is Resource.Success -> {
                    val categories = state.data ?: emptyList()
                    if (categories.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada kategori", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(categories) { category ->
                                CategoryItem(
                                    category = category,
                                    onEditClick = { onEditClick(category) },
                                    onDeleteClick = { onDeleteClick(category) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(category.color), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val iconVector = getCategoryIcon(category.icon)
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (category.isIncomeCategory) "Pemasukan" else "Pengeluaran",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (category.isIncomeCategory) Color(0xFF388E3C) else Color(0xFFD32F2F)
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryListScreenPreview() {
    val dummyCategories = listOf(
        Category(
            1,
            "Makan Siang",
            null,
            false,
            android.graphics.Color.BLUE,
            "fastfood",
            isSystemCategory = false,
            isActive = true,
            usageCount = 0,
            createdDate = 0
        ),
        Category(
            2,
            "Gaji",
            null,
            true,
            android.graphics.Color.GREEN,
            "attach_money",
            isSystemCategory = true,
            isActive = true,
            usageCount = 0,
            createdDate = 0
        ),
        Category(
            3,
            "Bensin",
            null,
            false,
            android.graphics.Color.RED,
            "local_gas_station",
            isSystemCategory = false,
            isActive = true,
            usageCount = 0,
            createdDate = 0
        )
    )

    CategoryListContent(
        state = Resource.Success(dummyCategories),
        selectedFilter = null,
        onBackClick = {},
        onAddClick = {},
        onFilterSelected = {},
        onEditClick = {},
        onDeleteClick = {}
    )
}