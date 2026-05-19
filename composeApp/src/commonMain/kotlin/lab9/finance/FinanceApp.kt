package lab9.finance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.round

enum class Compounding(val periodsPerYear: Int) {
    Monthly(12), Quarterly(4), Yearly(1)
}

data class FinanceInput(
    val principalText: String,
    val rateText: String,
    val yearsText: String,
    val compounding: Compounding,
)

data class FinanceResult(
    val principal: Double,
    val finalAmount: Double,
    val profit: Double,
    val points: List<Double>,
    val compounding: Compounding,
)

sealed class FinanceError(message: String) : Exception(message) {
    data object EmptyInput : FinanceError("empty_input")
    data object NegativeAmount : FinanceError("negative_amount")
    data object InvalidInput : FinanceError("invalid_input")
}

class FinanceCalculator {
    fun calculate(input: FinanceInput): FinanceResult {
        if (input.principalText.isBlank() || input.rateText.isBlank() || input.yearsText.isBlank()) {
            throw FinanceError.EmptyInput
        }
        val principal = input.principalText.replace(',', '.').toDoubleOrNull() ?: throw FinanceError.InvalidInput
        val ratePercent = input.rateText.replace(',', '.').toDoubleOrNull() ?: throw FinanceError.InvalidInput
        val years = input.yearsText.replace(',', '.').toDoubleOrNull() ?: throw FinanceError.InvalidInput
        if (principal < 0.0) throw FinanceError.NegativeAmount
        if (ratePercent < 0.0 || years <= 0.0) throw FinanceError.InvalidInput

        val rate = ratePercent / 100.0
        val n = input.compounding.periodsPerYear
        val amount = principal * (1 + rate / n).pow(n * years)
        val periods = years.toInt().coerceAtLeast(1)
        val points = (0..periods).map { year ->
            principal * (1 + rate / n).pow(n * year.toDouble())
        }
        return FinanceResult(principal, round2(amount), round2(amount - principal), points.map(::round2), input.compounding)
    }
}

class HistoryRepository(initial: List<FinanceResult> = emptyList()) {
    private val items = initial.toMutableList()
    fun all(): List<FinanceResult> = items.toList()
    fun add(result: FinanceResult) {
        items.add(0, result)
    }
    fun clear() {
        items.clear()
    }
}

fun round2(value: Double): Double = round(value * 100.0) / 100.0

enum class Lang { Ru, En, Be }

private val text = mapOf(
    Lang.Ru to mapOf(
        "title" to "Финансовый калькулятор",
        "principal" to "Начальная сумма",
        "rate" to "Ставка, %",
        "years" to "Срок, лет",
        "calculate" to "Рассчитать",
        "final" to "Конечная сумма",
        "profit" to "Прибыль",
        "history" to "История",
        "clear" to "Очистить",
        "dark" to "Темная тема",
    ),
    Lang.En to mapOf(
        "title" to "Financial calculator",
        "principal" to "Principal",
        "rate" to "Rate, %",
        "years" to "Years",
        "calculate" to "Calculate",
        "final" to "Final amount",
        "profit" to "Profit",
        "history" to "History",
        "clear" to "Clear",
        "dark" to "Dark theme",
    ),
    Lang.Be to mapOf(
        "title" to "Фінансавы калькулятар",
        "principal" to "Пачатковая сума",
        "rate" to "Стаўка, %",
        "years" to "Тэрмін, гадоў",
        "calculate" to "Разлічыць",
        "final" to "Канчатковая сума",
        "profit" to "Прыбытак",
        "history" to "Гісторыя",
        "clear" to "Ачысціць",
        "dark" to "Цёмная тэма",
    ),
)

@Composable
fun FinanceApp(
    calculator: FinanceCalculator = remember { FinanceCalculator() },
    history: HistoryRepository = remember { HistoryRepository() },
) {
    var principal by remember { mutableStateOf("1000") }
    var rate by remember { mutableStateOf("8") }
    var years by remember { mutableStateOf("5") }
    var compounding by remember { mutableStateOf(Compounding.Monthly) }
    var lang by remember { mutableStateOf(Lang.Ru) }
    var dark by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val results = remember { mutableStateListOf<FinanceResult>().apply { addAll(history.all()) } }
    val s = text.getValue(lang)

    MaterialTheme(colorScheme = if (dark) androidx.compose.material3.darkColorScheme() else androidx.compose.material3.lightColorScheme()) {
        Surface {
            LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text(s.getValue("title"), style = MaterialTheme.typography.headlineMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { lang = Lang.Ru }) { Text("RU") }
                        Button(onClick = { lang = Lang.En }) { Text("EN") }
                        Button(onClick = { lang = Lang.Be }) { Text("BE") }
                        Text(s.getValue("dark"))
                        Switch(dark, { dark = it })
                    }
                }
                item {
                    Card {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(principal, { principal = it }, label = { Text(s.getValue("principal")) }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(rate, { rate = it }, label = { Text(s.getValue("rate")) }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(years, { years = it }, label = { Text(s.getValue("years")) }, modifier = Modifier.fillMaxWidth())
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Compounding.entries.forEach { item ->
                                    Button(onClick = { compounding = item }) { Text(item.name) }
                                }
                            }
                            Button(onClick = {
                                try {
                                    val result = calculator.calculate(FinanceInput(principal, rate, years, compounding))
                                    history.add(result)
                                    results.add(0, result)
                                    error = null
                                } catch (e: Exception) {
                                    error = e.message
                                    println("[FinanceUi] ${e.message}")
                                }
                            }) { Text(s.getValue("calculate")) }
                            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
                item {
                    Button(onClick = {
                        history.clear()
                        results.clear()
                    }) { Text(s.getValue("clear")) }
                }
                items(results) { result ->
                    FinanceCard(result, s)
                }
            }
        }
    }
}

@Composable
fun FinanceCard(result: FinanceResult, s: Map<String, String>) {
    Card {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("${s.getValue("final")}: ${result.finalAmount}")
            Text("${s.getValue("profit")}: ${result.profit}")
            GrowthChart(result.points)
        }
    }
}

@Composable
fun GrowthChart(points: List<Double>) {
    Canvas(Modifier.fillMaxWidth().height(140.dp)) {
        if (points.size < 2) return@Canvas
        val min = points.minOrNull() ?: 0.0
        val max = points.maxOrNull() ?: 1.0
        val range = (max - min).takeIf { it > 0.0 } ?: 1.0
        val stepX = size.width / (points.lastIndex)
        val mapped = points.mapIndexed { index, value ->
            Offset(
                x = index * stepX,
                y = size.height - (((value - min) / range).toFloat() * size.height),
            )
        }
        for (i in 0 until mapped.lastIndex) {
            drawLine(Color(0xFF2E7D32), mapped[i], mapped[i + 1], strokeWidth = 4f)
        }
    }
}
