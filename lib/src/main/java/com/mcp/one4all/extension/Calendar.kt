package com.mcp.one4all.extension

import java.util.Calendar
import java.util.Date

internal fun Calendar.toDate(): Date = Date(this.timeInMillis)

fun Calendar.clearTime(): Calendar {
    this[Calendar.MILLISECOND] = 0
    this[Calendar.MINUTE] = 0
    this[Calendar.SECOND] = 0
    this[Calendar.HOUR_OF_DAY] = 0
    this.timeInMillis
    return this
}

fun Calendar.clearDate(): Calendar {
    this[Calendar.YEAR] = 0
    this[Calendar.MONTH] = 0
    this[Calendar.DATE] = 0
    this.timeInMillis
    return this
}