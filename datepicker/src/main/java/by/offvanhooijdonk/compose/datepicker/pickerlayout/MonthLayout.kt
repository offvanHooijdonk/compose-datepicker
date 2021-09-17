package by.offvanhooijdonk.compose.datepicker.pickerlayout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import by.offvanhooijdonk.compose.datepicker.ext.*
import by.offvanhooijdonk.compose.datepicker.ext.DAYS_IN_WEEK
import by.offvanhooijdonk.compose.datepicker.ext.isDateInRange
import by.offvanhooijdonk.compose.datepicker.ext.weekDaysNames
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DatePickerLayoutMonth(
    datesList: List<LocalDate?>,
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
            items(weekDaysNames) { dayName ->
                DayOfWeekItem(dayTitle = dayName)
            }
            items(items = datesList) { date ->
                DateItem(
                    date = date,
                    isPicked = date == pickedDate,
                    isToday = date == nowDate,
                    isCurrentMonth = date?.isSameMonth(displayMonth) ?: false,
                    isCanPick = date?.let { isDateInRange(date, dateFrom, dateTo) } ?: false,
                    onPick = {
                        onSelect(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun DateItem(
    date: LocalDate?,
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
                date?.let { onPick(it) }
            }
    ) {
        val (shape, text) = createRefs()

        if (date != null) {
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
        }

        Text(
            text = date?.dayOfMonth?.toString() ?: "", // todo any better
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