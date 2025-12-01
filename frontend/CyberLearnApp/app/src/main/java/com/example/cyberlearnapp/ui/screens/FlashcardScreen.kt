package com.example.cyberlearnapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.viewmodel.GlossaryViewModel
import com.example.cyberlearnapp.viewmodel.PracticeSession

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    navController: NavController,
    viewModel: GlossaryViewModel = hiltViewModel()
) {
    val session by viewModel.practiceSession.collectAsState()
    var cardFace by remember { mutableStateOf(CardFace.Front) }

    // Reset card face cuando cambia la pregunta
    LaunchedEffect(session?.currentIndex) {
        cardFace = CardFace.Front
    }

    if (session == null || session?.isFinished == true) {
        // Navegar a resultados si terminó
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
                title = { Text("🎴 Flashcards") },
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Término ${currentSession.currentIndex + 1}/${currentSession.terms.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${(currentSession.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = currentSession.progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            // ✨ TARJETA CON ANIMACIÓN DE VOLTEO
            FlipCard(
                cardFace = cardFace,
                onClick = { cardFace = it.next },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                front = {
                    FlashcardFront(term = currentTerm.termEs)
                },
                back = {
                    FlashcardBack(
                        definition = currentTerm.definitionEs,
                        example = currentTerm.exampleEs
                    )
                }
            )

            Spacer(Modifier.height(24.dp))

            // Instrucción
            if (cardFace == CardFace.Front) {
                Text(
                    text = "👆 Toca la tarjeta para ver la definición",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "¿Recordaste la definición?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            // Botones de respuesta (solo aparecen cuando se voltea)
            if (cardFace == CardFace.Back) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.recordAnswer(false)
                            viewModel.nextQuestion()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(Icons.Default.Close, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("No lo sabía", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.recordAnswer(true)
                            viewModel.nextQuestion()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(Icons.Default.Check, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Lo sabía", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit = {},
    back: @Composable () -> Unit = {}
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
            }
            .clickable { onClick(cardFace.next) },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation.value <= 90f) {
                // Frente
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    front()
                }
            } else {
                // Reverso (invertido horizontalmente)
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .graphicsLayer { rotationY = 180f }
                ) {
                    back()
                }
            }
        }
    }
}

@Composable
fun FlashcardFront(term: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📚",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = term,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun FlashcardBack(definition: String, example: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "💡 Definición",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = definition,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            if (example != null) {
                Spacer(Modifier.height(24.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📝 Ejemplo",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = example,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}