package com.example.moneymanager.presentation.recurring.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.moneymanager.common.extension.cleanToDouble
import com.example.moneymanager.common.extension.formatToThousandSeparator
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringDialog(
    categories: List<Category>,
    assets: List<Asset>,
    onDismiss: () -> Unit,
    onSave: (Double, String, Boolean, Int, Int, String) -> Unit,
    onTypeChanged: (Boolean) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var selectedFrequency by remember { mutableStateOf("MONTHLY") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedAsset by remember { mutableStateOf<Asset?>(null) }

    LaunchedEffect(isIncome) {
        onTypeChanged(isIncome)
        selectedCategory = null
    }

    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) selectedCategory = categories.first()
    }
    LaunchedEffect(assets) {
        if (selectedAsset == null && assets.isNotEmpty()) selectedAsset = assets.first()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Tambah Jadwal Rutin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = { isIncome = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = if (!isIncome) Color.Red else Color.Gray)
                    ) { Text("Pengeluaran") }
                    TextButton(
                        onClick = { isIncome = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = if (isIncome) Color.Green else Color.Gray)
                    ) { Text("Pemasukan") }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.formatToThousandSeparator() },
                    label = { Text("Nominal (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Catatan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Frekuensi:", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("WEEKLY" to "Mingguan", "MONTHLY" to "Bulanan").forEach { (code, label) ->
                        FilterChip(
                            selected = selectedFrequency == code,
                            onClick = { selectedFrequency = code },
                            label = { Text(label) }
                        )
                    }
                }

                DropdownSelector(
                    label = "Kategori",
                    currentValue = selectedCategory?.name ?: "Pilih Kategori",
                    items = categories,
                    itemLabel = { it.name },
                    onItemSelected = { selectedCategory = it }
                )

                DropdownSelector(
                    label = "Dompet / Aset",
                    currentValue = selectedAsset?.name ?: "Pilih Dompet",
                    items = assets,
                    itemLabel = { it.name },
                    onItemSelected = { selectedAsset = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val amountDouble = amount.cleanToDouble()
                        if (amountDouble > 0 && selectedCategory != null && selectedAsset != null) {
                            onSave(amountDouble, note, isIncome, selectedCategory!!.id, selectedAsset!!.id, selectedFrequency)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = amount.isNotEmpty() && selectedCategory != null && selectedAsset != null
                ) {
                    Text("Simpan Jadwal")
                }
            }
        }
    }
}

@Composable
private fun <T> DropdownSelector(
    label: String,
    currentValue: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            Text(currentValue)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemLabel(item)) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}