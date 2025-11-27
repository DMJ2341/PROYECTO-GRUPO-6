package com.example.cyberlearnapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// =========================================================================
// ESQUEMA DE COLORES OSCURO - ALTO CONTRASTE
// Optimizado para legibilidad en pantallas oscuras
// =========================================================================
private val DarkColorScheme = darkColorScheme(
    // --- FONDOS ---
    background = BackgroundMain,           // Fondo principal de la app
    surface = SurfaceCard,                 // Superficie de cards y componentes
    surfaceVariant = SurfaceElevated,      // Contenedores secundarios

    // --- COLORES PRIMARIOS ---
    primary = PrimaryCyan,                 // Botones principales e interacciones
    onPrimary = BackgroundMain,            // Texto sobre primario (oscuro para contraste)
    primaryContainer = SurfaceActive,      // Contenedor de elementos primarios
    onPrimaryContainer = TextPrimary,      // Texto sobre contenedor primario

    // --- COLORES SECUNDARIOS ---
    secondary = PrimaryPurple,             // Elementos secundarios
    onSecondary = TextPrimary,             // Texto sobre secundario
    secondaryContainer = PrimaryPurple.copy(alpha = 0.2f),
    onSecondaryContainer = TextPrimary,

    // --- COLORES TERCIARIOS ---
    tertiary = AccentGold,                 // Acentos especiales
    onTertiary = BackgroundMain,           // Texto sobre terciario
    tertiaryContainer = AccentGold.copy(alpha = 0.2f),
    onTertiaryContainer = TextPrimary,

    // --- TEXTO SOBRE FONDOS ---
    onBackground = TextPrimary,            // Texto principal sobre BackgroundMain
    onSurface = TextPrimary,               // Texto principal sobre SurfaceCard
    onSurfaceVariant = TextSecondary,      // Texto secundario sobre superficies

    // --- ESTADOS ---
    error = ErrorRed,                      // Color de error
    onError = TextPrimary,                 // Texto sobre error
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = TextPrimary,

    // --- BORDES Y DIVISORES ---
    outline = SurfaceActive,               // Bordes de componentes
    outlineVariant = SurfaceElevated,      // Bordes sutiles

    // --- OTROS ---
    scrim = Color.Black.copy(alpha = 0.5f), // Overlay para modals/dialogs
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundMain,
    inversePrimary = PrimaryCyan,
    surfaceTint = PrimaryCyan,
)

// =========================================================================
// ESQUEMA DE COLORES CLARO (Opcional - No usado por defecto)
// =========================================================================
private val LightColorScheme = lightColorScheme(
    primary = PrimaryCyan,
    secondary = PrimaryPurple,
    tertiary = AccentGold,
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

// =========================================================================
// TEMA PRINCIPAL DE LA APLICACIÓN
// =========================================================================
@Composable
fun CyberLearnAppTheme(
    darkTheme: Boolean = true, // Forzamos tema oscuro por defecto
    content: @Composable () -> Unit
) {
    // Siempre usamos el esquema oscuro para CyberLearnApp
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Color de la barra de estado igual al fondo principal
            window.statusBarColor = colorScheme.background.toArgb()
            // Iconos de la barra de estado en color claro (para fondo oscuro)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}