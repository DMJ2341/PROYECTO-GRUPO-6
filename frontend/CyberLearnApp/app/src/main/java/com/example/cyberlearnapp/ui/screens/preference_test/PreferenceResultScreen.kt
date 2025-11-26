// app/src/main/java/com/example/cyberlearnapp/ui/screens/preference/PreferenceResultScreen.kt
package com.example.cyberlearnapp.ui.screens.preference_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.cyberlearnapp.network.models.assessments.Section
import com.example.cyberlearnapp.viewmodel.PreferenceResultViewModel

@Composable
fun PreferenceResultScreen(
    profileType: String,
    navController: NavController,
    viewModel: PreferenceResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Lottie Confetti
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.example.cyberlearnapp.R.raw.confetti))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1 // Solo una vez
    )

    LaunchedEffect(profileType) {
        viewModel.loadResult(profileType)
    }

    val uiData = state.uiData

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    if (uiData == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay datos para mostrar") }
        return
    }

    val primaryColor = parseColor(uiData.theme.primary)
    val secondaryColor = parseColor(uiData.theme.secondary)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(primaryColor, secondaryColor)))
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Animación Lottie encima del header
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    )

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "¡Tu perfil es:",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = state.profileName.uppercase(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        // Primer sección suele ser la Hero Card
                        uiData.sections.firstOrNull()?.let { hero ->
                            Text(
                                text = hero.subtitle ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = hero.description ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Renderizado dinámico de secciones
            items(uiData.sections.size) { index ->
                val section = uiData.sections[index]
                // Saltamos la primera si ya la mostramos arriba como Hero
                if (index > 0) {
                    when (section.type) {
                        "why_this_result" -> ResultCard("¿Por qué este resultado?") { WhySection(section) }
                        "skills_required" -> ResultCard("Skills Necesarias") { SkillsSection(section) }
                        "tools" -> ResultCard("Herramientas") { ToolsSection(section) }
                        "certifications" -> ResultCard("Certificaciones") { CertsSection(section) }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Botón flotante inferior
        Button(
            onClick = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Ir al Dashboard", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun ResultCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun WhySection(section: Section) {
    section.bullets?.forEach { bullet ->
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = bullet, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
        }
    }
}

@Composable
fun SkillsSection(section: Section) {
    section.categories?.forEach { cat ->
        Text(text = cat.label, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            cat.skills?.forEach { skill ->
                SuggestionChip(onClick = {}, label = { Text(skill) }, modifier = Modifier.padding(end = 4.dp))
            }
        }
    }
}

@Composable
fun ToolsSection(section: Section) {
    section.toolCategories?.forEach { cat ->
        Text(text = cat.category, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Text(text = cat.tools.joinToString(", "), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
    }
}

@Composable
fun CertsSection(section: Section) {
    section.roadmap?.forEach { level ->
        Text(text = level.level, style = MaterialTheme.typography.labelLarge, color = Color(0xFF6200EA))
        level.certs.forEach { cert ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = cert.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// Utilidad para convertir Hex String (#RRGGBB) a Color
fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}