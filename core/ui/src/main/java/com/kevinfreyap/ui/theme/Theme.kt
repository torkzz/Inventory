package com.kevinfreyap.ui.theme

import android.app.Activity
import androidx.compose.ui.graphics.Color
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    background = CyberBg,

    primary = NeonCyan,
    onPrimary = CyberBg,

    secondary = NeonPurple,
    onSecondary = White,

    surface = CyberCard,
    surfaceVariant = CyberSurfaceVariant,
    onSurface = TextPrimary,
    onSurfaceVariant = TextMuted,

    secondaryContainer = NeonCyan.copy(alpha = 0.15f),
    onSecondaryContainer = NeonCyan,

    surfaceContainer = CyberSurface,

    error = NeonRed,
    onError = White
)

private val LightColorScheme = darkColorScheme(
    background = CyberBg,

    primary = NeonCyan,
    onPrimary = CyberBg,

    secondary = NeonPurple,
    onSecondary = White,

    surface = CyberCard,
    surfaceVariant = CyberSurfaceVariant,
    onSurface = TextPrimary,
    onSurfaceVariant = TextMuted,

    secondaryContainer = NeonCyan.copy(alpha = 0.15f),
    onSecondaryContainer = NeonCyan,

    surfaceContainer = CyberSurface,

    error = NeonRed,
    onError = White
)

@Immutable
data class ExtendedColors(
    val primaryText: Color,
    val secondaryText: Color,
    val success: Color,
    val warning: Color,
    val shimmer: Color,
    val hint: Color
)

val LightExtendedColors = ExtendedColors(
    primaryText = TextPrimary,
    secondaryText = TextSecondary,
    success = NeonGreen,
    warning = NeonAmber,
    shimmer = CyberSurfaceVariant,
    hint = TextMuted
)

val DarkExtendedColors = ExtendedColors(
    primaryText = TextPrimary,
    secondaryText = TextSecondary,
    success = NeonGreen,
    warning = NeonAmber,
    shimmer = CyberSurfaceVariant,
    hint = TextMuted
)

// Fallback
val LocalExtendedColors = staticCompositionLocalOf {
    LightExtendedColors
}

@Composable
fun InventoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = CyberBg.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = CyberSurface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    CompositionLocalProvider(
        LocalExtendedColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

object Theme {
    val custom: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}