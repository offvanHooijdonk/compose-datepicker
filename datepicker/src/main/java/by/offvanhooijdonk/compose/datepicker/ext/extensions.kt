package by.offvanhooijdonk.compose.datepicker.ext

import java.time.LocalDate
import java.time.Period


fun LocalDate.diffMonths(other: LocalDate): Int =
    Period.between(this, other).toTotalMonths().toInt()

fun LocalDate.isSameMonth(other: LocalDate) =
    this.monthValue == other.monthValue && this.year == other.year