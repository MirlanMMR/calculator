package com.mirlan.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

@Composable
fun LayoutPickerScreen(vm: CalculatorViewModel) {
    val currentPresetId = vm.selectedPreset.id
    val currentShape = vm.selectedShape

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .systemBarsPadding()
    ) {
        // Header
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
            Text("Раскладки", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }

        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            // Shape picker
            item {
                Text(
                    "Форма кнопок",
                    color = Color(0xFF888888),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ShapeOption.entries.forEach { option ->
                        ShapeOptionBtn(
                            option = option,
                            selected = option == currentShape,
                            onClick = { vm.selectShape(option) }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    "Раскладка",
                    color = Color(0xFF888888),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Preset grid — 2 columns via manual rows
            val presets = Presets.all
            items(presets.size / 2 + presets.size % 2) { rowIdx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val i1 = rowIdx * 2
                    val i2 = rowIdx * 2 + 1
                    PresetCard(
                        preset = presets[i1],
                        shapeOption = currentShape,
                        selected = presets[i1].id == currentPresetId,
                        modifier = Modifier.weight(1f),
                        onClick = { vm.selectPreset(presets[i1]) }
                    )
                    if (i2 < presets.size) {
                        PresetCard(
                            preset = presets[i2],
                            shapeOption = currentShape,
                            selected = presets[i2].id == currentPresetId,
                            modifier = Modifier.weight(1f),
                            onClick = { vm.selectPreset(presets[i2]) }
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ShapeOptionBtn(option: ShapeOption, selected: Boolean, onClick: () -> Unit) {
    val border = if (selected) Color(0xFFFF9F0A) else Color.Transparent
    val bg = if (selected) Color(0xFF2C2C2E) else Color(0xFF1C1C1E)

    val shape: Shape = option.toComposeShape()

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(0.2f))
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Mini button preview using the actual shape
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(shape)
                .background(if (selected) Color(0xFFFF9F0A) else Color(0xFF3A3A3A))
        )
    }
}

@Composable
private fun PresetCard(
    preset: LayoutPreset,
    shapeOption: ShapeOption,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (selected) Color(0xFFFF9F0A) else Color.Transparent

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(0.2f))
            ) { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MiniPreview(preset, shapeOption)
        Spacer(Modifier.height(10.dp))
        Text(preset.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MiniPreview(preset: LayoutPreset, shapeOption: ShapeOption) {
    val theme = preset.theme
    val cellSize = 14.dp
    val gap = 3.dp

    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
        preset.grid.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                row.forEach { cell ->
                    val isOp = cell.key in listOf(CalcKey.Plus, CalcKey.Minus, CalcKey.Multiply, CalcKey.Divide)
                    val isEq = cell.key == CalcKey.Equals
                    val isFn = cell.key.isFn

                    val bg = when { isOp -> theme.opBg; isEq -> theme.eqBg; isFn -> theme.fnBg; else -> theme.numBg }
                    val w = cellSize * cell.colSpan + gap * (cell.colSpan - 1)
                    val h = cellSize * cell.rowSpan + gap * (cell.rowSpan - 1)

                    val shape: Shape = shapeOption.toMiniShape()

                    Box(modifier = Modifier.width(w).height(h).clip(shape).background(bg))
                }
            }
        }
    }
}
