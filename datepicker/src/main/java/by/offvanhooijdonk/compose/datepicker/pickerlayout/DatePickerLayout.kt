package by.offvanhooijdonk.compose.datepicker.pickerlayout

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import by.offvanhooijdonk.compose.datepicker.ext.*
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatePickerLayout(
    modifier: Modifier = Modifier,
    displayMonth: LocalDate,
    initialPickedDate: LocalDate,
    dateFrom: LocalDate? = null,
    dateTo: LocalDate? = null,
    onSelect: (LocalDate) -> Unit
) {
    val nowDate = LocalDate.now()
    val pickedDate = remember { mutableStateOf(initialPickedDate) }

    val datesList = calculateDatesRange(displayMonth.monthValue, displayMonth.year) // todo fixate to 6 rows
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = displayMonth.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy")
                )
            ) // todo extract text format
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            contentPadding = PaddingValues(0.dp),
            cells = GridCells.Fixed(DAYS_IN_WEEK),
        ) {
            items(getDaysLabels()) { titleRes ->
                DayOfWeekItem(dayTitle = stringResource(titleRes))
            }
            items(items = datesList) { date ->
                CompositionLocalProvider(LocalIndication provides rememberRipple(bounded = false)) {
                    DateItem(
                        date = date,
                        isPicked = date == pickedDate.value,
                        isToday = date == nowDate,
                        isCurrentMonth = date.isSameMonth(displayMonth),
                        isCanPick = isDateInRange(date, dateFrom, dateTo),
                        onPick = {
                            pickedDate.value = it
                            onSelect(it)
                        }
                    )
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
            displayMonth = LocalDate.now(),
            initialPickedDate = LocalDate.now().plusDays(7)
        )
    }
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