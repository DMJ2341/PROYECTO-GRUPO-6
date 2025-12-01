package com.example.cyberlearnapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.theme.BackgroundMain
import com.example.cyberlearnapp.viewmodel.TestViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel(),
    token: String
) {
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val answers by viewModel.answers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isCompleted by viewModel.isCompleted.collectAsState()
    val error by viewModel.error.collectAsState()
    val result by viewModel.result.collectAsState()

    // Cargar preguntas al inicio
    LaunchedEffect(Unit) {
        viewModel.loadQuestions(token)
    }

    // 1️⃣ EFECTO: Enviar datos cuando el usuario termina (localmente)
    LaunchedEffect(isCompleted) {
        if (isCompleted && result == null && !isLoading) {
            viewModel.submitTest(token)
        }
    }

    // 2️⃣ EFECTO: Navegar SOLO cuando el resultado ya existe en el ViewModel
    LaunchedEffect(result) {
        if (result != null) {
            navController.navigate("test_result_summary") {
                popUpTo("test_questions") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "🎯 Test de Preferencias",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (questions.isNotEmpty()) {
                            Text(
                                "Pregunta ${currentIndex + 1} de ${questions.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (viewModel.canGoBack()) {
                        IconButton(onClick = { viewModel.previousQuestion() }) {
                            Icon(Icons.Default.ArrowBack, "Anterior")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundMain
                )
            )
        }
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(if (questions.isEmpty()) "Cargando preguntas..." else "Analizando perfil...")
                    }
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "❌ Error",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(error ?: "Error desconocido")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            if (isCompleted) viewModel.submitTest(token)
                            else viewModel.loadQuestions(token)
                        }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            questions.isNotEmpty() -> {
                val currentQuestion = questions[currentIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { viewModel.getProgress() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedEmojiDisplay(emoji = currentQuestion.emoji)

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = {
                            slideInVertically { it } + fadeIn() togetherWith
                                    slideOutVertically { -it } + fadeOut()
                        },
                        label = "question_animation"
                    ) { index ->
                        Text(
                            text = questions[index].question,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Escala de rating 1-5
                    RatingScale(
                        currentAnswer = answers[currentQuestion.id.toString()],
                        onRatingSelected = { rating ->
                            viewModel.answerQuestion(currentQuestion.id, rating)
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            "💭 Responde con honestidad. No hay respuestas correctas o incorrectas.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedEmojiDisplay(emoji: String) {
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(emoji) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale.value)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displayLarge,
            fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5
        )
    }
}

@Composable
fun RatingScale(
    currentAnswer: Int?,
    onRatingSelected: (Int) -> Unit
) {
    val ratings = listOf(
        1 to "😞",
        2 to "😐",
        3 to "😊",
        4 to "😄",
        5 to "😍"
    )

    val labels = listOf(
        "Muy en desacuerdo",
        "En desacuerdo",
        "Neutral",
        "De acuerdo",
        "Muy de acuerdo"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Botones de rating con números abajo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ratings.forEach { (value, emoji) ->
                // ✅ MODIFICACIÓN: Envolvemos en columna para poner número abajo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RatingButton(
                        value = value,
                        emoji = emoji,
                        isSelected = currentAnswer == value,
                        onClick = { onRatingSelected(value) }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // ✅ MODIFICACIÓN: Texto del número
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (currentAnswer == value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (currentAnswer == value) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Labels (Texto descriptivo)
        if (currentAnswer != null) {
            Text(
                text = labels[currentAnswer - 1],
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RatingButton(
    value: Int,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val colors = listOf(
        Color(0xFFE53935), // Rojo
        Color(0xFFFF6F00), // Naranja oscuro
        Color(0xFFFF9800), // Naranja
        Color(0xFF66BB6A), // Verde claro
        Color(0xFF4CAF50)  // Verde
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .size(60.dp)
            .scale(scale),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) colors[value - 1] else MaterialTheme.colorScheme.surfaceVariant
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        // ✅ MODIFICACIÓN: Ya no mostramos el número adentro, solo el emoji
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleLarge
        )
    }
}