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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import by.offvanhooijdonk.compose.datepicker.ext.*
import by.offvanhooijdonk.compose.datepicker.ext.DAYS_IN_WEEK
import by.offvanhooijdonk.compose.datepicker.ext.DateModel
import by.offvanhooijdonk.compose.datepicker.ext.calculateDatesRange
import by.offvanhooijdonk.compose.datepicker.ext.getDaysLabels
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatePickerLayout(
    modifier: Modifier = Modifier,
    monthOffset: Int = 0,
    currentPickedDate: Date,
    dateFrom: Date? = null,
    dateTo: Date? = null,
    onSelect: (day: Int, month: Int, year: Int) -> Unit
) {
    val now = Calendar.getInstance()// current date
    val nowDate = DateModel(now)
    val pickedDate = DateModel(currentPickedDate)
    val monthCalendar = now.copy().apply { add(Calendar.MONTH, monthOffset) }
    val displayedMonth = DateModel(
        monthCalendar.day,
        monthCalendar.month,
        monthCalendar.year
    )

    val datesList = calculateDatesRange(displayedMonth.month, displayedMonth.year)
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(now.time)) // todo extract text format
        }
        Spacer(modifier = Modifier.height(12.dp))

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
                        isPicked = date == pickedDate,
                        isToday = date == nowDate,
                        isCurrentMonth = date.isSameMonth(displayedMonth),
                        isCanPick = isDateInRange(date, dateFrom, dateTo),
                        onPick = { onSelect(it.day, it.month, it.year) }
                    )
                }
            }
        }
    }

}

@Composable
private fun DateItem(
    date: DateModel,
    isPicked: Boolean = false,
    isToday: Boolean = false,
    isCurrentMonth: Boolean = false,
    isCanPick: Boolean = true,
    onPick: (DateModel) -> Unit = {}
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
            text = date.day.toString(),
            modifier = Modifier
                .constrainAs(text) {
                    parentAll()
                }.padding(8.dp),
            color = getDateTextColor(isPicked,  isCurrentMonth, isCanPick)
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
    DatePickerLayout(onSelect = { _, _, _ -> }, currentPickedDate = Date())
}

private fun isHighlight(isPicked: Boolean, isToday: Boolean) = isPicked || isToday

@Composable
private fun getHighlightColor(isPicked: Boolean, isToday: Boolean) =
    when {
        isPicked -> MaterialTheme.colors.secondary
        isToday -> MaterialTheme.colors.primary.copy(alpha = 0.1f) // todo extract
        else -> Color.White
    }

@Composable
private fun getDateTextColor(isPicked: Boolean, isCurrentMonth: Boolean, canPick: Boolean) =
    when {
        isPicked && canPick -> MaterialTheme.colors.onSecondary
        isCurrentMonth && canPick -> MaterialTheme.colors.onSurface
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.4f) // todo extract
    }