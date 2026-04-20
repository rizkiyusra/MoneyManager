package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.BackupData

interface BackupDataRepository {
    suspend fun createBackupData(): BackupData

    suspend fun restoreBackupData(backupData: BackupData)
}