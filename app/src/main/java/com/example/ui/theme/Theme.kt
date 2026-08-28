package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = OnPrimary,
  primaryContainer = CyberSurfaceVariant,
  onPrimaryContainer = PrimaryContainer,
  secondary = NeonPurple,
  onSecondary = OnPrimary,
  secondaryContainer = CyberSurfaceVariant,
  onSecondaryContainer = NeonPurple,
  tertiary = NitroGreen,
  onTertiary = CyberBackground,
  background = CyberBackground,
  onBackground = TextPrimary,
  surface = CyberSurface,
  onSurface = TextPrimary,
  surfaceVariant = CyberSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = CyberSurfaceBorder,
  error = RecoilRed,
  onError = CyberBackground
)

@Composable
fun GamepadKeymapperProTheme(
  darkTheme: Boolean = true, // Gaming theme defaults to dark mode
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  MyApplicationTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Gaming theme defaults to dark mode
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = DarkColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = CyberBackground.toArgb()
      window.navigationBarColor = CyberBackground.toArgb()
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
