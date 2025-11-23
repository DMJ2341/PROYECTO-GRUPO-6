package com.example.cyberlearnapp.ui.screens.lessons.shared

/**
 * Data classes para componentes de lecciones
 * Separadas de los @Composable para evitar conflictos
 */

/**
 * Tarjeta de impacto para Story Hook
 */
data class ImpactCardData(
    val icon: String,
    val value: String,
    val label: String,
    val detail: String? = null
)

/**
 * Estadística para pantallas de resumen
 */
data class StatisticItemData(
    val icon: String,
    val value: String,
    val label: String
)

/**
 * Punto clave para infografías
 */
data class KeyPointData(
    val icon: String,
    val text: String
)

/**
 * Paso de proceso para infografías paso a paso
 */
data class ProcessStepData(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: String
)

/**
 * Evento de línea de tiempo
 */
data class TimelineEventData(
    val time: String,
    val description: String,
    val isHighlight: Boolean = false
)

/**
 * Información de insignia
 */
data class BadgeInfoData(
    val icon: String,
    val name: String
)