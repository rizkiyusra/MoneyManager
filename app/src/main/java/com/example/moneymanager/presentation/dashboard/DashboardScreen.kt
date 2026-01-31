package com.example.moneymanager.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneymanager.R
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.presentation.components.ErrorContent
import com.example.moneymanager.presentation.components.LoadingContent
import com.example.moneymanager.presentation.dashboard.components.IncomeExpenseRow
import com.example.moneymanager.presentation.dashboard.components.RecentTransactionsSection
import com.example.moneymanager.presentation.dashboard.components.TotalBalanceCard
import com.example.moneymanager.presentation.theme.income
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToAddTransaction: (Int?) -> Unit,
    onNavigateToBudget: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DashboardViewModel.DashboardEvent.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "BATAL",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.restoreTransaction()
                    }
                }
                is DashboardViewModel.DashboardEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    DashboardContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRetry = { viewModel.retryLoading() },
        onNavigateToAddTransaction = onNavigateToAddTransaction,
        onNavigateToBudget = onNavigateToBudget,
        onDeleteTransaction = { transaction ->
            viewModel.deleteTransaction(transaction)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onNavigateToAddTransaction: (Int?) -> Unit,
    onNavigateToBudget: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToBudget) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = "Budget",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddTransaction(null) },
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
                LoadingContent(modifier = Modifier.padding(paddingValues))
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
                    onDeleteTransaction = onDeleteTransaction,
                    onNavigateToAddTransaction = onNavigateToAddTransaction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DashboardSuccessContent(
    uiState: DashboardUiState,
    onDeleteTransaction: (Transaction) -> Unit,
    onNavigateToAddTransaction: (Int?) -> Unit,
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
            RecentTransactionsSection(
                transactions = uiState.recentTransactions,
                onDeleteTransaction = onDeleteTransaction,
                onTransactionClick = { transactionId ->
                    onNavigateToAddTransaction(transactionId)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    MaterialTheme {
        DashboardSuccessContent(
            uiState = DashboardUiState(
                totalBalance = 1500000.0,
                monthlyIncome = 5000000.0,
                monthlyExpense = 3500000.0,
                recentTransactions = emptyList()
            ),
            onDeleteTransaction = {},
            onNavigateToAddTransaction = {}
        )
    }
}