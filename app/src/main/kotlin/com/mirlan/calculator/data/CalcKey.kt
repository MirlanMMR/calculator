package com.mirlan.calculator.data

enum class CalcKey(val defaultLabel: String) {
    N0("0"), N1("1"), N2("2"), N3("3"), N4("4"),
    N5("5"), N6("6"), N7("7"), N8("8"), N9("9"),
    Decimal("."),
    Plus("+"), Minus("−"), Multiply("×"), Divide("÷"),
    Equals("="),
    Clear("C"),
    Backspace("⌫"),
    Percent("%"),
    Negate("+/−"),
    MemClear("MC"), MemRecall("MR"), MemPlus("M+"), MemMinus("M−"), MemStore("MS"),
    Sqrt("√"), Square("x²"), Inverse("1/x"), Power("xʸ"),
    Sin("sin"), Cos("cos"), Tan("tan"), Ln("ln"), Log("log"),
    Pi("π"), Euler("e"),
    Empty("")
}

private val NON_FUNCTION = setOf(
    CalcKey.N0, CalcKey.N1, CalcKey.N2, CalcKey.N3, CalcKey.N4,
    CalcKey.N5, CalcKey.N6, CalcKey.N7, CalcKey.N8, CalcKey.N9,
    CalcKey.Decimal, CalcKey.Plus, CalcKey.Minus, CalcKey.Multiply, CalcKey.Divide,
    CalcKey.Equals, CalcKey.Empty
)

val CalcKey.isFn: Boolean get() = this !in NON_FUNCTION
