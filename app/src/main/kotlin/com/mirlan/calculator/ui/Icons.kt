package com.mirlan.calculator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val heartPath: Path = Path().apply {
    moveTo(12f, 21.35f)
    lineTo(10.55f, 20.03f)
    cubicTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
    cubicTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
    cubicTo(9.24f, 3f, 10.91f, 3.81f, 12f, 5.09f)
    cubicTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
    cubicTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
    cubicTo(22f, 12.28f, 18.6f, 15.36f, 13.45f, 20.04f)
    close()
}

@Composable
fun HeartIcon(filled: Boolean, color: Color, size: Dp = 20.dp) {
    Canvas(Modifier.size(size)) {
        scale(this.size.width / 24f, pivot = Offset.Zero) {
            if (filled) drawPath(heartPath, color, style = Fill)
            else drawPath(heartPath, color, style = Stroke(2f, join = StrokeJoin.Round))
        }
    }
}
