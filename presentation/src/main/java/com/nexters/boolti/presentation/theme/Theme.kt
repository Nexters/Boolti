package com.nexters.boolti.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Orange01,
    onPrimary = Grey05,
    error = Error,
    background = Grey95,
    onBackground = Grey10,
    surface = Grey90,
    surfaceTint = Grey85,
    secondaryContainer = Grey80,
    onSecondaryContainer = Grey40,
    onSurface = Grey10,
    onSurfaceVariant = Grey15,
    outline = Grey80,
    outlineVariant = Grey10,
)

@Composable
fun BooltiTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
