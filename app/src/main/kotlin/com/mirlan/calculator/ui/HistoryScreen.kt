package com.mirlan.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirlan.calculator.CalculatorViewModel

@Composable
fun HistoryScreen(vm: CalculatorViewModel) {
    val theme = vm.selectedPreset.theme
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopIconBtn("←", theme) { vm.back() }
            Spacer(Modifier.weight(1f))
            if (vm.history.isNotEmpty()) TopIconBtn("✕", theme) { vm.clearHistory() }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        ) {
            items(vm.history.asReversed()) { e ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(e) {
                            detectTapGestures(
                                onTap = { vm.useHistory(e) },
                                onLongPress = { copyText(context, "${e.expr} = ${e.result}") }
                            )
                        }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "${e.expr} =",
                        color = theme.expressionText,
                        fontSize = when {
                            e.expr.length > 30 -> 11.sp
                            e.expr.length > 22 -> 13.sp
                            e.expr.length > 16 -> 15.sp
                            else -> 18.sp
                        },
                        maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                    Text(
                        e.result,
                        color = theme.displayText,
                        fontSize = when {
                            e.result.length > 12 -> 24.sp
                            e.result.length > 9 -> 30.sp
                            else -> 38.sp
                        },
                        maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
