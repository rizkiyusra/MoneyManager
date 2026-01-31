package com.example.moneymanager.presentation.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.presentation.category.components.*
import com.example.moneymanager.presentation.components.categoryIconKeys
import com.example.moneymanager.presentation.components.getCategoryIcon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AddEditCategoryScreen(
    navController: NavController,
    viewModel: AddEditCategoryViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditCategoryViewModel.UiEvent.SaveCategorySuccess -> navController.popBackStack()
                is AddEditCategoryViewModel.UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    AddEditCategoryContent(
        name = viewModel.categoryName,
        onNameChange = viewModel::onNameChange,
        isIncome = viewModel.isIncomeCategory,
        onTypeChange = { viewModel.onTypeChange(it) },
        selectedIcon = viewModel.categoryIcon,
        onIconChange = { viewModel.onIconChange(it) },
        selectedColor = viewModel.categoryColor,
        onColorChange = { viewModel.onColorChange(it) },
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onSaveClick = { viewModel.onSaveCategory() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditCategoryContent(
    name: String,
    onNameChange: (String) -> Unit,
    isIncome: Boolean,
    onTypeChange: (Boolean) -> Unit,
    selectedIcon: String,
    onIconChange: (String) -> Unit,
    selectedColor: Int,
    onColorChange: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var showColorSheet by remember { mutableStateOf(false) }
    var showIconSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Simpan Kategori") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoryPreviewSection(
                name = name,
                color = selectedColor,
                icon = selectedIcon
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nama Kategori") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TypeSelectionSegmented(
                isIncome = isIncome,
                onTypeChanged = onTypeChange
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            SelectionItem(
                title = "Warna Kategori",
                subtitle = "Pilih warna identitas",
                icon = Icons.Default.Palette,
                onClick = { showColorSheet = true }
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(selectedColor))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=0.5f), CircleShape)
                )
            }

            SelectionItem(
                title = "Ikon Kategori",
                subtitle = "Pilih simbol visual",
                icon = Icons.Default.Star,
                onClick = { showIconSheet = true }
            ) {
                Icon(
                    imageVector = getCategoryIcon(selectedIcon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Simpan Kategori",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showColorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showColorSheet = false },
            sheetState = sheetState
        ) {
            ColorPickerContent(
                selectedColor = selectedColor,
                onColorSelected = {
                    onColorChange(it)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showColorSheet = false
                    }
                }
            )
        }
    }

    if (showIconSheet) {
        ModalBottomSheet(
            onDismissRequest = { showIconSheet = false },
            sheetState = sheetState
        ) {
            IconPickerContent(
                icons = categoryIconKeys,
                selectedIcon = selectedIcon,
                onIconSelected = {
                    onIconChange(it)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showIconSheet = false
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditCategoryScreenPreview() {
    MaterialTheme {
        AddEditCategoryContent(
            name = "Makan Siang",
            onNameChange = {},
            isIncome = false,
            onTypeChange = {},
            selectedIcon = "fastfood",
            onIconChange = {},
            selectedColor = android.graphics.Color.BLUE,
            onColorChange = {},
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onSaveClick = {}
        )
    }
}