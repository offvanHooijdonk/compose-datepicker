package by.offvanhooijdonk.compose.datepicker.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.integerResource
import by.offvanhooijdonk.compose.datepicker.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

internal val DAYS_IN_WEEK = DayOfWeek.values().size

internal fun calculateDatesRange(date: LocalDate): List<LocalDate> { // todo break into functions for testability
    // add all days of current month
    val monthDate = date.with(TemporalAdjusters.firstDayOfMonth())
    val dates = LinkedList<LocalDate>()//mutableListOf<LocalDate>()
    dates.addAll(Array(monthDate.lengthOfMonth()) { monthDate.withDayOfMonth(it + 1) })

    // add days before 1st date to complete the week
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val startOfCurrentWeek = monthDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    var dayBefore = startOfCurrentWeek
    val extraDaysBefore = mutableListOf<LocalDate>()
    while (dayBefore != monthDate) {
        extraDaysBefore.add(dayBefore)
        dayBefore = dayBefore.plusDays(1)
    }
    dates.addAll(0, extraDaysBefore)

    // add days after last date to complete the week
    val lastDayOfWeek = firstDayOfWeek.minus(1)
    val lastMonthDate = monthDate.with(TemporalAdjusters.lastDayOfMonth())
    val endOfWeek = lastMonthDate.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))
    var dayAfter = endOfWeek
    val extraDaysAfter = LinkedList<LocalDate>()
    while (dayAfter != lastMonthDate) {
        extraDaysAfter.add(0, dayAfter)
        dayAfter = dayAfter.minusDays(1)
    }
    dates.addAll(extraDaysAfter)

    // add rows of dates to fill to max weeks in month possible. NOTE those dates considered FAKE
    val extraWeeks = createExtraWeekRows(monthDate)
    dates.addAll(extraWeeks)

    return dates
}

private const val MAX_WEEKS = 6
private fun createExtraWeekRows(monthDate: LocalDate): List<LocalDate> {
    val extraWeeksNum = MAX_WEEKS - getWeeksNumber(monthDate)
    return if (extraWeeksNum > 0) {
        /** get last day of month's last week */
        val date = monthDate.with(TemporalAdjusters.lastDayOfMonth()).with(
            TemporalAdjusters.nextOrSame(
                WeekFields.of(Locale.getDefault()).firstDayOfWeek.minus(1)
            )
        )
        val dates = mutableListOf<LocalDate>()
        for (i in 1L..(extraWeeksNum * DayOfWeek.values().size)) {
            dates.add(date.plusDays(i))
        }
        dates
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

internal fun isDateInRange(date: LocalDate, dateFrom: LocalDate?, dateTo: LocalDate?): Boolean =
    dateFrom?.minusDays(1)?.isBefore(date) ?: true
            && dateTo?.plusDays(1)?.isAfter(date) ?: true


object PickerSettings {
    internal val defaultMaxYearsForward: Int
        @Composable
        get() = integerResource(id = R.integer.max_years_forward)
}