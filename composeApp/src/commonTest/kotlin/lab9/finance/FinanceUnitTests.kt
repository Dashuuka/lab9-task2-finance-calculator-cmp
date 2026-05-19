package lab9.finance

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FinanceUnitTests {
    private val calculator = FinanceCalculator()

    @Test
    fun monthlyCompoundFormulaMatchesExpectedValue() {
        val result = calculator.calculate(FinanceInput("1000", "12", "1", Compounding.Monthly))
        assertEquals(1126.83, result.finalAmount)
    }

    @Test
    fun yearlyCompoundFormulaMatchesExpectedValue() {
        val result = calculator.calculate(FinanceInput("1000", "12", "1", Compounding.Yearly))
        assertEquals(1120.0, result.finalAmount)
    }

    @Test
    fun negativePrincipalIsRejected() {
        assertFailsWith<FinanceError.NegativeAmount> {
            calculator.calculate(FinanceInput("-10", "5", "2", Compounding.Quarterly))
        }
    }
}
