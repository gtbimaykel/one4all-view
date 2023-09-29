package com.mcp.one4all

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Patterns
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.mcp.one4all.databinding.ViewInputFieldBinding
import com.mcp.one4all.extension.setTextColorRes
import com.mcp.one4all.extension.setTextSizeSp
import com.mcp.one4all.util.InputValidator
import com.mcp.one4all.util.calendarDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class One4AllView : IOne4AllView, LinearLayout {
    private var isLabelAllCaps: Boolean = false
    private var validator = InputFieldValidator()
    private var onInputChangeListener: OnInputChangeListener? = null
    private var minLength: Int = 0
    private var maxLength: Int = 0
    private var dateFormat: String? = "MM/dd/yyyy"
    private var timeFormat: String? = "hh:mma"
    private val durationFormat: String = "H:m:s"
    var isRequired: Boolean = false
    private var isShowRequired: Boolean = true
    private var defaultDate: String = ""
    private var defaultTime: String = ""
    var minDate: MinDate = MinDate.None
    var maxDate: MaxDate = MaxDate.None
    private var hideLabelOnEdit: Boolean = false
    private lateinit var binding: ViewInputFieldBinding

    constructor(context: Context) : super(context) {
        initViews(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initViews(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initViews(attrs)
    }

    private fun initViews(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.InputField)
        if (array.hasValue(R.styleable.InputField_dateFormat)) {
            dateFormat = array.getString(R.styleable.InputField_dateFormat)
        }
        dateFormatter = SimpleDateFormat(dateFormat, Locale.US)
        if (array.hasValue(R.styleable.InputField_timeFormat)) {
            timeFormat = array.getString(R.styleable.InputField_timeFormat)
        }
        timeFormatter = SimpleDateFormat(timeFormat, Locale.US)
        if (array.hasValue(R.styleable.InputField_minDate)) {
            minDate = MinDate.values()[array.getInt(R.styleable.InputField_minDate, 0)]
        }
        if (array.hasValue(R.styleable.InputField_maxDate)) {
            maxDate = MaxDate.values()[array.getInt(R.styleable.InputField_maxDate, 0)]
        }
        binding = ViewInputFieldBinding.inflate(LayoutInflater.from(context), this, true)

        array.apply {
            initAttributes(array)
            array.recycle()
        }
    }

    private val hidePassword = PasswordTransformationMethod.getInstance()
    var inputType: Int = EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        set(value) {
            field = value
            binding.editText.inputType = value
        }

    override var endIconOnClickListener: OnClickListener? = null
        set(value) {
            field = value
            binding.apply {
                buttonEndIcon.setOnClickListener(value)
                if (viewType == ViewType.dropdown) {
                    editText.setOnClickListener(value)
                }
            }
        }

    var useAsPassword: Boolean = false
        set(value) {
            field = value
            if (value) {
                binding.apply {
                    editText.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                    editText.transformationMethod = hidePassword
                    endIcon = R.drawable.btn_password_toggle
                    buttonEndIcon.setOnClickListener {
                        buttonEndIcon.isChecked = !buttonEndIcon.isChecked
                        if (buttonEndIcon.isChecked) {
                            editText.transformationMethod = null
                        } else {
                            editText.transformationMethod = hidePassword
                        }
                    }
                }
            }
        }

    var useAsDropDown: Boolean = false
        set(value) {
            field = value
            if (value) {
                binding.apply {
                    buttonEndIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.btn_dropdown_toggle,
                        0
                    )
                    buttonEndIcon.isVisible = true
                    editText.isFocusable = false
                    editText.isClickable = true
                }
            }
        }

    var viewType: ViewType = ViewType.text
        set(value) {
            field = value

            when (value) {
                ViewType.text -> {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                }
                ViewType.textUsername -> {
                    inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                }
                ViewType.textMultiLine -> useAsMultilineText = true
                ViewType.textEmail -> useAsEmailAddress = true
                ViewType.textPassword -> useAsPassword = true
                ViewType.textPhone -> useAsPhoneNumber = true
                ViewType.textNumber, ViewType.currency -> useAsNumber = true
                ViewType.textUrl -> useAsUrl = true
                ViewType.time -> useAsTimePicker = true
                ViewType.date -> useAsDatePicker = true
                ViewType.checkbox -> useAsCheckbox = true
                ViewType.dropdown -> useAsDropDown = true
                ViewType.dateRange -> useAsDateRangePicker = true
            }
        }

    var useAsMultilineText: Boolean = false
        set(value) {
            field = value
            binding.editText.apply {
                inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE /*IMPORTANT: SET THIS FIRST*/
                isSingleLine = false
                isElegantTextHeight = true
                maxLines = 4
            }
        }

    var useAsNumber: Boolean = false
        set(value) {
            field = value
            inputType = InputType.TYPE_CLASS_NUMBER
        }

    var useAsEmailAddress: Boolean = false
        set(value) {
            field = value
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

    var useAsPhoneNumber: Boolean = false
        set(value) {
            field = value
            inputType = InputType.TYPE_CLASS_PHONE
        }

    var useAsUrl: Boolean = false
        set(value) {
            field = value
            inputType = InputType.TYPE_TEXT_VARIATION_URI
        }

    var useAsCheckbox: Boolean = false
        set(value) {
            field = value
        }

    var useAsTimePicker: Boolean = false
        set(value) {
            field = value
            if (value) {
                var time = defaultTime
                if (defaultTime.isNullOrEmpty()) {
                    time = timeFormatter.format(Date())
                }
                binding.apply {
                    buttonEndIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_clock,
                        0
                    )
                    buttonEndIcon.isVisible = true
                    editText.isFocusable = false
                    editText.isClickable = true
                    editText.setOnClickListener { showTimePicker(time) }
                }
            }
        }


    var useAsDateRangePicker: Boolean = false
        set(value) {
            field = value
            if (value) {
                var date = defaultDate
                if (defaultDate.isNullOrEmpty()) {
                    date = dateFormatter.format(Date())
                }
                binding.apply {
                    buttonEndIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_calendar,
                        0
                    )
                    buttonEndIcon.isVisible = true
                    editText.isFocusable = false
                    editText.isClickable = true
                    editText.setOnClickListener { showDateRangePicker(date) }
                }
            }
        }

    var useAsDatePicker: Boolean = false
        set(value) {
            field = value
            if (value) {
                var date = defaultDate
                if (defaultDate.isNullOrEmpty()) {
                    date = dateFormatter.format(Date())
                }
                binding.apply {
                    buttonEndIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_calendar,
                        0
                    )
                    buttonEndIcon.isVisible = true
                    editText.isFocusable = false
                    editText.isClickable = true
                    editText.setOnClickListener { showDatePicker(date) }
                }
            }
        }

    override var endIcon: Int = 0
        set(value) {
            field = value
            binding.apply {
                buttonEndIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, value, 0)
                buttonEndIcon.isVisible = value != 0
            }
        }
    override var isVisible: Boolean
        get() = super.getVisibility() == View.VISIBLE
        set(value) {
            when (value) {
                true -> super.setVisibility(View.VISIBLE)
                else -> super.setVisibility(View.GONE)
            }
        }

    var onSubmitListener: OnClickListener? = null

    private var dateFormatter: SimpleDateFormat = SimpleDateFormat(dateFormat)
    private var timeFormatter: SimpleDateFormat = SimpleDateFormat(timeFormat)
    private var durationFormatter: SimpleDateFormat = SimpleDateFormat(durationFormat)

    val dateAsMillis: Long
        get() {
            if (!useAsDatePicker) {
                return 0
            }

            try {
                val date = dateFormatter.parse(text)
                return date?.time!!
            } catch (e: ParseException) {
                return 0
            }

        }

    var label: String = ""
        set(value) {
            field = value
            binding.apply {
                val _label = if (isRequired && isShowRequired) "$value *"
                else value
                if (isLabelAllCaps) {
                    textLabel.hint = _label.toUpperCase()
                } else {
                    textLabel.hint = _label
                }
                editText.hint = _label
            }
        }

    private fun updateLabel() {
        binding.apply {
            if (hideLabelOnEdit)
                textLabel.isVisible = false
            else textLabel.isVisible = text.isNotEmpty()
        }
    }

    protected var text: String
        get() = binding.editText.text.toString()
        set(value) {
            binding.editText.setText(value)
        }

    var dateFrom: String? = ""
    var dateTo: String? = ""

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun afterTextChanged(editable: Editable) {
            if (editable.isNotEmpty()) {
                validator.validateAsTyping()
            }

            if (onInputChangeListener != null) {
                onInputChangeListener!!.onInputChange(this@One4AllView)
            }
            updateLabel()
            updateDisplayType(editable.toString())
        }
    }

    private fun updateDisplayType(string: String) {
        if (useAsDropDown) {
            binding.buttonEndIcon.isChecked = string.isNotEmpty()
        }
    }

    var helperText: String = ""
        set(value) {
            field = value
            binding.textHelper.text = value
            binding.textHelper.isVisible = value.isNotEmpty()
        }

    var minLines: Int = 1
        set(value) {
            field = value
            binding.editText.minLines = value
        }

    private fun initAttributes(array: TypedArray) {
        isShowRequired = array.getBoolean(R.styleable.InputField_showRequired, true)
        isRequired = array.getBoolean(R.styleable.InputField_required, false)
        inputType = array.getInt(
            R.styleable.InputField_android_inputType,
            EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        )
        viewType = ViewType.values()[array.getInt(R.styleable.InputField_type, 0)]
        setValue(array.getString(R.styleable.InputField_value) ?: "")

        isLabelAllCaps = array.getBoolean(R.styleable.InputField_labelAllCaps, false)
        label = array.getString(R.styleable.InputField_label) ?: ""

        if (array.hasValue(R.styleable.InputField_endIcon)) {
            endIcon = array.getResourceId(R.styleable.InputField_endIcon, 0)
        }

        defaultDate = array.getString(R.styleable.InputField_defaultDate) ?: ""
        defaultTime = array.getString(R.styleable.InputField_defaultTime) ?: ""
        minLength = array.getInteger(R.styleable.InputField_minLength, 0)
        maxLength = array.getInteger(R.styleable.InputField_maxLength, -1)
        hideLabelOnEdit = array.getBoolean(R.styleable.InputField_hideLabelOnEdit, false)

        binding.apply {
            editText.setOnFocusChangeListener { _, _ ->
                updateLabel()
            }

            if (array.hasValue(R.styleable.InputField_android_textColor)) {
                val textColor = array.getColor(R.styleable.InputField_android_textColor, 0)
                if (textColor != 0) {
                    editText.setTextColor(textColor)
                }
            }

            if (array.hasValue(R.styleable.InputField_android_gravity)) {
                val gravity = array.getInt(R.styleable.InputField_android_gravity, Gravity.START)
                editText.gravity = gravity
            }

            if (array.hasValue(R.styleable.InputField_android_minLines)) {
                minLines = array.getInt(R.styleable.InputField_android_minLines, 1)
            }

            if (array.hasValue(R.styleable.InputField_android_textSize)) {
                val indexOfAttrTextSize = array.getIndex(R.styleable.InputField_android_textSize)
                val textSize = array.getDimensionPixelSize(indexOfAttrTextSize, -1)
                editText.setTextSizeSp(textSize.toFloat())
            }

            if (array.hasValue(R.styleable.InputField_helperText)) {
                helperText = array.getString(R.styleable.InputField_helperText) ?: ""
            }

            if (array.hasValue(R.styleable.InputField_android_enabled)) {
                isEnabled = array.getBoolean(R.styleable.InputField_android_enabled, true)
            }

            editText.imeOptions = array.getInt(R.styleable.InputField_android_imeOptions, 0)
            addTextChangedListener(textWatcher)
            editText.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE) {
                    onSubmitListener?.onClick(editText)
                }
                true
            }

            if (array.hasValue(R.styleable.InputField_android_lineSpacingExtra)) {
                val lineSpacing =
                    array.getDimensionPixelSize(R.styleable.InputField_android_lineSpacingExtra, 0)
                editText.setLineSpacing(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        lineSpacing.toFloat(),
                        resources.displayMetrics
                    ), 1.0f
                );

            }
        }
    }


    fun setError(error: CharSequence?) {
        binding.apply {
            textError.text = error
            textError.visibility = if (error.isNullOrEmpty()) View.GONE else View.VISIBLE
            if (error.isNullOrEmpty()) {
                textLabel.setTextColorRes(R.color.colorNormal)
                editText.setBackgroundResource(R.drawable.textbox_background)
                textHelper.text = helperText
            } else {
                textLabel.text = label
                textLabel.setTextColorRes(R.color.errorColor)
                editText.setBackgroundResource(R.drawable.textbox_background_error)
                textHelper.text = ""
            }
        }
    }

    fun setValidator(validator: InputFieldValidator) {
        this.validator = validator
    }

    fun validate(showError: Boolean): Boolean {
        return validator.validate(showError)
    }

    private fun showTimePicker(defaultTime: String) {
        var mHour = 0
        var mMinute = 0

        val newCalendar = calendarDate()
        try {
            val currentTime =
                if (text.isEmpty()) defaultTime else text
            timeFormatter.parse(currentTime)?.apply {
                newCalendar.time = this
                newCalendar.timeInMillis
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->

                val pickedTime = calendarDate()
                pickedTime.clear()
                pickedTime[Calendar.HOUR_OF_DAY] = hourOfDay
                pickedTime[Calendar.MINUTE] = minute

                text = timeFormatter.format(pickedTime.time) ?: ""
            },
            mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    private fun showDatePicker(defaultDate: String) {

        val newCalendar = calendarDate()
        try {
            val currentDate =
                if (text.isEmpty()) defaultDate else text
            dateFormatter.parse(currentDate)?.apply {
                newCalendar.time = this
                newCalendar.timeInMillis
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val datePickerDialog =
            DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val pickedDate = calendarDate()
                    pickedDate.set(year, monthOfYear, dayOfMonth)
                    text = dateFormatter.format(pickedDate.time) ?: ""

                    //                Uncomment these to allow user to type into date field. Not fool proof yet.
                    //                editText.setSelection(editText.getText().length());
                    //                editText.setFocusableInTouchMode(true);
                    //                editText.requestFocus();
                },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH)
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

    private fun showDateRangePicker(defaultDate: String) {
        /* Constraints */
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
            text = dateFormatter.format(dateFrom.time) + " - " + dateFormatter.format(dateTo.time)
        }
        dateRangePickerDialog.show((context as AppCompatActivity).supportFragmentManager,"DateRangePicker")
    }

    open inner class InputFieldValidator {
        private var showError: Boolean = false

        fun validate(showError: Boolean): Boolean {
            this.showError = showError
            val isValid =
                validateRequired() && validateInputType() && validateTextLength() && validateOther(
                    this@One4AllView,
                    showError
                )
            if (isValid) {
                setError(null)
            }
            return isValid
        }

        fun validateAsTyping(): Boolean {
            var isValid =
                text.isEmpty() || validateInputType() && validateTextLength() && validateOther(
                    this@One4AllView,
                    showError
                )

            if (isValid) {
                setError(null)
            }
            return isValid
        }

        private fun validateTextLength(): Boolean {
            val length = binding.editText.length()
            var error: String? = null
            if (length > 0) {
                if (minLength > 0 && length < minLength) {
                    error = context.getString(R.string.validationError_too_short, minLength)
                } else if (maxLength in 1 until length) {
                    error = context.getString(R.string.validationError_too_long, maxLength)
                }
            }
            if (showError && error != null) {
                setError(error)
            }

            return error == null
        }

        private fun validateInputType(): Boolean {
            var isValid = true

            var labelType = label.toLowerCase()

            when (viewType) {
                ViewType.textEmail -> {
                    isValid = text.isEmpty() || InputValidator.isValidEmail(text)
                }
                ViewType.textPhone -> {
                    isValid = text.isEmpty() || isValidPhoneNumber(text)
                }
                ViewType.textUrl -> {
                    isValid = text.isEmpty() || InputValidator.isValidUrl(text)
                }
                ViewType.textUsername -> {
                    isValid = text.isEmpty() || InputValidator.isValidUsername(text)
                }
                ViewType.textPassword -> {
                    isValid = text.isEmpty() || InputValidator.isValidPassword(text)
                }
                else -> {
                }
            }

            if (useAsDatePicker) {
                labelType = "date"
                try {
                    dateFormatter.parse(text)
                } catch (e: ParseException) {
                    isValid = false
                    binding.editText.isFocusableInTouchMode = false
                }

            }

            //
            if (!isValid && showError) {
                if (viewType == ViewType.textUsername) {
                    setError(context.getString(R.string.validationError_invalid_username_format))
                } else if (viewType == ViewType.textPassword) {
                    setError(context.getString(R.string.validationError_invalid_password_format))
                } else {
                    setError(context.getString(R.string.validationError_invalid, labelType))
                }
            }

            return isValid
        }

        protected fun validateRequired(): Boolean {
            if (isRequired && text.isEmpty()) {
                if (showError) {
                    setError(context.getString(R.string.validationError_input_required))
                }
                return false
            }

            return true
        }

        /**
         * Clients can sub class and override this method if need to add other validations
         *
         * @param textInput
         * @return
         */
        open fun validateOther(textInput: One4AllView, showError: Boolean): Boolean {
            return true
        }
    }

    override fun setEnabled(enabled: Boolean) {
        binding.editText.isEnabled = enabled
    }

    override fun addTextChangedListener(textWatcher: TextWatcher) {
        binding.editText.addTextChangedListener(textWatcher)
    }

    fun grabFocus(): Boolean {
        return binding.editText.requestFocus()
    }

    private var _value: String = ""
    override fun value() = _value.trim()

    override fun setValue(value: String) {
        binding.apply {
            when (viewType) {
                ViewType.checkbox -> {
                    _value = value
                    text = _value
                }
                ViewType.currency -> {
                    _value = value
                    if (value.isNullOrEmpty()) {
                        _value = "0"
                    }
                    text = value
                }
                ViewType.textNumber -> {
                    _value = value
                    if (value.isEmpty()) {
                        _value = "0"
                    }
                    text = _value
                }
                ViewType.textPhone, ViewType.textEmail -> {
                    _value = value

                }
                else -> {
                    _value = value
                    text = _value
                }
            }
        }
    }

    interface OnInputChangeListener {
        fun onInputChange(inputView: One4AllView)
    }

    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        return if (phoneNumber.isNullOrEmpty()) {
            false
        } else {
            Patterns.PHONE.matcher(phoneNumber).matches()
        }
    }
}