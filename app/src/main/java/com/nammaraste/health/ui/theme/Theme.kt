package com.nammaraste.health.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary          = Brand300,
    onPrimary        = Brand900,
    primaryContainer = Brand700,
    onPrimaryContainer = Brand100,
    secondary        = Accent500,
    onSecondary      = Brand900,
    secondaryContainer = Color(0xFF3A2A00),
    onSecondaryContainer = Accent300,
    tertiary         = HealthGood,
    onTertiary       = Brand900,
    background       = DarkBackground,
    onBackground     = TextPrimary,
    surface          = DarkSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = DarkCard,
    onSurfaceVariant = TextSecondary,
    outline          = DarkDivider,
    outlineVariant   = DarkDivider,
    error            = HealthCritical,
    onError          = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary          = Brand700,
    onPrimary        = LightSurface,
    primaryContainer = Brand100,
    onPrimaryContainer = Brand900,
    secondary        = Accent500,
    onSecondary      = Brand900,
    background       = LightBackground,
    onBackground     = Brand900,
    surface          = LightSurface,
    onSurface        = Brand900,
    surfaceVariant   = Color(0xFFE8EFF6),
    onSurfaceVariant = Brand600,
    outline          = Color(0xFFB0C0D4),
    error            = HealthCritical,
    onError          = TextPrimary
)

@Composable
fun NammaRasteTheme(
    darkTheme: Boolean = true, // Default to dark for premium feel
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}


