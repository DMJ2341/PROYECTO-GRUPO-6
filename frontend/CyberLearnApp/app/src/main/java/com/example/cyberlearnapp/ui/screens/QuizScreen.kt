package com.example.cyberlearnapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.network.models.GlossaryTerm
import com.example.cyberlearnapp.viewmodel.GlossaryViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    viewModel: GlossaryViewModel = hiltViewModel()
) {
    val session by viewModel.practiceSession.collectAsState()
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var quizOptions by remember { mutableStateOf<List<GlossaryTerm>>(emptyList()) }

    // Generar opciones cuando cambia la pregunta
    LaunchedEffect(session?.currentIndex) {
        val currentTerm = session?.currentTerm
        if (currentTerm != null) {
            quizOptions = viewModel.generateQuizOptions(currentTerm)
            selectedOption = null
            showFeedback = false
        }
    }

    if (session == null || session?.isFinished == true) {
        LaunchedEffect(Unit) {
            if (session?.isFinished == true) {
                val correct = session!!.correctCount
                val total = session!!.terms.size
                navController.navigate("practice_result/$correct/$total") {
                    popUpTo("glossary") { inclusive = false }
                }
            } else {
                navController.popBackStack()
            }
        }
        return
    }

    val currentSession = session!!
    val currentTerm = currentSession.currentTerm ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎯 Quiz") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.endPracticeSession()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Close, "Salir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            // Progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pregunta ${currentSession.currentIndex + 1}/${currentSession.terms.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${currentSession.correctCount}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${currentSession.incorrectCount}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = currentSession.progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            // Pregunta
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "¿Qué término corresponde a esta definición?",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = currentTerm.definitionEs,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Opciones
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                quizOptions.forEachIndexed { index, option ->
                    QuizOptionCard(
                        option = option,
                        isSelected = selectedOption == option.id,
                        showFeedback = showFeedback,
                        isCorrect = option.id == currentTerm.id,
                        onClick = {
                            if (!showFeedback) {
                                selectedOption = option.id
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Botón de confirmar/siguiente
            Button(
                onClick = {
                    if (!showFeedback && selectedOption != null) {
                        // Mostrar feedback
                        showFeedback = true
                        val isCorrect = selectedOption == currentTerm.id
                        viewModel.recordAnswer(isCorrect)
                    } else if (showFeedback) {
                        // Avanzar a siguiente pregunta
                        viewModel.nextQuestion()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedOption != null
            ) {
                Text(
                    text = if (showFeedback) "Siguiente ➡️" else "Confirmar Respuesta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuizOptionCard(
    option: GlossaryTerm,
    isSelected: Boolean,
    showFeedback: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !showFeedback && isSelected -> MaterialTheme.colorScheme.primaryContainer
        showFeedback && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        showFeedback && isSelected && !isCorrect -> Color(0xFFF44336).copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        !showFeedback && isSelected -> MaterialTheme.colorScheme.primary
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> Color(0xFFF44336)
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = !showFeedback,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected || (showFeedback && isCorrect)) 2.dp else 1.dp,
            color = borderColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio button / Icono
            if (showFeedback) {
                if (isCorrect) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                } else if (isSelected) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    RadioButton(
                        selected = false,
                        onClick = null,
                        enabled = false
                    )
                }
            } else {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = option.termEs,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}