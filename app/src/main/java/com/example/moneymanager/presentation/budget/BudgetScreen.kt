package com.example.moneymanager.presentation.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Budget
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.presentation.budget.components.AddBudgetDialog
import com.example.moneymanager.presentation.budget.components.BudgetItemCard
import java.text.DateFormatSymbols

@Composable
fun BudgetScreen(
    navController: NavController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val budgetList by viewModel.budgetList.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()
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

    BudgetContent(
        budgetList = budgetList,
        categories = categories,
        currentMonth = currentMonth,
        currentYear = currentYear,
        snackbarHostState = snackbarHostState,
        onNavigateBack = { navController.popBackStack() },
        onPreviousMonth = { viewModel.changeMonth(-1) },
        onNextMonth = { viewModel.changeMonth(1) },
        onAddBudget = { catId, name, limit -> viewModel.addBudget(catId, name, limit) },
        onDeleteBudget = { budget -> viewModel.deleteBudget(budget) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetContent(
    budgetList: List<Budget>,
    categories: List<Category>,
    currentMonth: Int,
    currentYear: Int,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddBudget: (Int, String, Double) -> Unit,
    onDeleteBudget: (Budget) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var budgetToDelete by remember { mutableStateOf<Budget?>(null) }
    val monthName = remember(currentMonth) { DateFormatSymbols().months[currentMonth] }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Anggaran Bulanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Budget")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev")
                }
                Text(
                    text = "$monthName $currentYear",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }

            if (budgetList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada anggaran bulan ini.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = budgetList, key = { it.id }) { budget ->
                        BudgetItemCard(
                            budget = budget,
                            onDelete = { budgetToDelete = budget }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddBudgetDialog(
                categories = categories,
                onDismiss = { showAddDialog = false },
                onSave = { catId, name, limit ->
                    onAddBudget(catId, name, limit)
                    showAddDialog = false
                }
            )
        }

        if (budgetToDelete != null) {
            AlertDialog(
                onDismissRequest = { budgetToDelete = null },
                title = { Text("Hapus Anggaran?") },
                text = { Text("Hapus '${budgetToDelete?.name}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            budgetToDelete?.let { onDeleteBudget(it) }
                            budgetToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Hapus") }
                },
                dismissButton = {
                    TextButton(onClick = { budgetToDelete = null }) { Text("Batal") }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetScreenPreview() {
    val dummyCategories = listOf(
        Category(1, "Makanan", "Desc", false, android.graphics.Color.RED, "fastfood"),
        Category(2, "Transport", "Desc", false, android.graphics.Color.BLUE, "directions_car")
    )
    val dummyBudgets = listOf(
        Budget(1, 1, "Makanan", 1000000.0, "Monthly", 0, 2024, 500000.0),
        Budget(2, 2, "Bensin", 300000.0, "Monthly", 0, 2024, 280000.0)
    )

    MaterialTheme {
        BudgetContent(
            budgetList = dummyBudgets,
            categories = dummyCategories,
            currentMonth = 0,
            currentYear = 2024,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onAddBudget = { _, _, _ -> },
            onDeleteBudget = {}
        )
    }
}