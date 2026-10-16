package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.ui.AiCoreState
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGreen
import com.example.ui.theme.ZaraNeonPink
import kotlin.math.sin

@Composable
fun VoiceWaveform(
    state: AiCoreState,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val barCount = 18
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f, // 2*PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val isActive = state == AiCoreState.LISTENING || state == AiCoreState.SPEAKING

    val gradientColors = when (state) {
        AiCoreState.LISTENING -> listOf(ZaraNeonGreen, ZaraNeonCyan)
        AiCoreState.SPEAKING -> listOf(ZaraNeonPink, ZaraCyberViolet)
        else -> listOf(ZaraNeonCyan.copy(alpha = 0.5f), ZaraCyberViolet.copy(alpha = 0.5f))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val offsetFactor = sin(phase + (i * 0.38f)).toFloat()
            val baseHeight = if (isActive) {
                val waveVal = (offsetFactor * 0.5f + 0.5f)
                val ampBoost = amplitude * 22f
                (6f + (waveVal * 16f) + ampBoost).coerceIn(4f, 36f)
            } else {
                3f + (sin((i.toFloat() / barCount) * 3.14f) * 4f)
            }

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(baseHeight.dp)
                    .background(
                        brush = Brush.verticalGradient(gradientColors),
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            if (i < barCount - 1) {
                Box(modifier = Modifier.width(3.5.dp))
            }
        }
    }
}
