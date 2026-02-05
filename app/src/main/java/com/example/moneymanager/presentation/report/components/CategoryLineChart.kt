package com.example.moneymanager.presentation.report.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.moneymanager.domain.model.MonthlyCategoryTrend
import java.text.DateFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CategoryLineChart(
    data: List<MonthlyCategoryTrend>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    if (data.isEmpty()) return

    // State untuk menyimpan titik mana yang sedang diklik user
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    // Hitung nilai maksimum untuk skala Y (Buffer 30%)
    val maxAmount = remember(data) {
        val max = data.maxOfOrNull { it.total } ?: 1.0
        if (max == 0.0) 1.0 else max * 1.3
    }

    // Tools Menggambar Teks (Label Bulan)
    val textPaint = remember(textColor) {
        Paint().apply {
            color = textColor.toArgb()
            textAlign = Paint.Align.CENTER
            textSize = 30f
        }
    }

    // Tools Menggambar Teks Tooltip (Putih)
    val tooltipTextPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = 34f
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    // Locale yang benar (Fix Deprecated warning)
    val locale = remember { Locale.forLanguageTag("id-ID") }
    val monthSymbols = remember { DateFormatSymbols(locale).shortMonths }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(16.dp)
            .padding(top = 20.dp)
            .pointerInput(data) { // Key diganti data agar refresh jika data berubah
                detectTapGestures(
                    onTap = { tapOffset ->
                        val width = size.width.toFloat()

                        // 1. LOGIKA DETEKSI YANG LEBIH AMAN (FIX ERROR DIVISION)
                        if (data.size == 1) {
                            // Jika data cuma 1, titik ada di tengah
                            val pointX = width / 2
                            // Cek jarak tap ke tengah (toleransi 100px)
                            selectedIndex = if (abs(tapOffset.x - pointX) < 100f) {
                                if (selectedIndex == 0) null else 0
                            } else {
                                null
                            }
                        } else {
                            // Jika data banyak, hitung berdasarkan spasi
                            val pointSpacing = width / (data.size - 1).toFloat()

                            // (Fix Error: div(Float)) -> Menggunakan roundToInt untuk akurasi tap
                            val rawIndex = (tapOffset.x / pointSpacing).roundToInt()
                            val index = rawIndex.coerceIn(0, data.size - 1)

                            // Cek akurasi tap
                            val pointX = index * pointSpacing
                            selectedIndex = if (abs(tapOffset.x - pointX) < 80f) {
                                if (selectedIndex == index) null else index
                            } else {
                                null
                            }
                        }
                    }
                )
            }
    ) {
        val width = size.width
        val height = size.height

        // (Fix Error: Explicit .toFloat())
        val pointSpacing = if (data.size > 1) width / (data.size - 1).toFloat() else 0f

        val path = Path()
        val points = mutableListOf<Offset>()

        // 1. Kalkulasi Koordinat
        data.forEachIndexed { index, item ->
            // (Fix Error: Times(Int)) -> index dikonversi ke Float dulu
            val x = if (data.size == 1) width / 2 else index.toFloat() * pointSpacing

            // Y Axis Calculation
            val normalizedY = (item.total / maxAmount).toFloat()
            val y = height - (normalizedY * height)

            points.add(Offset(x, y))

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        // 2. Gambar Garis
        if (data.size > 1) {
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // 3. Gambar Titik & Label
        points.forEachIndexed { index, point ->
            val isSelected = index == selectedIndex

            // Lingkaran membesar jika dipilih
            val outerRadius = if (isSelected) 8.dp.toPx() else 5.dp.toPx()
            val innerRadius = if (isSelected) 4.dp.toPx() else 2.5.dp.toPx()

            drawCircle(color = lineColor, center = point, radius = outerRadius)
            drawCircle(color = Color.White, center = point, radius = innerRadius)

            // Label Bulan
            val monthStr = data[index].monthYear
            val monthIndex = monthStr?.takeLast(2)?.toIntOrNull()

            // (Fix: Assignment Lifted out of IF)
            val label = if (monthIndex != null && monthIndex in 1..12) {
                monthSymbols[monthIndex - 1]
            } else {
                monthStr?.takeLast(2) ?: ""
            }

            drawContext.canvas.nativeCanvas.drawText(
                label,
                point.x,
                height + 30.dp.toPx(),
                textPaint
            )

            // 4. Tooltip
            if (isSelected) {
                val amountText = formatCurrency(data[index].total, locale)

                val textWidth = tooltipTextPaint.measureText(amountText)
                val tooltipWidth = textWidth + 40f
                val tooltipHeight = 70f
                val tooltipX = point.x - (tooltipWidth / 2)
                val tooltipY = point.y - tooltipHeight - 25f

                // Background Tooltip
                drawRoundRect(
                    color = lineColor,
                    topLeft = Offset(tooltipX, tooltipY),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = CornerRadius(16f, 16f)
                )

                // Segitiga Panah
                val trianglePath = Path().apply {
                    moveTo(point.x - 15f, tooltipY + tooltipHeight)
                    lineTo(point.x + 15f, tooltipY + tooltipHeight)
                    lineTo(point.x, tooltipY + tooltipHeight + 15f)
                    close()
                }
                drawPath(trianglePath, lineColor)

                // Teks Tooltip
                drawContext.canvas.nativeCanvas.drawText(
                    amountText,
                    point.x,
                    tooltipY + 45f,
                    tooltipTextPaint
                )
            }
        }
    }
}

// Helper formatting rupiah (Sekarang menerima Locale agar dinamis)
private fun formatCurrency(amount: Double, locale: Locale): String {
    val format = NumberFormat.getCurrencyInstance(locale)
    format.maximumFractionDigits = 0
    return format.format(amount)
}