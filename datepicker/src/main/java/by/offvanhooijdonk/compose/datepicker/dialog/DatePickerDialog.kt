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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import by.offvanhooijdonk.compose.datepicker.ext.PickerDefaults
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
import java.time.temporal.TemporalAdjusters
import java.util.*

/** @author Yahor Fralou (aka offvanhooijdonk, originally Egor Frolov)
 * Renders a dialog to pick a single date.
 * @param initialPickedDate The date to be marked as currently selected. Default is today.
 * @param dateFrom The first date allowed to be selected. All previous dates are marked greyed and cannot be picked. Default is today.
 * @param dateTo The date starting with user cannot pick a date. Next month and next year are not presented in the picker.
 * @param settings Customization of the picker layout and behavior, see [DatePickerSettings].
 * @param onDismissRequest Called when negative button is clicked.
 * @param onPick Called when positive button is clicked. Gets picked date as parameter.
 */
@Composable
fun DatePickerDialog(
    initialPickedDate: LocalDate = LocalDate.now(),
    dateFrom: LocalDate = LocalDate.now(),
    dateTo: LocalDate? = null,
    settings: DatePickerSettings = DatePickerSettings(),
    onDismissRequest: () -> Unit,
    onPick: (LocalDate) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(4.dp)) {
            val pickedDate = remember { mutableStateOf(initialPickedDate) }

            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                ConstraintLayout {
                    val (header, pager, buttons) = createRefs()
                    DatePickedHeader(
                        modifier = Modifier.constrainAs(header) {
                            height = Dimension.wrapContent
                            top.linkTo(parent.top)
                        },
                        dateModel = pickedDate.value,
                        style = settings.headerStyle
                    )

                    DatePickerPager(
                        modifier = Modifier.constrainAs(pager) {
                            top.linkTo(header.bottom, margin = 8.dp)
                            bottom.linkTo(buttons.top)
                            height = Dimension.preferredWrapContent
                        },
                        initialPickedDate = initialPickedDate,
                        dateFrom = dateFrom,
                        dateTo = dateTo,
                        settings = settings,
                        onDateSelected = {
                            pickedDate.value = it
                        }
                    )

                    DatePickerButtonsBlock(
                        modifier = Modifier.constrainAs(buttons) {
                            bottom.linkTo(parent.bottom)
                            height = Dimension.wrapContent
                        },
                        onPositiveButtonClick = {
                            onPick(pickedDate.value)
                        }, onNegativeButtonClick = {
                            onDismissRequest()
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DatePickerPager(
    modifier: Modifier = Modifier,
    initialPickedDate: LocalDate,
    dateFrom: LocalDate = LocalDate.now(),
    dateTo: LocalDate? = null,
    settings: DatePickerSettings = DatePickerSettings(),
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
        modifier = Modifier
            .then(modifier)
            .clipToBounds(),
        verticalAlignment = Alignment.Top,
        state = pagerState,
        dragEnabled = mode.value == DatePickerMode.MONTHS
    ) { page ->
        val displayDate = dateFrom.plusMonths(page.toLong())
        DatePickerLayout(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .then(modifier),
            displayDate = displayDate,
            initialPickedDate = pickedDate.value,
            dateFrom = dateFrom,
            mode = mode.value,
            settings = settings,
            dateTo = dateToActual,
            onSelect = {
                pickedDate.value = it
                onDateSelected(it)
            },
            onYearChange = {
                mode.value = DatePickerMode.MONTHS
                coroutineScope.launch {
                    val newPage = getPageWithYear(dateFrom, dateToActual, displayDate, it)
                    pagerState.scrollToPage(newPage)
                }
            },
            onModeToggle = {
                mode.value = if (mode.value == DatePickerMode.MONTHS) DatePickerMode.YEARS else DatePickerMode.MONTHS
            }
        )
    }
}

@Composable
private fun DatePickedHeader(
    modifier: Modifier = Modifier,
    dateModel: LocalDate,
    style: DatePickerSettings.HeaderStyle
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(getHeaderBackgroundColor(style)),
        contentAlignment = Alignment.Center
    ) {
        Text( // todo try adjust paddings for different [style] for better design
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
            text = dateModel.format(
                DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEMMMddyyyy"))
            ), // todo extract format
            style = MaterialTheme.typography.h5,
            color = getHeaderTextColor(style)
        )
    }
}

@Composable
private fun DatePickerButtonsBlock(
    modifier: Modifier = Modifier,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onNegativeButtonClick) {
            Text(text = "Cancel")
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onPositiveButtonClick) {
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
    dateFrom.plusYears(PickerDefaults.maxYearsForward.toLong())

private const val PAGER_START_INDEX = 0
internal fun getMaxPages(dateFrom: LocalDate, dateTo: LocalDate): Int =
    dateFrom.diffMonths(dateTo)

internal fun getPageWithYear(dateFrom: LocalDate, dateTo: LocalDate, displayDate: LocalDate, year: Int): Int {
    val monthTo = dateTo.with(TemporalAdjusters.firstDayOfMonth())
    val newDate = displayDate.withYear(year).let {
        if (it.isBefore(monthTo)) it else monthTo
    }
    return dateFrom.diffMonths(newDate).let { if (it > 0) it else 0 }
}

@Composable
private fun getHeaderBackgroundColor(style: DatePickerSettings.HeaderStyle) =
    when(style) {
        DatePickerSettings.HeaderStyle.COLOR_PRIMARY -> MaterialTheme.colors.primary
        DatePickerSettings.HeaderStyle.COLOR_SURFACE -> MaterialTheme.colors.surface
    }

@Composable
private fun getHeaderTextColor(style: DatePickerSettings.HeaderStyle) =
    when(style) {
        DatePickerSettings.HeaderStyle.COLOR_PRIMARY -> MaterialTheme.colors.onPrimary
        DatePickerSettings.HeaderStyle.COLOR_SURFACE -> MaterialTheme.colors.onSurface
    }

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
