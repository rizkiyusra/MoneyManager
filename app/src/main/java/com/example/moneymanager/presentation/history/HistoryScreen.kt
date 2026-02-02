package com.example.moneymanager.presentation.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionFilter
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.presentation.components.TransactionItem
import com.example.moneymanager.presentation.history.components.HistoryFilterSheet

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onNavigateToEdit: (Int) -> Unit
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val assets by viewModel.assets.collectAsStateWithLifecycle()

    HistoryContent(
        transactions = transactions,
        filterState = filterState,
        categories = categories,
        assets = assets,
        onNavigateToEdit = onNavigateToEdit,
        onFilterAction = { action ->
            when (action) {
                is HistoryFilterAction.Apply -> {
                    viewModel.onTypeChanged(action.type)
                    viewModel.onDateRangeChanged(action.start, action.end)
                    viewModel.onCategoryChanged(action.categoryId)
                    viewModel.onAssetChanged(action.assetId)
                }
                HistoryFilterAction.Reset -> viewModel.onResetFilter()
            }
        }
    )
}

sealed interface HistoryFilterAction {
    data class Apply(val type: TransactionType?, val start: Long?, val end: Long?, val categoryId: Int?, val assetId: Int?) : HistoryFilterAction
    object Reset : HistoryFilterAction
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryContent(
    transactions: List<Transaction>,
    filterState: TransactionFilter,
    categories: List<Category>,
    assets: List<Asset>,
    onNavigateToEdit: (Int) -> Unit,
    onFilterAction: (HistoryFilterAction) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Transaksi") },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        BadgedBox(
                            badge = {
                                val isFilterActive = filterState.startDate != null ||
                                        filterState.categoryId != null ||
                                        filterState.transactionType != null ||
                                        filterState.assetId != null
                                if (isFilterActive) {
                                    Badge()
                                }
                            }
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada transaksi", color = Color.Gray)
            }
        } else {
            TransactionListContent(
                transactions = transactions,
                onItemClick = { onNavigateToEdit(it.id) },
                modifier = Modifier.padding(padding)
            )
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState
            ) {
                HistoryFilterSheet(
                    currentFilter = filterState,
                    categories = categories,
                    assets = assets,
                    onApplyFilter = { type, start, end, catId, assetId ->
                        onFilterAction(HistoryFilterAction.Apply(type, start, end, catId, assetId))
                        showFilterSheet = false
                    },
                    onResetFilter = {
                        onFilterAction(HistoryFilterAction.Reset)
                        showFilterSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionListContent(
    transactions: List<Transaction>,
    onItemClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTransactions = remember(transactions) {
        transactions.groupBy { it.date.toReadableDate() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        groupedTransactions.forEach { (dateHeader, transactionList) ->
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dateHeader,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            items(transactionList) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onItemClick(transaction) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryContentPreview() {
    MaterialTheme {
        HistoryContent(
            transactions = listOf(
                Transaction(
                    id = 1, amount = 50000.0, categoryName = "Makan",
                    categoryColor = android.graphics.Color.RED, type = TransactionType.EXPENSE,
                    date = System.currentTimeMillis(), title = "Nasi Goreng",
                    fromAssetId = 1, toAssetId = null, categoryId = 1,
                    currency = "IDR", convertedAmountIDR = 50000.0, exchangeRate = 1.0,
                    note = "", location = null, receiptImagePath = null,
                    createdDate = System.currentTimeMillis(), categoryIcon = "restaurant", fromAssetName = "Tunai"
                ),
                Transaction(
                    id = 2, amount = 1000000.0, categoryName = "Gaji",
                    categoryColor = android.graphics.Color.GREEN, type = TransactionType.INCOME,
                    date = System.currentTimeMillis(), title = "Gaji Bulanan",
                    fromAssetId = 1, toAssetId = null, categoryId = 2,
                    currency = "IDR", convertedAmountIDR = 1000000.0, exchangeRate = 1.0,
                    note = "", location = null, receiptImagePath = null,
                    createdDate = System.currentTimeMillis(), categoryIcon = "work", fromAssetName = "Bank"
                )
            ),
            filterState = TransactionFilter(),
            categories = emptyList(),
            assets = emptyList(),
            onNavigateToEdit = {},
            onFilterAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryContentEmptyPreview() {
    MaterialTheme {
        HistoryContent(
            transactions = emptyList(),
            filterState = TransactionFilter(),
            categories = emptyList(),
            assets = emptyList(),
            onNavigateToEdit = {},
            onFilterAction = {}
        )
    }
}