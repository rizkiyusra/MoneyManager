package com.example.moneymanager.presentation.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.domain.model.CategoryExpense
import com.example.moneymanager.domain.model.DailySummary
import com.example.moneymanager.presentation.report.components.CategoryExpenseList
import com.example.moneymanager.presentation.report.components.ExpensePieChart
import com.example.moneymanager.presentation.report.components.FinancialTrendChart
import com.example.moneymanager.presentation.report.components.MonthSelector

@Composable
fun ReportsScreen(
    viewModel: ReportViewModel = hiltViewModel(),
    onNavigateToDetail: (Int, String) -> Unit
) {
    val dateRange by viewModel.dateRange.collectAsStateWithLifecycle()
    val categoryExpenses by viewModel.categoryExpenses.collectAsStateWithLifecycle()
    val financialTrend by viewModel.financialTrend.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<CategoryExpense?>(null) }
    var selectedDay by remember { mutableStateOf<DailySummary?>(null) }

    ReportsContent(
        currentDate = dateRange.first,
        categoryExpenses = categoryExpenses,
        financialTrend = financialTrend,
        onPreviousMonth = { viewModel.previousMonth() },
        onNextMonth = { viewModel.nextMonth() },
        onCategoryClick = { category ->
            onNavigateToDetail(category.categoryId, category.categoryName)
        },
        onDayClick = { selectedDay = it }
    )

    if (selectedCategory != null) {
        val item = selectedCategory!!
        AlertDialog(
            onDismissRequest = { selectedCategory = null },
            title = { Text(item.categoryName) },
            text = {
                Column {
                    Text("Total Pengeluaran:")
                    Text(
                        text = item.totalAmount.toRupiah(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedCategory = null }) {
                    Text("Tutup")
                }
            }
        )
    }

    if (selectedDay != null) {
        val day = selectedDay!!
        AlertDialog(
            onDismissRequest = { selectedDay = null },
            title = { Text(day.date.toReadableDate()) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pemasukan:", color = Color(0xFF4CAF50))
                        Text(day.income.toRupiah(), fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pengeluaran:", color = Color(0xFFF44336))
                        Text(day.expense.toRupiah(), fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Netto:", fontWeight = FontWeight.Bold)
                        val profit = day.income - day.expense
                        val color = if (profit >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                        Text(profit.toRupiah(), color = color, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDay = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportsContent(
    currentDate: Long,
    categoryExpenses: List<CategoryExpense>,
    financialTrend: List<DailySummary>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onCategoryClick: (CategoryExpense) -> Unit,
    onDayClick: (DailySummary) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analisis Keuangan") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            MonthSelector(
                currentDate = currentDate,
                onPrevious = onPreviousMonth,
                onNext = onNextMonth
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pengeluaran per Kategori",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (categoryExpenses.isEmpty()) {
                EmptyStateMessage("Belum ada pengeluaran bulan ini")
            } else {
                ExpensePieChart(
                    expenses = categoryExpenses,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onCategoryClick = onCategoryClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                CategoryExpenseList(
                    expenses = categoryExpenses,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Trend Harian",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                FinancialTrendChart(
                    dailySummaries = financialTrend,
                    currentMonthStart = currentDate,
                    modifier = Modifier.padding(16.dp),
                    onDateClick = onDayClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportsScreenPreview() {
    val dummyExpenses = listOf(
        CategoryExpense(1, "Makanan", 150000.0, android.graphics.Color.RED, "restaurant"),
        CategoryExpense(2, "Tagihan", 350000.0, android.graphics.Color.YELLOW, "bolt"),
    )

    val dummyTrend = listOf(
        DailySummary(System.currentTimeMillis(), 500000.0, 100000.0),
        DailySummary(System.currentTimeMillis() - 86400000, 0.0, 50000.0)
    )

    MaterialTheme {
        ReportsContent(
            currentDate = System.currentTimeMillis(),
            categoryExpenses = dummyExpenses,
            financialTrend = dummyTrend,
            onPreviousMonth = {},
            onNextMonth = {},
            onCategoryClick = {},
            onDayClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportsScreenEmptyPreview() {
    MaterialTheme {
        ReportsContent(
            currentDate = System.currentTimeMillis(),
            categoryExpenses = emptyList(),
            financialTrend = emptyList(),
            onPreviousMonth = {},
            onNextMonth = {},
            onCategoryClick = {},
            onDayClick = {}
        )
    }
}