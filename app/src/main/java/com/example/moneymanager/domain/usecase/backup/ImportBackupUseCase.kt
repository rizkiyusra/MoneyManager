package com.example.moneymanager.domain.usecase.backup

import com.example.moneymanager.domain.model.BackupData
import com.example.moneymanager.domain.repository.BackupDataRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImportBackupUseCase @Inject constructor(
    private val repository: BackupDataRepository,
    private val gson: Gson
) {
    suspend operator fun invoke(jsonString: String) {
        withContext(Dispatchers.IO) {
            val backupData = gson.fromJson(jsonString, BackupData::class.java)

            if (backupData == null) {
                throw IllegalArgumentException("File backup tidak valid atau rusak.")
            }

            repository.restoreBackupData(backupData)
        }
    }
}