package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ZaraCyberColorScheme = darkColorScheme(
    primary = ZaraNeonCyan,
    onPrimary = ZaraDeepVoid,
    primaryContainer = ZaraGlassSurfaceLighter,
    onPrimaryContainer = ZaraNeonCyan,
    secondary = ZaraCyberViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF281C40),
    onSecondaryContainer = ZaraCyberViolet,
    tertiary = ZaraNeonPink,
    onTertiary = Color.White,
    background = ZaraDeepVoid,
    onBackground = ZaraTextPrimary,
    surface = ZaraMidnightNavy,
    onSurface = ZaraTextPrimary,
    surfaceVariant = ZaraGlassSurface,
    onSurfaceVariant = ZaraTextSecondary,
    outline = ZaraGlassBorder,
    error = ZaraNeonRed,
    onError = Color.White
)

@Composable
fun ZaraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZaraCyberColorScheme,
        typography = Typography,
        content = content
    )
}
