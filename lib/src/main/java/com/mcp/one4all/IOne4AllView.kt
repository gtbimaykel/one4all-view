package com.mcp.one4all

import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener

interface IOne4AllView {

    var dateFormat: String
    var timeFormat: String
    var endIcon: Int
    var isRequired: Boolean
    var isVisible: Boolean
    var onEndIconClickListener: OnClickListener?

    fun value(): String

    fun setValue(value: String)

    fun addTextChangedListener(textWatcher: TextWatcher)

    fun setOnClickListener(l: OnClickListener?)

    fun setError(error: CharSequence?)

    fun setValidator(validator: One4AllView.InputFieldValidator)

    fun validate(showError: Boolean): Boolean

    fun updateView()

    fun useAsText()

    fun useAsMultilineText()

    fun useAsEmailAddress()

    fun useAsPassword()

    fun useAsDropDown()

    fun useAsPhoneNumber()

    fun useAsNumber()

    fun useAsUrl()

    fun useAsTimePicker()

    fun useAsDatePicker()

    fun useAsCheckbox()

    fun useAsDateRangePicker()
}

interface OnInputChangeListener {
    fun onInputChange(inputView: One4AllView)
}