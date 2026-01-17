package com.example.moneymanager.domain.model

data class BackupMetadata(
    val id: Int = 0,
    val name: String,
    val path: String,
    val type: String,
    val size: Long,
    val totalAssets: Int,
    val totalTransactions: Int,
    val date: Long,
    val isAutoBackup: Boolean = false
)
