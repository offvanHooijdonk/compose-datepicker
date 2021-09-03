package by.offvanhooijdonk.compose.datepicker.ext

import org.junit.Assert.assertEquals
import org.junit.Test

class DateModelUnitTest {
    @Test
    fun `test DateModel diff months`() {
        val dateModel = DateModel(1, 5, 2020)
        val (_, month, year) = dateModel
        assertEquals(0, dateModel.getDiffMonths(dateModel))

        assertEquals(-1, dateModel.getDiffMonths(dateModel.copy(month = month + 1)))

        assertEquals(-12, dateModel.getDiffMonths(dateModel.copy(year = year + 1)))

        assertEquals(-10, dateModel.getDiffMonths(dateModel.copy(month = month - 2, year = year + 1)))

        assertEquals(-15, dateModel.getDiffMonths(dateModel.copy(month = month + 3, year = year + 1)))

        assertEquals(1, dateModel.getDiffMonths(dateModel.copy(month = month -1)))

        assertEquals(12, dateModel.getDiffMonths(dateModel.copy(year = year - 1)))

        assertEquals(10, dateModel.getDiffMonths(dateModel.copy(month = month + 2, year = year - 1)))

        assertEquals(15, dateModel.getDiffMonths(dateModel.copy(month = month -3, year = year - 1)))
    }
}