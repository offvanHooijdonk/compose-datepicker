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
    // 9/24/2021 - !NOTE: We do NOT add ending days for the week or extra week to fit to maximum weeks (which is 6)
    // because initially date range is stubbed with an array of 7 * 6 length, and therefore layout is measured respectfully already.
    // If this situation changes and layout breaks, the logic of adding extra days/weeks can be found in older commits

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