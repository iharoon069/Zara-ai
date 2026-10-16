package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.AiCoreState
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraDeepVoid
import com.example.ui.theme.ZaraElectricBlue
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGold
import com.example.ui.theme.ZaraNeonGreen
import com.example.ui.theme.ZaraNeonPink
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AiCoreVisualizer(
    state: AiCoreState,
    audioAmplitude: Float,
    modifier: Modifier = Modifier,
    size: Dp = 230.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_core_transition")

    // Slow rotation for outer ring
    val outerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_rotation"
    )

    // Reverse faster rotation for inner cyber ring
    val innerAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_rotation"
    )

    // Pulsing core scale
    val pulseSpeed = when (state) {
        AiCoreState.LISTENING -> 800
        AiCoreState.THINKING -> 500
        AiCoreState.SPEAKING -> 700
        AiCoreState.EXECUTING -> 450
        AiCoreState.IDLE -> 2400
    }

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = pulseSpeed, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Dynamic colors based on state
    val (primaryGlow, accentGlow) = when (state) {
        AiCoreState.IDLE -> Pair(ZaraNeonCyan, ZaraCyberViolet)
        AiCoreState.LISTENING -> Pair(ZaraNeonGreen, ZaraNeonCyan)
        AiCoreState.THINKING -> Pair(ZaraNeonGold, ZaraCyberViolet)
        AiCoreState.SPEAKING -> Pair(ZaraNeonPink, ZaraCyberViolet)
        AiCoreState.EXECUTING -> Pair(ZaraElectricBlue, ZaraNeonCyan)
    }

    // Effective dynamic pulse with audio amplitude
    val effectiveScale = (pulseScale + (audioAmplitude * 0.28f)).coerceIn(0.85f, 1.4f)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2f

            // 1. Outer ambient glow halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryGlow.copy(alpha = 0.35f),
                        accentGlow.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 0.98f
                ),
                radius = baseRadius * 0.98f,
                center = center
            )

            // 2. Outer segmented tech ring
            rotate(degrees = outerAngle, pivot = center) {
                drawCircle(
                    color = primaryGlow.copy(alpha = 0.5f),
                    radius = baseRadius * 0.88f,
                    center = center,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 18f, 6f, 18f), 0f)
                    )
                )

                // 4 satellite energy ticks
                for (i in 0 until 4) {
                    val angleRad = Math.toRadians((i * 90.0)).toFloat()
                    val tickX = center.x + (baseRadius * 0.88f) * cos(angleRad)
                    val tickY = center.y + (baseRadius * 0.88f) * sin(angleRad)
                    drawCircle(
                        color = primaryGlow,
                        radius = 3.5.dp.toPx(),
                        center = Offset(tickX, tickY)
                    )
                }
            }

            // 3. Middle orbital arc
            rotate(degrees = innerAngle, pivot = center) {
                drawCircle(
                    color = accentGlow.copy(alpha = 0.65f),
                    radius = baseRadius * 0.72f,
                    center = center,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(45f, 25f), 0f)
                    )
                )
            }

            // 4. Reactive audio energy wave ring
            val reactiveRadius = (baseRadius * 0.55f * effectiveScale).coerceAtMost(baseRadius * 0.82f)
            drawCircle(
                color = primaryGlow.copy(alpha = 0.4f),
                radius = reactiveRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // 5. Central glowing cyber core sphere
            val coreRadius = baseRadius * 0.42f * (if (state == AiCoreState.SPEAKING || state == AiCoreState.LISTENING) effectiveScale else 1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryGlow,
                        accentGlow,
                        ZaraDeepVoid.copy(alpha = 0.9f)
                    ),
                    center = center,
                    radius = coreRadius
                ),
                radius = coreRadius,
                center = center
            )

            // 6. Central iris node
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = 5.dp.toPx(),
                center = center
            )
        }
    }
}
