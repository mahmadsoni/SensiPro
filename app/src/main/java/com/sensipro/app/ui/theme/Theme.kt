package com.sensipro.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SensiProColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = BackgroundDeep,
    secondary = NeonViolet,
    onSecondary = TextPrimary,
    tertiary = NeonMagenta,
    background = BackgroundDeep,
    onBackground = TextPrimary,
    surface = SurfaceGlass,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceGlassElevated,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    error = DangerRed
)

@Composable
fun SensiProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SensiProColorScheme,
        typography = SensiProTypography,
        content = content
    )
}
