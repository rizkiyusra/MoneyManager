package com.example.moneymanager.presentation.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanager.domain.model.DailySummary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun FinancialTrendChart(
    dailySummaries: List<DailySummary>,
    currentMonthStart: Long,
    onDateClick: (DailySummary) -> Unit,
    modifier: Modifier = Modifier
) {
    val chartData = remember(dailySummaries, currentMonthStart) {
        prepareMonthlyData(dailySummaries, currentMonthStart)
    }

    val maxAmount = remember(chartData) {
        chartData.maxOfOrNull { maxOf(it.income, it.expense) } ?: 1.0
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendDot(color = Color(0xFF4CAF50), text = "Pemasukan")
            Spacer(modifier = Modifier.width(16.dp))
            LegendDot(color = Color(0xFFF44336), text = "Pengeluaran")
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chartData) { dayData ->
                DayBarItem(
                    data = dayData,
                    maxAmount = maxAmount,
                    onClick = { onDateClick(dayData) }
                )
            }
        }
    }
}

@Composable
private fun DayBarItem(
    data: DailySummary,
    maxAmount: Double,
    onClick: () -> Unit
) {
    val dayLabel = remember(data.date) {
        SimpleDateFormat("dd", Locale.getDefault()).format(Date(data.date))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxHeight()
            .clickable { onClick() }
            .padding(horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (data.income > 0) {
                Bar(
                    value = data.income,
                    max = maxAmount,
                    color = Color(0xFF4CAF50)
                )
            }

            if (data.expense > 0) {
                Bar(
                    value = data.expense,
                    max = maxAmount,
                    color = Color(0xFFF44336)
                )
            }

            if (data.income == 0.0 && data.expense == 0.0) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = dayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun Bar(value: Double, max: Double, color: Color) {
    val heightFraction = (value / max).toFloat().coerceAtLeast(0.05f)

    Box(
        modifier = Modifier
            .width(8.dp)
            .fillMaxHeight(fraction = heightFraction)
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(color)
    )
}

@Composable
private fun LegendDot(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(8.dp).height(8.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

private fun prepareMonthlyData(
    existingData: List<DailySummary>,
    monthStartDate: Long
): List<DailySummary> {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = monthStartDate
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val fullList = mutableListOf<DailySummary>()

    val dataMap = existingData.associateBy {
        val c = Calendar.getInstance()
        c.timeInMillis = it.date
        c.get(Calendar.DAY_OF_MONTH)
    }

    for (day in 1..daysInMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val currentTimestamp = calendar.timeInMillis

        val summary = dataMap[day] ?: DailySummary(
            date = currentTimestamp,
            income = 0.0,
            expense = 0.0
        )
        fullList.add(summary)
    }
    return fullList
}

@Preview(showBackground = true)
@Composable
private fun TrendChartPreview() {
    val dummy = listOf(
        DailySummary(System.currentTimeMillis(), 100000.0, 50000.0),
        DailySummary(System.currentTimeMillis() - 86400000, 0.0, 200000.0)
    )
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FinancialTrendChart(
                dailySummaries = dummy,
                currentMonthStart = System.currentTimeMillis(),
                onDateClick = {}
            )
        }
    }
}