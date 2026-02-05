package com.example.moneymanager.presentation.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.domain.model.MonthlyCategoryTrend
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.presentation.report.components.CategoryLineChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    navController: NavController,
    categoryName: String,
    viewModel: CategoryDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            CategoryDetailContent(
                padding = padding,
                totalExpense = state.totalExpense,
                monthlyTrend = state.monthlyTrend,
                transactions = state.transactions
            )
        }
    }
}

@Composable
private fun CategoryDetailContent(
    padding: PaddingValues,
    totalExpense: Double,
    monthlyTrend: List<MonthlyCategoryTrend>,
    transactions: List<Transaction>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Total Pengeluaran",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = totalExpense.toRupiah(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            if (monthlyTrend.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Trend Bulanan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CategoryLineChart(
                            data = monthlyTrend,
                            modifier = Modifier.height(200.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Riwayat Transaksi",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada riwayat transaksi", color = Color.Gray)
                }
            }
        } else {
            items(transactions) { transaction ->
                TransactionItemSimple(transaction)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun TransactionItemSimple(transaction: Transaction) {
    ListItem(
        headlineContent = {
            Text(transaction.note.orEmpty().ifEmpty { "Tanpa Catatan" })
        },
        supportingContent = {
            Text(transaction.date.toReadableDate())
        },
        trailingContent = {
            Text(
                text = "- ${transaction.amount.toRupiah()}",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CategoryDetailScreenPreview() {
    val dummyTrend = listOf(
        MonthlyCategoryTrend("2026-01", 50000.0),
        MonthlyCategoryTrend("2026-02", 150000.0),
        MonthlyCategoryTrend("2026-03", 100000.0),
        MonthlyCategoryTrend("2026-04", 300000.0),
        MonthlyCategoryTrend("2026-05", 250000.0)
    )

    val dummyTransactions = listOf(
        Transaction(
            id = 1,
            amount = 25000.0,
            date = System.currentTimeMillis(),
            note = "Nasi Goreng Spesial",
            type = TransactionType.EXPENSE,
            categoryId = 1,
            fromAssetId = 1,
            toAssetId = null,
            categoryName = "Makanan",
            categoryIcon = "restaurant",
            categoryColor = android.graphics.Color.RED,
            fromAssetName = "Dompet",
            currency = "IDR",
            convertedAmountIDR = 25000.0,
            exchangeRate = 1.0,
            title = "",
            location = null,
            receiptImagePath = null,
            createdDate = System.currentTimeMillis()
        ),
        Transaction(
            id = 2,
            amount = 150000.0,
            date = System.currentTimeMillis() - 86400000,
            note = "Belanja Bulanan",
            type = TransactionType.EXPENSE,
            categoryId = 1,
            fromAssetId = 1,
            toAssetId = null,
            categoryName = "Makanan",
            categoryIcon = "restaurant",
            categoryColor = android.graphics.Color.RED,
            fromAssetName = "BCA",
            currency = "IDR",
            convertedAmountIDR = 150000.0,
            exchangeRate = 1.0,
            title = "",
            location = null,
            receiptImagePath = null,
            createdDate = System.currentTimeMillis()
        )
    )

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Makanan & Minuman") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                )
            }
        ) { padding ->
            CategoryDetailContent(
                padding = padding,
                totalExpense = 175000.0,
                monthlyTrend = dummyTrend,
                transactions = dummyTransactions
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CategoryDetailEmptyPreview() {
    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Kategori Kosong") }) }
        ) { padding ->
            CategoryDetailContent(
                padding = padding,
                totalExpense = 0.0,
                monthlyTrend = emptyList(),
                transactions = emptyList()
            )
        }
    }
}