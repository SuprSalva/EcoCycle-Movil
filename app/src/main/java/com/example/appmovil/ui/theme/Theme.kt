package com.example.appmovil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF20B034) // Exact green from logofinalpng.png
val OnPrimaryColor = Color(0xFFFFFFFF)
val PrimaryContainerColor = Color(0xFF86EFAC) // Lighter green
val OnPrimaryContainerColor = Color(0xFF064E3B)
val SecondaryColor = Color(0xFF4ADE80)
val SecondaryContainerColor = Color(0xFFDCFCE7)
val OnSecondaryContainerColor = Color(0xFF14532D)
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
