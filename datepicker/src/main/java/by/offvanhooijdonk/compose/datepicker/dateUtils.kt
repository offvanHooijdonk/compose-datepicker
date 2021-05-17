package by.offvanhooijdonk.compose.datepicker

import java.util.*

internal data class DateModel(val day: Int, val month: Int, val year: Int) {

    constructor(calendar: Calendar) : this(calendar.day, calendar.month, calendar.year)

    constructor(date: Date) : this(Calendar.getInstance().apply { time = date })

    fun isSameMonth(other: DateModel) = this.year == other.year && this.month == other.month

    fun toCalendar(): Calendar = Calendar.getInstance().apply {
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

internal fun isDateInRange(dateModel: DateModel, dateFrom: Date?, dateTo: Date?) =
    dateModel.toCalendar().let { current ->
        (dateFrom?.time?.let { it <= current.timeInMillis } ?: true)
                && (dateTo?.time?.let { it >= current.timeInMillis } ?: true)
    }

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
