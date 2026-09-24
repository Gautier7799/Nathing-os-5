package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.LauncherThemeMode

// Theme Nuit (Dark - Image 2)
private val NothingDarkColorScheme = darkColorScheme(
  primary = NothingRed,
  onPrimary = NothingWhite,
  primaryContainer = NothingElevated,
  onPrimaryContainer = NothingWhite,
  secondary = NothingWhite,
  onSecondary = NothingBlack,
  secondaryContainer = NothingDarkSurface,
  onSecondaryContainer = NothingWhite,
  tertiary = NothingRedLight,
  background = NothingBlack,
  onBackground = NothingWhite,
  surface = NothingMatteBlack,
  onSurface = NothingWhite,
  surfaceVariant = NothingDarkSurface,
  onSurfaceVariant = NothingDimWhite,
  outline = NothingBorder,
  outlineVariant = NothingUnlitDot
)

// Theme Jour (Light - Image 3)
private val NothingLightColorScheme = lightColorScheme(
  primary = NothingRed,
  onPrimary = NothingWhite,
  primaryContainer = NothingLightElevated,
  onPrimaryContainer = NothingLightTextPrimary,
  secondary = NothingLightTextPrimary,
  onSecondary = NothingLightSurface,
  secondaryContainer = NothingLightSurface,
  onSecondaryContainer = NothingLightTextPrimary,
  tertiary = NothingGreenAccent,
  background = NothingLightBackground,
  onBackground = NothingLightTextPrimary,
  surface = NothingLightSurface,
  onSurface = NothingLightTextPrimary,
  surfaceVariant = NothingLightElevated,
  onSurfaceVariant = NothingLightTextSecondary,
  outline = NothingLightBorder,
  outlineVariant = NothingLightUnlitDot
)

data class LauncherThemeColors(
  val isDark: Boolean = true,
  val background: Color = NothingBlack,
  val surface: Color = NothingDarkSurface,
  val elevated: Color = NothingElevated,
  val border: Color = NothingBorder,
  val textPrimary: Color = NothingWhite,
  val textSecondary: Color = NothingGrey,
  val unlitDot: Color = NothingUnlitDot,
  val dockBg: Color = NothingDarkSurface,
  val dockButtonBg: Color = NothingElevated,
  val dockIconTint: Color = NothingWhite,
  val searchPillBg: Color = NothingDarkSurface
)

val LocalLauncherTheme = staticCompositionLocalOf {
  LauncherThemeColors()
}

@Composable
fun MyApplicationTheme(
  themeMode: LauncherThemeMode = LauncherThemeMode.SYSTEM,
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val resolvedDark = when (themeMode) {
    LauncherThemeMode.DARK -> true
    LauncherThemeMode.LIGHT -> false
    LauncherThemeMode.SYSTEM -> darkTheme
  }
  val colorScheme = if (resolvedDark) NothingDarkColorScheme else NothingLightColorScheme
  val themeColors = if (resolvedDark) {
    LauncherThemeColors(
      isDark = true,
      background = NothingBlack,
      surface = NothingDarkSurface,
      elevated = NothingElevated,
      border = NothingBorder,
      textPrimary = NothingWhite,
      textSecondary = NothingGrey,
      unlitDot = NothingUnlitDot,
      dockBg = NothingDarkSurface.copy(alpha = 0.88f),
      dockButtonBg = NothingElevated,
      dockIconTint = NothingWhite,
      searchPillBg = NothingDarkSurface
    )
  } else {
    LauncherThemeColors(
      isDark = false,
      background = NothingLightBackground,
      surface = NothingLightSurface,
      elevated = NothingLightElevated,
      border = NothingLightBorder,
      textPrimary = NothingLightTextPrimary,
      textSecondary = NothingLightTextSecondary,
      unlitDot = NothingLightUnlitDot,
      dockBg = NothingLightSurface.copy(alpha = 0.92f),
      dockButtonBg = Color.White,
      dockIconTint = Color(0xFF1A1A1A),
      searchPillBg = NothingLightSurface
    )
  }

  CompositionLocalProvider(LocalLauncherTheme provides themeColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
