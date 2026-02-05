package com.example.moneymanager.presentation.report.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.domain.model.CategoryExpense

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpensePieChart(
    expenses: List<CategoryExpense>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 200.dp,
    strokeWidth: Dp = 30.dp,
    onCategoryClick: (CategoryExpense) -> Unit // Callback saat kategori diklik
) {
    val totalAmount = expenses.sumOf { it.totalAmount }

    // Animasi
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "PieChartAnimation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- CHART AREA ---
        Box(
            modifier = Modifier.size(chartSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(chartSize)) {
                var startAngle = -90f

                expenses.forEach { item ->
                    val sweepAngle = (item.totalAmount / totalAmount).toFloat() * 360f * animatedProgress

                    drawArc(
                        color = Color(item.color),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle
                }
            }

            // Total di Tengah
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = totalAmount.toRupiah(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- LEGEND AREA (INTERACTIVE & PERCENTAGE) ---
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            maxItemsInEachRow = 2
        ) {
            expenses.forEach { item ->
                // Hitung Persentase
                val percentage = if (totalAmount > 0) (item.totalAmount / totalAmount * 100).toInt() else 0

                LegendItem(
                    color = Color(item.color),
                    name = item.categoryName,
                    amount = item.totalAmount.toRupiah(),
                    percentage = "$percentage%", // Tambahkan persen
                    onClick = { onCategoryClick(item) } // Tambahkan aksi klik
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    name: String,
    amount: String,
    percentage: String,
    onClick: () -> Unit
) {
    // Surface membuat efek 'ripple' saat diklik
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent,
        modifier = Modifier.padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indikator Warna
            Surface(
                modifier = Modifier.size(10.dp),
                shape = CircleShape,
                color = color
            ) {}

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // Tampilkan Persentase Bold
                    Text(
                        text = "($percentage)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = amount,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}