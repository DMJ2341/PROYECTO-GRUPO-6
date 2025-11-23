package com.example.cyberlearnapp.ui.screens.lessons.templates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.QuizOption
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * ❓ PLANTILLA: QUIZ
 * Pantalla de pregunta con opciones múltiples y feedback
 *
 * Usado en:
 * - Lección 1: Interno vs Externo
 * - Lección 5: ¿Qué principio CIA proteger?
 * - Lección 6: Evaluación final
 */
@Composable
fun QuizScreen(
    title: String,
    question: String,
    options: List<QuizOption>,
    explanation: String? = null,
    screenNumber: Int,
    totalScreens: Int,
    onAnswerRecorded: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    val selectedOption = options.find { it.id == selectedOptionId }
    val isCorrect = selectedOption?.isCorrect ?: false

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
            // Pregunta
            Text(
                text = question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opciones
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    QuizOptionCard(
                        option = option,
                        isSelected = selectedOptionId == option.id,
                        showResult = showFeedback,
                        onClick = {
                            if (!showFeedback) {
                                selectedOptionId = option.id
                                showFeedback = true
                            }
                        }
                    )
                }
            }

            // Feedback específico de la opción seleccionada
            if (showFeedback && selectedOption != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FeedbackMessage(
                    isCorrect = isCorrect,
                    message = selectedOption.feedback
                )

                // Explicación adicional (opcional)
                if (explanation != null && isCorrect) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "💡 $explanation",
                        fontSize = 13.sp,
                        color = CyberColors.NeonBlue,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

/**
 * 🎯 VARIANTE: Quiz con Imagen
 * Para preguntas que requieren analizar una imagen
 */
@Composable
fun QuizWithImageScreen(
    title: String,
    question: String,
    imageDescription: String, // Descripción de la imagen a mostrar
    options: List<QuizOption>,
    screenNumber: Int,
    totalScreens: Int,
    onAnswerRecorded: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    val selectedOption = options.find { it.id == selectedOptionId }
    val isCorrect = selectedOption?.isCorrect ?: false

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
            // Pregunta
            Text(
                text = question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Placeholder para imagen (aquí iría la imagen real)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CyberColors.CardBg
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "🖼️ $imageDescription",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Opciones
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    QuizOptionCard(
                        option = option,
                        isSelected = selectedOptionId == option.id,
                        showResult = showFeedback,
                        onClick = {
                            if (!showFeedback) {
                                selectedOptionId = option.id
                                showFeedback = true
                            }
                        }
                    )
                }
            }

            // Feedback
            if (showFeedback && selectedOption != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FeedbackMessage(
                    isCorrect = isCorrect,
                    message = selectedOption.feedback
                )
            }
        }
    }
}

/**
 * 📝 VARIANTE: Multi-Question Quiz
 * Para evaluaciones con múltiples preguntas
 */
@Composable
fun MultiQuestionQuizScreen(
    title: String,
    questions: List<QuizQuestion>,
    screenNumber: Int,
    totalScreens: Int,
    onScoreRecorded: (Int, Int) -> Unit, // (correctas, totales)
    onNext: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var correctAnswers by remember { mutableStateOf(0) }

    val currentQuestion = questions[currentQuestionIndex]
    val selectedOption = currentQuestion.options.find { it.id == selectedOptionId }
    val isCorrect = selectedOption?.isCorrect ?: false
    val isLastQuestion = currentQuestionIndex == questions.size - 1

    ScreenContainer(
        title = title,
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = {
            if (isLastQuestion && showFeedback) {
                onScoreRecorded(correctAnswers, questions.size)
                onNext()
            }
        },
        buttonText = if (isLastQuestion) "Ver Resultados" else "Siguiente Pregunta",
        buttonEnabled = showFeedback
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicador de progreso
            Text(
                text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}",
                fontSize = 12.sp,
                color = CyberColors.NeonBlue,
                fontWeight = FontWeight.Bold
            )

            // Score actual
            Text(
                text = "Correctas: $correctAnswers",
                fontSize = 12.sp,
                color = CyberColors.NeonGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pregunta
            Text(
                text = currentQuestion.question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 20.sp
            )

            // Opciones
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                currentQuestion.options.forEach { option ->
                    QuizOptionCard(
                        option = option,
                        isSelected = selectedOptionId == option.id,
                        showResult = showFeedback,
                        onClick = {
                            if (!showFeedback) {
                                selectedOptionId = option.id
                                showFeedback = true
                            }
                        }
                    )
                }
            }

            // Feedback
            if (showFeedback && selectedOption != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FeedbackMessage(
                    isCorrect = isCorrect,
                    message = selectedOption.feedback
                )

                // Botón para siguiente pregunta
                if (!isLastQuestion) {
                    CyberButton(
                        text = "Siguiente Pregunta →",
                        onClick = {
                            if (isCorrect) correctAnswers++
                            currentQuestionIndex++
                            selectedOptionId = null
                            showFeedback = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (isCorrect) {
                    correctAnswers++
                }
            }
        }
    }
}

/**
 * Componente: Quiz Option Card
 */
@Composable
fun QuizOptionCard(
    option: QuizOption,
    isSelected: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !showResult && isSelected -> CyberColors.NeonBlue.copy(alpha = 0.2f)
        showResult && isSelected && option.isCorrect -> CyberColors.NeonGreen.copy(alpha = 0.2f)
        showResult && isSelected && !option.isCorrect -> CyberColors.NeonPink.copy(alpha = 0.2f)
        else -> CyberColors.CardBg
    }

    val borderColor = when {
        !showResult && isSelected -> CyberColors.NeonBlue
        showResult && option.isCorrect -> CyberColors.NeonGreen
        showResult && isSelected && !option.isCorrect -> CyberColors.NeonPink
        else -> CyberColors.BorderGlow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = option.text,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            // Indicador de resultado
            if (showResult) {
                Text(
                    text = if (option.isCorrect) "✅" else if (isSelected) "❌" else "",
                    fontSize = 24.sp
                )
            }
        }
    }
}

/**
 * Modelo de datos: Quiz Question
 */
data class QuizQuestion(
    val question: String,
    val options: List<QuizOption>
)