package com.example.cyberlearnapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Definimos SOLAMENTE el esquema oscuro para forzarlo siempre
private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = PrimaryDark, // Texto sobre el color primario (ej. botones)

    secondary = AccentPurple,
    onSecondary = TextWhite,

    tertiary = NeonGreen,

    background = BackgroundDark, // TU COLOR DE FONDO (0xFF0F172A)
    onBackground = TextWhite,

    surface = CardBg,            // TU COLOR DE TARJETAS (0xFF1E293B)
    onSurface = TextWhite,

    surfaceVariant = SecondaryDark,
    onSurfaceVariant = TextGray,

    error = Danger,
    onError = TextWhite
)

@Composable
fun CyberLearnAppTheme(
    content: @Composable () -> Unit
) {
    // Aplicamos siempre el DarkColorScheme, ignorando el modo claro del sistema
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography, // Asegúrate de tener Typography.kt o usa MaterialTheme.typography
        content = content
    )
}