package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// --- IMPORTACIONES DE COLOR AÑADIDAS ---
import com.example.cyberlearnapp.ui.theme.AccentCyan
import com.example.cyberlearnapp.ui.theme.PrimaryDark
import com.example.cyberlearnapp.ui.theme.TextGray
import com.example.cyberlearnapp.ui.theme.TextWhite
// --- FIN DE IMPORTACIONES ---
import com.example.cyberlearnapp.ui.components.ProgressCard
import com.example.cyberlearnapp.ui.components.CourseCard
import com.example.cyberlearnapp.viewmodel.UserViewModel

@Composable
fun DashboardScreen(
    userViewModel: UserViewModel,
    onCourseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProgress by userViewModel.userProgress.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    // ✅ Cargar datos cuando se abre la pantalla
    LaunchedEffect(Unit) {
        println("🔹 Dashboard - Cargando progreso del usuario...")
        userViewModel.loadUserProgress()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryDark) // <-- Ahora funciona
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan) // <-- Ahora funciona
                    Text(
                        text = "Cargando...",
                        color = TextWhite, // <-- Ahora funciona
                        modifier = Modifier.padding(top = 80.dp)
                    )
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "❌ Error",
                            color = TextWhite, // <-- Ahora funciona
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = TextGray, // <-- Ahora funciona
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { userViewModel.loadUserProgress() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            userProgress != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "CyberLearn",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextWhite, // <-- Ahora funciona
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Aprende. Hackea. Protege.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentCyan, // <-- Ahora funciona
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    ProgressCard(
                        progress = userProgress!!,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "Continúa aprendiendo",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite, // <-- Ahora funciona
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // --- ESTA ES LA SECCIÓN PROBLEMÁTICA ---
                    // Esta pantalla (Dashboard) no debería saber de Cursos
                    // Debería ser la pantalla "CoursesScreen" la que
                    // llame al CourseViewModel.
                    // Por ahora, lo dejamos como estaba en tu código:
                    CourseCard(
                        emoji = "🚀",
                        title = "Fundamentos de Ciberseguridad",
                        description = "El curso obligatorio para iniciar",
                        level = "Principiante",
                        xp = 150,
                        progress = 0, // El progreso debe venir de UserViewModel
                        onCourseClick = { onCourseClick("Fundamentos de Ciberseguridad") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Este es otro curso estático, debería venir de la API
                    // a través de CoursesScreen.
                    CourseCard(
                        emoji = "🎣",
                        title = "Phishing e Ingeniería Social",
                        description = "Detecta correos falsos",
                        level = "Principiante",
                        xp = 35,
                        progress = 0,
                        onCourseClick = { onCourseClick("Phishing e Ingeniería Social") }
                    )
                    // --- FIN DE LA SECCIÓN PROBLEMÁTICA ---


                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            else -> {
                // Estado inicial - mostrar algo mientras carga
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan) // <-- Ahora funciona
                }
            }
        }
    }
}