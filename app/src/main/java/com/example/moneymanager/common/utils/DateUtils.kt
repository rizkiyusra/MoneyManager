package com.example.moneymanager.common.utils

import java.util.Calendar

object DateUtils {
    fun getCurrentMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        return getMonthRange(calendar)
    }

    fun getPreviousMonthRange(currentStartTimestamp: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentStartTimestamp
        calendar.add(Calendar.MONTH, -1)
        return getMonthRange(calendar)
    }

    fun getNextMonthRange(currentStartTimestamp: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentStartTimestamp
        calendar.add(Calendar.MONTH, 1)
        return getMonthRange(calendar)
    }

    private fun getMonthRange(calendar: Calendar): Pair<Long, Long> {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.SECOND, -1)
        val endOfMonth = calendar.timeInMillis

        return Pair(startOfMonth, endOfMonth)
    }
}