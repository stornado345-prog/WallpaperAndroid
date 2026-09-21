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

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldAmber,
    onPrimary = ObsidianBlack,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = TitaniumSilver,
    onSecondary = ObsidianBlack,
    tertiary = CyberCyan,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = PlatinumWhite,
    surface = CarbonDark,
    onSurface = PlatinumWhite,
    surfaceVariant = CarbonCard,
    onSurfaceVariant = TitaniumSilver,
    outline = CarbonBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GoldAmberDark,
    onPrimary = Color.White,
    primaryContainer = GoldAmberLight,
    onPrimaryContainer = ObsidianBlack,
    secondary = CarbonCard,
    onSecondary = Color.White,
    tertiary = CyberCyan,
    onTertiary = ObsidianBlack,
    background = Color(0xFFF6F8FC),
    onBackground = ObsidianBlack,
    surface = Color.White,
    onSurface = ObsidianBlack,
    surfaceVariant = Color(0xFFECEFF5),
    onSurfaceVariant = Color(0xFF434A59),
    outline = Color(0xFFD2D6E0)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For luxury automotive aesthetic, default to sleek dark theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> DarkColorScheme // Keep immersive dark automotive aesthetic
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

