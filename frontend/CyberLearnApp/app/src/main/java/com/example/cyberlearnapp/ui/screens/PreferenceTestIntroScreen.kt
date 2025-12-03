package com.example.cyberlearnapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.cyberlearnapp.utils.AuthManager
import com.example.cyberlearnapp.viewmodel.TestViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PreferenceTestIntroScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel()
) {
    val currentResult by viewModel.currentResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val token = AuthManager.getToken() ?: ""

    // ✅ Estado para controlar si ya cargó el resultado
    var hasCheckedResult by remember { mutableStateOf(false) }

    // ✅ Verificar resultado previo al cargar
    LaunchedEffect(Unit) {
        viewModel.checkExistingResult(token)
        hasCheckedResult = true
    }

    // Animación del icono central
    val scale = remember { Animatable(0.5f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        while (true) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
            rotation.snapTo(0f)
        }
    }

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
        // ✅ MOSTRAR LOADING MIENTRAS CARGA EL RESULTADO
        if (!hasCheckedResult || (isLoading && currentResult == null)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF00D9FF),
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HEADER CON BOTÓN DE REGRESO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF1A2332),
                            contentColor = Color(0xFF00D9FF)
                        )
                    ) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ✅ VERIFICAR SI HAY RESULTADO PREVIO
                if (currentResult != null) {
                    // ========================================
                    // MODO: USUARIO CON RESULTADO PREVIO
                    // ========================================
                    PreviousResultView(
                        result = currentResult!!,
                        onViewDetails = {
                            navController.navigate("test_result_summary")
                        },
                        onRetakeTest = {
                            viewModel.resetTest()
                            navController.navigate("test_questions")
                        }
                    )
                } else {
                    // ========================================
                    // MODO: NUEVO USUARIO (INTRO NORMAL)
                    // ========================================

                    // ICONO CENTRAL ANIMADO
                    Card(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(scale.value)
                            .shadow(20.dp, CircleShape),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF8B5CF6)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "🎯",
                                fontSize = 80.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    // TÍTULO PRINCIPAL
                    Text(
                        "¿Cuál es tu camino?",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // SUBTÍTULO
                    Text(
                        "Responde 28 preguntas para descubrir si eres más Red Team, Blue Team o Purple Team",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(0.7f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(48.dp))

                    // SECCIÓN: ESPECIALIDADES
                    Text(
                        "Especialidades",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    // RED TEAM CARD
                    TeamCard(
                        emoji = "⚔️",
                        title = "Red Team",
                        description = "Especialistas en ataque y pentesting. Simulan amenazas para encontrar vulnerabilidades.",
                        tags = listOf("Pentesting", "Hacking Ético", "Exploits"),
                        color = Color(0xFFEF4444),
                        gradient = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFEF4444).copy(0.3f),
                                Color.Transparent
                            )
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // BLUE TEAM CARD
                    TeamCard(
                        emoji = "🛡️",
                        title = "Blue Team",
                        description = "Especialistas en defensa y protección. Monitorean y responden a incidentes de seguridad.",
                        tags = listOf("SOC", "Forense", "Monitoreo"),
                        color = Color(0xFF3B82F6),
                        gradient = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(0.3f),
                                Color.Transparent
                            )
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // PURPLE TEAM CARD
                    TeamCard(
                        emoji = "🔮",
                        title = "Purple Team",
                        description = "Lo mejor de ambos mundos. Combinan habilidades de ataque y defensa para mejorar la seguridad.",
                        tags = listOf("Híbrido", "Estrategia", "Colaboración"),
                        color = Color(0xFF8B5CF6),
                        gradient = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6).copy(0.3f),
                                Color.Transparent
                            )
                        )
                    )

                    Spacer(Modifier.height(48.dp))

                    // BOTÓN COMENZAR
                    Button(
                        onClick = { navController.navigate("test_questions") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .shadow(16.dp, RoundedCornerShape(20.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00D9FF)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🚀", fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Comenzar Test Vocacional",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF0F1419)
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ✅ VISTA DE RESULTADO PREVIO (SIN CAMBIOS)
@Composable
fun PreviousResultView(
    result: com.example.cyberlearnapp.models.TestResult,
    onViewDetails: () -> Unit,
    onRetakeTest: () -> Unit
) {
    val role = CyberRole.fromString(result.recommendedRole)!!

    // Formatear fecha
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
    val date = try {
        val parsedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(result.createdAt)
        dateFormat.format(parsedDate ?: Date())
    } catch (e: Exception) {
        "Fecha desconocida"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TÍTULO
        Text(
            "Tu Perfil Actual",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Color.White,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        // TARJETA DE RESULTADO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color(role.color).copy(0.2f)
            ),
            border = BorderStroke(4.dp, Color(role.color)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(role.color).copy(0.3f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // EMOJI DEL ROL
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(16.dp, CircleShape)
                            .background(Color(role.color), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            role.emoji,
                            fontSize = 72.sp
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    // NOMBRE DEL ROL
                    Text(
                        role.displayName,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    // DESCRIPCIÓN
                    Text(
                        role.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(0.9f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    // CONFIANZA Y FECHA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${(result.confidence * 100).toInt()}%",
                                fontWeight = FontWeight.Black,
                                color = Color(role.color),
                                fontSize = 32.sp
                            )
                            Text(
                                "Confianza",
                                color = Color.White.copy(0.7f),
                                fontSize = 14.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "📅",
                                fontSize = 32.sp
                            )
                            Text(
                                date,
                                color = Color.White.copy(0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        // BOTONES DE ACCIÓN
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // BOTÓN: VER DETALLES
            Button(
                onClick = onViewDetails,
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
                    Icons.Default.Visibility,
                    null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Ver Detalles Completos",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            // BOTÓN: VOLVER A HACER
            OutlinedButton(
                onClick = onRetakeTest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF00D9FF)
                ),
                border = BorderStroke(2.dp, Color(0xFF00D9FF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Volver a Hacer el Test",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// TeamCard sin cambios...
@Composable
fun TeamCard(
    emoji: String,
    title: String,
    description: String,
    tags: List<String>,
    color: Color,
    gradient: Brush
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(0.15f)
        ),
        border = BorderStroke(2.dp, color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.Top
            ) {
                Card(
                    modifier = Modifier.size(64.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = color
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 36.sp)
                    }
                }

                Spacer(Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 22.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        description,
                        color = Color.White.copy(0.85f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tags.forEach { tag ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = color.copy(0.3f)
                                ),
                                border = BorderStroke(1.dp, color),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    tag,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}