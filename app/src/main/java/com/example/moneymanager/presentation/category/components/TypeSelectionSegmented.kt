package com.example.moneymanager.presentation.category.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSelectionSegmented(
    isIncome: Boolean,
    onTypeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        FilterChip(
            selected = !isIncome,
            onClick = { onTypeChanged(false) },
            label = { Text("Pengeluaran", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            leadingIcon = if (!isIncome) { { Icon(Icons.Default.Check, null) } } else null,
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.errorContainer)
        )
        FilterChip(
            selected = isIncome,
            onClick = { onTypeChanged(true) },
            label = { Text("Pemasukan", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            modifier = Modifier.weight(1f).padding(start = 4.dp),
            leadingIcon = if (isIncome) { { Icon(Icons.Default.Check, null) } } else null,
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
        )
    }
}