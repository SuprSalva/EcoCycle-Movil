package com.example.appmovil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF10B981) // Vibrant Emerald Green
val OnPrimaryColor = Color(0xFFFFFFFF)
val PrimaryContainerColor = Color(0xFFA7F3D0)
val OnPrimaryContainerColor = Color(0xFF064E3B)
val SecondaryColor = Color(0xFF84CC16) // Lime Green / Esmeralda accent
val SecondaryContainerColor = Color(0xFFD9F99D)
val OnSecondaryContainerColor = Color(0xFF3F6212)
val BackgroundColor = Color(0xFFF9F9F9)
val OnBackgroundColor = Color(0xFF1A1C1C)
val SurfaceColor = Color(0xFFFFFFFF) // surface-container-lowest
val OnSurfaceColor = Color(0xFF1A1C1C)
val SurfaceVariantColor = Color(0xFFE2E2E2)
val OnSurfaceVariantColor = Color(0xFF40493D)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimaryColor,
    primaryContainer = PrimaryContainerColor,
    onPrimaryContainer = OnPrimaryContainerColor,
    secondary = SecondaryColor,
    secondaryContainer = SecondaryContainerColor,
    onSecondaryContainer = OnSecondaryContainerColor,
    background = BackgroundColor,
    onBackground = OnBackgroundColor,
    surface = SurfaceColor,
    onSurface = OnSurfaceColor,
    surfaceVariant = SurfaceVariantColor,
    onSurfaceVariant = OnSurfaceVariantColor
)

@Composable
fun EcoCycleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
