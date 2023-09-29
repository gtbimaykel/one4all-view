package com.mcp.one4all

import com.mcp.one4all.extension.plusDays
import com.mcp.one4all.util.today
import java.util.Date

enum class MinDate {
    None,
    Today,
    Tomorrow;

    fun date(): Date? {
        return when (this) {
            Today -> today()
            Tomorrow -> today().plusDays(1)
            else -> null
        }
    }
}

enum class MaxDate {
    None,
    Today,
    Yesterday;

    fun date(): Date? {
        return when (this) {
            Today -> today()
            Yesterday -> today().plusDays(-1)
            else -> null
        }
    }
}