package com.example.moneymanager.domain.usecase.backup

import com.example.moneymanager.domain.repository.BackupDataRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExportBackupUseCase @Inject constructor(
    private val repository: BackupDataRepository,
    private val gson: Gson
) {
    suspend operator fun invoke(): String {
        return withContext(Dispatchers.IO) {
            val backupData = repository.createBackupData()
            gson.toJson(backupData)
        }
    }
}