package lab9.finance

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FinanceTests {
    private val calculator = FinanceCalculator()

    @Test
    fun compoundFormulaMonthly() {
        val result = calculator.calculate(FinanceInput("1000", "12", "1", Compounding.Monthly))
        assertEquals(1126.83, result.finalAmount)
    }

    @Test
    fun compoundFormulaQuarterly() {
        val result = calculator.calculate(FinanceInput("1000", "12", "1", Compounding.Quarterly))
        assertEquals(1125.51, result.finalAmount)
    }

    @Test
    fun compoundFormulaYearly() {
        val result = calculator.calculate(FinanceInput("1000", "12", "1", Compounding.Yearly))
        assertEquals(1120.0, result.finalAmount)
    }

    @Test
    fun rejectsNegativeAmount() {
        assertFailsWith<FinanceError.NegativeAmount> {
            calculator.calculate(FinanceInput("-1", "10", "1", Compounding.Monthly))
        }
    }

    @Test
    fun rejectsEmptyInput() {
        assertFailsWith<FinanceError.EmptyInput> {
            calculator.calculate(FinanceInput("", "10", "1", Compounding.Monthly))
        }
    }

    @Test
    fun storesAndClearsHistory() {
        val history = HistoryRepository()
        history.add(calculator.calculate(FinanceInput("1000", "10", "1", Compounding.Yearly)))
        assertTrue(history.all().isNotEmpty())
        history.clear()
        assertTrue(history.all().isEmpty())
    }
}
