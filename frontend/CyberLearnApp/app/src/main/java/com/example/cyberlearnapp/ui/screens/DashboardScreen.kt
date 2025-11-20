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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.theme.*
import com.example.cyberlearnapp.ui.components.ProgressCard
import com.example.cyberlearnapp.ui.components.CourseCard
import com.example.cyberlearnapp.viewmodel.UserViewModel

@Composable
fun DashboardScreen(
    navController: NavController,  // ✅ AGREGADO
    userViewModel: UserViewModel = hiltViewModel(),  // ✅ Inyectado con Hilt
    modifier: Modifier = Modifier
) {
    val userProgress by userViewModel.userProgress.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    // ✅ Cargar datos cuando se abre la pantalla
    LaunchedEffect(Unit) {
        println("🔹 Dashboard - Cargando progreso del usuario...")
        userViewModel.loadUserDashboard()  // ✅ Usar loadUserDashboard para cargar todo
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AccentCyan)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando...",
                            color = TextWhite
                        )
                    }
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { userViewModel.loadUserDashboard() }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            userProgress != null -> {
                // ✅ CONTENIDO PRINCIPAL
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header
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

                    // Progress Card
                    ProgressCard(
                        progress = com.example.cyberlearnapp.network.models.Progress(
                            name = userProgress?.userName ?: "Estudiante",
                            email = userProgress?.userEmail ?: "",
                            level = userProgress?.level ?: 1,
                            xpTotal = userProgress?.totalXp ?: 0,
                            streak = userProgress?.currentStreak ?: 0,
                            badges = emptyList(),
                            lessonsCompleted = userProgress?.completedLessons ?: 0,
                            coursesCompleted = userProgress?.completedCourses ?: 0,
                            nextLevelXp = userProgress?.nextLevelXp ?: 100,
                            progressPercentage = userProgress?.progressPercentage ?: 0.0
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Section Title
                    Text(
                        text = "Continúa aprendiendo",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // ✅ CURSOS DINÁMICOS desde coursesProgress
                    val coursesProgress = userProgress?.coursesProgress ?: emptyList()

                    if (coursesProgress.isNotEmpty()) {
                        coursesProgress.forEach { courseProgress ->
                            CourseCard(
                                emoji = getCourseEmoji(courseProgress.courseId),
                                title = courseProgress.courseTitle,
                                description = "Progreso: ${courseProgress.progressPercent.toInt()}%",
                                level = getCourseLevel(courseProgress.courseId),
                                xp = 0, // Puedes calcular XP restante si lo necesitas
                                progress = courseProgress.progressPercent.toInt(),
                                onCourseClick = {
                                    println("🎯 [DASHBOARD] Click en curso: ${courseProgress.courseId}")
                                    navController.navigate("course_detail/${courseProgress.courseId}")
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    } else {
                        // ✅ FALLBACK: Cursos hardcodeados si no hay progreso
                        val defaultCourses = listOf(
                            CourseData(
                                id = 1,
                                emoji = "🚀",
                                title = "Fundamentos de Ciberseguridad",
                                description = "El curso obligatorio para iniciar",
                                level = "Principiante",
                                xp = 150,
                                progress = 0
                            )
                        )

                        defaultCourses.forEach { course ->
                            CourseCard(
                                emoji = course.emoji,
                                title = course.title,
                                description = course.description,
                                level = course.level,
                                xp = course.xp,
                                progress = course.progress,
                                onCourseClick = {
                                    println("🎯 [DASHBOARD] Click en curso: ${course.id}")
                                    navController.navigate("course_detail/${course.id}")
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            else -> {
                // Estado inicial
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

// ✅ Funciones helper para mapear courseId a emoji y nivel
private fun getCourseEmoji(courseId: Int): String {
    return when (courseId) {
        1 -> "🚀"
        2 -> "🌐"
        3 -> "💻"
        4 -> "☁️"
        5 -> "🕵️"
        else -> "📚"
    }
}

private fun getCourseLevel(courseId: Int): String {
    return when (courseId) {
        1 -> "Principiante"
        2 -> "Intermedio"
        3 -> "Intermedio"
        4 -> "Avanzado"
        5 -> "Avanzado"
        else -> "Principiante"
    }
}

// ✅ Data class corregida con ID Int
data class CourseData(
    val id: Int,  // ✅ Cambiado de String a Int
    val emoji: String,
    val title: String,
    val description: String,
    val level: String,
    val xp: Int,
    val progress: Int
)