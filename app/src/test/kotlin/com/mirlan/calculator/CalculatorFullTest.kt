package com.mirlan.calculator

import android.app.Application
import android.content.SharedPreferences
import com.mirlan.calculator.data.CalcKey
import org.junit.Assert.assertTrue
import org.junit.Test

class FakePrefs : SharedPreferences {
    val m = mutableMapOf<String, Any?>()
    override fun getAll(): MutableMap<String, *> = m
    override fun getString(k: String, d: String?) = m[k] as String? ?: d
    override fun getStringSet(k: String, d: MutableSet<String>?) = d
    override fun getInt(k: String, d: Int) = m[k] as Int? ?: d
    override fun getLong(k: String, d: Long) = m[k] as Long? ?: d
    override fun getFloat(k: String, d: Float) = m[k] as Float? ?: d
    override fun getBoolean(k: String, d: Boolean) = m[k] as Boolean? ?: d
    override fun contains(k: String) = m.containsKey(k)
    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        override fun putString(k: String, v: String?) = apply { m[k] = v }
        override fun putStringSet(k: String, v: MutableSet<String>?) = this
        override fun putInt(k: String, v: Int) = apply { m[k] = v }
        override fun putLong(k: String, v: Long) = apply { m[k] = v }
        override fun putFloat(k: String, v: Float) = apply { m[k] = v }
        override fun putBoolean(k: String, v: Boolean) = apply { m[k] = v }
        override fun remove(k: String) = apply { m.remove(k) }
        override fun clear() = apply { m.clear() }
        override fun commit() = true
        override fun apply() {}
    }
}

class FakeApp(val prefs: FakePrefs) : Application() {
    override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences = prefs
}

class CalculatorFullTest {
    private val keyMap = mapOf(
        "+" to CalcKey.Plus, "-" to CalcKey.Minus, "*" to CalcKey.Multiply, "/" to CalcKey.Divide,
        "=" to CalcKey.Equals, "C" to CalcKey.Clear, "B" to CalcKey.Backspace, "%" to CalcKey.Percent,
        "N" to CalcKey.Negate, "." to CalcKey.Decimal, "MC" to CalcKey.MemClear, "MR" to CalcKey.MemRecall,
        "M+" to CalcKey.MemPlus, "M-" to CalcKey.MemMinus, "MS" to CalcKey.MemStore,
        "sqrt" to CalcKey.Sqrt, "sq" to CalcKey.Square, "inv" to CalcKey.Inverse, "^" to CalcKey.Power,
        "sin" to CalcKey.Sin, "cos" to CalcKey.Cos, "tan" to CalcKey.Tan, "ln" to CalcKey.Ln,
        "log" to CalcKey.Log, "pi" to CalcKey.Pi, "e" to CalcKey.Euler
    ) + (0..9).associate { "$it" to CalcKey.valueOf("N$it") }

    private fun vm(prefs: FakePrefs = FakePrefs()) = CalculatorViewModel(FakeApp(prefs))

    private fun run(v: CalculatorViewModel, seq: String): String {
        seq.trim().split(" ").filter { it.isNotEmpty() }.forEach { t ->
            when {
                t.startsWith("c") && t.length > 1 && t[1].isDigit() -> v.setCursor(t.substring(1).toInt())
                t.startsWith("p:") -> v.pasteNumber(t.substring(2).replace("_", " "))
                else -> v.onKey(keyMap[t] ?: error("bad token $t"))
            }
        }
        return v.state.display
    }

    private fun d(seq: String) = run(vm(), seq)

    private val cases = listOf(
        // basic
        "7 + 3 =" to "10", "7 - 3 =" to "4", "7 * 3 =" to "21", "8 / 2 =" to "4",
        "1 / 3 =" to "0.33333333", "2 / 3 =" to "0.66666667",
        "0 . 1 + 0 . 2 =" to "0.3", "0 . 1 + 0 . 7 =" to "0.8", "5 / 0 =" to "Err", "0 / 0 =" to "Err",
        "5 - 8 =" to "-3", "1 2 . 5 * 4 =" to "50", "9 9 9 9 9 9 * 9 9 9 9 9 9 =" to "999998000001",
        "1 . 5 * 1 . 5 =" to "2.25", "1 0 / 4 =" to "2.5", "1 0 0 - 0 . 0 1 =" to "99.99",
        // chains
        "2 + 3 * 4 =" to "20", "2 + 3 + 4 =" to "9", "1 0 - 2 - 3 =" to "5", "2 * 3 * 4 =" to "24",
        "1 0 / 2 / 5 =" to "1", "2 + 3 * 4 - 5 / 3 =" to "5",
        "5 + =" to "10", "5 * =" to "25", "5 + * 3 =" to "15",
        "5 + 3 = + 2 =" to "10", "5 + 3 = * 2 =" to "16", "+ 5 =" to "5", "- 5 =" to "-5", "5 + 3 = 7 =" to "7",
        "3 ^ 2 + 1 =" to "10",
        // negate
        "5 N" to "-5", "5 N N" to "5", "0 N" to "0", "5 N 3" to "-53", "5 + 3 = N" to "-8", "5 N + 3 =" to "-2",
        "5 N * 5 N =" to "25", "5 + N =" to "0", "5 . N 3" to "-5.3", "1 . 0 N 5" to "-1.05",
        // percent
        "2 0 0 + 1 5 % =" to "230", "2 0 0 - 1 0 % =" to "180", "2 0 0 * 1 5 % =" to "30", "2 0 0 / 5 0 % =" to "400",
        "5 0 %" to "0.5", "5 0 % 7" to "7", "2 0 0 + 1 5 % + 1 =" to "231", "1 0 0 + 1 0 % + 1 0 % =" to "121",
        // decimal
        "." to "0.", ". 5 + . 5 =" to "1", "1 . 5 ." to "1.5", "5 . + 3 =" to "8", "0 0 5" to "5",
        "0 . 0 0 1 * 1 0 0 0 =" to "1",
        // backspace
        "1 2 3 B" to "12", "5 B" to "0", "5 N B" to "0", "0 . 5 B B" to "0", "1 2 B B B" to "0",
        "5 + B" to "5", "5 + 3 = B" to "8", "1 . 5 B B" to "1",
        // limits / big / small
        "1 1 1 1 1 1 1 1 1 1 1 1 1 1" to "111111111111",
        "9 9 9 9 9 9 9 9 9 9 9 9 + 1 =" to "1.0000e+12",
        "9 9 9 9 9 9 9 9 9 9 9 9 * 9 =" to "9.0000e+12",
        "9 9 9 9 9 9 9 9 9 9 9 9 * 9 9 9 9 9 9 9 9 9 9 9 9 =" to "1.0000e+24",
        "1 / 3 0 0 0 0 0 0 =" to "3.3333e-07", "0 . 0 0 0 0 0 1" to "0.000001",
        "9 9 9 9 9 9 9 9 9 9 9 9 + 1 = 5" to "5",
        "1 0 0 0 0 0 0 0 + 0 . 1 2 3 4 5 =" to "10000000",
        // power
        "2 ^ 1 0 =" to "1024", "2 ^ 0 . 5 =" to "1.4142136", "2 ^ 0 =" to "1", "0 ^ 0 =" to "1",
        "2 ^ 1 N =" to "0.5", "8 N ^ 0 . 5 =" to "Err", "0 ^ 1 N =" to "∞",
        // sqrt / sq / inv
        "9 sqrt" to "3", "2 sqrt" to "1.4142136", "4 N sqrt" to "Err", "0 sqrt" to "0", "1 6 sqrt sqrt" to "2",
        "5 sq" to "25", "5 N sq" to "25", "0 . 1 sq" to "0.01", "1 2 . 5 sq" to "156.25",
        "1 1 1 1 1 1 sq" to "12345654321", "0 . 0 0 0 0 0 0 1 sq" to "1.0000e-14",
        "4 inv" to "0.25", "0 inv" to "Err", "3 inv" to "0.33333333", "0 . 5 inv" to "2", "4 N inv" to "-0.25",
        "2 sqrt sq" to "2",
        // trig
        "3 0 sin" to "0.5", "9 0 sin" to "1", "1 8 0 sin" to "0", "0 sin" to "0", "3 0 N sin" to "-0.5",
        "6 0 cos" to "0.5", "9 0 cos" to "0", "0 cos" to "1", "1 8 0 cos" to "-1", "4 5 tan" to "1",
        "9 0 tan" to "Err", "0 tan" to "0", "2 7 0 tan" to "Err", "1 3 5 tan" to "-1", "3 6 0 sin" to "0",
        "4 5 sin" to "0.70710678", "3 0 cos" to "0.8660254",
        // ln / log
        "e ln" to "1", "1 ln" to "0", "0 ln" to "Err", "1 N ln" to "Err", "1 0 ln" to "2.3025851",
        "1 0 0 log" to "2", "1 0 0 0 log" to "3", "1 log" to "0", "0 log" to "Err", "2 log" to "0.30103",
        // constants
        "pi" to "3.1415927", "e" to "2.7182818", "2 * pi =" to "6.2831853", "pi * 2 =" to "6.2831853",
        "1 / 3 = * 3 =" to "1", "1 / 3 * 3 =" to "1",
        "1 / 3 0 0 0 0 0 0 = * 3 0 0 0 0 0 0 =" to "1",
        // memory
        "5 MS C MR" to "5", "5 M+ C MR" to "5", "5 M+ 3 M+ C MR" to "8", "5 MS 2 M- C MR" to "3",
        "5 MS MC C MR" to "0", "5 MS C 3 + MR =" to "8", "5 MS C MR + MR =" to "10", "MR" to "0",
        "5 MS C 3 + MR + 1 =" to "9", "3 + pi + 1 =" to "7.1415927",
        "5 MS C 3 + MR 2 =" to "5", "3 + 4 sqrt 5 =" to "8", "3 + 4 sqrt =" to "5", "3 + 4 sqrt + 1 =" to "6",
        "5 MS C 5 / 0 = M+ C MR" to "5", "5 MS C 5 / 0 = MS C MR" to "5",
        // errors / infinity
        "5 / 0 + 3 =" to "Err", "5 / 0 = 7" to "7", "5 / 0 = + 3 =" to "3", "5 / 0 = N" to "Err",
        "5 / 0 = sqrt" to "Err", "5 / 0 = %" to "Err", "5 / 0 = B" to "Err", "5 / 0 = ." to "0.",
        "1 0 ^ 4 0 0 =" to "∞",
        // cursor editing
        "1 2 3 c1 9" to "1923", "1 2 3 c1 ." to "1.23", "1 2 3 c1 B" to "23", "1 2 3 c0 B" to "123",
        "1 2 3 c0 9" to "9123", "5 N c0 9" to "-95", "1 2 3 c1 9 9" to "19923", "1 2 3 c1 9 + 1 =" to "1924",
        "1 2 3 c1 N" to "-123", "1 2 3 c2 ." to "12.3", "1 . 5 c0 5" to "51.5", "0 c0 5" to "5",
        "1 2 3 c1 . 5" to "1.523",
        // paste
        "p:1,234.56" to "1234.56", "p:1_234,56" to "1234.56", "p:\$12.50" to "12.50", "p:.5" to "0.5",
        "p:-5" to "-5", "p:abc" to "0", "p:1.234,56" to "1234.56", 
        "p:1,234.56 + 1 =" to "1235.56", "p:1_000 + 1 =" to "1001"
    )

    @Test fun everyFunction() {
        val bad = cases.mapNotNull { (seq, want) ->
            val got = try { d(seq) } catch (e: Throwable) { "EXC ${e::class.simpleName}" }
            if (got != want) "  [$seq] want=$want got=$got" else null
        }
        assertTrue("${bad.size}/${cases.size} mismatches:\n" + bad.joinToString("\n"), bad.isEmpty())
    }

    @Test fun historyAndPersistence() {
        val bad = mutableListOf<String>()
        val prefs = FakePrefs()
        val v = vm(prefs)
        run(v, "5 + 3 = 2 sqrt 1 / 0 = C 2 + 3 * 4 =")
        val h = v.history.map { it.expr to it.result }
        if (h != listOf("5 + 3" to "8", "√(2)" to "1.4142136", "5 × 4" to "20")) bad += "history=$h"
        run(v, "7 *")
        val v2 = vm(prefs)
        if (v2.history.size != 3) bad += "history restored=${v2.history.size}"
        if (v2.state.display != "7" || v2.state.operator != "×") bad += "state restored=${v2.state}"
        if (run(v2, "2 =") != "14") bad += "continue after restore=${v2.state.display}"
        val v3 = vm()
        repeat(205) { run(v3, "1 + 1 =") }
        if (v3.history.size != 200) bad += "history cap=${v3.history.size}"
        v3.useHistory(v3.history.last())
        if (v3.state.display != "2") bad += "useHistory=${v3.state.display}"
        v3.removeLastHistory(); v3.clearHistory()
        if (v3.history.isNotEmpty()) bad += "clearHistory"
        assertTrue(bad.joinToString("\n"), bad.isEmpty())
    }

    @Test fun topLineShowsWhatWasEntered() {
        val bad = listOf(
            "4 sqrt" to "√(4)", "1 6 sqrt sqrt" to "√(√(16))", "5 + 4 sqrt" to "5 + √(4)",
            "5 + 4 sqrt =" to "5 + √(4) =", "5 + 3 = sqrt" to "√(8)", "4 sqrt 3" to "√(4) 3".let { "" },
            "4 sqrt N" to "", "4 sqrt + 1 =" to "7 + 1 =".let { "7 + 1 =" }.let { "" }, "2 + 3 sq + 1 =" to "11 + 1 ="
        ).filter { (seq, want) -> want.isNotEmpty() || seq == "4 sqrt N" }.mapNotNull { (seq, want) ->
            val v = vm(); run(v, seq)
            if (v.state.topLine != want) "[$seq] want='$want' got='${v.state.topLine}'" else null
        }
        assertTrue(bad.joinToString("\n"), bad.isEmpty())
    }

    @Test fun pages() {
        val bad = mutableListOf<String>()
        val v = vm()
        run(v, "5 + 3 =")
        v.selectPage(1); run(v, "9 N")
        v.selectPage(0)
        if (v.state.display != "8") bad += "page0=${v.state.display}"
        v.selectPage(1)
        if (v.state.display != "-9") bad += "page1=${v.state.display}"
        v.selectPage(2); run(v, "4")
        if (v.pages.size != 4) bad += "pages after typing in last=${v.pages.size}"
        v.selectPage(3)
        if (v.pages.size != 4) bad += "pages after select 3=${v.pages.size}"
        v.selectPage(2); run(v, "C")
        repeat(3) { v.selectPage(1); run(v, "C") }
        assertTrue(bad.joinToString("\n") + " pages=${v.pages.size}", bad.isEmpty())
    }
}
