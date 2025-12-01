package com.example.cyberlearnapp.ui.screens

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
import androidx.navigation.NavController
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeResultScreen(
    navController: NavController,
    correctCount: Int,
    totalCount: Int
) {
    val percentage = if (totalCount > 0) (correctCount.toFloat() / totalCount * 100).toInt() else 0
    val incorrectCount = totalCount - correctCount
    val showConfetti = percentage >= 80

    // Estado para confeti
    var playConfetti by remember { mutableStateOf(showConfetti) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Resultados") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack("glossary", false) }) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Confeti si el resultado es bueno
            if (playConfetti) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(
                        Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                            position = Position.Relative(0.5, 0.3),
                            emitter = Emitter(duration = 3, TimeUnit.SECONDS).max(100)
                        )
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emoji e icono según resultado
                Text(
                    text = when {
                        percentage >= 90 -> "🏆"
                        percentage >= 80 -> "🎉"
                        percentage >= 70 -> "👏"
                        percentage >= 60 -> "👍"
                        else -> "💪"
                    },
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = when {
                        percentage >= 90 -> "¡Excelente!"
                        percentage >= 80 -> "¡Muy bien!"
                        percentage >= 70 -> "¡Buen trabajo!"
                        percentage >= 60 -> "Sigue practicando"
                        else -> "Necesitas repasar"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Card con resultados
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Porcentaje grande
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "de acierto",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(Modifier.height(24.dp))

                        HorizontalDivider()

                        Spacer(Modifier.height(24.dp))

                        // Desglose de respuestas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "$correctCount",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Correctas",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = Color(0xFFF44336),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "$incorrectCount",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF44336)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Incorrectas",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Mensaje motivacional
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = when {
                            percentage >= 90 -> "¡Dominas estos términos! Estás listo para nuevos desafíos."
                            percentage >= 80 -> "Tienes un excelente conocimiento. Sigue así."
                            percentage >= 70 -> "Vas por buen camino. Repasa algunos términos."
                            percentage >= 60 -> "Buen progreso. Practica más para mejorar."
                            else -> "Sigue estudiando. La práctica hace al maestro."
                        },
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Botones de acción
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navController.popBackStack("glossary", false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Home, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Volver al Glosario",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            playConfetti = false
                            navController.popBackStack("glossary", false)
                            // Aquí podrías triggear reiniciar la práctica si tienes esa lógica
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(Icons.Default.Replay, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Practicar de Nuevo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}