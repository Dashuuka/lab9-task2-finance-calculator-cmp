package lab9.finance

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FinanceUiStateTests {
    private val calculator = FinanceCalculator()

    @Test
    fun emptyInputShowsErrorState() {
        val state = FinanceUiState(principal = "", rate = "10", years = "1").calculateWith(calculator)
        assertEquals("empty_input", state.error)
        assertEquals(null, state.result)
    }

    @Test
    fun validCalculationShowsResultState() {
        val state = FinanceUiState(principal = "1000", rate = "10", years = "1").calculateWith(calculator)
        assertNotNull(state.result)
        assertFalse(state.history.isEmpty())
    }

    @Test
    fun switchesCompoundingThemeAndLanguageState() {
        val state = FinanceUiState()
            .compoundingChanged(Compounding.Quarterly)
            .themeToggled()
            .languageChanged(Lang.En)
        assertEquals(Compounding.Quarterly, state.compounding)
        assertTrue(state.darkTheme)
        assertEquals(Lang.En, state.lang)
    }
}
