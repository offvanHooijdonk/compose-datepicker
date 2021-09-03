package by.offvanhooijdonk.compose.datepicker.sample.ext

import java.text.DateFormat
import java.util.*

fun createDate(day: Int, month: Int, year: Int): Date =
    Calendar.getInstance().apply {
        timeInMillis = 0
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month)
        set(Calendar.YEAR, year)
    }.time

fun Date.toDateString(): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM).format(this)