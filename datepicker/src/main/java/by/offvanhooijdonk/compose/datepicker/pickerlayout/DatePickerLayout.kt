package by.offvanhooijdonk.compose.datepicker.pickerlayout

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import by.offvanhooijdonk.compose.datepicker.dialog.DatePickerSettings
import by.offvanhooijdonk.compose.datepicker.dialog.getDefaultDateTo
import by.offvanhooijdonk.compose.datepicker.ext.PickerDefaults
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
    settings: DatePickerSettings = DatePickerSettings(),
    onSelect: (LocalDate) -> Unit,
    onYearChange: (Int) -> Unit = {},
    onModeToggle: () -> Unit = {}
) {
    val nowDate = LocalDate.now()
    val pickedDate = remember { mutableStateOf(initialPickedDate) }

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            MonthLabel(
                displayMonth = displayDate,
                modesEnabled = settings.yearsPickEnabled,
                mode = mode
            ) { // todo change [modesEnabled] to settings when implemented
                onModeToggle()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        val modeState = remember(mode) { mutableStateOf(mode) }
        Crossfade(targetState = modeState) { currentMode ->
            when {
                settings.yearsPickEnabled && currentMode.value == DatePickerMode.YEARS -> {
                    val dateFromActual = dateFrom ?: LocalDate.now()
                    val dateToActual = dateTo ?: getDefaultDateTo(dateFromActual)
                    val columnsNumber = settings.yearColumnsNumber.let {
                        if (it > 0) it else PickerDefaults.yearColumnsNumber
                    }

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                        DatePickerLayoutYears(
                            years = createYearsMatrix(dateFromActual, dateToActual, columnsNumber),
                            nowDate = nowDate,
                            displayYear = displayDate.year,
                            onSelect = {
                                onYearChange(it)
                            }
                        )
                    }
                }
                else -> {
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
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MonthLabel(
    displayMonth: LocalDate,
    modesEnabled: Boolean,
    mode: DatePickerMode = DatePickerMode.MONTHS,
    onClick: (() -> Unit) = {}
) {
    Surface(shape = RoundedCornerShape(6.dp), onClick = onClick
        /*modifier = Modifier.clickable(enabled = modesEnabled) { onClick() }*/) {
        Row(modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            /*.clickable(enabled = modesEnabled) { onClick() }*/
        ) {
            Text(
                text = displayMonth.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy")
                ),
                modifier = Modifier.padding(start = 8.dp)
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