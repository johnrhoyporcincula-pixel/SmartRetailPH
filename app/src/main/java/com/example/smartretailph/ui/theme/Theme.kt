package com.example.smartretailph.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color.Black,
    secondary = BlueSecondaryDark,
    tertiary = BlueTertiaryDark,
    background = Color(0xFF0A1A2B),
    surface = Color(0xFF071423),
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimaryLight,
    onPrimary = Color.White,
    secondary = BlueSecondaryLight,
    tertiary = BlueTertiaryLight,
    background = Color(0xFFF2F9FF),
    surface = Color.White,
    onBackground = Color(0xFF0B2540),
    onSurface = Color(0xFF0B2540)
)

@Composable
fun SmartRetailPHTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Force our own blue palette for consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}