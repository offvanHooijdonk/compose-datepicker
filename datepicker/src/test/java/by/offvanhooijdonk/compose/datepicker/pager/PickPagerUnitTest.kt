package by.offvanhooijdonk.compose.datepicker.pager

import by.offvanhooijdonk.compose.datepicker.dialog.getInitialPage
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PickPagerUnitTest {
    @Test
    fun `test pager initial page calculation`() {
        val now = LocalDate.of(2020, 7, 1)

        assertEquals(0, getInitialPage(
            now = now, dateFrom = now, pickedDate = now
        ))
        assertEquals(1, getInitialPage(
            now = now, dateFrom = now, pickedDate = now.plusMonths(1)
        ))
        assertEquals(0, getInitialPage(
            now = now, dateFrom = now.plusMonths(1), pickedDate = now.plusMonths(1)
        ))
        assertEquals(2, getInitialPage(
            now = now, dateFrom = now.plusMonths(-1), pickedDate = now.plusMonths(1)
        ))
        assertEquals(0, getInitialPage(
            now = now, dateFrom = now.plusMonths(1), pickedDate = now
        ))
        assertEquals(1, getInitialPage(
            now = now, dateFrom = now.plusMonths(-1), pickedDate = now.plusMonths(-2)
        ))
        assertEquals(0, getInitialPage(
            now = now, dateFrom = now, pickedDate = now.plusMonths(-2)
        ))
    }
}