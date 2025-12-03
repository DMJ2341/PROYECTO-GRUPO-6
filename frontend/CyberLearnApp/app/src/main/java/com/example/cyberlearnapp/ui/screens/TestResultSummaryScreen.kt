package com.example.cyberlearnapp.ui.screens

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.models.CyberRole
import com.example.cyberlearnapp.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultSummaryScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel()
) {
    val currentResult by viewModel.currentResult.collectAsState()
    val result by viewModel.result.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // ✅ DEBUG LOGS
    LaunchedEffect(Unit) {
        Log.d("TestResultSummary", "🔍 Screen cargada")
        Log.d("TestResultSummary", "🔍 currentResult = ${currentResult?.recommendedRole}")
        Log.d("TestResultSummary", "🔍 result = ${result?.recommendedRole}")
        Log.d("TestResultSummary", "🔍 isLoading = $isLoading")
    }

    LaunchedEffect(currentResult, result) {
        Log.d("TestResultSummary", "🔄 Estados cambiaron:")
        Log.d("TestResultSummary", "   currentResult = ${currentResult?.recommendedRole}")
        Log.d("TestResultSummary", "   result = ${result?.recommendedRole}")
    }

    // Usar result en lugar de currentResult
    val finalResult = result ?: currentResult

    // ✅ FONDO CON GRADIENTE
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1419),
                        Color(0xFF1A2332)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎉", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Resultados del Test",
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("dashboard") }) {
                            Icon(Icons.Default.Close, "Cerrar", tint = Color(0xFF00D9FF))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A2332)
                    )
                )
            }
        ) { padding ->

            if (finalResult == null || isLoading) {
                Log.d("TestResultSummary", "⏳ Mostrando loading (finalResult=$finalResult, isLoading=$isLoading)")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color(0xFF00D9FF),
                            modifier = Modifier.size(64.dp),
                            strokeWidth = 6.dp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Cargando resultados...",
                            color = Color.White.copy(0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Log.d("TestResultSummary", "✅ Mostrando resultado: ${finalResult.recommendedRole}")
                val role = CyberRole.fromString(finalResult.recommendedRole)!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ... resto del código sin cambios
                    // Reemplaza todas las referencias a result!! con finalResult

                    Spacer(Modifier.height(16.dp))
                    RoleEmojiCard(role = role)
                    Spacer(Modifier.height(24.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Tu perfil ideal es:",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(0.7f),
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            role.displayName,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = Color(role.color),
                            fontSize = 36.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            role.description,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(0.9f),
                            fontSize = 17.sp,
                            lineHeight = 26.sp
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    ConfidenceCard(
                        confidence = finalResult.confidence,
                        color = Color(role.color)
                    )
                    Spacer(Modifier.height(24.dp))
                    TopDimensionsCard(topDimensions = finalResult.topDimensions)
                    Spacer(Modifier.height(40.dp))

                    // BOTONES...
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("test_recommendations") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .shadow(12.dp, RoundedCornerShape(20.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(role.color)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                modifier = Modifier.size(28.dp),
                                tint = Color.White
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Ver Certificaciones y Labs",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("test_skills") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF00D9FF)
                            ),
                            border = BorderStroke(2.dp, Color(0xFF00D9FF)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Star, null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Ver Skills Necesarias",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        TextButton(
                            onClick = {
                                viewModel.resetTest()
                                navController.navigate("test_questions") {
                                    popUpTo("test_result_summary") { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = Color(0xFF00D9FF))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Retomar Test",
                                color = Color(0xFF00D9FF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// RoleEmojiCard, ConfidenceCard, TopDimensionsCard sin cambios...

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
            .shadow(20.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(role.color)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = role.emoji,
            fontSize = 100.sp
        )
    }
}

@Composable
fun ConfidenceCard(confidence: Float, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        border = BorderStroke(3.dp, color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Nivel de Confianza:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    "${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = color,
                    fontSize = 36.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { confidence },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = color,
                trackColor = Color(0xFF1A2332)
            )
        }
    }
}

@Composable
fun TopDimensionsCard(topDimensions: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        ),
        border = BorderStroke(2.dp, Color(0xFF00D9FF).copy(0.5f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "🧩",
                    fontSize = 28.sp
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Tus Dimensiones Principales",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            topDimensions.take(3).forEach { dimension ->
                val (icon, displayName, color) = when(dimension) {
                    "INVESTIGATIVE" -> Triple("🔍", "Investigativo - Análisis", Color(0xFF3B82F6))
                    "REALISTIC" -> Triple("🛠️", "Realista - Técnico", Color(0xFF10B981))
                    "SOCIAL" -> Triple("🤝", "Social - Colaboración", Color(0xFFFBBF24))
                    "CONVENTIONAL" -> Triple("📋", "Convencional - Procesos", Color(0xFF8B5CF6))
                    "ENTERPRISING" -> Triple("🎯", "Emprendedor - Liderazgo", Color(0xFFEF4444))
                    "ARTISTIC" -> Triple("🎨", "Artístico - Creatividad", Color(0xFFEC4899))
                    else -> Triple("✓", dimension, Color(0xFF00D9FF))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = color.copy(0.15f)
                    ),
                    border = BorderStroke(1.5.dp, color),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = icon,
                            fontSize = 28.sp
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}