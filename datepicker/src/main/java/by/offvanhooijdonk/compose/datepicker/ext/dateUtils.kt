package by.offvanhooijdonk.compose.datepicker.ext

import by.offvanhooijdonk.compose.datepicker.R
import java.util.*

internal const val DAYS_IN_WEEK = 7

internal data class DateModel(val day: Int, val month: Int, val year: Int) {

    constructor(calendar: Calendar) : this(calendar.day, calendar.month, calendar.year)

    constructor(date: Date) : this(Calendar.getInstance().apply { time = date })

    fun isSameMonth(other: DateModel) = this.year == other.year && this.month == other.month

    fun toCalendar(): Calendar = Calendar.getInstance().apply {
        timeInMillis = 0
        this.day = this@DateModel.day; this.month = this@DateModel.month; this.year = this@DateModel.year
    }
}

internal fun calculateDatesRange(month: Int, year: Int): List<DateModel> {
    // add all days of current month
    val calendarMonth = Calendar.getInstance().apply {
        this.month = month; this.year = year
    }
    val dates = mutableListOf<DateModel>()
    dates.addAll(Array(calendarMonth.maxDays) { DateModel(it + 1, month, year) })

    // add days before 1st date to complete the week // todo add whole week even if week is complete
    val firstDayInWeek = calendarMonth.apply {
        day = getActualMinimum(Calendar.DAY_OF_MONTH)
    }.dayOfWeek
    val calPrevMonth = Calendar.getInstance().apply {
        this.month = month; this.year = year; this.day = 1
    }
    for (i in (firstDayInWeek - 1) downTo 1) {
        calPrevMonth.add(Calendar.DAY_OF_MONTH, -1)
        dates.add(0, DateModel(calPrevMonth))
    }

    // add days after last date to complete the week // todo add whole week even if week is complete
    val lastDayInWeek = calendarMonth.apply {
        day = getActualMaximum(Calendar.DAY_OF_MONTH)
    }.dayOfWeek
    val calNextMonth = Calendar.getInstance().apply {
        this.month = month
        this.year = year
        this.day = getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    for (i in (lastDayInWeek + 1)..7) {
        calNextMonth.add(Calendar.DAY_OF_MONTH, 1)
        dates.add(DateModel(calNextMonth))
    }
    return dates
}

internal fun getDaysLabels(): List<Int> {
    val days = mutableListOf<Int>()
    val firstDay = Calendar.getInstance().firstDayOfWeek
    for (i in firstDay - 1..6) {
        days.add(dayNames[i])
    }
    for (i in 0 until (firstDay - 1)) {
        days.add(dayNames[i])
    }
    return days
}

internal val dayNames = listOf(
    R.string.day_sunday_short,
    R.string.day_monday_short,
    R.string.day_tuesday_short,
    R.string.day_wednesday_short,
    R.string.day_thursday_short,
    R.string.day_friday_short,
    R.string.day_saturday_short,
)

internal fun isDateInRange(dateModel: DateModel, dateFrom: Date?, dateTo: Date?): Boolean {
    return dateModel.toCalendar().let { current ->
        (dateFrom?.toPlainDate()?.time?.let { it <= current.timeInMillis } ?: true)
                && (dateTo?.toPlainDate()?.time?.let { it >= current.timeInMillis } ?: true)
    }
}