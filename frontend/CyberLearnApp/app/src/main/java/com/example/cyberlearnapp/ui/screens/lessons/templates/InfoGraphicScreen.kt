package com.example.cyberlearnapp.ui.screens.lessons.templates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * 📊 PLANTILLA: INFOGRAPHIC
 * Pantalla de mapas, estadísticas y visualización de datos
 *
 * Usado en:
 * - Lección 1: Mapa de Infección Global
 * - Lección 2: Tipos de Ingeniería Social
 * - Lección 3: Infografía de Ransomware
 * - Lección 4: Redes Wi-Fi disponibles
 * - Lección 5: Tríada CIA
 */
@Composable
fun InfoGraphicScreen(
    title: String,
    subtitle: String? = null,
    keyPoints: List<KeyPoint>? = null,
    statistics: List<StatisticItem>,
    conclusionText: String? = null,
    screenNumber: Int,
    totalScreens: Int,
    buttonText: String = "Siguiente",
    onNext: () -> Unit
) {
    ScreenContainer(
        title = title,
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = onNext,
        buttonText = buttonText
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subtítulo
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Puntos clave (si existen)
            if (keyPoints != null) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    keyPoints.forEach { point ->
                        KeyPointItem(
                            icon = point.icon,
                            text = point.text
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Título de estadísticas
            Text(
                text = "🎯 DATOS CLAVE:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            // Grid de estadísticas
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (statistics.size <= 3) statistics.size else 2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height((statistics.size / 2 * 150).dp.coerceAtLeast(200.dp))
            ) {
                items(statistics) { stat ->
                    StatDisplay(
                        icon = stat.icon,
                        percentage = stat.value,
                        label = stat.label
                    )
                }
            }

            // Conclusión (si existe)
            if (conclusionText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = conclusionText,
                    fontSize = 14.sp,
                    color = CyberColors.NeonBlue,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * 🗺️ VARIANTE: Infographic con Proceso (paso a paso)
 * Para explicar procesos como "Cómo funciona un ransomware"
 */
@Composable
fun ProcessInfographicScreen(
    title: String,
    processSteps: List<ProcessStep>,
    impactSummary: String? = null,
    screenNumber: Int,
    totalScreens: Int,
    onNext: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }

    ScreenContainer(
        title = title,
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = onNext,
        buttonText = "Continuar",
        buttonEnabled = currentStep == processSteps.size - 1
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Paso actual
            val step = processSteps[currentStep]

            Text(
                text = "PASO ${step.stepNumber}/${processSteps.size}: ${step.title}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = step.description,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 18.sp
            )

            // Icono visual del paso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.icon,
                    fontSize = 80.sp
                )
            }

            // Indicador de progreso de pasos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                processSteps.forEachIndexed { index, _ ->
                    Text(
                        text = if (index == currentStep) "🔴" else "⚪",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // Botón para avanzar pasos
            if (currentStep < processSteps.size - 1) {
                CyberButton(
                    text = "👆 TOCA PARA CONTINUAR",
                    onClick = { currentStep++ },
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (impactSummary != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📊 IMPACTO REAL:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonBlue
                )
                Text(
                    text = impactSummary,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Componente: Key Point Item
 */
@Composable
fun KeyPointItem(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

/**
 * Modelo de datos: Key Point
 */
data class KeyPoint(
    val icon: String,
    val text: String
)

/**
 * Modelo de datos: Statistic Item
 */
data class StatisticItem(
    val icon: String,
    val value: String,
    val label: String
)

/**
 * Modelo de datos: Process Step
 */
data class ProcessStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: String
)