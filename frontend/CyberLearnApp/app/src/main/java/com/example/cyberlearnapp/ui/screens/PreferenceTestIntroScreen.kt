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
import androidx.navigation.NavController
import com.example.cyberlearnapp.navigation.Screens
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceTestIntroScreen(
    navController: NavController
) {
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
        // Rotación infinita
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
                    // Anillo exterior rotando
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(rotation.value / 360f * 0.2f + 0.9f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "🎯",
                            fontSize = 80.sp
                        )
                    }
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
                "Responde 20 preguntas para descubrir si eres más Red Team, Blue Team o Purple Team",
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
                color = Color(0xFFE53935),
                gradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFE53935).copy(0.3f),
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
                color = Color(0xFF1E88E5),
                gradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1E88E5).copy(0.3f),
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
                color = Color(0xFF8E24AA),
                gradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF8E24AA).copy(0.3f),
                        Color.Transparent
                    )
                )
            )

            Spacer(Modifier.height(48.dp))

            // BOTÓN COMENZAR
            Button(
                onClick = { navController.navigate(Screens.PreferenceTest.route) },
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
                // ICONO
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

                // CONTENIDO
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

                    // TAGS
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