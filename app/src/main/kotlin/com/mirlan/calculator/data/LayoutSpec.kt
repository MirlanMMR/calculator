package com.mirlan.calculator.data

enum class ZeroPos {
    TopLeft, TopCenter, TopRight, BottomLeft, BottomCenter, BottomRight
}

enum class OpSide { Right, Left }

data class LayoutSpec(val zero: ZeroPos, val ops: OpSide) {

    // AC % ⌫ = is always the top row. Zero on top: the zero row, then - + ÷ × down the operator column.
    // Zero at the bottom: the same rows below the top row in reverse order.
    fun generate(): List<List<KeyCell>> {
        val right = ops == OpSide.Right
        fun k(key: CalcKey, span: Int = 1) = KeyCell(key, span)
        fun digits(a: CalcKey, b: CalcKey, c: CalcKey, op: CalcKey) =
            if (right) listOf(k(a), k(b), k(c), k(op)) else listOf(k(op), k(a), k(b), k(c))

        val fnRow = if (right)
            listOf(k(CalcKey.Clear), k(CalcKey.Percent), k(CalcKey.Backspace), k(CalcKey.Equals))
        else
            listOf(k(CalcKey.Equals), k(CalcKey.Backspace), k(CalcKey.Percent), k(CalcKey.Clear))

        val minus = k(CalcKey.Minus)
        val dot = k(CalcKey.Decimal)
        val wide = k(CalcKey.N0, 2)
        val zeroRow = when (zero) {
            ZeroPos.BottomCenter, ZeroPos.TopCenter ->
                if (right) listOf(k(CalcKey.Negate), k(CalcKey.N0), dot, minus)
                else listOf(minus, dot, k(CalcKey.N0), k(CalcKey.Negate))
            ZeroPos.BottomLeft, ZeroPos.TopLeft ->
                if (right) listOf(wide, dot, minus) else listOf(minus, wide, dot)
            ZeroPos.BottomRight, ZeroPos.TopRight ->
                if (right) listOf(dot, wide, minus) else listOf(minus, dot, wide)
        }

        val topLayout = listOf(
            fnRow,
            zeroRow,
            digits(CalcKey.N1, CalcKey.N2, CalcKey.N3, CalcKey.Plus),
            digits(CalcKey.N4, CalcKey.N5, CalcKey.N6, CalcKey.Divide),
            digits(CalcKey.N7, CalcKey.N8, CalcKey.N9, CalcKey.Multiply)
        )
        val top = zero == ZeroPos.TopLeft || zero == ZeroPos.TopCenter || zero == ZeroPos.TopRight
        return if (top) topLayout else listOf(fnRow) + topLayout.drop(1).reversed()
    }
}
