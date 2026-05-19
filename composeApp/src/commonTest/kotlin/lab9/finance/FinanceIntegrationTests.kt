package lab9.finance

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FinanceIntegrationTests {
    private val calculator = FinanceCalculator()

    @Test
    fun calculationIsSavedToHistoryRepository() {
        val history = HistoryRepository()
        val result = calculator.calculate(FinanceInput("2000", "7", "3", Compounding.Monthly))
        history.add(result)
        assertEquals(result.finalAmount, history.all().first().finalAmount)
    }

    @Test
    fun repositoryCanBeRecreatedFromPreviousHistorySnapshot() {
        val result = calculator.calculate(FinanceInput("1000", "5", "2", Compounding.Yearly))
        val restored = HistoryRepository(listOf(result))
        assertEquals(1, restored.all().size)
        assertEquals(1102.5, restored.all().first().finalAmount)
    }

    @Test
    fun clearHistoryRemovesCalculatedItems() {
        val state = FinanceUiState(principal = "1000", rate = "10", years = "1")
            .calculateWith(calculator)
            .historyCleared()
        assertTrue(state.history.isEmpty())
    }
}
