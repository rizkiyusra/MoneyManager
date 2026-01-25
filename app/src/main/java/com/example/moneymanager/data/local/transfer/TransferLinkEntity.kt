package com.example.moneymanager.data.local.transfer

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.moneymanager.data.local.transaction.TransactionEntity

@Entity(
    tableName = "transfer_links",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["transactionId"],
            childColumns = ["transferOutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["transactionId"],
            childColumns = ["transferInId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("transferOutId"),
        Index("transferInId")
    ]
)
data class TransferLinkEntity(
    @PrimaryKey(autoGenerate = true)
    val linkId: Int = 0,
    val transferOutId: Int,
    val transferInId: Int
)