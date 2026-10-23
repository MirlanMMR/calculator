package com.mirlan.calculator.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import com.mirlan.calculator.CalculatorViewModel
import com.mirlan.calculator.data.*
import kotlin.math.roundToInt

private data class DragState(
    val key: CalcKey,
    val fx: Float, val fy: Float,
    val grabX: Float, val grabY: Float
)

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun CalculatorScreen(vm: CalculatorViewModel) {
    val state = vm.state
    val preset = vm.selectedPreset
    val shapeOption = vm.selectedShape
    val theme = preset.theme.copy(
        buttonShape = shapeOption.shape,
        cornerRadius = shapeOption.cornerDp.dp
    )
    val grid = vm.activeGrid
    val editMode = vm.editMode

    var dragState by remember { mutableStateOf<DragState?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    var pickerSlot by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var favName by remember(editMode) { mutableStateOf("") }


    pickerSlot?.let { (pr, pc) ->
        androidx.compose.ui.window.Dialog(onDismissRequest = { pickerSlot = null }) {
            FlowRow(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vm.removedKeys.forEach { k ->
                    SmallBtn(k.defaultLabel, theme.numBg, theme.numText) {
                        vm.placeKeyAt(pr, pc, k)
                        pickerSlot = null
                    }
                }
            }
        }
    }

    val blinkTransition = rememberInfiniteTransition(label = "blink")
    val blink by blinkTransition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(520, easing = LinearEasing), RepeatMode.Reverse),
        label = "blink"
    )

    // Wiggle animation in edit mode
    val infiniteTransition = rememberInfiniteTransition(label = "wiggle")
    val wiggle by infiniteTransition.animateFloat(
        initialValue = -3.5f, targetValue = 3.5f,
        animationSpec = infiniteRepeatable(tween(120, easing = LinearEasing), RepeatMode.Reverse),
        label = "wiggle"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .systemBarsPadding()
    ) {
        val gap = theme.buttonGap
        val cols = preset.cols
        val btnSize = (maxWidth - gap * (cols + 1)) / cols
        val btnSizePx = with(androidx.compose.ui.platform.LocalDensity.current) { btnSize.toPx() }
        val gapPx    = with(androidx.compose.ui.platform.LocalDensity.current) { gap.toPx() }
        val rows = grid.size
        val cellH = minOf(btnSize, (maxHeight * 0.58f - gap * (rows - 1)) / rows)
        val cellHPx = with(androidx.compose.ui.platform.LocalDensity.current) { cellH.toPx() }

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top bar ──────────────────────────────────────────────
            if (editMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallBtn("✕", Color(0xFF555555), Color.White) { vm.cancelEditMode() }
                    Spacer(Modifier.width(8.dp))
                    NameField(favName, { favName = it }, theme, blink, Modifier.width(110.dp))
                    Spacer(Modifier.weight(1f))
                    val saved = vm.currentFavoriteIndex() >= 0
                    TopIconBox(theme, active = saved, onClick = {
                        android.widget.Toast.makeText(context, vm.toggleFavorite(favName), android.widget.Toast.LENGTH_SHORT).show()
                    }) { c -> HeartIcon(filled = saved, color = c) }
                    Spacer(Modifier.width(28.dp))
                    SmallBtn("✓", theme.opBg, theme.opText) { vm.saveEditMode() }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Layout picker
                    TopIconBtn("⊞", theme) { vm.openLayoutPicker() }
                    Spacer(Modifier.width(8.dp))
                    // Edit mode
                    TopIconBtn("✎", theme) { vm.enterEditMode() }
                    Spacer(Modifier.width(8.dp))
                    val isFav = vm.currentFavoriteIndex() >= 0
                    TopIconBox(theme, active = isFav, onClick = { vm.openFavorites() }) { c ->
                        HeartIcon(filled = isFav, color = c)
                    }
                    Spacer(Modifier.weight(1f))
                    PageDots(vm.pages.size, vm.page, theme)
                    Spacer(Modifier.weight(1f))
                    TopIconBtn("fx", theme, active = vm.advanced) { vm.toggleAdvanced() }
                    Spacer(Modifier.width(8.dp))
                    TopIconBtn("⟲", theme) { vm.openHistory() }
                }
            }

            // ── Display ──────────────────────────────────────────────
            if (!editMode) {
                val pagerState = androidx.compose.foundation.pager.rememberPagerState(initialPage = vm.page) { vm.pages.size }
                LaunchedEffect(pagerState.currentPage) { vm.selectPage(pagerState.currentPage) }
                androidx.compose.foundation.pager.HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) { i ->
                    val ps = vm.pages.getOrNull(i) ?: com.mirlan.calculator.CalcState()
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                        Text(
                            text = ps.topLine,
                            color = theme.expressionText,
                            fontSize = 20.sp,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        )
                        var menuOpen by remember { mutableStateOf(false) }
                        var layout by remember { mutableStateOf<androidx.compose.ui.text.TextLayoutResult?>(null) }
                        Box(Modifier.fillMaxWidth()) {
                        Text(
                            text = ps.display,
                            color = theme.displayText,
                            fontSize = when {
                                ps.display.length > 10 -> 40.sp
                                ps.display.length > 7  -> 56.sp
                                else                   -> 72.sp
                            },
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { layout = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 4.dp)
                                .pointerInput(ps.display) {
                                    detectTapGestures(
                                        onTap = { o ->
                                            layout?.let { vm.setCursor(it.getOffsetForPosition(o)) }
                                        },
                                        onLongPress = { menuOpen = true }
                                    )
                                }
                                .drawWithContent {
                                    drawContent()
                                    val l = layout
                                    if (l != null && !ps.waitingForOperand && !ps.justEvaluated &&
                                        ps.display.toDoubleOrNull() != null
                                    ) {
                                        val r = l.getCursorRect((ps.display.length - ps.cursorFromEnd).coerceIn(0, ps.display.length))
                                        drawRect(
                                            color = theme.displayText.copy(alpha = blink),
                                            topLeft = androidx.compose.ui.geometry.Offset(r.left, r.top),
                                            size = androidx.compose.ui.geometry.Size(2.dp.toPx(), r.height)
                                        )
                                    }
                                }
                        )
                            Box(Modifier.align(Alignment.BottomEnd).padding(end = 24.dp)) {
                            androidx.compose.material3.DropdownMenu(
                                expanded = menuOpen,
                                onDismissRequest = { menuOpen = false },
                                containerColor = theme.fnBg
                            ) {
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("Copy", color = theme.displayText) },
                                    onClick = { copyText(context, ps.display); menuOpen = false }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("Paste", color = theme.displayText) },
                                    onClick = { vm.pasteNumber(readClipboard(context)); menuOpen = false }
                                )
                            }
                            }
                        }
                    }
                }
            } else {
                RemovedTray(vm, theme)
                Spacer(Modifier.weight(1f))
            }

            if (vm.advanced && !editMode) FunctionStrip(vm, theme)
            Spacer(Modifier.height(8.dp))

            // ── Button grid ──────────────────────────────────────────
            val density = androidx.compose.ui.platform.LocalDensity.current
            val cellStep = btnSizePx + gapPx
            val cellStepY = cellHPx + gapPx
            val gridNow by rememberUpdatedState(grid)
            val rowStarts = grid.map { row ->
                var acc = 0
                row.map { c -> val s = acc; acc += c.colSpan; s }
            }
            val totalH = with(density) { (grid.size * cellStepY - gapPx).toDp() }
            fun slotAt(fx: Float, fy: Float): Pair<Int, Int> {
                val g = gridNow
                val r = Math.floor((fy / cellStepY).toDouble()).toInt().coerceIn(0, g.size - 1)
                val v = Math.floor((fx / cellStep).toDouble()).toInt().coerceIn(0, cols - 1)
                var acc = 0
                var col = 0
                g[r].forEachIndexed { i, c -> if (v >= acc) col = i; acc += c.colSpan }
                return r to col
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = gap, end = gap, bottom = gap + 16.dp)
                    .height(totalH)
                    .then(
                        if (!editMode && vm.favorites.isNotEmpty()) Modifier.pointerInput(Unit) {
                            var total = 0f
                            detectHorizontalDragGestures(
                                onDragStart = { total = 0f },
                                onHorizontalDrag = { change, dx -> change.consume(); total += dx },
                                onDragEnd = {
                                    if (Math.abs(total) > 72.dp.toPx()) {
                                        vm.swipeFavorite(next = total < 0)?.let {
                                            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        } else Modifier
                    )
                    .then(
                        if (editMode) Modifier.pointerInput(cellStep, cellStepY) {
                            detectTapGestures(
                                onTap = { o ->
                                    val (r, c) = slotAt(o.x, o.y)
                                    if (gridNow[r][c].key == CalcKey.Empty) pickerSlot = r to c
                                    else vm.removeAt(r, c)
                                },
                                onLongPress = { o ->
                                    val (r, c) = slotAt(o.x, o.y)
                                    val k = gridNow[r][c].key
                                    if (k == CalcKey.N0) vm.cycleSize(k)
                                }
                            )
                        } else Modifier
                    )
                    .then(
                        if (editMode) Modifier.pointerInput(cellStep, cellStepY) {
                            detectDragGestures(
                                onDragStart = { o ->
                                    val (r, c) = slotAt(o.x, o.y)
                                    val g = gridNow
                                    if (g[r][c].key != CalcKey.Empty) {
                                        val startVis = g[r].take(c).sumOf { it.colSpan }
                                        dragState = DragState(
                                            key = g[r][c].key, fx = o.x, fy = o.y,
                                            grabX = o.x - startVis * cellStep, grabY = o.y - r * cellStepY
                                        )
                                    }
                                },
                                onDrag = { change, amount ->
                                    change.consume()
                                    dragState?.let { cur ->
                                        val fx = cur.fx + amount.x
                                        val fy = cur.fy + amount.y
                                        val g = gridNow
                                        val r0 = g.indexOfFirst { row -> row.any { it.key == cur.key } }
                                        if (r0 >= 0) {
                                            val c0 = g[r0].indexOfFirst { it.key == cur.key }
                                            val v0 = g[r0].take(c0).sumOf { it.colSpan }
                                            val span = g[r0][c0].colSpan
                                            val fr = Math.floor((fy / cellStepY).toDouble()).toInt().coerceIn(0, g.size - 1)
                                            val fv = Math.floor((fx / cellStep).toDouble()).toInt().coerceIn(0, cols - 1)
                                            val dr = fr - r0
                                            val dv = when {
                                                fv < v0 -> fv - v0
                                                fv >= v0 + span -> fv - (v0 + span - 1)
                                                else -> 0
                                            }
                                            if (dr != 0 || dv != 0) {
                                                if (Math.abs(dr) >= Math.abs(dv)) vm.moveInColumn(cur.key, fr)
                                                else {
                                                    var acc = 0
                                                    var col = 0
                                                    g[r0].forEachIndexed { i, cell -> if (fv >= acc) col = i; acc += cell.colSpan }
                                                    vm.moveInRow(cur.key, col)
                                                }
                                            }
                                        }
                                        dragState = cur.copy(fx = fx, fy = fy)
                                    }
                                },
                                onDragEnd = { dragState = null },
                                onDragCancel = { dragState = null }
                            )
                        } else Modifier
                    )
            ) {
                grid.forEachIndexed { rowIdx, row ->
                    row.forEachIndexed { colIdx, cell ->
                        key(if (cell.key == CalcKey.Empty) "e$rowIdx-$colIdx" else cell.key) {
                            val w = btnSize * cell.colSpan + gap * (cell.colSpan - 1)
                            val h = cellH * cell.rowSpan + gap * (cell.rowSpan - 1)
                            val label = if (cell.key == CalcKey.Clear) vm.clearLabel else cell.key.defaultLabel
                            val slot = Offset(rowStarts[rowIdx][colIdx] * cellStep, rowIdx * cellStepY)
                            val anim by animateOffsetAsState(
                                slot, spring(dampingRatio = 0.9f, stiffness = 380f), label = "slot"
                            )
                            val ds = dragState
                            val isDragging = ds?.key == cell.key
                            val pos = if (isDragging && ds != null) Offset(ds.fx - ds.grabX, ds.fy - ds.grabY) else anim

                            Box(
                                modifier = Modifier
                                    .offset { androidx.compose.ui.unit.IntOffset(pos.x.roundToInt(), pos.y.roundToInt()) }
                                    .width(w)
                                    .height(h)
                                    .zIndex(if (isDragging) 10f else 0f)
                                    .graphicsLayer {
                                        if (isDragging) {
                                            scaleX = 1.12f; scaleY = 1.12f
                                            shadowElevation = 24f
                                            clip = false
                                        } else if (editMode) {
                                            rotationZ = if ((rowIdx + colIdx) % 2 == 0) wiggle else -wiggle
                                        }
                                    }
                            ) {
                                if (cell.key == CalcKey.Empty) {
                                    if (editMode) Box(
                                        Modifier.fillMaxSize()
                                            .border(1.5.dp, theme.fnBg, shapeOption.toComposeShape())
                                    )
                                } else CalcButton(
                                    label = label,
                                    cell = cell,
                                    theme = theme,
                                    shape = shapeOption.toComposeShape(),
                                    isDimmed = false,
                                    isTarget = false,
                                    editMode = editMode,
                                    activeOp = state.operator,
                                    waitingForOperand = state.waitingForOperand,
                                    onClick = { if (!editMode) vm.onKey(cell.key) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun readClipboard(context: android.content.Context): String {
    val cm = context.getSystemService(android.content.ClipboardManager::class.java)
    return cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(context)?.toString() ?: ""
}

internal fun copyText(context: android.content.Context, text: String) {
    val cm = context.getSystemService(android.content.ClipboardManager::class.java)
    cm.setPrimaryClip(android.content.ClipData.newPlainText("result", text))
    android.widget.Toast.makeText(context, "Copied", android.widget.Toast.LENGTH_SHORT).show()
}

private val SCIENTIFIC_KEYS = listOf(
    CalcKey.Sqrt, CalcKey.Square, CalcKey.Power, CalcKey.Inverse,
    CalcKey.Sin, CalcKey.Cos, CalcKey.Tan, CalcKey.Ln, CalcKey.Log,
    CalcKey.Pi, CalcKey.Euler
)

@Composable
private fun FunctionStrip(vm: CalculatorViewModel, theme: CalcTheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SCIENTIFIC_KEYS.forEach { k ->
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .widthIn(min = 56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.fnBg)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = theme.fnText.copy(alpha = 0.3f))
                    ) { vm.onKey(k) }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(k.defaultLabel, color = theme.fnText, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun PageDots(count: Int, current: Int, theme: CalcTheme) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { i ->
            Box(
                Modifier
                    .size(if (i == current) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (i == current) theme.displayText else theme.expressionText)
            )
        }
    }
}

@Composable
private fun RemovedTray(vm: CalculatorViewModel, theme: CalcTheme) {
    val removed = vm.removedKeys
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallBtn("↺", theme.fnBg, theme.fnText) { vm.resetEdit() }
        removed.forEach { k -> SmallBtn(k.defaultLabel, theme.numBg, theme.numText) { vm.placeKey(k) } }
    }
}

@Composable
internal fun TopIconBtn(label: String, theme: CalcTheme, active: Boolean = false, onClick: () -> Unit) {
    TopIconBox(theme, active, onClick) { c -> Text(label, color = c, fontSize = 18.sp) }
}

@Composable
private fun NameField(value: String, onChange: (String) -> Unit, theme: CalcTheme, blink: Float, modifier: Modifier) {
    var focused by remember { mutableStateOf(false) }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(theme.fnBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusRequester.requestFocus() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = { onChange(it.replace(Regex("[\\t\\r\\n]"), " ").take(20)) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = theme.fnText, fontSize = 15.sp),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.fnText),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focused = it.isFocused },
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!focused) Box(
                                Modifier.width(2.dp).height(18.dp).background(theme.fnText.copy(alpha = blink))
                            )
                            Text(
                                "Name",
                                color = theme.fnText.copy(alpha = 0.45f),
                                fontSize = 15.sp,
                                modifier = Modifier.padding(start = if (focused) 0.dp else 4.dp)
                            )
                        }
                    }
                    inner()
                }
            }
        )
    }
}

@Composable
internal fun TopIconBox(theme: CalcTheme, active: Boolean = false, onClick: () -> Unit, content: @Composable (Color) -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) theme.opBg else theme.fnBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = theme.fnText.copy(alpha = 0.3f))
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content(if (active) theme.opText else theme.fnText)
    }
}

@Composable
internal fun SmallBtn(label: String, bg: Color, textColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = textColor.copy(0.3f))
            ) { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CalcButton(
    label: String,
    cell: KeyCell,
    theme: CalcTheme,
    shape: Shape,
    isDimmed: Boolean,
    isTarget: Boolean,
    editMode: Boolean,
    activeOp: String?,
    waitingForOperand: Boolean,
    onClick: () -> Unit
) {
    val isOp = cell.key in listOf(CalcKey.Plus, CalcKey.Minus, CalcKey.Multiply, CalcKey.Divide)
    val isEq = cell.key == CalcKey.Equals
    val isFn = cell.key.isFn
    val isActiveOp = isOp && waitingForOperand && cell.key.defaultLabel == activeOp

    val baseBg = when {
        isActiveOp -> theme.activeOpBg
        isOp       -> theme.opBg
        isEq       -> theme.eqBg
        isFn       -> theme.fnBg
        else       -> theme.numBg
    }
    val bg = when {
        isTarget -> baseBg.copy(alpha = 0.5f)
        isDimmed -> baseBg.copy(alpha = 0.4f)
        else     -> baseBg
    }
    val textColor = when {
        isActiveOp -> theme.activeOpText
        isOp       -> theme.opText
        isEq       -> theme.eqText
        isFn       -> theme.fnText
        else       -> theme.numText
    }

    val borderMod = if (isTarget)
        Modifier.shadow(0.dp, shape).then(
            Modifier.background(theme.opBg.copy(alpha = 0.25f), shape)
        )
    else Modifier

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .background(bg)
            .then(borderMod)
            .then(
                if (!editMode) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = textColor.copy(alpha = 0.3f))
                ) { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isDimmed || isTarget) textColor.copy(alpha = 0.5f) else textColor,
            fontSize = if (isOp || isEq) 28.sp else 22.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
