package com.example.moneymanager.data.local.callback

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject

class DatabaseSeeder @Inject constructor() : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        db.beginTransaction()
        try {
            val now = System.currentTimeMillis()
            seedAssets(db, now)
            seedCategories(db, now)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    private fun seedAssets(db: SupportSQLiteDatabase, now: Long) {
        db.execSQL("""
            INSERT INTO assets (
                assetId, assetName, assetType, currentBalance, 
                balanceUnit, currencySymbol, isActive, sortOrder, 
                createdDate, lastModified
            ) VALUES (
                1, 'Dompet', 'CASH', 1000000.0, 
                'IDR', 'Rp', 1, 0, 
                $now, $now
            )
        """)
    }

    private fun seedCategories(db: SupportSQLiteDatabase, now: Long) {
        val categories = listOf(
            "1, 'Makanan', 'Makan & Minum', 0, -1047040, 'fastfood'",
            "2, 'Transport', 'Bensin & Kendaraan', 0, -16776961, 'transport'",
            "3, 'Belanja', 'Kebutuhan Harian', 0, -16711681, 'shopping'",
            "4, 'Kesehatan', 'Obat & Dokter', 0, -65281, 'medical'",
            "5, 'Lain-lain', 'Umum', 0, -7829368, 'category'",
            "6, 'Gaji', 'Gaji Bulanan', 1, -16711936, 'salary'",
            "7, 'Bonus', 'Tunjangan', 1, -16728876, 'salary'",
            "8, 'Pemberian', 'Hadiah', 1, -256, 'category'"
        )

        categories.forEach { values ->
            db.execSQL("""
                INSERT INTO categories (
                    categoryId, categoryName, categoryDescription, 
                    isIncomeCategory, categoryColor, categoryIcon, 
                    isSystemCategory, isActive, usageCount, createdDate
                ) VALUES ($values, 1, 1, 0, $now)
            """)
        }
    }
}