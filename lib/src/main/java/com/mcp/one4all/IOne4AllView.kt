package com.mcp.one4all

import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener

interface IOne4AllView {

    fun value(): String
    fun setValue(value: String)

    var endIcon: Int
    var isVisible: Boolean

    var endIconOnClickListener: View.OnClickListener?
    fun addTextChangedListener(textWatcher: TextWatcher)

    fun setOnClickListener(l: OnClickListener?)
}