package by.offvanhooijdonk.compose.datepicker.dialog

import android.text.format.DateFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import by.offvanhooijdonk.compose.datepicker.pickerlayout.DatePickerLayout
import by.offvanhooijdonk.compose.datepicker.ext.DateModel
import by.offvanhooijdonk.compose.datepicker.ext.PickerSettings
import by.offvanhooijdonk.compose.datepicker.ext.plusMonths
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.util.*

@Composable
fun DatePickerDialog(
    initialPickedDate: Date,
    dateFrom: Date = Date(),
    dateTo: Date? = null,
    onPick: (day: Int, month: Int, year: Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(4.dp)) {
            val pickedDate = remember { mutableStateOf(DateModel(initialPickedDate)) }
            Column {
                DatePickedHeader(dateModel = pickedDate.value)
                Spacer(modifier = Modifier.height(16.dp))

                DatePickerPager(initialPickedDate, dateFrom, dateTo,
                    onDateSelected = { day, month, year ->
                        pickedDate.value = DateModel(day, month, year)
                    }
                )

                DatePickerButtonsBlock(onPositiveButtonClicked = {
                    val (d, m, y) = pickedDate.value
                    onPick(d, m, y)
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
    initialPickedDate: Date,
    dateFrom: Date = Date(),
    dateTo: Date? = null,
    onDateSelected: (day: Int, month: Int, year: Int) -> Unit,
) {
    val dateModelFrom = DateModel(dateFrom)
    val dateToActual = dateTo ?: getDefaultDateTo(dateModelFrom)
    val pagerState = rememberPagerState(
        pageCount = getMaxPages(dateModelFrom, DateModel(dateToActual)),
        initialPage = getInitialPage(
            now = DateModel(Date()),
            dateFrom = DateModel(dateFrom),
            pickedDate = DateModel(initialPickedDate)
        )
    )

    HorizontalPager(verticalAlignment = Alignment.Top, state = pagerState) { page ->
        DatePickerLayout(
            modifier = Modifier.padding(horizontal = 16.dp),
            //monthOffset = /*getMonthOffset(dateModelFrom, initialPickedDate) +*/ page,
            displayMonthDate = dateFrom.plusMonths(page),
            initialPickedDate = initialPickedDate,
            dateFrom = dateFrom,
            dateTo = dateToActual,
            onSelect = { day, month, year ->
                onDateSelected(day, month, year)
                // todo if picked date beyond displayed month - set offset to show the month (if not greater than max offset)
            },
        )
    }
}

@Composable
private fun DatePickedHeader(dateModel: DateModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = DateFormat.format(DateFormat.getBestDateTimePattern(
                Locale.getDefault(),
                "EEMMMddyyyy"), dateModel.toCalendar().time).toString(), // todo extract format
            color = MaterialTheme.colors.onPrimary,
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

internal fun getInitialPage(now: DateModel, dateFrom: DateModel, pickedDate: DateModel): Int {
    val offsetNow = now.getDiffMonths(dateFrom)
    val offsetPicked = pickedDate.getDiffMonths(dateFrom)
    return PAGER_START_INDEX + when {
        offsetPicked >= 0 -> offsetPicked
        offsetNow >= 0 -> offsetNow
        else -> 0
    }
}

@Composable
internal fun getDefaultDateTo(dateFrom: DateModel): Date =
    dateFrom.copy(year = dateFrom.year + PickerSettings.maxYearsForward).toCalendar().time


internal fun getMaxPages(dateFrom: DateModel, dateTo: DateModel) =
    dateTo.getDiffMonths(dateFrom)

private const val PAGER_START_INDEX = 0

@Preview(showSystemUi = true)
@Composable
fun Preview_datePicker() {
    PreviewAppTheme {
        DatePickerDialog(
            initialPickedDate = Date(),
            onPick = { _, _, _ -> },
            onDismissRequest = {})
    }
}