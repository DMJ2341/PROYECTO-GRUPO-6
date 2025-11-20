package com.example.cyberlearnapp.ui.screens.lessons.templates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * 🏆 PLANTILLA: SUMMARY
 * Pantalla de resumen con logros, recompensas y estadísticas
 *
 * Usado en:
 * - Todas las lecciones al finalizar
 * - Pantalla 6 de cada lección
 */
@Composable
fun SummaryScreen(
    lessonTitle: String,
    achievements: List<String>,
    statistics: List<StatisticItem>? = null,
    xpEarned: Int,
    badgeName: String? = null,
    nextLessonTitle: String? = null,
    screenNumber: Int,
    totalScreens: Int,
    onComplete: () -> Unit
) {
    ScreenContainer(
        title = "🏆 MISIÓN CUMPLIDA - $lessonTitle",
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = onComplete,
        buttonText = if (nextLessonTitle != null) {
            "🚀 CONTINUAR A ${nextLessonTitle.uppercase()}"
        } else {
            "✅ FINALIZAR LECCIÓN"
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lista de logros
            Text(
                text = "✅ APRENDISTE SOBRE:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                achievements.forEach { achievement ->
                    Text(
                        text = achievement,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            // Estadísticas (si existen)
            if (statistics != null && statistics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "📊 ESTADÍSTICAS DE LA LECCIÓN:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonBlue
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    statistics.take(3).forEach { stat ->
                        StatDisplay(
                            icon = stat.icon,
                            percentage = stat.value,
                            label = stat.label
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recompensas
            Text(
                text = "🎁 RECOMPENSAS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            val rewardText = buildString {
                append("⭐ +$xpEarned XP")
                if (badgeName != null) {
                    append(" | 🛡️ Insignia \"$badgeName\"")
                }
            }

            Text(
                text = rewardText,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 🎯 VARIANTE: Summary con Score
 * Para evaluaciones con puntuación
 */
@Composable
fun SummaryWithScoreScreen(
    lessonTitle: String,
    score: Int, // 0-100
    correctAnswers: Int,
    totalQuestions: Int,
    achievements: List<String>,
    xpEarned: Int,
    badgeName: String? = null,
    strengths: List<String>? = null,
    improvements: List<String>? = null,
    screenNumber: Int,
    totalScreens: Int,
    onComplete: () -> Unit
) {
    ScreenContainer(
        title = "🏆 OPERACIÓN COMPLETADA - RESULTADOS",
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = onComplete,
        buttonText = "✅ COMPLETAR LECCIÓN"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score principal
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📊 PUNTUACIÓN FINAL:",
                        fontSize = 14.sp,
                        color = CyberColors.NeonBlue
                    )
                    Text(
                        text = "$score/100",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            score >= 90 -> CyberColors.NeonGreen
                            score >= 70 -> CyberColors.NeonBlue
                            else -> CyberColors.NeonPink
                        }
                    )
                    Text(
                        text = "$correctAnswers de $totalQuestions correctas",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Desglose por fases
            if (achievements.isNotEmpty()) {
                Text(
                    text = "DESGLOSE DE PUNTOS:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonGreen
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    achievements.forEach { achievement ->
                        Text(
                            text = achievement,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Fortalezas
            if (strengths != null && strengths.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🎯 FORTALEZAS DESTACADAS:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonGreen
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    strengths.forEach { strength ->
                        Text(
                            text = "• $strength",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Áreas de mejora
            if (improvements != null && improvements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 ÁREAS DE MEJORA:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonBlue
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    improvements.forEach { improvement ->
                        Text(
                            text = "• $improvement",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recompensas
            Text(
                text = "🎁 RECOMPENSAS OBTENIDAS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            val rewardText = buildString {
                append("⭐ +$xpEarned XP Totales")
                if (badgeName != null) {
                    append("\n🛡️ Insignia \"$badgeName\"")
                }
            }

            Text(
                text = rewardText,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * 🌟 VARIANTE: Course Completion Summary
 * Para finalización completa de un curso (no solo lección)
 */
@Composable
fun CourseCompletionScreen(
    courseTitle: String,
    totalXpEarned: Int,
    totalLessonsCompleted: Int,
    badges: List<BadgeInfo>,
    overallScore: Int,
    nextCourseTitle: String? = null,
    screenNumber: Int,
    totalScreens: Int,
    onComplete: () -> Unit
) {
    ScreenContainer(
        title = "🎓 CURSO COMPLETADO",
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = onComplete,
        buttonText = if (nextCourseTitle != null) {
            "🚀 CONTINUAR A $nextCourseTitle"
        } else {
            "📊 VER DASHBOARD"
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título del curso
            Text(
                text = courseTitle.uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Estadísticas principales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatDisplay(
                    icon = "📚",
                    percentage = "$totalLessonsCompleted",
                    label = "Lecciones\nCompletadas"
                )

                StatDisplay(
                    icon = "⭐",
                    percentage = "$totalXpEarned",
                    label = "XP\nGanado"
                )

                StatDisplay(
                    icon = "🎯",
                    percentage = "$overallScore%",
                    label = "Promedio\nGeneral"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Insignias obtenidas
            Text(
                text = "⭐ LOGROS OBTENIDOS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(badges) { badge ->
                    BadgeCard(
                        icon = badge.icon,
                        name = badge.name
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje motivacional
            Text(
                text = "💬 COMENTARIO DEL INSTRUCTOR:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            val motivationalMessage = when {
                overallScore >= 90 -> "¡Excelente trabajo! Dominas los conceptos fundamentales de ciberseguridad."
                overallScore >= 70 -> "¡Buen trabajo! Has demostrado un sólido entendimiento del material."
                else -> "¡Sigue practicando! La ciberseguridad requiere dedicación constante."
            }

            Text(
                text = "\"$motivationalMessage\"",
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * Componente: Badge Card
 */
@Composable
fun BadgeCard(icon: String, name: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.CardBg
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            Text(
                text = name,
                fontSize = 11.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/**
 * Modelo de datos: Badge Info
 */
data class BadgeInfo(
    val icon: String,
    val name: String
)