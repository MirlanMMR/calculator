package com.mirlan.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mirlan.calculator.data.CalcTheme

@Composable
fun UpdateCard(theme: CalcTheme, onUpdate: () -> Unit, onLater: () -> Unit) {
    Dialog(onDismissRequest = onLater) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(theme.fnBg)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Update available", color = theme.displayText, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Text("A new version of Calculator is ready to install.", color = theme.expressionText, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallBtn("Later", theme.numBg, theme.numText, onLater)
                Spacer(Modifier.width(12.dp))
                SmallBtn("Update", theme.opBg, theme.opText, onUpdate)
            }
        }
    }
}
