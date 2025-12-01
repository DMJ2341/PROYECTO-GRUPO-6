package com.example.cyberlearnapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.models.CyberRole
import com.example.cyberlearnapp.ui.theme.BackgroundMain
import com.example.cyberlearnapp.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultSummaryScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel()
) {
    val result by viewModel.result.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎉 Resultados del Test") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("dashboard") }) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundMain
                )
            )
        }
    ) { padding ->

        if (result == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Convertimos el string del rol a un objeto CyberRole
            val role = CyberRole.fromString(result!!.recommendedRole)!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // 1. EMOJI GIGANTE ANIMADO
                RoleEmojiCard(role = role)

                Spacer(modifier = Modifier.height(24.dp))

                // 2. TÍTULO Y DESCRIPCIÓN DEL ROL
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Tu perfil ideal es:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        role.displayName,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(role.color)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        role.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. TARJETA DE CONFIANZA
                ConfidenceCard(
                    confidence = result!!.confidence,
                    color = Color(role.color)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. TOP DIMENSIONES
                TopDimensionsCard(topDimensions = result!!.topDimensions)

                Spacer(modifier = Modifier.weight(1f))

                // 5. BOTONES DE ACCIÓN (NAVEGACIÓN)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón principal: Ver Recomendaciones
                    Button(
                        onClick = {
                            navController.navigate("test_recommendations")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(role.color)
                        )
                    ) {
                        Icon(Icons.Default.CheckCircle, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Ver Certificaciones y Labs",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Botón secundario: Ver Skills
                    OutlinedButton(
                        onClick = {
                            navController.navigate("test_skills")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Star, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Skills Necesarias")
                    }

                    // Botón terciario: Retomar
                    TextButton(
                        onClick = {
                            viewModel.resetTest()
                            navController.navigate("test_questions") {
                                popUpTo("test_result_summary") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retomar Test")
                    }
                }
            }
        }
    }
}

@Composable
fun RoleEmojiCard(role: CyberRole) {
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
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
            .size(160.dp)
            .scale(scale.value)
            .clip(CircleShape)
            .background(Color(role.color)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = role.emoji,
            style = MaterialTheme.typography.displayLarge,
            fontSize = MaterialTheme.typography.displayLarge.fontSize * 2
        )
    }
}

@Composable
fun ConfidenceCard(confidence: Float, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Nivel de Confianza:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { confidence },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun TopDimensionsCard(topDimensions: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "🧩 Tus Dimensiones Principales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            topDimensions.take(3).forEach { dimension ->
                val (icon, displayName) = when(dimension) {
                    "INVESTIGATIVE" -> "🔍" to "Investigativo - Análisis"
                    "REALISTIC" -> "🛠️" to "Realista - Técnico"
                    "SOCIAL" -> "🤝" to "Social - Colaboración"
                    "CONVENTIONAL" -> "📋" to "Convencional - Procesos"
                    "ENTERPRISING" -> "🎯" to "Emprendedor - Liderazgo"
                    "ARTISTIC" -> "🎨" to "Artístico - Creatividad"
                    else -> "✓" to dimension
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}