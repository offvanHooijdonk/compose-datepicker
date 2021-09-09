package by.offvanhooijdonk.compose.datepicker.dialog

import android.text.format.DateFormat
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import by.offvanhooijdonk.compose.datepicker.ext.PickerSettings
import by.offvanhooijdonk.compose.datepicker.ext.diffMonths
import by.offvanhooijdonk.compose.datepicker.pickerlayout.DatePickerLayout
import by.offvanhooijdonk.compose.datepicker.pickerlayout.DatePickerMode
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DatePickerDialog(
    initialPickedDate: LocalDate, // todo make optional
    dateFrom: LocalDate = LocalDate.now(),
    dateTo: LocalDate? = null,
    onPick: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(4.dp)) {
            val pickedDate = remember { mutableStateOf(initialPickedDate) }
            Column {// fixme fix layout to not overscroll years list
                DatePickedHeader(dateModel = pickedDate.value)
                Spacer(modifier = Modifier.height(16.dp))

                DatePickerPager(initialPickedDate, dateFrom, dateTo,
                    onDateSelected = {
                        pickedDate.value = it
                    }
                )

                DatePickerButtonsBlock(onPositiveButtonClicked = {
                    onPick(pickedDate.value)
                }, onNegativeButtonClick = {
                    onDismissRequest()
                })
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DatePickerPager(
    initialPickedDate: LocalDate,
    dateFrom: LocalDate = LocalDate.now(),
    dateTo: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit,
) {
    val pickedDate = remember { mutableStateOf(initialPickedDate) }
    val dateToActual = dateTo ?: getDefaultDateTo(dateFrom)

    val initPage = getInitialPage(now = LocalDate.now(), dateFrom = dateFrom, pickedDate = initialPickedDate)
    val pagerState = rememberPagerState(
        pageCount = getMaxPages(dateFrom, dateToActual),
        initialPage = initPage
    )
    val coroutineScope = rememberCoroutineScope()
    val mode = remember { mutableStateOf(DatePickerMode.MONTHS) }

    HorizontalPager(
        verticalAlignment = Alignment.Top,
        state = pagerState,
        dragEnabled = mode.value == DatePickerMode.MONTHS
    ) { page ->
        val displayDate = dateFrom.plusMonths(page.toLong())
        DatePickerLayout(
            modifier = Modifier.padding(horizontal = 16.dp),
            displayDate = displayDate,
            initialPickedDate = pickedDate.value,
            dateFrom = dateFrom,
            mode = mode.value,
            dateTo = dateToActual,
            onSelect = {
                pickedDate.value = it
                onDateSelected(it)
            },
            onYearChange = {
                mode.value = DatePickerMode.MONTHS
                coroutineScope.launch {
                    val newPage = getPageWithYear(dateFrom, displayDate, it)
                    pagerState.animateScrollToPage(newPage)
                }
            },
            onModeToggle = {
                mode.value = if (mode.value == DatePickerMode.MONTHS) DatePickerMode.YEARS else DatePickerMode.MONTHS
            }
        )
    }
}

@Composable
private fun DatePickedHeader(dateModel: LocalDate) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = dateModel.format(
                DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEMMMddyyyy"))
            ), // todo extract format
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
private fun DatePickerButtonsBlock(
    onPositiveButtonClicked: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onNegativeButtonClick) {
            Text(text = "Cancel")
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onPositiveButtonClicked) {
            Text(text = "OK")
        }
    }
}

internal fun getInitialPage(now: LocalDate, dateFrom: LocalDate, pickedDate: LocalDate): Int {
    val offsetNow = dateFrom.diffMonths(now)
    val offsetPicked = dateFrom.diffMonths(pickedDate)
    return PAGER_START_INDEX + when {
        offsetPicked >= 0 -> offsetPicked
        offsetNow >= 0 -> offsetNow
        else -> 0
    }
}

@Composable
internal fun getDefaultDateTo(dateFrom: LocalDate): LocalDate =
    dateFrom.plusYears(PickerSettings.defaultMaxYearsForward.toLong())


internal fun getMaxPages(dateFrom: LocalDate, dateTo: LocalDate): Int =
    dateFrom.diffMonths(dateTo)

internal fun getPageWithYear(dateFrom: LocalDate, displayDate: LocalDate, year: Int): Int {
    val newDate = displayDate.withYear(year)
    return dateFrom.diffMonths(newDate).let { if (it > 0) it else 0 }
}

private const val PAGER_START_INDEX = 0

@Preview(showSystemUi = true)
@Composable
fun Preview_datePicker() {
    PreviewAppTheme {
        DatePickerDialog(
            initialPickedDate = LocalDate.now(),
            onPick = {},
            onDismissRequest = {})
    }
}
