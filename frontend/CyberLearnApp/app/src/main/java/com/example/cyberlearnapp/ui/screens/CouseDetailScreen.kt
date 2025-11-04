package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cyberlearnapp.network.Lesson
import com.example.cyberlearnapp.network.RetrofitInstance
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    courseTitle: String,
    courseDescription: String,
    courseLevel: String,
    courseXp: Int,
    courseEmoji: String,
    token: String,
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    var lessons by remember { mutableStateOf<List<Lesson>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(courseId) {
        scope.launch {
            try {
                isLoading = true
                val response = RetrofitInstance.api.getCourseLessons(
                    courseId = courseId,
                    token = "Bearer $token"
                )
                if (response.isSuccessful && response.body() != null) {
                    lessons = response.body()!!.lessons
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error cargando lecciones"
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Curso", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A),
                            Color(0xFF3B82F6)
                        )
                    )
                )
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = courseEmoji,
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = courseTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = courseDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CourseInfoChip(icon = "📚", label = "Nivel: $courseLevel")
                        CourseInfoChip(icon = "⭐", label = "$courseXp XP")
                        CourseInfoChip(icon = "📖", label = "${lessons.size} lecciones")
                    }
                }
            }

            item {
                Text(
                    text = "📚 Lecciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            } else if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "❌ $errorMessage",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(lessons) { lesson ->
                    LessonItem(
                        lesson = lesson,
                        onClick = {
                            println("🔹 Lección clickeada: ${lesson.lesson_id} - ${lesson.title}")

                            // Detectar si es la lección interactiva
                            if (lesson.lesson_id == "phishing_anatomia_interactivo" ||
                                lesson.title.contains("Anatomía", ignoreCase = true) ||
                                lesson.title.contains("anatomia", ignoreCase = true)) {

                                println("🔹 Navegando a lección interactiva")

                                // Navegar a lección interactiva
                                navController.navigate(
                                    "interactive_lesson/phishing_anatomia_interactivo/${lesson.title}"
                                )
                            } else {
                                println("🔹 Navegando a lección normal")

                                // Navegar a lección normal
                                navController.navigate(
                                    "lesson/${lesson.lesson_id}/${lesson.title}"
                                )
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CourseInfoChip(icon: String, label: String) {
    Card(
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LessonItem(lesson: Lesson, onClick: () -> Unit) {
    // Badge especial para lección interactiva
    val isInteractive = lesson.lesson_id == "phishing_anatomia_interactivo" ||
            lesson.title.contains("Anatomía", ignoreCase = true) ||
            lesson.title.contains("anatomia", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ SIEMPRE número en círculo azul (para todas las lecciones)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(0xFF3B82F6), // Azul consistente
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${lesson.order}", // ✅ SIEMPRE el número de lección
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // ✅ Badge INTERACTIVA solo si es interactiva
                    if (isInteractive) {
                        Row(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "INTERACTIVA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5CF6)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        // Duración
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⏱️",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${lesson.duration_minutes} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // XP
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${lesson.xp_reward} XP",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // ✅ SIEMPRE triángulo azul (para todas las lecciones)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶", // ✅ Triángulo azul para todas
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF3B82F6) // Azul consistente
                    )
                }
            }
        }
    }
}