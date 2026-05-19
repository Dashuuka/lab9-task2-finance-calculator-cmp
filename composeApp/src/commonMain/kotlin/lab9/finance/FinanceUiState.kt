package lab9.finance

data class FinanceUiState(
    val principal: String = "",
    val rate: String = "",
    val years: String = "",
    val compounding: Compounding = Compounding.Monthly,
    val darkTheme: Boolean = false,
    val lang: Lang = Lang.Ru,
    val error: String? = null,
    val result: FinanceResult? = null,
    val history: List<FinanceResult> = emptyList(),
)

fun FinanceUiState.calculateWith(calculator: FinanceCalculator): FinanceUiState =
    try {
        val calculated = calculator.calculate(FinanceInput(principal, rate, years, compounding))
        copy(error = null, result = calculated, history = listOf(calculated) + history)
    } catch (e: FinanceError) {
        copy(error = e.message, result = null)
    }

fun FinanceUiState.compoundingChanged(value: Compounding): FinanceUiState =
    copy(compounding = value, error = null)

fun FinanceUiState.themeToggled(): FinanceUiState =
    copy(darkTheme = !darkTheme)

fun FinanceUiState.languageChanged(value: Lang): FinanceUiState =
    copy(lang = value)

fun FinanceUiState.historyCleared(): FinanceUiState =
    copy(history = emptyList())
