package com.example.cyberlearnapp.ui.screens.lessons.templates

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.Category
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * 🎯 PLANTILLA: CLASSIFIER
 * Pantalla de clasificación con categorías (drag & drop simplificado con clicks)
 *
 * Usado en:
 * - Lección 1: Clasificar SMiShing
 * - Lección 2: Clasificar Pretexting
 * - Lección 3: Tipo de ataque DDoS
 * - Lección 4: Red Wi-Fi segura
 * - Lección 5: Decidir principio CIA
 */
@Composable
fun ClassifierScreen(
    title: String,
    scenario: String,
    categories: List<Category>,
    correctCategoryId: String,
    correctFeedback: String,
    incorrectFeedback: String,
    screenNumber: Int,
    totalScreens: Int,
    onAnswerRecorded: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    val isCorrect = selectedCategory == correctCategoryId

    ScreenContainer(
        title = title,
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = {
            if (showFeedback) {
                onAnswerRecorded(isCorrect)
                onNext()
            }
        },
        buttonText = "Siguiente",
        buttonEnabled = showFeedback
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Instrucciones
            Text(
                text = "\"Selecciona la categoría correcta:\"",
                fontSize = 14.sp,
                color = Color.White
            )

            // Escenario a clasificar
            Text(
                text = "CASO:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = scenario,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categorías para seleccionar
            when (categories.size) {
                2 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categories.forEach { category ->
                            CategoryChip(
                                name = category.name,
                                icon = category.icon,
                                color = parseColor(category.color),
                                isSelected = selectedCategory == category.id,
                                onClick = {
                                    selectedCategory = category.id
                                    showFeedback = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                3 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categories.forEach { category ->
                            CategoryChip(
                                name = category.name,
                                icon = category.icon,
                                color = parseColor(category.color),
                                isSelected = selectedCategory == category.id,
                                onClick = {
                                    selectedCategory = category.id
                                    showFeedback = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        categories.forEach { category ->
                            CategoryChip(
                                name = category.name,
                                icon = category.icon,
                                color = parseColor(category.color),
                                isSelected = selectedCategory == category.id,
                                onClick = {
                                    selectedCategory = category.id
                                    showFeedback = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Feedback
            if (showFeedback && selectedCategory != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FeedbackMessage(
                    isCorrect = isCorrect,
                    message = if (isCorrect) correctFeedback else incorrectFeedback
                )
            }
        }
    }
}

/**
 * 🎮 VARIANTE: Multi-Step Classifier
 * Para clasificar múltiples casos seguidos
 */
@Composable
fun MultiStepClassifierScreen(
    title: String,
    cases: List<ClassifierCase>,
    categories: List<Category>,
    screenNumber: Int,
    totalScreens: Int,
    onAnswersRecorded: (Int) -> Unit, // Recibe número de correctas
    onNext: () -> Unit
) {
    var currentCaseIndex by remember { mutableStateOf(0) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }

    val currentCase = cases[currentCaseIndex]
    val isCorrect = selectedCategory == currentCase.correctCategoryId
    val isLastCase = currentCaseIndex == cases.size - 1

    ScreenContainer(
        title = title,
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = {
            if (isLastCase && showFeedback) {
                onAnswersRecorded(correctCount)
                onNext()
            }
        },
        buttonText = if (isLastCase) "Finalizar" else "Siguiente Caso",
        buttonEnabled = showFeedback
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicador de progreso
            Text(
                text = "Caso ${currentCaseIndex + 1} de ${cases.size}",
                fontSize = 12.sp,
                color = CyberColors.NeonBlue,
                fontWeight = FontWeight.Bold
            )

            // Escenario
            Text(
                text = currentCase.scenario,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categorías
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { category ->
                    CategoryChip(
                        name = category.name,
                        icon = category.icon,
                        color = parseColor(category.color),
                        isSelected = selectedCategory == category.id,
                        onClick = {
                            selectedCategory = category.id
                            showFeedback = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Feedback
            if (showFeedback && selectedCategory != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FeedbackMessage(
                    isCorrect = isCorrect,
                    message = if (isCorrect) {
                        currentCase.correctFeedback
                    } else {
                        currentCase.incorrectFeedback
                    }
                )

                // Botón para avanzar al siguiente caso (si no es el último)
                if (!isLastCase) {
                    CyberButton(
                        text = "Siguiente Caso →",
                        onClick = {
                            if (isCorrect) correctCount++
                            currentCaseIndex++
                            selectedCategory = null
                            showFeedback = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (isCorrect) {
                    correctCount++
                }
            }
        }
    }
}

/**
 * Función helper para parsear colores hex
 */
private fun parseColor(hexColor: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hexColor))
    } catch (e: Exception) {
        CyberColors.NeonGreen // Color por defecto
    }
}

/**
 * Modelo de datos: Classifier Case
 */
data class ClassifierCase(
    val scenario: String,
    val correctCategoryId: String,
    val correctFeedback: String,
    val incorrectFeedback: String
)