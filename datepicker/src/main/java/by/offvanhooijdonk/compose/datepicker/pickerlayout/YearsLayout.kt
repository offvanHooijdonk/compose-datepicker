package by.offvanhooijdonk.compose.datepicker.pickerlayout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.offvanhooijdonk.compose.datepicker.ext.createYearsMatrix
import by.offvanhooijdonk.compose.datepicker.theme.PreviewAppTheme
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DatePickerLayoutYears(
    years: List<List<Int>>,
    displayYear: Int,
    nowDate: LocalDate,
    onSelect: (Int) -> Unit
) {
    CompositionLocalProvider(LocalIndication provides rememberRipple(bounded = false)) {
        LazyColumn {
            items(years) { row ->
                Row {
                    row.forEach { year ->
                        YearItem(
                            year = year,
                            isSelected = year == displayYear,
                            isCurrentYear = year == nowDate.year,
                            onClick = {
                                onSelect(year)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearItem(year: Int, isSelected: Boolean, isCurrentYear: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(percent = 50),
        color = if (isSelected) MaterialTheme.colors.secondary else Color.Transparent
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            text = year.toString(),
            color = when {
                isSelected -> MaterialTheme.colors.onSecondary
                isCurrentYear -> MaterialTheme.colors.primary
                else -> MaterialTheme.colors.onSurface
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
internal fun Preview_YearsLayout() {
    val dateNow = LocalDate.now()
    val colsNum = 3
    PreviewAppTheme {
        //Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            DatePickerLayoutYears(
                years = createYearsMatrix(dateNow, dateNow.plusYears(16), cellsNumber = colsNum),
                displayYear = 2025,
                nowDate = dateNow,
                onSelect = {}
            )
        //}
    }
}