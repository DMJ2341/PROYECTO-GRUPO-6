package com.example.cyberlearnapp.ui.screens.interactive

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cyberlearnapp.network.models.LessonScreen
import com.example.cyberlearnapp.network.models.QuizQuestion
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
    screen: LessonScreen,
    quizAnswers: Map<String, String>,
    onAnswerSelected: (Int, String, Boolean, Int) -> Unit,
    onComplete: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showCompletion by remember { mutableStateOf(false) }

    val questions = screen.questions ?: emptyList()
    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    val scrollState = rememberScrollState()

    if (showCompletion) {
        CompletionScreen(
            totalQuestions = questions.size,
            correctAnswers = quizAnswers.count { (qId, answer) ->
                questions.find { it.id.toString() == qId }?.correctAnswer == answer
            },
            totalXp = questions.sumOf { it.xp },
            onComplete = onComplete
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Text(
                text = screen.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(questions.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = when {
                                        index < currentQuestionIndex -> Color(0xFF10B981)
                                        index == currentQuestionIndex -> Color(0xFF60A5FA)
                                        else -> Color.White.copy(alpha = 0.3f)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pregunta actual
            currentQuestion?.let { question ->
                AnimatedContent(
                    targetState = currentQuestionIndex,
                    transitionSpec = {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    },
                    label = "question"
                ) { _ ->
                    QuestionCard(
                        question = question,
                        selectedAnswer = selectedAnswer,
                        onAnswerSelected = { answer ->
                            selectedAnswer = answer
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón confirmar
                Button(
                    onClick = {
                        selectedAnswer?.let { answer ->
                            val correct = answer == question.correctAnswer
                            isCorrect = correct
                            onAnswerSelected(
                                question.id,
                                answer,
                                correct,
                                if (correct) question.xp else 0
                            )
                            showFeedback = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedAnswer != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF60A5FA),
                        disabledContainerColor = Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Confirmar respuesta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Feedback Dialog
        if (showFeedback) {
            currentQuestion?.let { question ->
                FeedbackQuizDialog(
                    question = question,
                    selectedAnswer = selectedAnswer ?: "",
                    isCorrect = isCorrect,
                    onNext = {
                        showFeedback = false
                        selectedAnswer = null

                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                        } else {
                            showCompletion = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuestionCard(
    question: QuizQuestion,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    Column {
        // Escenario
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = question.scenario,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pregunta
        Text(
            text = "❓ ${question.question}",
            color = Color(0xFFFBBF24),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opciones
        question.options.forEach { option ->
            OptionCard(
                option = option,
                isSelected = selectedAnswer == option.id,
                onSelect = { onAnswerSelected(option.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun OptionCard(
    option: com.example.cyberlearnapp.network.models.QuizOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFF60A5FA),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF60A5FA).copy(alpha = 0.2f)
            else
                Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (isSelected)
                            Color(0xFF60A5FA)
                        else
                            Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "${option.id}) ${option.text}",
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun FeedbackQuizDialog(
    question: QuizQuestion,
    selectedAnswer: String,
    isCorrect: Boolean,
    onNext: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Emoji y título
                Text(
                    text = if (isCorrect) "✅" else "❌",
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isCorrect) "¡CORRECTO!" else "¡CUIDADO!",
                    color = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Explicación
                Text(
                    text = question.explanation,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )

                if (isCorrect) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🎁 +${question.xp} XP",
                        color = Color(0xFFFBBF24),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCorrect)
                            Color(0xFF10B981)
                        else
                            Color(0xFF60A5FA)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Siguiente pregunta →",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CompletionScreen(
    totalQuestions: Int,
    correctAnswers: Int,
    totalXp: Int,
    onComplete: () -> Unit
) {
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showConfetti = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF3B82F6)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animación de celebración
            AnimatedVisibility(
                visible = showConfetti,
                enter = scaleIn() + fadeIn()
            ) {
                Text(
                    text = "🎉",
                    fontSize = 100.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡LECCIÓN COMPLETADA!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Resultados
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📊 Tu puntuación:",
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "$correctAnswers/$totalQuestions",
                        color = Color(0xFFFBBF24),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(totalQuestions) { index ->
                            Text(
                                text = if (index < correctAnswers) "⭐" else "☆",
                                fontSize = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Divider(color = Color.White.copy(alpha = 0.3f))

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "🎁 RECOMPENSAS:",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+$totalXp XP",
                            color = Color(0xFFFBBF24),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🏆", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Insignia: \"Detector de Trampas\"",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botones
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Siguiente lección →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}