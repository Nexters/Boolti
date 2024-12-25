package com.nexters.boolti.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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

//    secondary = PurpleGrey80,
//    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun BooltiTheme(
    darkTheme: Boolean = true, // FIXME light theme이 추가된다면 이 부분을 isSystemInDarkTheme()로 수정하세요.
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
