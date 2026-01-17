package com.example.moneymanager.data.local

import androidx.room.TypeConverter
import java.util.Date

enum class AssetType { BANK, E_WALLET, CRYPTO, GOLD, CASH }
enum class TransactionType { INCOME, EXPENSE, TRANSFER_IN, TRANSFER_OUT }

class Converters {
    @TypeConverter
    fun fromAssetType(value: AssetType?): String? = value?.name

    @TypeConverter
    fun toAssetType(value: String?): AssetType? = value?.let { AssetType.valueOf(it) }

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? = value?.let { TransactionType.valueOf(it) }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
