package com.mirlan.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirlan.calculator.CalculatorViewModel
import com.mirlan.calculator.MAX_FAVORITES
import com.mirlan.calculator.data.Presets

@Composable
fun FavoritesScreen(vm: CalculatorViewModel) {
    val theme = vm.selectedPreset.theme
    val current = vm.currentFavoriteIndex()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopIconBtn("←", theme) { vm.back() }
            Spacer(Modifier.width(16.dp))
            Text("Favorites", color = theme.displayText, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text("${vm.favorites.size}/$MAX_FAVORITES", color = theme.expressionText, fontSize = 14.sp)
        }

        if (vm.favorites.isEmpty()) {
            Text(
                "Nothing here yet. Open the constructor, arrange the keys, name it and tap the heart.",
                color = theme.expressionText,
                fontSize = 15.sp,
                modifier = Modifier.padding(24.dp)
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(vm.favorites.size) { i ->
                    val f = vm.favorites[i]
                    val t = Presets.all.find { it.id == f.presetId }?.theme ?: theme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                            .background(theme.fnBg)
                            .clickable { vm.applyFavorite(f); vm.back() }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(t.background)
                                .border(2.dp, if (i == current) theme.opBg else theme.expressionText.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(Modifier.size(12.dp).clip(CircleShape).background(t.numBg))
                                Box(Modifier.size(12.dp).clip(CircleShape).background(t.opBg))
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            f.name,
                            color = theme.fnText,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (i == current) HeartIcon(filled = true, color = theme.opBg)
                        Spacer(Modifier.width(12.dp))
                        TopIconBtn("✕", theme) { vm.removeFavorite(f) }
                    }
                }
            }
        }
    }
}
