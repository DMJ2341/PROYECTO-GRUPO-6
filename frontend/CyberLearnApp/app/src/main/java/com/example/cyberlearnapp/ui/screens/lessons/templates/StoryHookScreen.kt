package com.example.cyberlearnapp.ui.screens.lessons.templates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.ImpactCard
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * 🎬 PLANTILLA: STORY HOOK
 * Pantalla tipo "Caso Real" con fecha, descripción y tarjetas de impacto
 *
 * Usado en:
 * - Lección 1: WannaCry
 * - Lección 2: Equifax
 * - Lección 3: Colonial Pipeline
 * - Lección 4: Ataque a Tienda
 * - Lección 5: Target
 * - Lección 6: TecnoShop
 */
@Composable
fun StoryHookScreen(
    caseTitle: String,
    date: String,
    description: String,
    impactCards: List<ImpactCardData>,
    hookQuestion: String,
    screenNumber: Int,
    totalScreens: Int,
    buttonText: String = "🎯 ANALIZAR EL CASO",
    onNext: () -> Unit
) {
    ScreenContainer(
        title = caseTitle,
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = onNext,
        buttonText = buttonText
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fecha del caso
            Text(
                text = "\"$date\"",
                fontSize = 14.sp,
                color = CyberColors.NeonBlue,
                fontWeight = FontWeight.Bold
            )

            // Descripción del incidente
            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.White,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Instrucción de interacción
            Text(
                text = "👆 TOCA PARA VER EL IMPACTO:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            // Grid de tarjetas de impacto
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height((impactCards.size * 120).dp)
            ) {
                items(impactCards) { cardData ->
                    ImpactCard(
                        icon = cardData.icon,
                        value = cardData.value,
                        label = cardData.label,
                        detail = cardData.detail,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pregunta gancho
            Text(
                text = hookQuestion,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen,
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * 📊 VARIANTE: Story Hook con Timeline
 * Para casos que necesitan mostrar una línea de tiempo
 */
@Composable
fun StoryHookWithTimeline(
    caseTitle: String,
    timelineEvents: List<TimelineEventData>,
    impactCards: List<ImpactCardData>,
    hookQuestion: String,
    screenNumber: Int,
    totalScreens: Int,
    onNext: () -> Unit
) {
    ScreenContainer(
        title = caseTitle,
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = onNext,
        buttonText = "🎯 ANALIZAR CRONOLOGÍA"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "LÍNEA DE TIEMPO DEL ATAQUE:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            // Timeline
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                timelineEvents.forEach { event ->
                    TimelineItem(
                        time = event.time,
                        description = event.description,
                        isHighlight = event.isHighlight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas de impacto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                impactCards.take(2).forEach { cardData ->
                    ImpactCard(
                        icon = cardData.icon,
                        value = cardData.value,
                        label = cardData.label,
                        detail = cardData.detail,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = hookQuestion,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )
        }
    }
}

/**
 * Componente: Timeline Item
 */
@Composable
fun TimelineItem(
    time: String,
    description: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Punto en la línea de tiempo
        Text(
            text = if (isHighlight) "🔴" else "⚪",
            fontSize = 20.sp
        )

        Column {
            Text(
                text = time,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) CyberColors.NeonPink else CyberColors.NeonBlue
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 16.sp
            )
        }
    }
}