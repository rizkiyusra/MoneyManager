package com.example.moneymanager.data.local.backup

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupMetadataDao {
    @Query("SELECT * FROM backup_metadata ORDER BY backupDate DESC")
    fun getAllBackups(): Flow<List<BackupMetadataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackup(backup: BackupMetadataEntity): Long

    @Delete
    suspend fun deleteBackup(backup: BackupMetadataEntity)
}
