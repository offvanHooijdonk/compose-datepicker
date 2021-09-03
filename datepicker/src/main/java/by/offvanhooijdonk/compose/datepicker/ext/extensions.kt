package by.offvanhooijdonk.compose.datepicker.ext

import java.util.*

internal var Calendar.day: Int
    get() = this.get(Calendar.DAY_OF_MONTH)
    set(value) = this.set(Calendar.DAY_OF_MONTH, value)

internal var Calendar.month: Int
    get() = this.get(Calendar.MONTH)
    set(value) = this.set(Calendar.MONTH, value)
internal var Calendar.year: Int
    get() = this.get(Calendar.YEAR)
    set(value) = this.set(Calendar.YEAR, value)
internal val Calendar.maxDays: Int get() = this.getActualMaximum(Calendar.DAY_OF_MONTH)
internal val Calendar.dayOfWeek: Int // performing correction, cause `DAY_OF_WEEK` returns constants, where SUNDAY always 1, etc.
    get() = get(Calendar.DAY_OF_WEEK).let { num ->
        (num - (firstDayOfWeek - 1)).let { if (it <= 0) it + 7 else it }
    }
internal fun Calendar.copy() = Calendar.getInstance().also { it.timeInMillis = this.timeInMillis }

internal fun Date.toPlainDate(): Date {
    return Calendar.getInstance().apply {
        time = this@toPlainDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

internal fun Date.plusMonths(monthsToAdd: Int): Date =
    Calendar.getInstance().apply {
        time = this@plusMonths
        add(Calendar.MONTH, monthsToAdd)
    }.time