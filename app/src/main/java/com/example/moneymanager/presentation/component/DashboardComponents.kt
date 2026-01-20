package com.example.moneymanager.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneymanager.R
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.presentation.theme.expense
import com.example.moneymanager.presentation.theme.income

@Composable
fun TotalBalanceCard(balance: Double) {
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
                text = balance.toRupiah(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun IncomeExpenseRow(income: Double, expense: Double) {
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
                    text = income.toRupiah(),
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
                    text = expense.toRupiah(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.expense
                )
            }
        }
    }
}

@Composable
fun QuickActionsRow(
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