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

internal const val MAX_WEEKS = 6
internal val DAYS_IN_WEEK = DayOfWeek.values().size
internal val emptyPlaceholderMonth: List<LocalDate?> = Array<LocalDate?>(DAYS_IN_WEEK * MAX_WEEKS, init = { null }).toList()

internal fun calculateDatesRange(date: LocalDate): List<LocalDate?> { // todo break into functions for testability
    // add all days of current month
    val monthDate = date.with(TemporalAdjusters.firstDayOfMonth())
    val dates = LinkedList<LocalDate?>()
    dates.addAll(Array(monthDate.lengthOfMonth()) { monthDate.withDayOfMonth(it + 1) })

    // add days before 1st date to complete the week
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val startOfCurrentWeek = monthDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val diffBefore = (monthDate.dayOfWeek.value - startOfCurrentWeek.dayOfWeek.value).let {
        if (it >= 0) it else it + DAYS_IN_WEEK
    }
    for (i in 1..diffBefore) dates.add(0, null)

    // add days after last date to complete the week
    val lastDayOfWeek = firstDayOfWeek.minus(1)
    val lastMonthDate = monthDate.with(TemporalAdjusters.lastDayOfMonth())
    val endOfWeek = lastMonthDate.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))
    val diffAfter = (endOfWeek.dayOfWeek.value - lastMonthDate.dayOfWeek.value).let {
        if (it >= 0) it else it + DAYS_IN_WEEK
    }
    for (i in 1..diffAfter) dates.add(null)

    // add rows of dates to fill to max weeks in month possible. NOTE those dates considered FAKE
    dates.addAll(createExtraWeekRows(monthDate))

    return dates
}

internal fun createYearsMatrix(dateFrom: LocalDate, dateTo: LocalDate, cellsNumber: Int): List<List<Int>> {
    val yearsMatrix = mutableListOf<List<Int>>()
    var index = 0
    var year = dateFrom.year
    val toYear = dateTo.year
    var yearsRow = mutableListOf<Int>()

    while (year <= toYear) {
        if (index % 3 == 0) {
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

internal val weekDaysNames: List<String> by lazy { getWeekDaysShortNames() } // todo use LocalProvider
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


object PickerSettings {
    internal val defaultMaxYearsForward: Int
        @Composable
        get() = integerResource(id = R.integer.max_years_forward)

    internal val yearColumnsNumber: Int
        @Composable
        get() = integerResource(id = R.integer.years_columns_number)
}