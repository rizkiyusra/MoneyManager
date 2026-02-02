package com.example.moneymanager.presentation.history.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.TransactionFilter
import com.example.moneymanager.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryFilterSheet(
    currentFilter: TransactionFilter,
    categories: List<Category>,
    assets: List<Asset>,
    onApplyFilter: (TransactionType?, Long?, Long?, Int?, Int?) -> Unit,
    onResetFilter: () -> Unit
) {
    var selectedType by remember { mutableStateOf(currentFilter.transactionType) }
    var startDate by remember { mutableStateOf(currentFilter.startDate) }
    var endDate by remember { mutableStateOf(currentFilter.endDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isPickingStartDate by remember { mutableStateOf(true) }
    val datePickerState = rememberDatePickerState()

    var selectedCategoryId by remember { mutableStateOf(currentFilter.categoryId) }
    var selectedAssetId by remember { mutableStateOf(currentFilter.assetId) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var assetExpanded by remember { mutableStateOf(false) }
    val categoryLabel = categories.find { it.id == selectedCategoryId }?.name ?: "Semua Kategori"
    val assetLabel = assets.find { it.id == selectedAssetId }?.name ?: "Semua Aset"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Filter Transaksi",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text("Tipe Transaksi", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedType == null,
                onClick = { selectedType = null },
                label = { Text("Semua") }
            )
            FilterChip(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { selectedType = TransactionType.EXPENSE },
                label = { Text("Pengeluaran") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFCDD2))
            )
            FilterChip(
                selected = selectedType == TransactionType.INCOME,
                onClick = { selectedType = TransactionType.INCOME },
                label = { Text("Pemasukan") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFC8E6C9))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Rentang Tanggal", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    isPickingStartDate = true
                    showDatePicker = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = startDate?.toReadableDate() ?: "Mulai")
            }

            OutlinedButton(
                onClick = {
                    isPickingStartDate = false
                    showDatePicker = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = endDate?.toReadableDate() ?: "Sampai")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Kategori", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = categoryLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Semua Kategori") },
                    onClick = {
                        selectedCategoryId = null
                        categoryExpanded = false
                    }
                )
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategoryId = category.id
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Aset / Dompet", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = assetExpanded,
            onExpandedChange = { assetExpanded = !assetExpanded }
        ) {
            OutlinedTextField(
                value = assetLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assetExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
            )
            ExposedDropdownMenu(
                expanded = assetExpanded,
                onDismissRequest = { assetExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Semua Aset") },
                    onClick = {
                        selectedAssetId = null
                        assetExpanded = false
                    }
                )
                assets.forEach { asset ->
                    DropdownMenuItem(
                        text = { Text(asset.name) },
                        onClick = {
                            selectedAssetId = asset.id
                            assetExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onResetFilter,
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }
            Button(
                onClick = {
                    onApplyFilter(selectedType, startDate, endDate, selectedCategoryId, selectedAssetId)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Terapkan")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        if (isPickingStartDate) startDate = millis else endDate = millis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}