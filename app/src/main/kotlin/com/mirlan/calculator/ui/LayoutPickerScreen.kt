package com.mirlan.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirlan.calculator.CalculatorViewModel
import com.mirlan.calculator.data.*

private val Accent = Color(0xFFFF9F0A)
private val TileBg = Color(0xFF1C1C1E)

@Composable
fun LayoutPickerScreen(vm: CalculatorViewModel) {
    val spec = vm.layoutSpec
    val currentShape = vm.selectedShape

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2C2C2E))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White.copy(0.3f))
                    ) { vm.back() },
                contentAlignment = Alignment.Center
            ) {
                Text("←", color = Color.White, fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Text("Вид", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }

        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                Label("Тема")
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Presets.all.chunked(6).forEach { rowPresets ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            rowPresets.forEach { p ->
                                ThemeSwatch(p, p.id == vm.selectedPreset.id) { vm.selectPreset(p) }
                            }
                        }
                    }
                }

                Label("Форма кнопок")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ShapeOption.entries.forEach { option ->
                        ShapeOptionBtn(option, option == currentShape, vm.selectedPreset.theme) { vm.selectShape(option) }
                    }
                }

                Label("Операторы")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(OpSide.Left, OpSide.Right).forEach { side ->
                        OpsTile(vm.selectedPreset.theme, side, spec.ops == side) {
                            vm.selectLayout(LayoutSpec(spec.zero, side))
                        }
                    }
                }

                Label("Раскладка")
            }

            items(2) { rowIdx ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ZeroPos.entries.drop(rowIdx * 3).take(3).forEach { z ->
                        val s = LayoutSpec(z, spec.ops)
                        LayoutTile(
                            preset = vm.selectedPreset.copy(grid = s.generate()),
                            shapeOption = currentShape,
                            selected = spec.zero == z,
                            modifier = Modifier.weight(1f)
                        ) { vm.selectLayout(s) }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text,
        color = Color(0xFF888888),
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun ThemeSwatch(preset: LayoutPreset, selected: Boolean, onClick: () -> Unit) {
    val t = preset.theme
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(t.background)
            .border(2.dp, if (selected) Accent else Color(0xFF3A3A3A), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(0.2f))
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(t.numBg))
            Box(Modifier.size(12.dp).clip(CircleShape).background(t.opBg))
        }
    }
}

@Composable
private fun ShapeOptionBtn(option: ShapeOption, selected: Boolean, theme: CalcTheme, onClick: () -> Unit) {
    val shape: Shape = when (option.shape) {
        ButtonShape.Circle -> CircleShape
        ButtonShape.RoundedRect -> RoundedCornerShape((option.cornerDp * 0.42f).dp)
        ButtonShape.Square -> RoundedCornerShape(0.dp)
        ButtonShape.Hexagon -> HexagonShape
    }
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(theme.background)
            .border(if (selected) 3.dp else 2.dp, if (selected) Accent else Color(0xFF3A3A3A), RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(0.2f))
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(shape)
                .background(theme.opBg)
        )
    }
}

@Composable
private fun OpsTile(theme: CalcTheme, side: OpSide, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(TileBg)
            .border(1.5.dp, if (selected) Accent else Color.Transparent, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(0.2f))
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(4) { i ->
                        val isOp = if (side == OpSide.Right) i == 3 else i == 0
                        Box(
                            Modifier
                                .size(7.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isOp) theme.opBg else Color(0xFF4A4A4A))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LayoutTile(
    preset: LayoutPreset,
    shapeOption: ShapeOption,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(preset.theme.background)
            .border(2.dp, if (selected) Accent else Color(0xFF3A3A3A), RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(0.2f))
            ) { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        MiniPreview(preset, shapeOption)
    }
}

@Composable
private fun MiniPreview(preset: LayoutPreset, shapeOption: ShapeOption) {
    val theme = preset.theme
    val cellSize = 17.dp
    val gap = 2.dp

    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
        preset.grid.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                row.forEach { cell ->
                    val isOp = cell.key in listOf(CalcKey.Plus, CalcKey.Minus, CalcKey.Multiply, CalcKey.Divide)
                    val isEq = cell.key == CalcKey.Equals
                    val isFn = cell.key.isFn
                    val bg = when { isOp -> theme.opBg; isEq -> theme.eqBg; isFn -> theme.fnBg; else -> theme.numBg }
                    val fg = when { isOp -> theme.opText; isEq -> theme.eqText; isFn -> theme.fnText; else -> theme.numText }
                    val w = cellSize * cell.colSpan + gap * (cell.colSpan - 1)
                    val h = cellSize * cell.rowSpan + gap * (cell.rowSpan - 1)
                    val shape: Shape = shapeOption.toMiniShape()
                    Box(
                        modifier = Modifier.width(w).height(h).clip(shape).background(bg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cell.key.defaultLabel, color = fg, fontSize = 8.sp, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
