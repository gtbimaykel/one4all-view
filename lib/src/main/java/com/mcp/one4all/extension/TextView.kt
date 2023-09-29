package com.mcp.one4all.extension

import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


internal fun TextView.setTextColorRes(@ColorRes color: Int) {
    if (color != 0) {
        setTextColor(ContextCompat.getColor(this.context, color))
    }
}

internal fun TextView.setTextSizeSp(size: Float) =
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)