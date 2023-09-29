package com.mcp.sample

import com.mcp.one4all.ViewType

data class SampleData(
    var label: String,
    var inputType: ViewType,
    var helperText: String = "",
    var defaultValue: String? = null,
    var isRequired: Boolean = false
)