package com.example.moneymanager.presentation.transaction

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.presentation.component.CategorySelectionSheet
import com.example.moneymanager.presentation.component.getCategoryIcon

@Composable
fun AddTransactionScreen(
    navController: NavController,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val saveState by viewModel.saveState.collectAsState()
    val transactionToEdit by viewModel.transactionToEdit.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is Resource.Success -> {
                val message = if (transactionToEdit != null) "Transaksi Berhasil Diubah!" else "Transaksi Berhasil Disimpan!"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                navController.popBackStack()
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message ?: "Terjadi Kesalahan", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    AddTransactionContent(
        transactionToEdit = transactionToEdit,
        categories = categories ?: emptyList(),
        selectedCategory = selectedCategory,
        onBackClick = { navController.popBackStack() },
        onSaveClick = { amount, note, type, date, categoryId ->
            viewModel.saveTransaction(
                amount = amount,
                note = note,
                type = type,
                date = date,
                categoryId = categoryId,
                fromAssetId = 1
            )
        },

        onTypeChanged = { isIncomeCategory ->
            viewModel.loadCategories(isIncomeCategory)
        },

        onCategorySelected = { category ->
            viewModel.onCategorySelected(category)
        },
        isLoading = saveState is Resource.Loading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    transactionToEdit: Transaction?,
    categories: List<Category>,
    selectedCategory: Category?,
    onBackClick: () -> Unit,
    onSaveClick: (Double, String, TransactionType, Long, Int) -> Unit,
    onTypeChanged: (Boolean) -> Unit,
    onCategorySelected: (Category) -> Unit,
    isLoading: Boolean
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }

    LaunchedEffect(transactionToEdit) {
        transactionToEdit?.let { transaction ->
            amount = transaction.amount.toString().replace(".0", "").replace(".00", "")
            note = transaction.title
            selectedType = transaction.type
            selectedDateMillis = transaction.date
        }
    }

    LaunchedEffect(selectedType) {
        onTypeChanged(selectedType == TransactionType.INCOME)
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
                    showDatePicker = false
                }) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCategorySheet) {
        CategorySelectionSheet(
            categories = categories,
            onCategorySelected = {
                onCategorySelected(it)
                showCategorySheet = false
            },
            onDismiss = { showCategorySheet = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionToEdit != null) "Ubah Transaksi" else "Tambah Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = { selectedType = TransactionType.EXPENSE },
                    label = { Text("Pengeluaran") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.Red.copy(alpha = 0.2f))
                )
                FilterChip(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    label = { Text("Pemasukan") },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.Green.copy(alpha = 0.2f))
                )
            }

            OutlinedCard(
                onClick = { showCategorySheet = true },
                colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val color = selectedCategory?.color ?: Color.Gray.toArgb()
                    val iconName = selectedCategory?.icon ?: "category"
                    val name = selectedCategory?.name ?: "Pilih Kategori"

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(color).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(iconName),
                            contentDescription = null,
                            tint = Color(color)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            }

            OutlinedTextField(
                value = selectedDateMillis.toReadableDate(),
                onValueChange = {},
                label = { Text("Tanggal") },
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.all { char -> char.isDigit() }) amount = it },
                label = { Text("Jumlah (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan / Judul") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    onSaveClick(
                        amountDouble,
                        note,
                        selectedType,
                        selectedDateMillis,
                        selectedCategory?.id ?: 1
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = amount.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (transactionToEdit != null) "Ubah Transaksi" else "Simpan Transaksi")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAddTransactionContent() {
    MaterialTheme {
        AddTransactionContent(
            transactionToEdit = null,
            categories = emptyList(),
            selectedCategory = null,
            onBackClick = {},
            onSaveClick = { _, _, _, _, _ -> },
            onTypeChanged = {},
            onCategorySelected = {},
            isLoading = false
        )
    }
}