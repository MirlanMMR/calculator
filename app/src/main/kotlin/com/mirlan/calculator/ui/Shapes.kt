package com.mirlan.calculator.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mirlan.calculator.data.ButtonShape
import com.mirlan.calculator.data.ShapeOption
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

val HexagonShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = min(cx, cy) * 0.97f
        val path = Path().apply {
            moveTo(cx + r, cy)
            for (i in 1..5) {
                val angle = Math.toRadians(60.0 * i)
                lineTo((cx + r * cos(angle)).toFloat(), (cy + r * sin(angle)).toFloat())
            }
            close()
        }
        return Outline.Generic(path)
    }
}

fun ShapeOption.toComposeShape(): Shape = when (shape) {
    ButtonShape.Circle      -> CircleShape
    ButtonShape.RoundedRect -> RoundedCornerShape(cornerDp.dp)
    ButtonShape.Square      -> RoundedCornerShape(0.dp)
    ButtonShape.Hexagon     -> HexagonShape
}

fun ShapeOption.toMiniShape(): Shape = when (shape) {
    ButtonShape.Circle      -> CircleShape
    ButtonShape.RoundedRect -> RoundedCornerShape((cornerDp / 4.7f).dp)
    ButtonShape.Square      -> RoundedCornerShape(0.5.dp)
    ButtonShape.Hexagon     -> HexagonShape
}
