package by.offvanhooijdonk.compose.datepicker.dialog

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import by.offvanhooijdonk.compose.datepicker.ext.PickerSettings
import by.offvanhooijdonk.compose.datepicker.ext.diffMonths
import by.offvanhooijdonk.compose.datepicker.pickerlayout.DatePickerLayout
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DatePickerDialog(
    initialPickedDate: LocalDate,
    dateFrom: LocalDate = LocalDate.now(),
    dateTo: LocalDate? = null,
    onPick: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(4.dp)) {
            val pickedDate = remember { mutableStateOf(initialPickedDate) }
            Column {
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
//    val dateModelFrom = DateModel(dateFrom)
    val dateToActual = dateTo ?: getDefaultDateTo(dateFrom)
    val pagerState = rememberPagerState(
        pageCount = getMaxPages(dateFrom, dateToActual),
        initialPage = getInitialPage(
            now = LocalDate.now(),
            dateFrom = dateFrom,
            pickedDate = initialPickedDate
        )
    )

    HorizontalPager(verticalAlignment = Alignment.Top, state = pagerState) { page ->
        DatePickerLayout(
            modifier = Modifier.padding(horizontal = 16.dp),
            //monthOffset = /*getMonthOffset(dateModelFrom, initialPickedDate) +*/ page,
            displayMonth = dateFrom.plusMonths(page.toLong()),
            initialPickedDate = initialPickedDate,
            dateFrom = dateFrom,
            dateTo = dateToActual,
            onSelect = {
                onDateSelected(it)
                // todo if picked date beyond displayed month - set offset to show the month (if not greater than max offset)
            },
        )
    }
}

@Composable
private fun DatePickedHeader(dateModel: LocalDate) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = dateModel.format(
                DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEMMMddyyyy"))
            ), // todo extract format
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
    dateFrom.plusYears(PickerSettings.maxYearsForward.toLong())


internal fun getMaxPages(dateFrom: LocalDate, dateTo: LocalDate): Int =
    dateFrom.diffMonths(dateTo)

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
