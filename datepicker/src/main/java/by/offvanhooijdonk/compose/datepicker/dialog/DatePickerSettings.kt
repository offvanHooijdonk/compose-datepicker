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

    enum class HeaderStyle {
        COLOR_PRIMARY, COLOR_SURFACE
    }
}
