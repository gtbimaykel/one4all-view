package com.mcp.one4all.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.mcp.one4all.MaxDate
import com.mcp.one4all.MinDate
import com.mcp.one4all.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

open class DialogUtil {

    open fun showDatePickerDialog(
        context: Context, selectedDate: Calendar, minDate: MinDate, maxDate: MaxDate,
        dateFormatter: SimpleDateFormat,
        onSelect: (String) -> Unit
    ) {
        val datePickerDialog =
            DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val pickedDate = calendarDate()
                    pickedDate.set(year, monthOfYear, dayOfMonth)
                    val dateString = dateFormatter.format(pickedDate.time) ?: ""
                    onSelect(dateString)
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).apply {
                minDate.date()?.apply {
                    datePicker.minDate = this.time
                }
                maxDate.date()?.apply {
                    datePicker.maxDate = this.time
                }
            }
        datePickerDialog.show()
    }

    open fun showTimePickerDialog(
        context: Context, selectedTime: Calendar,
        timeFormatter: SimpleDateFormat,
        onSelect: (String) -> Unit
    ) {
        val hour = selectedTime.get(Calendar.HOUR_OF_DAY)
        val minute = selectedTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfHour ->

                val pickedTime = calendarDate()
                pickedTime.clear()
                pickedTime[Calendar.HOUR_OF_DAY] = hourOfDay
                pickedTime[Calendar.MINUTE] = minuteOfHour

                val timeString = timeFormatter.format(pickedTime.time) ?: ""
                onSelect(timeString)
            },
            hour, minute, false
        )
        timePickerDialog.show()
    }

    open fun showDateRangPickerDialog(context: Context, selectedRange: String, dateFormatter: SimpleDateFormat, onSelect: (String) -> Unit) {
        val selectOnlyFromTodayForwardConstraint =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val dateRangePickerDialog =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.title_select_dates)
//                .setTheme(R.style.ThemeMaterialCalendar)
                .setCalendarConstraints(selectOnlyFromTodayForwardConstraint.build())
                .build()
        dateRangePickerDialog.addOnPositiveButtonClickListener { range ->
            val dateFrom = Date(range.first)
            val dateTo = Date(range.second)
            val dateRange = dateFormatter.format(dateFrom.time) + " - " + dateFormatter.format(dateTo.time)
            onSelect(dateRange)
        }

        val fragmentManager: FragmentManager? = when (context) {
            is AppCompatActivity -> context.supportFragmentManager
            else -> null
        }
        fragmentManager?.apply {
//            dateRangePickerDialog.show(this,"DateRangePicker")
        }
    }

}