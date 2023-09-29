package com.mcp.one4all.extension

import com.mcp.one4all.util.calendarDate
import java.util.Calendar
import java.util.Date

fun Date.plusDays(numDays: Int?): Date {
    val result = this.toCalendar()
    numDays?.apply { result.add(Calendar.DATE, numDays) }
    return result.toDate()
}

fun Date.toCalendar(): Calendar =
    calendarDate().also { it.timeInMillis = this.time; it.timeInMillis }