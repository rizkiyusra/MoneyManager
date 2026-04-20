package com.example.moneymanager.presentation.backup

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupScreen(
    navController: NavController,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val backupState by viewModel.backupState.collectAsState()

    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val timestamp = dateFormat.format(Date())
    val jsonFileName = "MoneyManager_Backup_$timestamp.json"
    val csvFileName = "Laporan_Transaksi_$timestamp.csv"

    val exportJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportData(context, it) }
    }

    val importJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importData(context, it) }
    }

    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { viewModel.exportCsvData(context, it) }
    }

    LaunchedEffect(backupState) {
        when (val state = backupState) {
            is BackupState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is BackupState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    BackupScreenContent(
        backupState = backupState,
        onBackClick = { navController.popBackStack() },
        onExportJsonClick = { exportJsonLauncher.launch(jsonFileName) },
        onImportJsonClick = { importJsonLauncher.launch(arrayOf("*/*")) },
        onExportCsvClick = { exportCsvLauncher.launch(csvFileName) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreenContent(
    modifier: Modifier = Modifier,
    backupState: BackupState,
    onBackClick: () -> Unit,
    onExportJsonClick: () -> Unit,
    onImportJsonClick: () -> Unit,
    onExportCsvClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Laporan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (backupState is BackupState.Loading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Memproses data, mohon tunggu...", style = MaterialTheme.typography.bodyMedium)
            } else {

                Text(
                    text = "Backup Database (Sistem)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onExportJsonClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Buat Backup Data (.json)", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onImportJsonClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Pulihkan Data (.json)", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Laporan Transaksi (Excel)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onExportCsvClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Export ke Excel (.csv)", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Catatan:\nFile CSV dapat dibuka di aplikasi Spreadsheet (Excel, Google Sheets) untuk analisis lebih lanjut.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Kondisi Normal")
@Composable
fun BackupScreenPreviewIdle() {
    MaterialTheme {
        BackupScreenContent(
            backupState = BackupState.Idle,
            onBackClick = {},
            onExportJsonClick = {},
            onImportJsonClick = {},
            onExportCsvClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Kondisi Loading")
@Composable
fun BackupScreenPreviewLoading() {
    MaterialTheme {
        BackupScreenContent(
            backupState = BackupState.Loading,
            onBackClick = {},
            onExportJsonClick = {},
            onImportJsonClick = {},
            onExportCsvClick = {}
        )
    }
}