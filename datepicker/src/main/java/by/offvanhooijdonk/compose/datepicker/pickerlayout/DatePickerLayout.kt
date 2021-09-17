package by.offvanhooijdonk.compose.datepicker.pickerlayout

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.offvanhooijdonk.compose.datepicker.R
import by.offvanhooijdonk.compose.datepicker.dialog.getDefaultDateTo
import by.offvanhooijdonk.compose.datepicker.ext.PickerSettings
import by.offvanhooijdonk.compose.datepicker.ext.calculateDatesRange
import by.offvanhooijdonk.compose.datepicker.ext.createYearsMatrix
import by.offvanhooijdonk.compose.datepicker.ext.emptyPlaceholderMonth
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun DatePickerLayout(
    modifier: Modifier = Modifier,
    displayDate: LocalDate,
    initialPickedDate: LocalDate,
    dateFrom: LocalDate? = null,
    dateTo: LocalDate? = null,
    mode: DatePickerMode = DatePickerMode.MONTHS,
    onSelect: (LocalDate) -> Unit,
    onYearChange: (Int) -> Unit = {},
    onModeToggle: () -> Unit = {}
) {
    val nowDate = LocalDate.now()
    val pickedDate = remember { mutableStateOf(initialPickedDate) }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            MonthLabel(
                displayMonth = displayDate,
                modesEnabled = true,
                mode = mode
            ) { // todo change [modesEnabled] to settings when implemented
                onModeToggle()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        val visibilityMode = remember(mode) { mutableStateOf(mode == DatePickerMode.MONTHS)}
        AnimatedVisibility(visible = visibilityMode.value, enter = fadeIn(), exit = fadeOut()) {
            val datesList = remember { mutableStateOf(emptyPlaceholderMonth) }
            LaunchedEffect(key1 = null) {
                datesList.value =
                    withContext(Dispatchers.Default) {
                        calculateDatesRange(displayDate)
                    }
            }

            DatePickerLayoutMonth(
                datesList = datesList.value,
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

        AnimatedVisibility(visible = !visibilityMode.value, enter = fadeIn(), exit = fadeOut()) {
            val dateFromActual = dateFrom ?: LocalDate.now()
            val dateToActual = dateTo ?: getDefaultDateTo(dateFromActual)
            val columnsNum = PickerSettings.yearColumnsNumber

            DatePickerLayoutYears(
                years = createYearsMatrix(dateFromActual, dateToActual, columnsNum),
                nowDate = nowDate,
                displayYear = displayDate.year,
                onSelect = {
                    onYearChange(it)
                }
            )
        }
    }
}

@Composable
private fun MonthLabel(
    displayMonth: LocalDate,
    modesEnabled: Boolean,
    mode: DatePickerMode = DatePickerMode.MONTHS,
    onClick: (() -> Unit) = {}
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

enum class DatePickerMode {
    MONTHS, YEARS
}