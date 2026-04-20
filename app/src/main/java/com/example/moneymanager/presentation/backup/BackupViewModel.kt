package com.example.moneymanager.presentation.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.usecase.backup.ExportBackupUseCase
import com.example.moneymanager.domain.usecase.backup.ExportCsvUseCase
import com.example.moneymanager.domain.usecase.backup.ImportBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

sealed class BackupState {
    object Idle : BackupState()
    object Loading : BackupState()
    data class Success(val message: String) : BackupState()
    data class Error(val message: String) : BackupState()
}

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
    private val exportCsvUseCase: ExportCsvUseCase
) : ViewModel() {

    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState: StateFlow<BackupState> = _backupState

    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                val jsonString = exportBackupUseCase()

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }

                _backupState.value = BackupState.Success("Backup berhasil disimpan!")
            } catch (e: Exception) {
                _backupState.value = BackupState.Error(e.message ?: "Terjadi kesalahan saat backup")
            }
        }
    }

    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                val stringBuilder = StringBuilder()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                    }
                }
                val jsonString = stringBuilder.toString()

                importBackupUseCase(jsonString)

                _backupState.value = BackupState.Success("Data berhasil dipulihkan!")
            } catch (e: Exception) {
                _backupState.value = BackupState.Error(e.message ?: "File tidak valid atau rusak")
            }
        }
    }

    fun exportCsvData(context: Context, uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                val csvString = exportCsvUseCase()

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvString.toByteArray())
                }

                _backupState.value = BackupState.Success("Laporan CSV berhasil diekspor!")
            } catch (e: Exception) {
                _backupState.value = BackupState.Error(e.message ?: "Terjadi kesalahan saat export CSV")
            }
        }
    }

    fun resetState() {
        _backupState.value = BackupState.Idle
    }
}