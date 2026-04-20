package com.example.moneymanager.domain.usecase.backup

import com.example.moneymanager.domain.repository.BackupDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExportCsvUseCase @Inject constructor(
    private val repository: BackupDataRepository
) {
    suspend operator fun invoke(): String {
        return withContext(Dispatchers.IO) {
            val backupData = repository.createBackupData()
            val transactions = backupData.transactions
            val categories = backupData.categories
            val assets = backupData.assets

            val categoryMap = categories.associateBy { it.id }
            val assetMap = assets.associateBy { it.id }

            val csvBuilder = StringBuilder()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            csvBuilder.append("No.,Tipe,Judul,Kategori,Aset Asal,Aset Tujuan,Nominal,Mata Uang,Nilai IDR,Tanggal,Catatan\n")

            var noUrut = 1
            for (trx in transactions) {
                val dateString = dateFormat.format(Date(trx.date))
                val safeTitle = trx.title.replace(",", " ").replace("\n", " ")
                val safeNote = trx.note?.replace(",", " ")?.replace("\n", " ") ?: ""

                val categoryName = categoryMap[trx.categoryId]?.name ?: "-"
                val fromAssetName = assetMap[trx.fromAssetId]?.name ?: "-"
                val toAssetName = trx.toAssetId?.let { assetMap[it]?.name } ?: "-"

                csvBuilder.append("${noUrut++},")
                csvBuilder.append("${trx.type.name},")
                csvBuilder.append("$safeTitle,")
                csvBuilder.append("$categoryName,")
                csvBuilder.append("$fromAssetName,")
                csvBuilder.append("$toAssetName,")
                csvBuilder.append("${trx.amount},")
                csvBuilder.append("${trx.currency},")
                csvBuilder.append("${trx.convertedAmountIDR},")
                csvBuilder.append("$dateString,")
                csvBuilder.append("$safeNote\n")
            }

            csvBuilder.toString()
        }
    }
}