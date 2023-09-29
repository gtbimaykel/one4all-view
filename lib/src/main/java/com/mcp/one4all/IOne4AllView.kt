package com.mcp.one4all

import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener

interface IOne4AllView {

    var isRequired: Boolean
    var isShowRequiredIndicator: Boolean
    var isLabelAllCaps: Boolean
    var hideLabelOnEdit: Boolean
    var dateFormat: String?
    var timeFormat: String?

    fun value(): String
    fun setValue(value: String)

    var endIcon: Int
    var isVisible: Boolean

    var onEndIconClickListener: View.OnClickListener?
    fun addTextChangedListener(textWatcher: TextWatcher)

    fun setOnClickListener(l: OnClickListener?)
}

interface OnInputChangeListener {
    fun onInputChange(inputView: One4AllView)
}