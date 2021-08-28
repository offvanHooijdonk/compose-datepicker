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
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.util.*

@Composable
fun DatePickerDialog(
    initialPickedDate: Date,
    dateFrom: Date? = null,
    dateTo: Date? = null,
    onPick: (day: Int, month: Int, year: Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(4.dp)) {
            val pickedDate = remember { mutableStateOf(DateModel(initialPickedDate)) }
            Column {
                DatePickedHeader(dateModel = pickedDate.value)
                Spacer(modifier = Modifier.height(12.dp))

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
private fun DatePickerPager(
    initialPickedDate: Date,
    dateFrom: Date? = null,
    dateTo: Date? = null, // todo implement Date To
    onDateSelected: (day: Int, month: Int, year: Int) -> Unit,
) {
    val pagerState = rememberPagerState(
        pageCount = 3,
        initialPage = 0
    ) // todo set range and start page to correspond potential or provided date range

    HorizontalPager(verticalAlignment = Alignment.Top, state = pagerState) { page -> // todo to separate public fun
        DatePickerLayout(
            modifier = Modifier.padding(horizontal = 16.dp),
            monthOffset = getMonthOffset(initialPickedDate) + page,
            currentPickedDate = initialPickedDate,
            dateFrom = dateFrom ?: Date(),
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
            modifier = Modifier.padding(8.dp),
            text = DateFormat.getMediumDateFormat(LocalContext.current).format(dateModel.toCalendar().time),
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.h4
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

private fun getMonthOffset(initialPickDate: Date): Int {
    val (_, currentMonth, currentYear) = DateModel(Date())
    val (_, pickedDateMonth, pickedDateYear) = DateModel(initialPickDate)
    Log.w("datepickerlog", "pick: $pickedDateMonth, curr: $currentMonth")
    return pickedDateMonth - currentMonth + (pickedDateYear - currentYear) * 12
}

@Preview
@Composable
fun Preview_datePicker() {
    PreviewAppTheme {
        DatePickerDialog(
            initialPickedDate = Date(),
            onPick = { _, _, _ -> },
            onDismissRequest = {})
    }
}
