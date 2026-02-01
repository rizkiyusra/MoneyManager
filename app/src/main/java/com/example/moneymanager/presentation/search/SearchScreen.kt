package com.example.moneymanager.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.presentation.components.TransactionItem
import com.example.moneymanager.presentation.search.components.SearchEmptyState
import com.example.moneymanager.presentation.search.components.SearchTopBar

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToEdit: (Int) -> Unit
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()

    SearchContent(
        query = query,
        results = results,
        onQueryChange = viewModel::onQueryChange,
        onBackClick = { navController.popBackStack() },
        onClearClick = { viewModel.onQueryChange("") },
        onItemClick = { transaction ->
            onNavigateToEdit(transaction.id)
        }
    )
}

@Composable
fun SearchContent(
    query: String,
    results: List<Transaction>,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onItemClick: (Transaction) -> Unit
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChange = onQueryChange,
                onBackClick = onBackClick,
                onClearClick = onClearClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (query.isBlank()) {
                SearchEmptyState(
                    message = "Ketik judul atau catatan transaksi untuk mencari...",
                    icon = Icons.Default.Search
                )
            } else if (results.isEmpty()) {
                SearchEmptyState(
                    message = "Tidak ditemukan transaksi untuk \"$query\"",
                    icon = Icons.Default.SentimentDissatisfied
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "Ditemukan ${results.size} transaksi",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(results) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onItemClick(transaction) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "1. Empty State")
@Composable
private fun PreviewSearchEmpty() {
    MaterialTheme {
        SearchContent(
            query = "",
            results = emptyList(),
            onQueryChange = {}, onBackClick = {}, onClearClick = {}, onItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "2. No Results")
@Composable
private fun PreviewSearchNoResults() {
    MaterialTheme {
        SearchContent(
            query = "Mobil Mewah",
            results = emptyList(),
            onQueryChange = {}, onBackClick = {}, onClearClick = {}, onItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "3. With Results")
@Composable
private fun PreviewSearchResults() {
    val dummyData = listOf(
        Transaction(
            id = 1, amount = 50000.0, note = "Beli Bensin", title = "Transport",
            type = TransactionType.EXPENSE, date = System.currentTimeMillis(),
            categoryName = "Transport", categoryIcon = "local_gas_station", categoryColor = Color.Blue.toArgb(),
            fromAssetId = 1, fromAssetName = "Dompet", categoryId = 1,
            currency = "IDR", convertedAmountIDR = 50000.0
        ),
        Transaction(
            id = 2, amount = 150000.0, note = "Makan Malam", title = "Makan",
            type = TransactionType.EXPENSE, date = System.currentTimeMillis(),
            categoryName = "Makanan", categoryIcon = "restaurant", categoryColor = Color.Red.toArgb(),
            fromAssetId = 1, fromAssetName = "Dompet", categoryId = 2,
            currency = "IDR", convertedAmountIDR = 150000.0
        )
    )

    MaterialTheme {
        SearchContent(
            query = "Ma",
            results = dummyData,
            onQueryChange = {},
            onBackClick = {},
            onClearClick = {},
            onItemClick = {}
        )
    }
}