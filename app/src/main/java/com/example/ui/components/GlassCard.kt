package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraGlassBorder
import com.example.ui.theme.ZaraGlassSurface
import com.example.ui.theme.ZaraNeonCyan

@Composable
fun ZaraGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = ZaraGlassBorder,
    glowAccent: Boolean = false,
    content: @Composable () -> Unit
) {
    val borderBrush = if (glowAccent) {
        Brush.linearGradient(listOf(ZaraNeonCyan.copy(alpha = 0.5f), ZaraCyberViolet.copy(alpha = 0.4f)))
    } else {
        Brush.linearGradient(listOf(borderColor, borderColor.copy(alpha = 0.2f)))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(ZaraGlassSurface)
            .border(
                border = BorderStroke(1.dp, borderBrush),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(14.dp)
    ) {
        content()
    }
}
