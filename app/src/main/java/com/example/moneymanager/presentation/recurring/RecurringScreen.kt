package com.example.moneymanager.presentation.recurring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.extension.cleanToDouble
import com.example.moneymanager.common.extension.formatToThousandSeparator
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.RecurringTransaction
import com.example.moneymanager.presentation.theme.MoneyManagerTheme

@Composable
fun RecurringScreen(
    navController: NavController,
    viewModel: RecurringViewModel = hiltViewModel()
) {
    val recurringList by viewModel.recurringList.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val assets by viewModel.assets.collectAsState()

    RecurringContent(
        recurringList = recurringList,
        categories = categories,
        assets = assets,
        onBackClick = { navController.popBackStack() },
        onDeleteClick = { viewModel.deleteRecurring(it) },
        onSaveClick = { amount, note, isIncome, catId, assetId, freq ->
            viewModel.saveRecurringTransaction(amount, note, isIncome, catId, assetId, freq)
        },
        onTypeChanged = { isIncome ->
            viewModel.loadCategories(isIncome)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringContent(
    recurringList: List<RecurringTransaction>,
    categories: List<Category>,
    assets: List<Asset>,
    onBackClick: () -> Unit,
    onDeleteClick: (RecurringTransaction) -> Unit,
    onSaveClick: (Double, String, Boolean, Int, Int, String) -> Unit,
    onTypeChanged: (Boolean) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<RecurringTransaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Rutin (Otomatis)") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Jadwal")
            }
        }
    ) { padding ->
        if (recurringList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada jadwal transaksi otomatis.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recurringList) { item ->
                    RecurringItemCard(
                        item = item,
                        onDelete = { itemToDelete = item }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddRecurringDialog(
                categories = categories,
                assets = assets,
                onDismiss = { showAddDialog = false },
                onSave = { amount, note, isIncome, catId, assetId, freq ->
                    onSaveClick(amount, note, isIncome, catId, assetId, freq)
                    showAddDialog = false
                },
                onTypeChanged = onTypeChanged
            )
        }

        if (itemToDelete != null) {
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                title = { Text(text = "Hapus Jadwal?") },
                text = {
                    Text("Jadwal '${itemToDelete?.note}' akan dihentikan. Transaksi yang sudah lalu tidak akan terhapus.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            itemToDelete?.let { onDeleteClick(it) }
                            itemToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun RecurringItemCard(
    item: RecurringTransaction,
    onDelete: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when(item.frequency) {
                            "DAILY" -> "Setiap Hari"
                            "WEEKLY" -> "Setiap Minggu"
                            "MONTHLY" -> "Setiap Bulan"
                            "YEARLY" -> "Setiap Tahun"
                            else -> item.frequency
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.note, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = "Berikutnya: ${item.nextRunDate.toReadableDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.amount.toRupiah(),
                    color = if (item.isIncome) Color(0xFF4CAF50) else Color(0xFFE53935),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
                }
            }
        }
    }
}

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
                    onItemSelected = { selectedCategory = it }
                )

                DropdownSelector(
                    label = "Dompet / Aset",
                    currentValue = selectedAsset?.name ?: "Pilih Dompet",
                    items = assets,
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
fun <T> DropdownSelector(
    label: String,
    currentValue: String,
    items: List<T>,
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
                        text = {
                            val name = if (item is Category) item.name else if (item is Asset) item.name else item.toString()
                            Text(name)
                        },
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

@Preview(showBackground = true)
@Composable
fun RecurringScreenPreview() {
    val dummyList = listOf(
        RecurringTransaction(
            amount = 150000.0,
            note = "Langganan WiFi",
            isIncome = false,
            categoryId = 1,
            assetId = 1,
            frequency = "MONTHLY",
            nextRunDate = System.currentTimeMillis(),
            createdDate = System.currentTimeMillis()
        ),
        RecurringTransaction(
            amount = 5000000.0,
            note = "Gaji Bulanan",
            isIncome = true,
            categoryId = 2,
            assetId = 1,
            frequency = "MONTHLY",
            nextRunDate = System.currentTimeMillis() + 86400000L,
            createdDate = System.currentTimeMillis()
        )
    )

    MoneyManagerTheme {
        RecurringContent(
            recurringList = dummyList,
            categories = emptyList(),
            assets = emptyList(),
            onBackClick = {},
            onDeleteClick = {},
            onSaveClick = { _, _, _, _, _, _ -> },
            onTypeChanged = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecurringItemPreview() {
    MoneyManagerTheme {
        RecurringItemCard(
            item = RecurringTransaction(
                amount = 75000.0,
                note = "Spotify Premium",
                isIncome = false,
                categoryId = 1,
                assetId = 1,
                frequency = "MONTHLY",
                nextRunDate = System.currentTimeMillis(),
                createdDate = System.currentTimeMillis()
            ),
            onDelete = {}
        )
    }
}