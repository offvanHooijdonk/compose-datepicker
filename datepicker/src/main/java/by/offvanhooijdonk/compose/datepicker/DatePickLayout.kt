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