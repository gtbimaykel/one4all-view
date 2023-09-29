package com.mcp.one4all.util

import android.view.View
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import com.mcp.one4all.One4AllView

object One4AllViewDataBinder : ViewDataBinder<One4AllView>{

    @BindingAdapter("app:onClick")
    override fun setOnEditListener(view: One4AllView, callback: (() -> Unit)?) {
        super.setOnEditListener(view, callback)
    }

    @BindingAdapter(value = ["android:textAttrChanged"])
    override fun setListener(view: One4AllView, listener: InverseBindingListener?) {
        super.setListener(view, listener)
    }

    @BindingAdapter("android:text")
    override fun setValue(view: One4AllView, value: String?) {
        super.setValue(view, value)
    }

    @InverseBindingAdapter(attribute = "android:text")
    override fun value(view: One4AllView): String {
        return super.value(view)
    }

    @BindingAdapter("app:onEndIconClick")
    override fun setOnEndIconClickListener(view: One4AllView, onClickListener: View.OnClickListener?) {
        super.setOnEndIconClickListener(view, onClickListener)
    }

    @BindingAdapter("app:visible")
    override fun setVisibility(view: One4AllView, isVisible: Boolean) {
        super.setVisibility(view, isVisible)
    }

    @BindingAdapter("app:endIcon")
    override fun setEndIcon(view: One4AllView, @DrawableRes resId: Int) {
        super.setEndIcon(view, resId)
    }

    @BindingAdapter(value = ["app:dateFrom", "app:dateTo"], requireAll = true)
    override fun bindOnDateRangeChange(
        view: One4AllView,
        dateFrom: MutableLiveData<String?>,
        dateTo: MutableLiveData<String?>,
    ) {
        super.bindOnDateRangeChange(view, dateFrom, dateTo)
    }

}


