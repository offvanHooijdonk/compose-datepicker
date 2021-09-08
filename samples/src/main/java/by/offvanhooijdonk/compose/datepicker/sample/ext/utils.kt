package by.offvanhooijdonk.compose.datepicker.sample.ext

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

internal fun LocalDate.toDateString(): String =
    this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))