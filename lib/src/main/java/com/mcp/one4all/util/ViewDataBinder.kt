package com.mcp.one4all.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.DrawableRes
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import com.mcp.one4all.IOne4AllView

interface ViewDataBinder<T : IOne4AllView> {

    fun setOnEditListener(view: T, callback: (() -> Unit)?) {
        view.setOnClickListener { callback?.invoke() }
    }

    fun setListener(view: T, listener: InverseBindingListener?) {
        if (listener != null) {
            view.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(editable: Editable) {
                    listener.onChange()
                }
            })
        }
    }

    fun setValue(view: T, value: String?) {
        if (view.value() != value) {
            view.setValue(value ?: "")
        }
    }

    fun value(view: T): String {
        return view.value()
    }

    fun setEndIconClickListener(view: T, onClickListener: View.OnClickListener?) {
        onClickListener?.apply {
            view.endIconOnClickListener = this
        }
    }

    fun setVisibility(view: T, isVisible: Boolean) {
        view.isVisible = isVisible
    }

    fun setEndIcon(view: T, @DrawableRes resId: Int) {
        view.endIcon = resId
    }

    fun bindOnDateRangeChange(
        view: T,
        dateFrom: MutableLiveData<String?>,
        dateTo: MutableLiveData<String?>,
    ) {
        view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { range ->
                    val values = range.split(" - ")
                    if (values.size == 2) {
                        dateFrom.postValue(values[0])
                        dateTo.postValue(values[1])
                    } else {
                        dateFrom.postValue("")
                        dateTo.postValue("")
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

}


