package by.offvanhooijdonk.compose.datepicker.pager

import by.offvanhooijdonk.compose.datepicker.dialog.getInitialPage
import by.offvanhooijdonk.compose.datepicker.ext.DateModel
import org.junit.Test

import org.junit.Assert.*
import java.util.Date

class PickPagerUnitTest {
    @Test
    fun `test pager initial page calculation`() {
        val now = DateModel(1, 7, 2020)
        val (_, month, year) = now
        assertEquals(0, getInitialPage(
            now = now, dateFrom = now, pickedDate = now
        ))
        assertEquals(1, getInitialPage(
            now = now, dateFrom = now, pickedDate = now.copy(month = month + 1)
        ))
        assertEquals(0, getInitialPage(
            now = now, dateFrom = now.copy(month = month + 1), pickedDate = now.copy(month = month + 1)
        ))
        assertEquals(2, getInitialPage(
            now = now, dateFrom = now.copy(month = month - 1), pickedDate = now.copy(month = month + 1)
        ))
        assertEquals(0, getInitialPage(
            now = now, dateFrom = now.copy(month = month + 1), pickedDate = now
        ))
        assertEquals(1, getInitialPage(
            now = now, dateFrom = now.copy(month = month - 1), pickedDate = now.copy(month = month - 2)
        ))
        assertEquals(0, getInitialPage(
            now = now, dateFrom = now, pickedDate = now.copy(month = month - 2)
        ))
    }
}