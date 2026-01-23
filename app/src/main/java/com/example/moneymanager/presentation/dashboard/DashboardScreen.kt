package com.example.moneymanager.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneymanager.R
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.presentation.component.ErrorContent
import com.example.moneymanager.presentation.component.IncomeExpenseRow
import com.example.moneymanager.presentation.component.LoadingContent
import com.example.moneymanager.presentation.component.QuickActionsRow
import com.example.moneymanager.presentation.component.TotalBalanceCard
import com.example.moneymanager.presentation.component.TransactionItem
import com.example.moneymanager.presentation.theme.income
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToAddWallet: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAddTransaction: (Int?) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val deleteMessage = "Transaksi dihapus"
    val undoLabel = "BATAL"

    DashboardContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRetry = ({ viewModel.retryLoading() }),
        onNavigateToAddWallet = onNavigateToAddWallet,
        onNavigateToAssets = onNavigateToAssets,
        onNavigateToTransactions = onNavigateToTransactions,
        onNavigateToAddTransaction = onNavigateToAddTransaction,
        onNavigateToSettings = onNavigateToSettings,
        onDeleteTransaction = { transaction ->
            viewModel.deleteTransaction(transaction)

            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = deleteMessage,
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.restoreTransaction()
                }
            }
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
    onNavigateToAddWallet: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAddTransaction: (Int?) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    onDeleteTransaction = onDeleteTransaction,
                    onNavigateToAddWallet = onNavigateToAddWallet,
                    onNavigateToAssets = onNavigateToAssets,
                    onNavigateToTransactions = onNavigateToTransactions,
                    onNavigateToAddTransaction = onNavigateToAddTransaction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardSuccessContent(
    uiState: DashboardUiState,
    onDeleteTransaction: (Transaction) -> Unit,
    onNavigateToAddWallet: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToTransactions: () -> Unit,
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    val recentList = uiState.recentTransactions

                    if (recentList.isEmpty()) {
                        Text(
                            text = "Belum ada transaksi",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        recentList.forEach { transaction ->

                            key(transaction.id) {
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = {
                                        if (it == SwipeToDismissBoxValue.EndToStart) {
                                            onDeleteTransaction(transaction)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )

                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,
                                    backgroundContent = {
                                        val color = MaterialTheme.colorScheme.errorContainer
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(color)
                                                .padding(horizontal = 20.dp),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Hapus",
                                                tint = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                ) {
                                    TransactionItem(
                                        transaction = transaction,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(horizontal = 16.dp)
                                            .clickable { onNavigateToAddTransaction(transaction.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
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
            onNavigateToAddWallet = {},
            onNavigateToAssets = {},
            onNavigateToTransactions = {},
            onNavigateToAddTransaction = {},
        )
    }
}