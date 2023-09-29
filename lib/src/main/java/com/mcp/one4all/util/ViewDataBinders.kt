package com.mcp.one4all.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import com.google.android.material.slider.RangeSlider
import com.mcp.one4all.One4AllView

object ViewDataBinders {
    @JvmStatic
    @BindingAdapter("app:onClick")
    fun setOnEditListener(view: View, callback: (() -> Unit)?) {
        view.setOnClickListener { callback?.invoke() }
    }

    @JvmStatic
    @BindingAdapter(value = ["android:textAttrChanged"])
    fun setListener(textInput: One4AllView, listener: InverseBindingListener?) {
        if (listener != null) {
            textInput.addTextChangedListener(object : TextWatcher {
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

    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(textInput: One4AllView, text: String?) {
        if (textInput.text != text) {
            textInput.text = text ?: ""
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text")
    fun getText(textInput: One4AllView): String {
        return textInput.text
    }

    @JvmStatic
    @BindingAdapter("app:onEndIconClick")
    fun setEndIconClickListener(textInput: One4AllView, onClickListener: View.OnClickListener?) {
        onClickListener?.apply {
            textInput.endIconOnClickListener = this
        }
    }

    @JvmStatic
    @BindingAdapter("app:visible")
    fun setVisibility(view: View, isVisible: Boolean) {
        view.isVisible = isVisible
    }

    @JvmStatic
    @BindingAdapter("app:endIcon")
    fun setEndIcon(textInput: One4AllView, resId: Int) {
        textInput.endIcon = resId
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:valueFrom", "android:valueTo", "app:valueMin", "app:valueMax"],
        requireAll = true
    )
    fun bindOnChange(
        rangeSlider: RangeSlider,
        valueFrom: Int?,
        valueTo: Int?,
        valueMin: MutableLiveData<Int>,
        valueMax: MutableLiveData<Int>,
    ) {
        rangeSlider.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, _ ->
            if (valueFrom != null && valueTo != null) {
                var minValue = slider.values.minOrNull()?.toInt() ?: valueFrom
                var maxValue = slider.values.maxOrNull()?.toInt() ?: valueTo

                if (valueFrom > minValue) minValue = valueFrom
                if (valueTo < maxValue) maxValue = valueTo


                valueMin.postValue(minValue)
                valueMax.postValue(maxValue)
            }
        })
    }

    @JvmStatic
    @BindingAdapter(value = ["app:dateFrom", "app:dateTo"], requireAll = true)
    fun bindOnDateRangeChange(
        textInput: One4AllView,
        dateFrom: MutableLiveData<String?>,
        dateTo: MutableLiveData<String?>,
    ) {
        textInput.addTextChangedListener(object : TextWatcher {
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


