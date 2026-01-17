package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.BackupMetadata
import kotlinx.coroutines.flow.Flow

interface BackupMetadataRepository {
    fun getBackups(): Flow<List<BackupMetadata>>
    suspend fun insertBackup(backup: BackupMetadata): Long
    suspend fun deleteBackup(backup: BackupMetadata)
}
