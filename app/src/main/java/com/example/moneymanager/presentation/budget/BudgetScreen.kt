package com.example.moneymanager.presentation.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.extension.cleanToDouble
import com.example.moneymanager.common.extension.formatToThousandSeparator
import com.example.moneymanager.domain.model.Budget
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.common.extension.toRupiah
import java.text.DateFormatSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    navController: NavController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val budgetList by viewModel.budgetList.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var budgetToDelete by remember { mutableStateOf<Budget?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anggaran Bulanan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Budget")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev")
                }

                val monthName = DateFormatSymbols().months[currentMonth]
                Text(
                    text = "$monthName $currentYear",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }

            if (budgetList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada anggaran bulan ini.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(budgetList) { budget ->
                        BudgetItemCard(
                            budget = budget,
                            onDelete = { budgetToDelete = budget }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddBudgetDialog(
                categories = categories,
                onDismiss = { showAddDialog = false },
                onSave = { catId, name, limit ->
                    viewModel.addBudget(catId, name, limit)
                    showAddDialog = false
                }
            )
        }

        if (budgetToDelete != null) {
            AlertDialog(
                onDismissRequest = { budgetToDelete = null },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                title = { Text("Hapus Anggaran?") },
                text = { Text("Anggaran '${budgetToDelete?.name}' akan dihapus dari bulan ini.") },
                confirmButton = {
                    Button(
                        onClick = {
                            budgetToDelete?.let { viewModel.deleteBudget(it) }
                            budgetToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Hapus") }
                },
                dismissButton = {
                    TextButton(onClick = { budgetToDelete = null }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun BudgetItemCard(budget: Budget, onDelete: () -> Unit) {
    val progress = if (budget.limit > 0) (budget.currentSpent / budget.limit).toFloat() else 0f

    val progressColor = when {
        progress >= 1f -> Color.Red
        progress >= 0.8f -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }

    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = budget.name, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = budget.currentSpent.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = progressColor
                )
                Text(
                    text = budget.limit.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress.coerceAtMost(1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = progressColor,
                trackColor = Color.LightGray.copy(alpha = 0.5f),
            )

            Text(
                text = "${(progress * 100).toInt()}% Terpakai",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Int, String, Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) {
            selectedCategory = categories.first()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Buat Anggaran Baru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                if (categories.isNotEmpty()) {
                    Text("Pilih Kategori:", style = MaterialTheme.typography.bodySmall)

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Pilih Kategori",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text("Belum ada kategori. Silakan buat kategori dulu.", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it.formatToThousandSeparator()
                    },
                    label = { Text("Batas Anggaran (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val limit = amount.cleanToDouble()
                        if (limit > 0 && selectedCategory != null) {
                            onSave(selectedCategory!!.id, selectedCategory!!.name, limit)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = amount.isNotEmpty() && selectedCategory != null
                ) {
                    Text("Simpan Anggaran")
                }
            }
        }
    }
}