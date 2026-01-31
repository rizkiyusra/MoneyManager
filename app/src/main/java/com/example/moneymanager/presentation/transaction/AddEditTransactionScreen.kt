package com.example.moneymanager.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneymanager.common.extension.formatToThousandSeparator
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.presentation.components.CategorySelectionSheet
import com.example.moneymanager.presentation.components.getCategoryIcon
import com.example.moneymanager.presentation.transaction.components.AssetDropdown
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditTransactionScreen(
    navController: NavController,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val amount = viewModel.amount
    val note = viewModel.note
    val date = viewModel.date
    val type = viewModel.type
    val selectedCategoryId = viewModel.selectedCategoryId
    val selectedFromAssetId = viewModel.selectedFromAssetId
    val selectedToAssetId = viewModel.selectedToAssetId

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditTransactionViewModel.UiEvent.SaveSuccess -> navController.popBackStack()
                is AddEditTransactionViewModel.UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    AddEditTransactionContent(
        title = "Transaksi",
        snackbarHostState = snackbarHostState,
        amount = amount,
        note = note,
        date = date,
        type = type,
        selectedCategoryId = selectedCategoryId,
        selectedFromAssetId = selectedFromAssetId,
        selectedToAssetId = selectedToAssetId,
        assets = assets,
        categories = categories,
        onAmountChange = viewModel::onAmountChange,
        onNoteChange = viewModel::onNoteChange,
        onDateChange = viewModel::onDateChange,
        onTypeChange = viewModel::onTypeChange,
        onCategorySelect = viewModel::onCategorySelect,
        onFromAssetSelect = viewModel::onFromAssetSelect,
        onToAssetSelect = viewModel::onToAssetSelect,
        onSaveClick = viewModel::onSaveTransaction,
        onDeleteClick = viewModel::onDeleteTransaction,
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionContent(
    title: String,
    snackbarHostState: SnackbarHostState,
    amount: String,
    note: String,
    date: Long,
    type: TransactionType,
    selectedCategoryId: Int?,
    selectedFromAssetId: Int?,
    selectedToAssetId: Int?,
    assets: List<Asset>,
    categories: List<Category>,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onCategorySelect: (Int) -> Unit,
    onFromAssetSelect: (Int) -> Unit,
    onToAssetSelect: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val currentCategory = categories.find { it.id == selectedCategoryId }
    val currentFromAsset = assets.find { it.id == selectedFromAssetId }
    val currentToAsset = assets.find { it.id == selectedToAssetId }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
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
                    selected = type == TransactionType.EXPENSE,
                    onClick = { onTypeChange(TransactionType.EXPENSE) },
                    label = { Text("Pengeluaran", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFCDD2), selectedLabelColor = Color(0xFFD32F2F))
                )
                FilterChip(
                    selected = type == TransactionType.INCOME,
                    onClick = { onTypeChange(TransactionType.INCOME) },
                    label = { Text("Pemasukan", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFC8E6C9), selectedLabelColor = Color(0xFF388E3C))
                )
                FilterChip(
                    selected = type == TransactionType.TRANSFER_OUT,
                    onClick = { onTypeChange(TransactionType.TRANSFER_OUT) },
                    label = { Text("Transfer", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFBBDEFB), selectedLabelColor = Color(0xFF1976D2))
                )
            }

            if (type == TransactionType.TRANSFER_OUT) {
                Text("Dari Dompet", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                AssetDropdown(
                    label = "Pilih Aset Asal",
                    selectedAsset = currentFromAsset,
                    assets = assets,
                    onSelect = { onFromAssetSelect(it.id) }
                )
                Text("Ke Dompet", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                AssetDropdown(
                    label = "Pilih Aset Tujuan",
                    selectedAsset = currentToAsset,
                    assets = assets.filter { it.id != selectedFromAssetId },
                    onSelect = { onToAssetSelect(it.id) }
                )
            } else {
                Text("Kategori", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                OutlinedCard(
                    onClick = { showCategorySheet = true },
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        val color = currentCategory?.color ?: Color.Gray.toArgb()
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(color).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = getCategoryIcon(currentCategory?.icon ?: "Kategori"), contentDescription = null, tint = Color(color))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = currentCategory?.name ?: "Pilih Kategori", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                }

                Text("Dompet / Akun", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                AssetDropdown(
                    label = "Pilih Aset",
                    selectedAsset = currentFromAsset,
                    assets = assets,
                    onSelect = { onFromAssetSelect(it.id) }
                )
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { onAmountChange(it.formatToThousandSeparator()) },
                prefix = { Text("Rp ") },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Catatan / Judul") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date.toReadableDate(),
                onValueChange = {},
                label = { Text("Tanggal") },
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onSaveClick, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text(if (title.contains("Ubah")) "Update Transaksi" else "Simpan Transaksi")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { onDateChange(it) }; showDatePicker = false }) { Text("Pilih") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Batal") } }
        ) { DatePicker(state = datePickerState) }
    }
    if (showCategorySheet) {
        CategorySelectionSheet(
            categories = categories,
            onCategorySelected = { onCategorySelect(it.id); showCategorySheet = false },
            onDismiss = { showCategorySheet = false }
        )
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi?") },
            text = { Text("Data tidak bisa dipulihkan.") },
            confirmButton = { TextButton(onClick = { showDeleteDialog = false; onDeleteClick() }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Hapus") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") } }
        )
    }
}

@Preview(showBackground = true, name = "1. Edit Transaction Preview")
@Composable
private fun PreviewEditTransaction() {
    MaterialTheme {
        AddEditTransactionContent(
            title = "Ubah Transaksi",
            snackbarHostState = remember { SnackbarHostState() },
            amount = "150.000",
            note = "Makan Siang Tim",
            date = System.currentTimeMillis(),
            type = TransactionType.EXPENSE,
            selectedCategoryId = 1,
            selectedFromAssetId = 1,
            selectedToAssetId = null,
            assets = listOf(Asset(1, "BCA", "BANK", 5000000.0, "IDR", "Rp")),
            categories = listOf(Category(1, "Makanan", "Beli makan", true, Color.Red.toArgb(), "fastfood")),
            onAmountChange = {}, onNoteChange = {}, onDateChange = {},
            onTypeChange = {}, onCategorySelect = {}, onFromAssetSelect = {},
            onToAssetSelect = {}, onSaveClick = {}, onDeleteClick = {}, onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Add Transaction Preview")
@Composable
private fun PreviewAddTransaction() {
    MaterialTheme {
        AddEditTransactionContent(
            title = "Tambah Transaksi",
            snackbarHostState = remember { SnackbarHostState() },
            amount = "",
            note = "",
            date = System.currentTimeMillis(),
            type = TransactionType.EXPENSE,
            selectedCategoryId = null,
            selectedFromAssetId = null,
            selectedToAssetId = null,
            assets = emptyList(),
            categories = emptyList(),
            onAmountChange = {}, onNoteChange = {}, onDateChange = {},
            onTypeChange = {}, onCategorySelect = {}, onFromAssetSelect = {},
            onToAssetSelect = {}, onSaveClick = {}, onDeleteClick = {}, onBackClick = {}
        )
    }
}