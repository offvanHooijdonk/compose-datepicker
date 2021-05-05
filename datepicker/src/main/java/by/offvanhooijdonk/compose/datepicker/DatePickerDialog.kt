package by.offvanhooijdonk.compose.datepicker

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
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.util.*

@Composable
fun DatePickerDialog(
    initialPickedDate: Date,
    onPick: (day: Int, month: Int, year: Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(4.dp)) {
            DatePickerDialogLayout(initialPickedDate, onPick, onDismissRequest)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DatePickerDialogLayout(
    initialPickedDate: Date,
    onPick: (day: Int, month: Int, year: Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    val pickedDate = remember { mutableStateOf(initialPickedDate) }
    Column {
        DatePickedHeader(date = pickedDate.value)
        Spacer(modifier = Modifier.height(12.dp))

        val pagerState = rememberPagerState(pageCount = 3, initialPage = 0) // todo set range and start page to correspond potential or provided date range

        HorizontalPager(verticalAlignment = Alignment.Top, state = pagerState) { page ->
            DatePickLayout(
                modifier = Modifier.padding(horizontal = 16.dp),
                monthOffset = getMonthOffset(initialPickedDate) + page,
                currentPickedDate = pickedDate.value,
                dateFrom = Date(),
                onPick = { day, month, year ->
                    pickedDate.value = Calendar.getInstance().apply {
                        timeInMillis = 0; this.day = day; this.month = month; this.year = year
                    }.time // todo if picked date beyond displayed month - set offset to show the month (if not greater than max offset)
                },
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                val (d, m, y) = DateModel(pickedDate.value)
                onPick(d, m, y)
                onDismissRequest()
            }) {
                Text(text = "OK")
            }
        }
    }
}

@Composable
private fun DatePickedHeader(date: Date) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = DateFormat.getMediumDateFormat(LocalContext.current).format(date),
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.h4
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun Preview_datePicker() {
    PreviewAppTheme {
        Surface {
            DatePickerDialogLayout(
                initialPickedDate = Date(),
                onPick = { _, _, _ -> },
                onDismissRequest = {})
        }
    }
}

private fun getMonthOffset(initialPickDate: Date): Int {
    val (_, currentMonth, currentYear) = DateModel(Date())
    val (_, pickedDateMonth, pickedDateYear) = DateModel(initialPickDate)
    Log.w("datepickerlog", "pick: $pickedDateMonth, curr: $currentMonth")
    return pickedDateMonth - currentMonth + (pickedDateYear - currentYear) * 12
}