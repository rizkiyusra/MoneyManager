package com.example.moneymanager.presentation.report.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.domain.model.CategoryExpense

@Composable
fun CategoryExpenseList(
    expenses: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    val totalExpense = expenses.sumOf { it.totalAmount }

    Column(modifier = modifier.padding(16.dp)) {
        expenses.forEach { item ->
            val percentage = if (totalExpense > 0) (item.totalAmount / totalExpense).toFloat() else 0f

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = MaterialTheme.shapes.small,
                    color = Color(item.color)
                ) {}

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.categoryName, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "${(percentage * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(item.color),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.totalAmount.toRupiah(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}