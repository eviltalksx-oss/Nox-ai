package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NoxDarkColorScheme = darkColorScheme(
    primary = NoxBloodRed,
    onPrimary = NoxTextWhite,
    primaryContainer = NoxDarkRed,
    onPrimaryContainer = NoxTextWhite,
    secondary = NoxCrimson,
    onSecondary = NoxTextWhite,
    secondaryContainer = NoxCardSurface,
    onSecondaryContainer = NoxTextWhite,
    tertiary = NoxGlowRed,
    background = NoxBlack,
    onBackground = NoxTextWhite,
    surface = NoxCharcoal,
    onSurface = NoxTextWhite,
    surfaceVariant = NoxCardSurface,
    onSurfaceVariant = NoxTextMuted,
    outline = NoxCardBorder
)

@Composable
fun NoxTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NoxDarkColorScheme,
        typography = Typography,
        content = content
    )
}
