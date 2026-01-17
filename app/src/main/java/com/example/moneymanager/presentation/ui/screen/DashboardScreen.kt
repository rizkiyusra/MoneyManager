package com.example.moneymanager.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneymanager.R
import com.example.moneymanager.presentation.ui.component.ErrorContent
import com.example.moneymanager.presentation.ui.component.LoadingContent
import com.example.moneymanager.presentation.ui.component.TransactionItem
import com.example.moneymanager.presentation.ui.state.DashboardUiState
import com.example.moneymanager.presentation.ui.theme.expense
import com.example.moneymanager.presentation.ui.theme.income
import com.example.moneymanager.presentation.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToAssets: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        onRetry = { viewModel.retryLoading() },
        onNavigateToAssets = onNavigateToAssets,
        onNavigateToTransactions = onNavigateToTransactions,
        onNavigateToAddTransaction = onNavigateToAddTransaction,
        onNavigateToSettings = onNavigateToSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onRetry: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = MaterialTheme.colorScheme.income
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_transaction)
                )
            }
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingContent(
                    modifier = Modifier.padding(paddingValues)
                )
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error,
                    onRetry = onRetry,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            else -> {
                DashboardSuccessContent(
                    uiState = uiState,
                    onNavigateToAssets = onNavigateToAssets,
                    onNavigateToTransactions = onNavigateToTransactions,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DashboardSuccessContent(
    uiState: DashboardUiState,
    onNavigateToAssets: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TotalBalanceCard(balance = uiState.totalBalance)
        }

        item {
            IncomeExpenseRow(
                income = uiState.monthlyIncome,
                expense = uiState.monthlyExpense
            )
        }

        item {
            Text(
                text = stringResource(R.string.quick_actions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            QuickActionsRow(
                onNavigateToAssets = onNavigateToAssets,
                onNavigateToTransactions = onNavigateToTransactions
            )
        }

        item {
            Text(
                text = stringResource(R.string.recent_transactions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    uiState.recentTransactions.take(3).forEach { transaction ->
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.total_balance),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = formatCurrency(balance),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun IncomeExpenseRow(income: Double, expense: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.income.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.income),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.income
                )
                Text(
                    text = formatCurrency(income),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.income
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.expense.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.expense),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.expense
                )
                Text(
                    text = formatCurrency(expense),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.expense
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onNavigateToAssets: () -> Unit,
    onNavigateToTransactions: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onNavigateToAssets,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.view_assets))
        }
        Button(
            onClick = onNavigateToTransactions,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.transactions))
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DashboardSuccessContentPreview() {
    MaterialTheme {
        DashboardSuccessContent(
            uiState = DashboardUiState(
                isLoading = false,
                error = null,
                totalBalance = 1500000.0,
                monthlyIncome = 2000000.0,
                monthlyExpense = 500000.0,
                recentTransactions = emptyList()
            ),
            onNavigateToAssets = {},
            onNavigateToTransactions = {},
            modifier = Modifier
        )
    }
}
