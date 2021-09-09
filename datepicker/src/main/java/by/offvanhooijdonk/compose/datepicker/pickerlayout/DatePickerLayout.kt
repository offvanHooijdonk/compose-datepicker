package by.offvanhooijdonk.compose.datepicker.pickerlayout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import by.offvanhooijdonk.compose.datepicker.R
import by.offvanhooijdonk.compose.datepicker.ext.*
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatePickerLayout(
    modifier: Modifier = Modifier,
    displayDate: LocalDate,
    initialPickedDate: LocalDate,
    dateFrom: LocalDate? = null,
    dateTo: LocalDate? = null,
    onSelect: (LocalDate) -> Unit,
    onYearChange: (Int) -> Unit = {}
) {
    val nowDate = LocalDate.now()
    val pickedDate = remember { mutableStateOf(initialPickedDate) }
    val datesList = calculateDatesRange(displayDate)
    val mode = remember { mutableStateOf(DatePickerMode.MONTHS) }

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            MonthLabel(
                displayMonth = displayDate,
                modesEnabled = true,
                mode = mode.value
            ) { // todo change [modesEnabled] to settings when implemented
                mode.value = if (mode.value == DatePickerMode.MONTHS) DatePickerMode.YEARS else DatePickerMode.MONTHS
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (mode.value) {
            DatePickerMode.MONTHS -> {
                DatePickerLayoutMonth(
                    datesList = datesList,
                    pickedDate = pickedDate.value,
                    displayMonth = displayDate,
                    nowDate = nowDate,
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    onSelect = {
                        pickedDate.value = it
                        onSelect(it)
                    }
                )
            }
            DatePickerMode.YEARS -> {
                DatePickerLayoutYears(
                    years = Array(10) { 2017 + it }.toList(),
                    nowDate = nowDate,
                    displayYear = displayDate.year,
                    onSelect = {
                        onYearChange(it)
                        mode.value = DatePickerMode.MONTHS
                    }
                )
            }
        }
    }
}

@Composable
private fun MonthLabel(
    displayMonth: LocalDate,
    modesEnabled: Boolean,
    mode: DatePickerMode = DatePickerMode.MONTHS,
    onClick: (() -> Unit)
) {
    Row(modifier = Modifier.clickable(enabled = modesEnabled) { onClick() }) { // todo paddings for ripple
        Text(
            text = displayMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy")
            )
        ) // todo extract text format
        if (modesEnabled) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(id = if (mode == DatePickerMode.MONTHS) R.drawable.ic_arrow_drop_down_24 else R.drawable.ic_arrow_drop_up_24),
                tint = MaterialTheme.colors.onSurface, contentDescription = ""
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DatePickerLayoutMonth(
    datesList: List<LocalDate>,
    pickedDate: LocalDate,
    displayMonth: LocalDate,
    nowDate: LocalDate,
    dateFrom: LocalDate?,
    dateTo: LocalDate?,
    onSelect: (LocalDate) -> Unit
) {
    CompositionLocalProvider(LocalIndication provides rememberRipple(bounded = false)) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(DAYS_IN_WEEK),
        ) {
            items(getDaysLabels()) { titleRes ->
                DayOfWeekItem(dayTitle = stringResource(titleRes))
            }
            items(items = datesList) { date ->
                DateItem(
                    date = date,
                    isPicked = date == pickedDate,
                    isToday = date == nowDate,
                    isCurrentMonth = date.isSameMonth(displayMonth),
                    isCanPick = isDateInRange(date, dateFrom, dateTo),
                    onPick = {
                        onSelect(it)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DatePickerLayoutYears(
    years: List<Int>,
    displayYear: Int,
    nowDate: LocalDate,
    onSelect: (Int) -> Unit
) {
    CompositionLocalProvider(LocalIndication provides rememberRipple(bounded = false)) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(3)
        ) {
            items(years) { year ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 2.dp, vertical = 8.dp)
                        .clickable { onSelect(year) },
                    shape = RoundedCornerShape(percent = 50),
                    color = if (displayYear == year) MaterialTheme.colors.secondary else Color.Transparent
                ) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.h6,
                            text = year.toString(),
                            color = when {
                                displayYear == year -> MaterialTheme.colors.onSecondary
                                nowDate.year == year -> MaterialTheme.colors.primary
                                else -> MaterialTheme.colors.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateItem(
    date: LocalDate,
    isPicked: Boolean = false,
    isToday: Boolean = false,
    isCurrentMonth: Boolean = false,
    isCanPick: Boolean = true,
    onPick: (LocalDate) -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(2.dp)
            .clickable(enabled = isCanPick) {
                onPick(date)
            }
    ) {
        val (shape, text) = createRefs()

        if (isHighlight(isPicked, isToday)) {
            DateShape(
                modifier = Modifier.constrainAs(shape) {
                    parentAll()
                    this.height = Dimension.fillToConstraints
                    this.width = Dimension.fillToConstraints
                },
                color = getHighlightColor(isPicked, isToday)
            )
        }

        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier
                .constrainAs(text) {
                    parentAll()
                }
                .padding(8.dp),
            color = getDateTextColor(isPicked, isCurrentMonth, isCanPick)
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
    PreviewAppTheme(darkTheme = false) {
        DatePickerLayout(
            onSelect = {},
            displayDate = LocalDate.now(),
            initialPickedDate = LocalDate.now().plusDays(7)
        )
    }
}

private enum class DatePickerMode {
    MONTHS, YEARS
}

private fun isHighlight(isPicked: Boolean, isToday: Boolean) = isPicked || isToday

@Composable
private fun getHighlightColor(isPicked: Boolean, isToday: Boolean) =
    when {
        isPicked -> MaterialTheme.colors.secondary
        isToday -> MaterialTheme.colors.primary.copy(alpha = 0.1f) // todo extract
        else -> MaterialTheme.colors.background
    }

@Composable
private fun getDateTextColor(isPicked: Boolean, isCurrentMonth: Boolean, canPick: Boolean) =
    when {
        isPicked && canPick -> MaterialTheme.colors.onSecondary
        isPicked && !canPick -> MaterialTheme.colors.onSecondary.copy(alpha = 0.4f) // todo extract
        isCurrentMonth && canPick -> MaterialTheme.colors.onSurface
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.4f) // todo extract
    }