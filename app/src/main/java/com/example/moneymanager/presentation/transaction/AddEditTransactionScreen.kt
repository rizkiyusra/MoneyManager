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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneymanager.common.extension.cleanToDouble
import com.example.moneymanager.common.extension.formatToThousandSeparator
import com.example.moneymanager.common.extension.toReadableDate
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.presentation.component.CategorySelectionSheet
import com.example.moneymanager.presentation.component.getCategoryIcon

@Composable
fun AddEditTransactionScreen(
    navController: NavController,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val transactionToEdit by viewModel.transactionToEdit.collectAsStateWithLifecycle()

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

    AddEditTransactionContent(
        transactionToEdit = transactionToEdit,
        categories = categories ?: emptyList(),
        assets = assets ?: emptyList(),
        selectedCategory = selectedCategory,
        onBackClick = { navController.popBackStack() },
        onSaveClick = { amount, note, type, date, categoryId, fromAssetId, toAssetId ->
            viewModel.saveTransaction(
                amount = amount,
                note = note,
                type = type,
                date = date,
                categoryId = categoryId,
                fromAssetId = fromAssetId,
                toAssetId = toAssetId
            )
        },
        onDeleteClick = {
            viewModel.deleteTransaction()
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
fun AddEditTransactionContent(
    transactionToEdit: Transaction?,
    categories: List<Category>,
    assets: List<Asset>,
    selectedCategory: Category?,
    onBackClick: () -> Unit,
    onSaveClick: (Double, String, TransactionType, Long, Int, Int, Int?) -> Unit,
    onDeleteClick: () -> Unit,
    onTypeChanged: (Boolean) -> Unit,
    onCategorySelected: (Category) -> Unit,
    isLoading: Boolean
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var selectedFromAsset by remember { mutableStateOf<Asset?>(null) }
    var selectedToAsset by remember { mutableStateOf<Asset?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }

    var fromAssetDropdownExpanded by remember { mutableStateOf(false) }
    var toAssetDropdownExpanded by remember { mutableStateOf(false) }

    var isLocalLocked by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionToEdit, assets) {
        if (selectedFromAsset == null && assets.isNotEmpty()) {
            selectedFromAsset = assets.first()
        }

        transactionToEdit?.let { transaction ->
            amount = transaction.amount.toLong().toString().formatToThousandSeparator()
            note = transaction.title
            selectedType = transaction.type
            selectedDateMillis = transaction.date

            val foundAsset = assets.find { it.id == transaction.fromAssetId }
            if (foundAsset != null) selectedFromAsset = foundAsset

            if (transaction.type == TransactionType.TRANSFER_OUT || transaction.type == TransactionType.TRANSFER_IN) {
                val foundTo = assets.find { it.id == transaction.toAssetId }
                if (foundTo != null) selectedToAsset = foundTo

                selectedType = TransactionType.TRANSFER_OUT
            }
        }
    }

    LaunchedEffect(selectedType) {
        if (selectedType != TransactionType.TRANSFER_OUT) {
            onTypeChanged(selectedType == TransactionType.INCOME)
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            isLocalLocked = false
        }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi?") },
            text = { Text("Saldo dompet akan dikembalikan. Data tidak bisa dipulihkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
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
                },
                actions = {
                    if (transactionToEdit != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Transaksi",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
                    label = {
                        Text(
                            text = "Pengeluaran",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFCDD2),
                        selectedLabelColor = Color(0xFFD32F2F)
                    )
                )
                FilterChip(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    label = {
                        Text(
                            text = "Pemasukan",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFC8E6C9),
                        selectedLabelColor = Color(0xFF388E3C)
                    )
                )
                FilterChip(
                    selected = selectedType == TransactionType.TRANSFER_OUT,
                    onClick = { selectedType = TransactionType.TRANSFER_OUT },
                    label = {
                        Text(
                            text = "Transfer",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFBBDEFB),
                        selectedLabelColor = Color(0xFF1976D2)
                    )
                )
            }

            if (selectedType == TransactionType.TRANSFER_OUT) {
                Text("Dari Dompet", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                ExposedDropdownMenuBox(
                    expanded = fromAssetDropdownExpanded,
                    onExpandedChange = { fromAssetDropdownExpanded = !fromAssetDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedFromAsset?.name ?: "Pilih Dompet",
                        onValueChange = {}, readOnly = true,
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromAssetDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = fromAssetDropdownExpanded,
                        onDismissRequest = { fromAssetDropdownExpanded = false }) {
                        assets.forEach { asset ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            asset.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        ); Text(
                                        "Saldo: Rp${asset.balance.toRupiah()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    }
                                },
                                onClick = {
                                    selectedFromAsset = asset; fromAssetDropdownExpanded = false
                                    if (selectedToAsset?.id == asset.id) selectedToAsset = null
                                }
                            )
                        }
                    }
                }

                Text("Ke Dompet", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                ExposedDropdownMenuBox(
                    expanded = toAssetDropdownExpanded,
                    onExpandedChange = { toAssetDropdownExpanded = !toAssetDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedToAsset?.name ?: "Pilih Tujuan",
                        onValueChange = {}, readOnly = true,
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toAssetDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.outline, focusedBorderColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(expanded = toAssetDropdownExpanded, onDismissRequest = { toAssetDropdownExpanded = false }) {
                        assets.filter { it.id != selectedFromAsset?.id }.forEach { asset ->
                            DropdownMenuItem(
                                text = { Column { Text(asset.name, style = MaterialTheme.typography.bodyLarge); Text("Saldo: Rp${asset.balance.toRupiah()}", style = MaterialTheme.typography.bodySmall, color = Color.Gray) } },
                                onClick = { selectedToAsset = asset; toAssetDropdownExpanded = false }
                            )
                        }
                    }
                }
            } else {
                Text(
                    "Kategori",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
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
                        val iconName = selectedCategory?.icon ?: "Kategori"
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

                Text("Dompet / Akun",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                ExposedDropdownMenuBox(
                    expanded = fromAssetDropdownExpanded,
                    onExpandedChange = { fromAssetDropdownExpanded = !fromAssetDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedFromAsset?.name ?: "Pilih Dompet",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true).
                            fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromAssetDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = fromAssetDropdownExpanded,
                        onDismissRequest = { fromAssetDropdownExpanded = false }
                    ) {
                        if (assets.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Belum ada dompet") },
                                onClick = { fromAssetDropdownExpanded = false }
                            )
                        } else {
                            assets.forEach { asset ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(asset.name, style = MaterialTheme.typography.bodyLarge)
                                            Text("Saldo: Rp${asset.balance.toRupiah()}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        selectedFromAsset = asset
                                        fromAssetDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { input -> amount = input.formatToThousandSeparator() },
                prefix = { Text("Rp ")},
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan / Judul") },
                modifier = Modifier.fillMaxWidth()
            )

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

            Spacer(modifier = Modifier.height(16.dp))

            val cleanAmount = amount.cleanToDouble()
            val isTransferValid = selectedType == TransactionType.TRANSFER_OUT && selectedFromAsset != null && selectedToAsset != null
            val isNormalValid = selectedType != TransactionType.TRANSFER_OUT && selectedFromAsset != null && selectedCategory != null
            val isFormValid = (isTransferValid || isNormalValid) && cleanAmount > 0

            Button(
                onClick = {
                    isLocalLocked = true
                    onSaveClick(
                        cleanAmount,
                        note,
                        selectedType,
                        selectedDateMillis,
                        selectedCategory?.id ?: 0,
                        selectedFromAsset!!.id,
                        selectedToAsset?.id,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && !isLocalLocked && isFormValid
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
fun PreviewAddEditTransactionContent() {
    MaterialTheme {
        AddEditTransactionContent(
            transactionToEdit = null,
            assets = emptyList(),
            categories = emptyList(),
            selectedCategory = null,
            onBackClick = {},
            onSaveClick = { _, _, _, _, _, _, _ -> },
            onDeleteClick = {},
            onTypeChanged = {},
            onCategorySelected = {},
            isLoading = false
        )
    }
}