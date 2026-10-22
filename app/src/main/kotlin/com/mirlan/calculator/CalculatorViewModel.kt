package com.mirlan.calculator

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.mirlan.calculator.data.CalcKey
import com.mirlan.calculator.data.KeyCell
import com.mirlan.calculator.data.LayoutPreset
import com.mirlan.calculator.data.LayoutSpec
import com.mirlan.calculator.data.OpSide
import com.mirlan.calculator.data.ZeroPos
import com.mirlan.calculator.data.Presets
import com.mirlan.calculator.data.ShapeOption
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

data class HistoryEntry(val expr: String, val result: String)

sealed class Screen { object Calculator : Screen(); object LayoutPicker : Screen() }

data class CalcState(
    val display: String = "0",
    val expression: String = "",
    val operand1: Double? = null,
    val operator: String? = null,
    val waitingForOperand: Boolean = false,
    val justEvaluated: Boolean = false
)

class CalculatorViewModel(app: Application) : AndroidViewModel(app) {

    private companion object { const val MIN_PAGES = 3 }

    private val prefs = app.getSharedPreferences("calc_prefs", Context.MODE_PRIVATE)

    var advanced by mutableStateOf(prefs.getBoolean("advanced", false))
        private set

    fun toggleAdvanced() {
        advanced = !advanced
        prefs.edit().putBoolean("advanced", advanced).apply()
    }

    // --- History ---
    val history = mutableStateListOf<HistoryEntry>()

    init {
        prefs.getString("history", null)?.lines()?.forEach { l ->
            val p = l.split("\t")
            if (p.size == 2) history.add(HistoryEntry(p[0], p[1]))
        }
    }

    private fun record(expr: String, result: String) {
        if (result == "Ошибка") return
        history.add(HistoryEntry(expr, result))
        while (history.size > 200) history.removeAt(0)
        saveHistory()
    }

    private fun saveHistory() {
        prefs.edit().putString("history", history.joinToString("\n") { "${it.expr}\t${it.result}" }).apply()
    }

    fun pasteNumber(text: String) {
        val m = Regex("-?\\d+(\\.\\d+)?").find(text.replace(Regex("[\\s\u00A0]"), "").replace(',', '.')) ?: return
        val v = m.value.take(15)
        state = state.copy(display = v, waitingForOperand = false, justEvaluated = false)
    }

    fun useHistory(e: HistoryEntry) {
        state = state.copy(display = e.result, justEvaluated = true, waitingForOperand = false)
    }

    fun removeLastHistory() {
        if (history.isEmpty()) return
        history.removeAt(history.lastIndex)
        saveHistory()
    }

    fun clearHistory() {
        history.clear()
        saveHistory()
    }

    // --- Navigation ---
    var currentScreen by mutableStateOf<Screen>(Screen.Calculator)
        private set

    // --- Preset selection ---
    var selectedPreset by mutableStateOf(
        Presets.all.find { it.id == (prefs.getString("preset", "classic") ?: "classic") }
            ?: Presets.classic
    )
        private set

    var selectedShape by mutableStateOf(
        ShapeOption.entries.find { it.name == (prefs.getString("shape", "Circle") ?: "Circle") }
            ?: ShapeOption.Circle
    )
        private set

    fun selectShape(option: ShapeOption) {
        selectedShape = option
        prefs.edit().putString("shape", option.name).apply()
    }

    // Per-preset custom grids (in-memory; persisted in Phase 3)
    private val customGrids = mutableMapOf<String, List<List<KeyCell>>>()

    init {
        if (prefs.getInt("layout_v", 0) < 2) {
            val e = prefs.edit().putInt("layout_v", 2)
            Presets.all.forEach { e.remove("grid_${it.id}") }
            e.apply()
        }
        Presets.all.forEach { p ->
            prefs.getString("grid_${p.id}", null)?.let { s -> decodeGrid(s)?.let { customGrids[p.id] = it } }
        }
    }

    private fun encodeGrid(g: List<List<KeyCell>>) =
        g.joinToString("|") { row -> row.joinToString(",") { "${it.key.name}:${it.colSpan}:${it.rowSpan}" } }

    private fun decodeGrid(s: String): List<List<KeyCell>>? = try {
        s.split("|").map { row ->
            row.split(",").map { c ->
                val (k, cs, rs) = c.split(":")
                KeyCell(CalcKey.valueOf(k), cs.toInt(), rs.toInt())
            }
        }
    } catch (e: Exception) { null }

    // --- Edit mode ---
    var editMode by mutableStateOf(false)
        private set

    var editGrid by mutableStateOf<List<List<KeyCell>>>(emptyList())
        private set

    var layoutSpec by mutableStateOf(
        prefs.getString("layout", null)?.split(":")?.let { p ->
            if (p.size != 2) null else {
                val z = ZeroPos.entries.find { it.name == p[0] }
                val o = OpSide.entries.find { it.name == p[1] }
                if (z != null && o != null) LayoutSpec(z, o) else null
            }
        } ?: LayoutSpec(ZeroPos.BottomLeft, OpSide.Right)
    )
        private set

    val baseGrid: List<List<KeyCell>>
        get() = layoutSpec.generate()

    fun selectLayout(spec: LayoutSpec) {
        layoutSpec = spec
        customGrids.clear()
        val e = prefs.edit().putString("layout", "${spec.zero.name}:${spec.ops.name}")
        Presets.all.forEach { e.remove("grid_${it.id}") }
        e.apply()
    }

    val activeGrid: List<List<KeyCell>>
        get() = when {
            editMode -> editGrid
            customGrids.containsKey(selectedPreset.id) -> customGrids[selectedPreset.id]!!
            else -> baseGrid
        }

    // --- Calc state ---
    val pages = mutableStateListOf(CalcState())
    var page by mutableStateOf(0)
        private set

    var state: CalcState
        get() = pages[page.coerceIn(0, pages.lastIndex)]
        private set(v) { pages[page.coerceIn(0, pages.lastIndex)] = v }

    init {
        prefs.getString("pages", null)?.split("\n")?.mapNotNull { decodePage(it) }
            ?.takeIf { it.isNotEmpty() }?.let { pages.clear(); pages.addAll(it) }
        page = prefs.getInt("page", 0).coerceIn(0, pages.lastIndex)
        normalizePages()
    }

    private fun encodePage(s: CalcState) =
        listOf(s.display, s.expression, s.operand1?.toString() ?: "", s.operator ?: "",
            s.waitingForOperand.toString(), s.justEvaluated.toString()).joinToString("\t")

    private fun decodePage(l: String): CalcState? {
        val p = l.split("\t")
        if (p.size != 6) return null
        return CalcState(p[0], p[1], p[2].toDoubleOrNull(), p[3].ifEmpty { null }, p[4].toBoolean(), p[5].toBoolean())
    }

    private fun isBlank(s: CalcState) = s.display == "0" && s.expression.isEmpty() && s.operand1 == null

    private fun normalizePages() {
        while (pages.size > MIN_PAGES && page < pages.lastIndex && isBlank(pages.last()) && isBlank(pages[pages.lastIndex - 1]))
            pages.removeAt(pages.lastIndex)
        while (pages.size < MIN_PAGES) pages.add(CalcState())
        if (!isBlank(pages.last()) && pages.size < 5) pages.add(CalcState())
        prefs.edit()
            .putString("pages", pages.joinToString("\n") { encodePage(it) })
            .putInt("page", page).apply()
    }

    fun selectPage(i: Int) {
        if (i == page || i !in pages.indices) return
        page = i
        normalizePages()
    }

    private var memory: Double = 0.0

    val clearLabel: String get() = if (state.display != "0" && !state.waitingForOperand) "C" else "AC"

    // --- Navigation actions ---
    fun openLayoutPicker() { currentScreen = Screen.LayoutPicker }
    fun back() { currentScreen = Screen.Calculator }

    fun selectPreset(preset: LayoutPreset) {
        selectedPreset = preset
        prefs.edit().putString("preset", preset.id).apply()
    }

    // --- Edit mode actions ---
    fun enterEditMode() {
        editGrid = activeGrid.map { it.toList() }
        editMode = true
    }

    fun saveEditMode() {
        val id = selectedPreset.id
        val trimmed = editGrid.dropWhile { row -> row.all { it.key == CalcKey.Empty } }
            .dropLastWhile { row -> row.all { it.key == CalcKey.Empty } }
        if (trimmed == baseGrid) {
            customGrids.remove(id)
            prefs.edit().remove("grid_$id").apply()
        } else {
            customGrids[id] = trimmed
            prefs.edit().putString("grid_$id", encodeGrid(trimmed)).apply()
        }
        editMode = false
    }

    val removedKeys: List<CalcKey>
        get() = CalcKey.entries.filter { k -> k != CalcKey.Empty && editGrid.none { row -> row.any { it.key == k } } }

    fun removeAt(r: Int, c: Int) {
        if (editGrid.getOrNull(r)?.getOrNull(c)?.key.let { it == null || it == CalcKey.Empty }) return
        editGrid = editGrid.mapIndexed { i, row ->
            if (i != r) row else row.mapIndexed { j, cell -> if (j == c) cell.copy(key = CalcKey.Empty) else cell }
        }
    }

    fun placeKey(key: CalcKey) {
        var g = editGrid
        if (g.none { row -> row.any { it.key == CalcKey.Empty } }) {
            if (g.size >= 7) return
            g = listOf(List(selectedPreset.cols) { KeyCell(CalcKey.Empty) }) + g
        }
        val r = g.indexOfFirst { row -> row.any { it.key == CalcKey.Empty } }
        val c = g[r].indexOfFirst { it.key == CalcKey.Empty }
        editGrid = g.mapIndexed { i, row ->
            if (i != r) row else row.mapIndexed { j, cell -> if (j == c) cell.copy(key = key) else cell }
        }
    }

    fun placeKeyAt(r: Int, c: Int, key: CalcKey) {
        if (editGrid.getOrNull(r)?.getOrNull(c)?.key != CalcKey.Empty) return
        editGrid = editGrid.mapIndexed { i, row ->
            if (i != r) row else row.mapIndexed { j, cell -> if (j == c) cell.copy(key = key) else cell }
        }
    }

    fun cycleSize(key: CalcKey) {
        val g = editGrid
        val r = g.indexOfFirst { row -> row.any { it.key == key } }
        if (r < 0) return
        val row = g[r].toMutableList()
        val c = row.indexOfFirst { it.key == key }
        val cell = row[c]
        if (cell.colSpan > 1) {
            row[c] = cell.copy(colSpan = cell.colSpan - 1)
            row.add(c + 1, KeyCell(CalcKey.Empty))
        } else {
            val nbIdx = if (c + 1 < row.size) c + 1 else if (c > 0) c - 1 else return
            val nb = row[nbIdx]
            row[c] = cell.copy(colSpan = cell.colSpan + 1)
            if (nb.colSpan > 1) row[nbIdx] = nb.copy(colSpan = nb.colSpan - 1) else row.removeAt(nbIdx)
        }
        editGrid = g.mapIndexed { i, rw -> if (i == r) row else rw }
    }

    fun resetEdit() {
        editGrid = baseGrid.map { it.toList() }
    }

    private fun reorder(keys: MutableList<CalcKey>, from: Int, to: Int) {
        if (keys[to] == CalcKey.Empty) { val t = keys[to]; keys[to] = keys[from]; keys[from] = t }
        else keys.add(to, keys.removeAt(from))
    }

    fun cancelEditMode() {
        editMode = false
    }

    // Pushes a key along its row; keys between shift by one, slot shapes stay in place.
    fun moveInRow(key: CalcKey, toCol: Int) {
        val r = editGrid.indexOfFirst { row -> row.any { it.key == key } }
        if (r < 0) return
        val keys = editGrid[r].map { it.key }.toMutableList()
        val from = keys.indexOf(key)
        if (from == toCol || toCol !in keys.indices) return
        reorder(keys, from, toCol)
        editGrid = normalizeGrid(
            editGrid.mapIndexed { i, row ->
                if (i != r) row else row.mapIndexed { j, c -> c.copy(key = keys[j]) }
            },
            zeroSpan()
        )
    }

    // Pushes a key along its column (slots covering the same visual column).
    fun moveInColumn(key: CalcKey, toRow: Int) {
        val g = editGrid
        val r = g.indexOfFirst { row -> row.any { it.key == key } }
        if (r < 0 || r == toRow || toRow !in g.indices) return
        val c = g[r].indexOfFirst { it.key == key }
        val v = g[r].take(c).sumOf { it.colSpan }
        val slots = g.map { row ->
            var acc = 0
            var idx = 0
            row.forEachIndexed { i, cell -> if (v >= acc) idx = i; acc += cell.colSpan }
            idx
        }
        val keys = g.mapIndexed { i, row -> row[slots[i]].key }.toMutableList()
        reorder(keys, r, toRow)
        editGrid = normalizeGrid(
            g.mapIndexed { i, row ->
                row.mapIndexed { j, cell -> if (j == slots[i]) cell.copy(key = keys[i]) else cell }
            },
            zeroSpan()
        )
    }

    private fun zeroSpan() = editGrid.flatten().firstOrNull { it.key == CalcKey.N0 }?.colSpan ?: 1

    // The zero key keeps its width wherever it goes; rows are refilled to the full width.
    private fun normalizeGrid(g: List<List<KeyCell>>, zs: Int): List<List<KeyCell>> {
        val cols = selectedPreset.cols
        val spill = ArrayDeque<CalcKey>()
        val rows = g.map { row ->
            val cells = row.map { it.copy(colSpan = if (it.key == CalcKey.N0) zs else 1) }.toMutableList()
            var w = cells.sumOf { it.colSpan }
            while (w > cols) {
                var idx = cells.indexOfLast { it.key == CalcKey.Empty }
                if (idx < 0) idx = cells.indexOfLast { it.key != CalcKey.N0 }
                if (idx < 0) break
                val c = cells.removeAt(idx)
                w -= c.colSpan
                if (c.key != CalcKey.Empty) spill.addLast(c.key)
            }
            cells
        }
        return rows.map { cells ->
            val out = cells.toMutableList()
            var w = out.sumOf { it.colSpan }
            while (w < cols) { out.add(KeyCell(spill.removeFirstOrNull() ?: CalcKey.Empty)); w++ }
            out
        }
    }

    // --- Key handling ---
    fun onKey(key: CalcKey) {
        if (key == CalcKey.Clear && isBlank(state) && page < pages.lastIndex && pages.size > MIN_PAGES) {
            pages.removeAt(page)
            normalizePages()
            return
        }
        applyKey(key)
        normalizePages()
    }

    private fun applyKey(key: CalcKey) {
        when (key) {
            CalcKey.N0 -> digit("0"); CalcKey.N1 -> digit("1"); CalcKey.N2 -> digit("2")
            CalcKey.N3 -> digit("3"); CalcKey.N4 -> digit("4"); CalcKey.N5 -> digit("5")
            CalcKey.N6 -> digit("6"); CalcKey.N7 -> digit("7"); CalcKey.N8 -> digit("8")
            CalcKey.N9 -> digit("9")
            CalcKey.Decimal  -> decimal()
            CalcKey.Plus     -> operator("+"); CalcKey.Minus    -> operator("−")
            CalcKey.Multiply -> operator("×"); CalcKey.Divide   -> operator("÷")
            CalcKey.Equals   -> equals()
            CalcKey.Clear    -> clear()
            CalcKey.Backspace -> backspace()
            CalcKey.Percent  -> percent()
            CalcKey.Negate   -> negate()
            CalcKey.MemClear  -> memory = 0.0
            CalcKey.MemRecall -> { state = state.copy(display = fmt(memory), justEvaluated = true) }
            CalcKey.MemPlus   -> memory += state.display.toDoubleOrNull() ?: 0.0
            CalcKey.MemMinus  -> memory -= state.display.toDoubleOrNull() ?: 0.0
            CalcKey.MemStore  -> memory = state.display.toDoubleOrNull() ?: 0.0
            CalcKey.Sqrt    -> unary({ "√($it)" }) { if (it < 0) Double.NaN else sqrt(it) }
            CalcKey.Square  -> unary({ "($it)²" }) { it * it }
            CalcKey.Inverse -> unary({ "1/($it)" }) { if (it == 0.0) Double.NaN else 1.0 / it }
            CalcKey.Sin     -> unary({ "sin($it)" }) { sin(Math.toRadians(it)) }
            CalcKey.Cos     -> unary({ "cos($it)" }) { cos(Math.toRadians(it)) }
            CalcKey.Tan     -> unary({ "tan($it)" }) { if (abs(cos(Math.toRadians(it))) < 1e-12) Double.NaN else tan(Math.toRadians(it)) }
            CalcKey.Ln      -> unary({ "ln($it)" }) { if (it <= 0) Double.NaN else ln(it) }
            CalcKey.Log     -> unary({ "log($it)" }) { if (it <= 0) Double.NaN else log10(it) }
            CalcKey.Pi      -> constant(PI)
            CalcKey.Euler   -> constant(E)
            CalcKey.Power   -> operator("^")
            CalcKey.Empty   -> Unit
        }
    }

    private fun digit(d: String) {
        val s = state
        state = s.copy(
            display = when {
                s.waitingForOperand || s.justEvaluated -> d
                s.display == "0" -> d
                s.display.length >= 12 -> s.display
                else -> s.display + d
            },
            waitingForOperand = false, justEvaluated = false
        )
    }

    private fun decimal() {
        val s = state
        val d = if (s.waitingForOperand || s.justEvaluated) "0." else s.display
        if ("." in d) return
        state = s.copy(display = "$d.", waitingForOperand = false, justEvaluated = false)
    }

    private fun operator(op: String) {
        val s = state
        val cur = s.display.toDoubleOrNull() ?: 0.0
        val (res, expr) = if (s.operand1 != null && s.operator != null && !s.waitingForOperand)
            Pair(compute(s.operand1, cur, s.operator), "${fmt(compute(s.operand1, cur, s.operator))} $op")
        else Pair(cur, "${fmt(cur)} $op")
        state = s.copy(display = fmt(res), expression = expr, operand1 = res, operator = op,
            waitingForOperand = true, justEvaluated = false)
    }

    private fun equals() {
        val s = state
        val op1 = s.operand1 ?: return
        val op = s.operator ?: return
        val op2 = s.display.toDoubleOrNull() ?: 0.0
        val res = compute(op1, op2, op)
        record("${s.expression} ${fmt(op2)}", fmt(res))
        state = s.copy(display = fmt(res), expression = "${s.expression} ${fmt(op2)} =",
            operand1 = null, operator = null, waitingForOperand = false, justEvaluated = true)
    }

    private fun clear() {
        val s = state
        if (s.display != "0" && !s.waitingForOperand) state = s.copy(display = "0")
        else state = CalcState()
    }

    private fun backspace() {
        val s = state
        if (s.waitingForOperand || s.justEvaluated) return
        state = s.copy(display = if (s.display.length <= 1) "0"
        else s.display.dropLast(1).trimEnd('-').ifEmpty { "0" })
    }

    private fun percent() {
        val s = state
        val v = s.display.toDoubleOrNull() ?: return
        state = s.copy(display = fmt(if (s.operand1 != null) v / 100.0 * s.operand1!! else v / 100.0))
    }

    private fun negate() {
        val s = state
        val v = s.display.toDoubleOrNull() ?: return
        state = s.copy(display = fmt(-v))
    }

    private fun unary(expr: (String) -> String, f: (Double) -> Double) {
        val v = state.display.toDoubleOrNull() ?: return
        var r = f(v)
        if (abs(r) < 1e-12) r = 0.0
        val out = fmt(r)
        record(expr(fmt(v)), out)
        state = state.copy(display = out, justEvaluated = true, waitingForOperand = false)
    }

    private fun constant(v: Double) {
        state = state.copy(display = fmt(v), justEvaluated = true, waitingForOperand = false)
    }

    private fun compute(a: Double, b: Double, op: String) = when (op) {
        "+" -> a + b; "−" -> a - b; "×" -> a * b
        "÷" -> if (b != 0.0) a / b else Double.NaN
        "^" -> a.pow(b)
        else -> b
    }

    private fun fmt(v: Double): String {
        if (v.isNaN()) return "Ошибка"
        if (v.isInfinite()) return if (v > 0) "∞" else "-∞"
        val a = abs(v)
        return when {
            a >= 1e12 || (a < 1e-6 && a > 0) -> String.format("%.4e", v)
            v == floor(v) && a < 1e12 -> v.toLong().toString()
            else -> v.toBigDecimal().stripTrailingZeros().toPlainString()
                .let { if (it.length > 12) String.format("%.8g", v).trimEnd('0').trimEnd('.') else it }
        }
    }
}
