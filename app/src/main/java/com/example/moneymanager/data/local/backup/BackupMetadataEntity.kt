package com.example.moneymanager.data.local.backup

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup_metadata")
data class BackupMetadataEntity(
    @PrimaryKey(autoGenerate = true) val backupId: Int = 0,
    val backupName: String,
    val backupPath: String,
    val backupType: String,
    val backupSize: Long,
    val totalAssets: Int,
    val totalTransactions: Int,
    val backupDate: Long,
    val isAutoBackup: Boolean = false
)
