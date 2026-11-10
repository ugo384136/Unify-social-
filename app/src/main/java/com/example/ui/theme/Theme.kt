package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = UnifyCyan,
  onPrimary = Color(0xFF002022),
  primaryContainer = Color(0xFF004F55),
  onPrimaryContainer = Color(0xFF82F4FF),
  secondary = UnifyViolet,
  onSecondary = Color(0xFF280057),
  secondaryContainer = Color(0xFF4C1D95),
  onSecondaryContainer = Color(0xFFE9D5FF),
  tertiary = UnifyPink,
  onTertiary = Color(0xFF4A0028),
  background = UnifyBgDark,
  onBackground = UnifyTextPrimaryDark,
  surface = UnifySurfaceDark,
  onSurface = UnifyTextPrimaryDark,
  surfaceVariant = UnifySurfaceVariantDark,
  onSurfaceVariant = UnifyTextSecondaryDark,
  outline = UnifySurfaceBorderDark,
  error = UnifyRose,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF0284C7),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFBAE6FD),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = UnifyVioletDark,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFDDD6FE),
  onSecondaryContainer = Color(0xFF5B21B6),
  tertiary = UnifyPink,
  onTertiary = Color.White,
  background = UnifyBgLight,
  onBackground = UnifyTextPrimaryLight,
  surface = UnifySurfaceLight,
  onSurface = UnifyTextPrimaryLight,
  surfaceVariant = UnifySurfaceVariantLight,
  onSurfaceVariant = UnifyTextSecondaryLight,
  outline = UnifySurfaceBorderLight,
  error = UnifyRose,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek UNIFY dark theme
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

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
