package com.mcp.one4all.util

import com.mcp.one4all.extension.clearTime
import com.mcp.one4all.extension.toDate
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

internal fun calendarDate(): Calendar =
    GregorianCalendar.getInstance().clearTime()

internal fun today(): Date = calendarDate().toDate()