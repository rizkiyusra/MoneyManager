package com.example.moneymanager.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.presentation.theme.expense
import com.example.moneymanager.presentation.theme.income

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(transaction.categoryColor).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(transaction.categoryIcon),
                contentDescription = transaction.categoryName,
                tint = Color(transaction.categoryColor)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.title.ifEmpty { transaction.categoryName },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row {
                Text(
                    text = transaction.categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(transaction.categoryColor)
                )
                Text(
                    text = " • ${transaction.date.toReadableDate()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val isExpense = transaction.type == TransactionType.EXPENSE || transaction.type == TransactionType.TRANSFER_OUT
        val amountColor = if (isExpense) {
            MaterialTheme.colorScheme.expense
        } else {
            MaterialTheme.colorScheme.income
        }
        val prefix = if (isExpense) "- " else "+ "

        Text(
            text = prefix + transaction.amount.toRupiah(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Composable
fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "fastfood" -> Icons.Default.Fastfood
        "restaurant" -> Icons.Default.Restaurant
        "shopping" -> Icons.Default.ShoppingBag
        "transport" -> Icons.Default.DirectionsCar
        "salary" -> Icons.Default.AttachMoney
        "medical" -> Icons.Default.MedicalServices
        else -> Icons.Default.Category
    }
}