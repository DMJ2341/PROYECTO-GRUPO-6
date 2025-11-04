package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cyberlearnapp.ui.theme.*
import com.example.cyberlearnapp.ui.components.CourseCard

@Composable
fun CoursesScreen(
    onCourseClick: (String, String, String, String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Datos de cursos actualizados con los que están en el backend
    val courses = listOf(
        CourseItem(
            id = "fundamentos_ciberseguridad",
            title = "Fundamentos de Ciberseguridad",
            description = "Aprende los conceptos básicos de ciberseguridad",
            progress = 0f,
            level = "Principiante",
            xpReward = 155,
            lessonsTotal = 6,
            lessonsCompleted = 0,
            image = "🛡️"
        ),
        CourseItem(
            id = "phishing_ingenieria_social",
            title = "Phishing e Ingeniería Social",
            description = "Identifica y protégete de ataques de phishing",
            progress = 0f,
            level = "Principiante",
            xpReward = 185,
            lessonsTotal = 7,
            lessonsCompleted = 0,
            image = "🎣"
        ),
        CourseItem(
            id = "malware_ransomware",
            title = "Malware y Ransomware",
            description = "Comprende y prevén infecciones de malware",
            progress = 0f,
            level = "Intermedio",
            xpReward = 150,
            lessonsTotal = 6,
            lessonsCompleted = 0,
            image = "🦠",
            locked = true
        ),
        CourseItem(
            id = "seguridad_redes_wifi",
            title = "Seguridad en Redes y WiFi",
            description = "Protege tus conexiones y datos en redes",
            progress = 0f,
            level = "Intermedio",
            xpReward = 140,
            lessonsTotal = 6,
            lessonsCompleted = 0,
            image = "📡",
            locked = true
        ),
        CourseItem(
            id = "criptografia_usuarios",
            title = "Criptografía para Usuarios",
            description = "Protege tu información con criptografía",
            progress = 0f,
            level = "Intermedio",
            xpReward = 125,
            lessonsTotal = 5,
            lessonsCompleted = 0,
            image = "🔐",
            locked = true
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Cursos Disponibles",
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Domina la ciberseguridad paso a paso",
                style = MaterialTheme.typography.bodyMedium,
                color = AccentCyan,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Cursos en progreso
            val coursesInProgress = courses.filter { it.progress > 0 }
            if (coursesInProgress.isNotEmpty()) {
                Text(
                    text = "En progreso",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextWhite,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                coursesInProgress.forEach { course ->
                    CourseCard(
                        title = course.title,
                        description = course.description,
                        progress = course.progress,
                        onClick = {
                            onCourseClick(
                                course.id,
                                course.title,
                                course.description,
                                course.level,
                                course.xpReward,
                                course.image
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Cursos disponibles
            Text(
                text = "Disponibles",
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )

            courses.filter { it.progress == 0f && !it.locked }.forEach { course ->
                CourseCard(
                    title = course.title,
                    description = course.description,
                    progress = course.progress,
                    onClick = {
                        onCourseClick(
                            course.id,
                            course.title,
                            course.description,
                            course.level,
                            course.xpReward,
                            course.image
                        )
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Cursos bloqueados
            val lockedCourses = courses.filter { it.locked }
            if (lockedCourses.isNotEmpty()) {
                Text(
                    text = "Próximamente",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextGray,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )

                lockedCourses.forEach { course ->
                    CourseCard(
                        title = course.title,
                        description = course.description,
                        progress = course.progress,
                        onClick = { }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

data class CourseItem(
    val id: String,
    val title: String,
    val description: String,
    val progress: Float,
    val level: String,
    val xpReward: Int,
    val lessonsTotal: Int,
    val lessonsCompleted: Int,
    val image: String,
    val locked: Boolean = false
)