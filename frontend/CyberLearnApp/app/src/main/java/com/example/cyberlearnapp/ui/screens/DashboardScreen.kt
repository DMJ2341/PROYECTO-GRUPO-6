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
import com.example.cyberlearnapp.ui.theme.*
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
            .background(PrimaryDark)
    ) {
        when {
            isLoading && userProgress == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan)
                    Text(
                        text = "Cargando...",
                        color = TextWhite,
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
                            color = TextWhite,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = TextGray,
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
                // ✅ CONTENIDO PRINCIPAL - Ya tenemos datos
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "CyberLearn",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Aprende. Hackea. Protege.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentCyan,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    ProgressCard(
                        progress = com.example.cyberlearnapp.network.models.Progress(
                            name = userProgress?.userName ?: "Estudiante",
                            email = userProgress?.userEmail ?: "",
                            level = userProgress?.level ?: 1,
                            xpTotal = userProgress?.totalXp ?: 0,
                            streak = userProgress?.currentStreak ?: 0,
                            badges = emptyList(), // Por ahora vacío
                            lessonsCompleted = userProgress?.completedLessons ?: 0,
                            coursesCompleted = userProgress?.completedCourses ?: 0,
                            nextLevelXp = userProgress?.nextLevelXp ?: 100,
                            progressPercentage = userProgress?.progressPercentage ?: 0.0
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "Continúa aprendiendo",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val courses = listOf(
                        CourseData(
                            id = "fundamentos",
                            emoji = "🚀",
                            title = "Fundamentos de Ciberseguridad",
                            description = "El curso obligatorio para iniciar",
                            level = "Principiante",
                            xp = 150,
                            progress = 0 //userProgress?.courseProgress?.get("fundamentos") ?: 0
                        ),
                        CourseData(
                            id = "phishing",
                            emoji = "🎣",
                            title = "Phishing e Ingeniería Social",
                            description = "Detecta correos falsos",
                            level = "Principiante",
                            xp = 35,
                            progress = 0 //userProgress?.courseProgress?.get("phishing") ?: 0
                        )
                    )

                    courses.forEach { course ->
                        CourseCard(
                            emoji = course.emoji,
                            title = course.title,
                            description = course.description,
                            level = course.level,
                            xp = course.xp,
                            progress = course.progress,
                            onCourseClick = { onCourseClick(course.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            else -> {
                // Estado inicial - mostrar algo mientras carga (sin texto)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            }
        }
    }
}

// Data class para cursos
data class CourseData(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val level: String,
    val xp: Int,
    val progress: Int
)