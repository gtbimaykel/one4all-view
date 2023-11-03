package com.mcp.one4all

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.mcp.one4all.databinding.ViewInputFieldBinding
import com.mcp.one4all.extension.parseBoolean
import com.mcp.one4all.extension.setTextColorRes
import com.mcp.one4all.extension.setTextSizeSp
import com.mcp.one4all.util.DialogUtil
import com.mcp.one4all.util.InputValidator
import com.mcp.one4all.util.calendarDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class One4AllView : IOne4AllView, LinearLayout {
    private lateinit var binding: ViewInputFieldBinding
    protected var imeOptions: Int = 0
    protected var isShowRequiredIndicator: Boolean = DefaultValues.isShowRequiredIndicator
    protected var isLabelAllCaps: Boolean = DefaultValues.isShowRequiredIndicator
    override var isRequired: Boolean = DefaultValues.isRequired
    private var defaultDate: String = ""
    private var defaultTime: String = ""
    var minDate: MinDate = MinDate.None
    var maxDate: MaxDate = MaxDate.None

    var onSubmitListener: OnClickListener? = null
    private var hideLabelOnEdit: Boolean = DefaultValues.hideLabelOnEdit
    private var validator = InputFieldValidator()
    private var onInputChangeListener: OnInputChangeListener? = null
    private var minLength: Int = 0
    private var maxLength: Int = -1
    private val hidePassword = PasswordTransformationMethod.getInstance()
    private val dialogUtil = DialogUtil()

    private var dateFormatter: SimpleDateFormat = SimpleDateFormat(DefaultValues.dateFormat)
    private var timeFormatter: SimpleDateFormat = SimpleDateFormat(DefaultValues.timeFormat)

    var onCheckChangedListener: ((IOne4AllView, Boolean) -> Unit)? = null

    override var dateFormat: String = DefaultValues.dateFormat
        set(value) {
            field = value
            dateFormatter = SimpleDateFormat(value)
        }
    override var timeFormat: String = DefaultValues.timeFormat
        set(value) {
            field = value
            timeFormatter = SimpleDateFormat(value)
        }

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
        val array = context.obtainStyledAttributes(attrs, R.styleable.One4AllView)
        binding = ViewInputFieldBinding.inflate(LayoutInflater.from(context), this, true)

        array.apply {
            initAttributes(array)
            array.recycle()
        }
    }

    var inputType: Int = EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        set(value) {
            field = value
            binding.editText.inputType = value
        }

    override var onEndIconClickListener: OnClickListener? = null
        set(value) {
            field = value
            binding.apply {
                buttonEndIcon.setOnClickListener(value)
                if (viewType == ViewType.dropdown) {
                    editText.setOnClickListener(value)
                }
            }
        }

    var viewType: ViewType = ViewType.text
        set(value) {
            field = value

            when (value) {
                ViewType.text -> useAsText()
                ViewType.textMultiLine -> useAsMultilineText()
                ViewType.textEmail -> useAsEmailAddress()
                ViewType.textPassword -> useAsPassword()
                ViewType.textPhone -> useAsPhoneNumber()
                ViewType.textNumber, ViewType.currency -> useAsNumber()
                ViewType.textUrl -> useAsUrl()
                ViewType.time -> useAsTimePicker()
                ViewType.date -> useAsDatePicker()
                ViewType.checkbox -> useAsCheckbox()
                ViewType.dropdown -> useAsDropDown()
                ViewType.dateRange -> useAsDateRangePicker()
            }
        }

    override fun useAsText() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    }

    override fun useAsPassword() {
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

    override fun useAsDropDown() {
        endIcon = R.drawable.btn_dropdown_toggle
        binding.apply {
            buttonEndIcon.isVisible = true
            editText.isFocusable = false
            editText.isClickable = true
        }
    }

    override fun useAsMultilineText() {
        binding.editText.apply {
            inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE /*IMPORTANT: SET THIS FIRST*/
            isSingleLine = false
            isElegantTextHeight = true
            maxLines = 4
        }
    }

    override fun useAsNumber() {
        inputType = InputType.TYPE_CLASS_NUMBER
    }

    override fun useAsEmailAddress() {
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    }

    override fun useAsPhoneNumber() {
        inputType = InputType.TYPE_CLASS_PHONE
    }

    override fun useAsUrl() {
        inputType = InputType.TYPE_TEXT_VARIATION_URI
    }

    override fun useAsCheckbox() {
        endIcon = R.drawable.btn_checkbox
        binding.apply {
            buttonEndIcon.setOnClickListener {
                buttonEndIcon.isChecked = !buttonEndIcon.isChecked
                setValue(buttonEndIcon.isChecked.toString())
                onCheckChangedListener?.invoke(this@One4AllView, buttonEndIcon.isChecked)
            }
            buttonEndIcon.isVisible = true
            editText.isFocusable = false
            editText.isClickable = false
        }
    }

    override fun useAsTimePicker() {
        var time = defaultTime
        if (defaultTime.isEmpty()) {
            time = timeFormatter.format(Date())
        }
        endIcon = R.drawable.ic_clock
        binding.apply {
            buttonEndIcon.isVisible = true
            editText.isFocusable = false
            editText.isClickable = false
            editText.setOnClickListener { showTimePicker(time) }
        }
    }

    override fun useAsDateRangePicker() {
        var date = defaultDate
        if (defaultDate.isEmpty()) {
            date = dateFormatter.format(Date())
        }
        endIcon = R.drawable.ic_calendar
        binding.apply {
            buttonEndIcon.isVisible = true
            editText.isFocusable = false
            editText.isClickable = false
            editText.setOnClickListener { showDateRangePicker(date) }
        }
    }

    override fun useAsDatePicker() {
        var date = defaultDate
        if (defaultDate.isEmpty()) {
            date = dateFormatter.format(Date())
        }
        endIcon = R.drawable.ic_calendar
        binding.apply {
            buttonEndIcon.isVisible = true
            editText.isFocusable = false
            editText.isClickable = false
            editText.setOnClickListener { showDatePicker(date) }
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

    val dateAsMillis: Long
        get() {
            if (viewType != ViewType.date) {
                return 0
            }

            return try {
                val date = dateFormatter.parse(text)
                date?.time ?: 0L
            } catch (e: ParseException) {
                0
            }

        }

    var label: String = ""
        set(value) {
            field = value
            binding.apply {
                val _label = if (isRequired && isShowRequiredIndicator) "$value *"
                else value
                if (isLabelAllCaps) {
                    textLabel.hint = _label.uppercase(Locale.getDefault())
                } else {
                    textLabel.hint = _label
                }
                editText.hint = _label
            }
        }

    protected var text: String
        get() = binding.editText.text.toString()
        set(value) {
            binding.editText.setText(value)
        }

    protected val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun afterTextChanged(editable: Editable) {
            if (editable.isNotEmpty()) {
                validator.validateAsTyping()
            }

            onInputChangeListener?.onInputChange(this@One4AllView)

            updateView()
        }
    }

    override fun updateView() {
        binding.apply {
            if (hideLabelOnEdit) {
                textLabel.isVisible = false
            } else {
                textLabel.isVisible = text.isNotEmpty()
            }

            if (viewType == ViewType.dropdown) {
                buttonEndIcon.isChecked = text.isNotEmpty()
            }
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
        if (array.hasValue(R.styleable.One4AllView_dateFormat)) {
            dateFormat = array.getString(R.styleable.One4AllView_dateFormat) ?: DefaultValues.dateFormat
        }
        dateFormatter = SimpleDateFormat(dateFormat, Locale.US)
        if (array.hasValue(R.styleable.One4AllView_timeFormat)) {
            timeFormat = array.getString(R.styleable.One4AllView_timeFormat) ?: DefaultValues.timeFormat
        }
        timeFormatter = SimpleDateFormat(timeFormat, Locale.US)
        if (array.hasValue(R.styleable.One4AllView_minDate)) {
            minDate = MinDate.values()[array.getInt(R.styleable.One4AllView_minDate, 0)]
        }
        if (array.hasValue(R.styleable.One4AllView_maxDate)) {
            maxDate = MaxDate.values()[array.getInt(R.styleable.One4AllView_maxDate, 0)]
        }
        isShowRequiredIndicator = array.getBoolean(
            R.styleable.One4AllView_showRequiredIndicator,
            DefaultValues.isShowRequiredIndicator
        )
        isRequired = array.getBoolean(R.styleable.One4AllView_required, DefaultValues.isRequired)
        inputType = array.getInt(
            R.styleable.One4AllView_android_inputType,
            EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        )
        viewType = ViewType.values()[array.getInt(R.styleable.One4AllView_type, 0)]
        setValue(array.getString(R.styleable.One4AllView_value) ?: "")

        isLabelAllCaps =
            array.getBoolean(R.styleable.One4AllView_labelAllCaps, DefaultValues.isLabelAllCaps)
        label = array.getString(R.styleable.One4AllView_label) ?: ""

        if (array.hasValue(R.styleable.One4AllView_endIcon)) {
            endIcon = array.getResourceId(R.styleable.One4AllView_endIcon, 0)
        }

        defaultDate = array.getString(R.styleable.One4AllView_defaultDate) ?: ""
        defaultTime = array.getString(R.styleable.One4AllView_defaultTime) ?: ""
        minLength = array.getInteger(R.styleable.One4AllView_minLength, 0)
        maxLength = array.getInteger(R.styleable.One4AllView_maxLength, -1)
        hideLabelOnEdit = array.getBoolean(R.styleable.One4AllView_hideLabelOnEdit, DefaultValues.hideLabelOnEdit)
        if (array.hasValue(R.styleable.One4AllView_android_minLines)) {
            minLines = array.getInt(R.styleable.One4AllView_android_minLines, 1)
        }
        if (array.hasValue(R.styleable.One4AllView_helperText)) {
            helperText = array.getString(R.styleable.One4AllView_helperText) ?: ""
        }
        if (array.hasValue(R.styleable.One4AllView_android_enabled)) {
            isEnabled = array.getBoolean(R.styleable.One4AllView_android_enabled, true)
        }
        imeOptions = array.getInt(R.styleable.One4AllView_android_imeOptions, 0)

        binding.apply {
            applyEditTextStyle(editText, array)
            initEditText(editText)
        }
    }

    private fun applyEditTextStyle(editText: EditText, array: TypedArray) {
        if (array.hasValue(R.styleable.One4AllView_android_textColor)) {
            val textColor = array.getColor(R.styleable.One4AllView_android_textColor, 0)
            if (textColor != 0) {
                editText.setTextColor(textColor)
            }
        }

        if (array.hasValue(R.styleable.One4AllView_android_gravity)) {
            val gravity = array.getInt(R.styleable.One4AllView_android_gravity, Gravity.START)
            editText.gravity = gravity
        }

        if (array.hasValue(R.styleable.One4AllView_android_textSize)) {
            val indexOfAttrTextSize = array.getIndex(R.styleable.One4AllView_android_textSize)
            val textSize = array.getDimensionPixelSize(indexOfAttrTextSize, -1)
            editText.setTextSizeSp(textSize.toFloat())
        }

        if (array.hasValue(R.styleable.One4AllView_android_lineSpacingExtra)) {
            val lineSpacing =
                array.getDimensionPixelSize(R.styleable.One4AllView_android_lineSpacingExtra, 0)
            editText.setLineSpacing(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    lineSpacing.toFloat(),
                    resources.displayMetrics
                ), 1.0f
            )
        }
    }

    protected fun initEditText(editText: EditText) {
        editText.apply {
            imeOptions = this@One4AllView.imeOptions
            setOnFocusChangeListener { _, _ ->
                updateView()
            }

            addTextChangedListener(textWatcher)
            setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE) {
                    onSubmitListener?.onClick(editText)
                }
                true
            }
        }
    }


    override fun setError(error: CharSequence?) {
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

    override fun setValidator(validator: InputFieldValidator) {
        this.validator = validator
    }

    override fun validate(showError: Boolean): Boolean {
        return validator.validate(showError)
    }

    private fun showTimePicker(defaultTime: String) {
        val selectedTime = calendarDate()
        try {
            val currentTime = if (text.isEmpty()) { defaultTime } else { text }
            timeFormatter.parse(currentTime)?.apply {
                selectedTime.time = this
                selectedTime.timeInMillis
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        dialogUtil.showTimePickerDialog(context, selectedTime, timeFormatter) {
            text = it
        }
    }

    private fun showDatePicker(defaultDate: String) {
        val selectedDate = calendarDate()
        try {
            val currentDate =
                if (text.isEmpty()) defaultDate else text
            dateFormatter.parse(currentDate)?.apply {
                selectedDate.time = this
                selectedDate.timeInMillis
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        dialogUtil.showDatePickerDialog(context, selectedDate, minDate, maxDate, dateFormatter) {
            text = it
        }
    }

    private fun showDateRangePicker(selectedRange: String) {
        dialogUtil.showDateRangPickerDialog(context, selectedRange, dateFormatter) {
            text = it
        }
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
            val length = text.length
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

            var labelType = label.lowercase(Locale.getDefault())

            when (viewType) {
                ViewType.textEmail -> {
                    isValid = text.isEmpty() || InputValidator.isValidEmail(text)
                }

                ViewType.textPhone -> {
                    isValid = text.isEmpty() || InputValidator.isValidPhoneNumber(text)
                }

                ViewType.textUrl -> {
                    isValid = text.isEmpty() || InputValidator.isValidUrl(text)
                }

                ViewType.textPassword -> {
                    isValid = text.isEmpty() || InputValidator.isValidPassword(text)
                }

                else -> {
                }
            }

            if (viewType == ViewType.date) {
                labelType = "date"
                try {
                    dateFormatter.parse(text)
                } catch (e: ParseException) {
                    isValid = false
                    binding.editText.isFocusableInTouchMode = false
                }

            }

            if (!isValid && showError) {
                if (viewType == ViewType.textPassword) {
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

    fun isChecked(): Boolean {
        binding.apply {
            return when (viewType) {
                ViewType.checkbox -> buttonEndIcon.isChecked
                else -> _value.parseBoolean()
            }
        }
    }

    override fun setValue(value: String) {
        binding.apply {
            when (viewType) {
                ViewType.checkbox -> {
                    _value = value
                    binding.buttonEndIcon.isChecked = value.parseBoolean()
                }

                ViewType.currency -> {
                    _value = value
                    if (value.isEmpty()) {
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
                    text = _value
                }

                else -> {
                    _value = value
                    text = _value
                }
            }
        }
    }
}
