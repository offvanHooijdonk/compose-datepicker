package by.offvanhooijdonk.compose.datepicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatePickLayout(
    modifier: Modifier = Modifier,
    monthOffset: Int = 0,
    currentPickedDate: Date,
    dateFrom: Date? = null,
    dateTo: Date? = null,
    onPick: (day: Int, month: Int, year: Int) -> Unit
) {
    val now = Calendar.getInstance()//.apply { add(Calendar.MONTH, monthOffset) }
    val nowDate = DateModel(
        now.day,
        now.month,
        now.year
    )
    val pickedDate = DateModel(currentPickedDate)
    val displayMonth = DateModel(
        1,
        now.apply { add(Calendar.MONTH, monthOffset) }.month,
        now.year
    )

    val datesList = calculateDatesRange(displayMonth.month, displayMonth.year)
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(now.time))
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyVerticalGrid(
            contentPadding = PaddingValues(0.dp),
            cells = GridCells.Fixed(7),
        ) {
            items(getDaysLabels()) { titleRes ->
                DayOfWeekItem(dayTitle = stringResource(titleRes))
            }
            items(items = datesList) { date ->
                CompositionLocalProvider(LocalIndication provides rememberRipple(bounded = false)) {
                    DateItem(
                        date = date,
                        nowDate = nowDate,
                        monthDate = displayMonth,
                        pickedDate = pickedDate,
                        canPick = isDateInRange(date, dateFrom, dateTo),
                        onPick = { onPick(it.day, it.month, it.year) }
                    )
                }
            }
        }
    }

}

@Composable
private fun DateItem(
    date: DateModel,
    pickedDate: DateModel,
    nowDate: DateModel,
    monthDate: DateModel,
    canPick: Boolean = true,
    onPick: (DateModel) -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(2.dp)
            .clickable(enabled = canPick) {
                onPick(date)
            }
    ) {
        val (shape, text) = createRefs()

        if (isHighlight(date, nowDate, pickedDate)) {
            DateShape(
                modifier = Modifier.constrainAs(shape) {
                    parentAll()
                    this.height = Dimension.fillToConstraints
                    this.width = Dimension.fillToConstraints
                },
                color = getHighlightColor(date, nowDate, pickedDate)
            )
        }

        Text(
            text = date.day.toString(),
            modifier = Modifier
                .constrainAs(text) {
                    parentAll()
                }
                .padding(8.dp),
            color = getDateTextColor(date, monthDate, pickedDate, canPick)
        )
    }
}

@Composable
private fun DateShape(
    modifier: Modifier,
    color: Color
) {
    Canvas(modifier = modifier, onDraw = {
        drawCircle(color = color)
    })
}

@Composable
private fun DayOfWeekItem(dayTitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp), contentAlignment = Alignment.Center
    ) {
        Text(text = dayTitle)
    }
}

@Preview(showSystemUi = true)
@Composable
fun Preview_DatePickLayout() {
    DatePickLayout(onPick = { _, _, _ -> }, currentPickedDate = Date())
}

internal data class DateModel(val day: Int, val month: Int, val year: Int) {

    constructor(calendar: Calendar) : this(calendar.day, calendar.month, calendar.year)

    constructor(date: Date) : this(Calendar.getInstance().apply { time = date })

    fun isSameMonth(other: DateModel) = this.year == other.year && this.month == other.month

    fun toCalendar(): Calendar = Calendar.getInstance().apply {
        this.day = this@DateModel.day; this.month = this@DateModel.month; this.year = this@DateModel.year
    }
}

private fun isHighlight(date: DateModel, now: DateModel, picked: DateModel) =
    date == now || date == picked

@Composable
private fun getHighlightColor(date: DateModel, now: DateModel, picked: DateModel) =
    when (date) {
        picked -> MaterialTheme.colors.secondary
        now -> MaterialTheme.colors.primary.copy(alpha = 0.1f) // todo extract
        else -> Color.White
    }

@Composable
private fun getDateTextColor(date: DateModel, currentMonth: DateModel, picked: DateModel, canPick: Boolean) =
    when {
        date == picked && canPick -> MaterialTheme.colors.onSecondary
        date.isSameMonth(currentMonth) && canPick -> MaterialTheme.colors.onSurface
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
    }

private fun ConstrainScope.parentAll() {
    top.linkTo(parent.top)
    end.linkTo(parent.end)
    bottom.linkTo(parent.bottom)
    start.linkTo(parent.start)
}

private fun calculateDatesRange(month: Int, year: Int): List<DateModel> {
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

private fun getDaysLabels(): List<Int> {
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

private val dayNames = listOf(
    R.string.day_sunday_short,
    R.string.day_monday_short,
    R.string.day_tuesday_short,
    R.string.day_wednesday_short,
    R.string.day_thursday_short,
    R.string.day_friday_short,
    R.string.day_saturday_short,
)

private fun isDateInRange(dateModel: DateModel, dateFrom: Date?, dateTo: Date?) =
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
