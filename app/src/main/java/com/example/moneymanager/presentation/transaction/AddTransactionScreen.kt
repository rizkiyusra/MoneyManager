package com.example.moneymanager.presentation.transaction

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.presentation.transaction.AddTransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddTransactionScreen(
    navController: NavController,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val saveState by viewModel.saveState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is Resource.Success -> {
                Toast.makeText(context, "Transaksi Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                navController.popBackStack()
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    AddTransactionContent(
        onBackClick = { navController.popBackStack() },
        onSaveClick = { amount, note, type, date ->
            viewModel.saveTransaction(
                amount = amount,
                note = note,
                type = type,
                date = date,
                categoryId = 1,
                fromAssetId = 1
            )
        },
        isLoading = saveState is Resource.Loading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    onBackClick: () -> Unit,
    onSaveClick: (Double, String, TransactionType, Long) -> Unit,
    isLoading: Boolean
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID")) }

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
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Transaksi") },
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

            OutlinedTextField(
                value = dateFormatter.format(Date(selectedDateMillis)),
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showDatePicker = true }
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
                    onSaveClick(amountDouble, note, selectedType, selectedDateMillis)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = amount.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Transaksi")
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
            onBackClick = {},
            onSaveClick = { _, _, _, _ -> },
            isLoading = false
        )
    }
}