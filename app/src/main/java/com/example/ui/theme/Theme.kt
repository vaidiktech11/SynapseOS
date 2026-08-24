package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SynapseColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = SynapseBackground,
    primaryContainer = SynapseSurfaceVariant,
    onPrimaryContainer = ElectricCyanLight,
    secondary = NeonViolet,
    onSecondary = TextPrimary,
    secondaryContainer = SynapseSurfaceElevated,
    onSecondaryContainer = NeonVioletLight,
    tertiary = CyberAmber,
    onTertiary = SynapseBackground,
    tertiaryContainer = SynapseSurfaceVariant,
    onTertiaryContainer = CyberAmberLight,
    background = SynapseBackground,
    onBackground = TextPrimary,
    surface = SynapseSurface,
    onSurface = TextPrimary,
    surfaceVariant = SynapseSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SynapseBorder,
    error = ErrorCrimson,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // SynapseOS defaults to cyber dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = SynapseColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SynapseBackground.toArgb()
            window.navigationBarColor = SynapseBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
