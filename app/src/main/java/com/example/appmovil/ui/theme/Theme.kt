package com.example.appmovil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF0D631B)
val OnPrimaryColor = Color(0xFFFFFFFF)
val PrimaryContainerColor = Color(0xFF2E7D32)
val OnPrimaryContainerColor = Color(0xFFCBFFC2)
val SecondaryColor = Color(0xFF3C6842)
val SecondaryContainerColor = Color(0xFFBDEFBE)
val OnSecondaryContainerColor = Color(0xFF426E47)
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
        content = content
    )
}
