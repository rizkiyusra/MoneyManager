package com.example.moneymanager.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneymanager.common.utils.CurrencyFormatter

@Composable
fun CurrencyPreviewCard(
    assetName: String,
    currencyCode: String,
    amount: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = assetName, style = MaterialTheme.typography.titleMedium)

            Text(
                text = CurrencyFormatter.format(amount, currencyCode),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyFormatterPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Portofolio Aset", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            CurrencyPreviewCard("Tabungan Bank", "IDR", 15500000.0)
            CurrencyPreviewCard("Saham Luar Negeri", "USD", 1250.75)
            CurrencyPreviewCard("Investasi Kripto", "BTC", 0.04512000)
            CurrencyPreviewCard("Logam Mulia", "XAU", 25.5)
        }
    }
}