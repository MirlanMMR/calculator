package com.mirlan.calculator.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonShape { Circle, RoundedRect, Square, Hexagon }

enum class ShapeOption(val label: String, val shape: ButtonShape, val cornerDp: Int) {
    Hexagon  ("⬡",  ButtonShape.Hexagon,      0),
    Circle   ("●",  ButtonShape.Circle,       0),
    Soft     ("▢",  ButtonShape.RoundedRect, 20),
    Sharp    ("⬜", ButtonShape.RoundedRect,   6),
    Square   ("■",  ButtonShape.Square,        0),
}

data class KeyCell(
    val key: CalcKey,
    val colSpan: Int = 1,
    val rowSpan: Int = 1
)

data class CalcTheme(
    val background: Color,
    val numBg: Color,
    val numText: Color,
    val opBg: Color,
    val opText: Color,
    val fnBg: Color,
    val fnText: Color,
    val eqBg: Color,
    val eqText: Color,
    val activeOpBg: Color,
    val activeOpText: Color,
    val displayText: Color,
    val expressionText: Color,
    val buttonShape: ButtonShape,
    val cornerRadius: Dp = 0.dp,
    val buttonGap: Dp = 12.dp
)

data class LayoutPreset(
    val id: String,
    val name: String,
    val cols: Int,
    val grid: List<List<KeyCell>>,
    val theme: CalcTheme
)
