package com.mcp.one4all.extension

import java.util.Locale

fun String.capitalizeFirstLetter(): String = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.ROOT
    ) else it.toString()
}

fun String.parseBoolean(): Boolean {
    return when (this.lowercase(Locale.getDefault())) {
        "1", "true" -> true
        else -> false
    }
}