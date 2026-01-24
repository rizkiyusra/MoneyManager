package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.backup.BackupMetadataDao
import com.example.moneymanager.data.local.backup.BackupMetadataEntity
import com.example.moneymanager.domain.model.BackupMetadata
import com.example.moneymanager.domain.repository.BackupMetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BackupMetadataRepositoryImpl @Inject constructor(
    private val dao: BackupMetadataDao
) : BackupMetadataRepository {

    override fun getBackups(): Flow<List<BackupMetadata>> =
        dao.getAllBackups().map { list -> list.map { it.toDomain() } }

    override suspend fun insertBackup(backup: BackupMetadata): Long =
        dao.insertBackup(backup.toEntity())

    override suspend fun deleteBackup(backup: BackupMetadata) =
        dao.deleteBackup(backup.toEntity())

    private fun BackupMetadataEntity.toDomain() = BackupMetadata(
        id = backupId,
        name = backupName,
        path = backupPath,
        type = backupType,
        size = backupSize,
        totalAssets = totalAssets,
        totalTransactions = totalTransactions,
        date = backupDate,
        isAutoBackup = isAutoBackup
    )

    private fun BackupMetadata.toEntity() = BackupMetadataEntity(
        backupId = id,
        backupName = name,
        backupPath = path,
        backupType = type,
        backupSize = size,
        totalAssets = totalAssets,
        totalTransactions = totalTransactions,
        backupDate = date,
        isAutoBackup = isAutoBackup
    )
}
