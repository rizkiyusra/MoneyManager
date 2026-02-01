package com.example.moneymanager.data.local.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "transactions_fts")
@Fts4(contentEntity = TransactionEntity::class)
data class TransactionFtsEntity(
    @ColumnInfo(name = "transactionTitle")
    val transactionTitle: String,
    @ColumnInfo(name = "transactionNote")
    val transactionNote: String
)
