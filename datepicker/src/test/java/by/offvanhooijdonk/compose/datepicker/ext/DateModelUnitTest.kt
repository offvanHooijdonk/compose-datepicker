package by.offvanhooijdonk.compose.datepicker.ext

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateModelUnitTest {
    @Test
    fun `test DateModel diff months`() {
        val date = LocalDate.of(2020, 5, 1)

        assertEquals(0, date.diffMonths(date))
        assertEquals(1, date.diffMonths(date.plusMonths(1)))
        assertEquals(12, date.diffMonths(date.plusYears(1)))
        assertEquals(10, date.diffMonths(date.plusMonths(-2).plusYears(1)))
        assertEquals(15, date.diffMonths(date.plusMonths(3).plusYears(1)))
        assertEquals(-1, date.diffMonths(date.plusMonths(-1)))
        assertEquals(-12, date.diffMonths(date.plusYears(-1)))
        assertEquals(-10, date.diffMonths(date.plusMonths(2).plusYears(-1)))
        assertEquals(-15, date.diffMonths(date.plusMonths(-3).plusYears(-1)))
    }
}