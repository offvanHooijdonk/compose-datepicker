package by.offvanhooijdonk.compose.datepicker.dialog

/**
 * Settings to vary DatePicker behavior and layout
 * @param yearsPickEnabled If @`true` then user can switch to years layout for fast switching to desired year
 * @param yearColumnsNumber A number of columns to display available years in. Default value is in [by.offvanhooijdonk.compose.datepicker.R.integer.dtpk_default_years_columns_number]
 * @param headerStyle Allows to pick whether the header containing picked date has background color of Primary color or Surface color of current Theme
 */
data class DatePickerSettings(
    val yearsPickEnabled: Boolean = true,
    val yearColumnsNumber: Int = 0,
    val headerStyle: HeaderStyle = HeaderStyle.COLOR_SURFACE,
) {
    companion object {
        fun builder() = Builder()
    }

    class Builder {
        private val default = DatePickerSettings()
        private var yearsPickEnabled: Boolean = default.yearsPickEnabled
        private var yearColumnsNumber: Int = default.yearColumnsNumber
        private var headerStyle: HeaderStyle = default.headerStyle

        fun build() = DatePickerSettings(
            yearsPickEnabled = yearsPickEnabled,
            yearColumnsNumber = yearColumnsNumber,
            headerStyle = headerStyle,
        )

        fun headerColorPrimary() { headerStyle = HeaderStyle.COLOR_PRIMARY }
        fun headerColorSurface() { headerStyle = HeaderStyle.COLOR_SURFACE }
        fun yearColumns(number: Int) { yearColumnsNumber = number }
        fun yearPickEnabled() { yearsPickEnabled = true }
        fun yearPickDisabled() { yearsPickEnabled = false }
        fun yearPick(isEnabled: Boolean) { yearsPickEnabled = isEnabled }
    }

    enum class HeaderStyle {
        COLOR_PRIMARY, COLOR_SURFACE
    }
}
