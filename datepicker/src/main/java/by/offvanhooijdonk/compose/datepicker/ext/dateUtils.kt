package by.offvanhooijdonk.compose.datepicker.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.integerResource
import by.offvanhooijdonk.compose.datepicker.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

// TODO Move functions to packages they are used in where possible, so the lib could be easy split into several if needed
internal const val MAX_WEEKS = 6
internal val DAYS_IN_WEEK = DayOfWeek.values().size
internal val emptyPlaceholderMonth: List<LocalDate?> by lazy { Array<LocalDate?>(DAYS_IN_WEEK * MAX_WEEKS, init = { null }).toList() }

internal fun calculateDatesRange(date: LocalDate): List<LocalDate?> {
    val dates = LinkedList<LocalDate?>()

    // add all days of current month
    dates.addAll(getMonthDates(date))

    // add days before 1st date to complete the week
    dates.addAll(0, getFirstWeekLeadingPlaceHolders(date))

    // add days after last date to complete the week
    dates.addAll(getLastWeekTrailingPlaceHolders(date))

    return dates
}

internal fun getMonthDates(monthDate: LocalDate): List<LocalDate> {
    val monthStartDate = monthDate.with(TemporalAdjusters.firstDayOfMonth())
    val dates = mutableListOf<LocalDate>()
    dates.addAll(Array(monthDate.lengthOfMonth()) { monthStartDate.withDayOfMonth(it + 1) })

    return dates
}

internal fun getFirstWeekLeadingPlaceHolders(monthDate: LocalDate): List<LocalDate?> {
    val placeHolders = mutableListOf<LocalDate?>()
    val monthStartDate = monthDate.with(TemporalAdjusters.firstDayOfMonth())
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val startOfCurrentWeek = monthStartDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val diffBefore = (monthStartDate.dayOfWeek.value - startOfCurrentWeek.dayOfWeek.value).let {
        if (it >= 0) it else it + DAYS_IN_WEEK
    }
    for (i in 1..diffBefore) placeHolders.add(null)

    return placeHolders
}

internal fun getLastWeekTrailingPlaceHolders(monthDate: LocalDate): List<LocalDate?> {
    val placeHolders = mutableListOf<LocalDate?>()

    val monthStartDate = monthDate.with(TemporalAdjusters.firstDayOfMonth())
    val lastDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek.minus(1)
    val lastMonthDate = monthStartDate.with(TemporalAdjusters.lastDayOfMonth())
    val endOfWeek = lastMonthDate.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))
    val diffAfter = (endOfWeek.dayOfWeek.value - lastMonthDate.dayOfWeek.value).let {
        if (it >= 0) it else it + DAYS_IN_WEEK
    }
    for (i in 1..diffAfter) placeHolders.add(null)

    return placeHolders
}

internal fun createYearsMatrix(dateFrom: LocalDate, dateTo: LocalDate, cellsNumber: Int): List<List<Int>> {
    val yearsMatrix = mutableListOf<List<Int>>()
    var index = 0
    var year = dateFrom.year
    val toYear = dateTo.year
    var yearsRow = mutableListOf<Int>()

    while (year <= toYear) {
        if (index % cellsNumber == 0) {
            if (index - 1 >= 0) {
                yearsRow = mutableListOf()
            }
            yearsMatrix.add(yearsRow)
        }
        yearsRow.add(year++)
        index++
    }

    return yearsMatrix
}

private fun createExtraWeekRows(monthDate: LocalDate): List<LocalDate?> {
    val extraWeeksNum = MAX_WEEKS - getWeeksNumber(monthDate)
    return if (extraWeeksNum > 0) {
        Array<LocalDate?>(extraWeeksNum * DAYS_IN_WEEK) { null }.toList()
    } else {
        emptyList()
    }
}

private fun getWeeksNumber(date: LocalDate): Int {
    val fieldWeekOfYear = WeekFields.of(Locale.getDefault()).weekOfYear()
    val weekStartNum = date.with(TemporalAdjusters.firstDayOfMonth()).get(fieldWeekOfYear)
    val weekEndNum = date.with(TemporalAdjusters.lastDayOfMonth()).get(fieldWeekOfYear)
    return weekEndNum - weekStartNum + 1
}

internal val weekDaysNames: List<String> by lazy { getWeekDaysShortNames() }
internal fun getWeekDaysShortNames(): List<String> {
    val days = mutableListOf<String>()
    val locale = Locale.getDefault()
    val firstDay = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    for (i in 0L until DAYS_IN_WEEK) {
        days.add(firstDay.plus(i).getDisplayName(TextStyle.NARROW, locale))
    }

    return days
}

internal fun isDateInRange(date: LocalDate, dateFrom: LocalDate?, dateTo: LocalDate?): Boolean =
    dateFrom?.minusDays(1)?.isBefore(date) ?: true
            && dateTo?.plusDays(1)?.isAfter(date) ?: true


internal object PickerDefaults {
    internal val maxYearsForward: Int
        @Composable
        get() = integerResource(id = R.integer.max_years_forward)

    internal val yearColumnsNumber: Int
        @Composable
        get() = integerResource(id = R.integer.dtpk_default_years_columns_number)
}