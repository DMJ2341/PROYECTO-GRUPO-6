package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // ✅ IMPORTANTE: Necesario para usar items(list)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.* // ✅ IMPORTANTE: Incluye getValue, setValue, collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cyberlearnapp.viewmodel.CourseViewModel
import com.example.cyberlearnapp.network.models.Lesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    navController: NavController,
    viewModel: CourseViewModel,
    courseId: Int
) {
    // 1. Cargar lecciones al entrar a la pantalla
    LaunchedEffect(courseId) {
        viewModel.loadLessons(courseId)
    }

    // 2. Observar el estado (Ahora funcionará gracias a los imports de runtime.*)
    val lessons by viewModel.lessons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val courses by viewModel.courses.collectAsState()

    // Buscamos el curso actual para el título
    val currentCourse = courses.find { it.id == courseId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentCourse?.title ?: "Detalle del Curso") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                // Mensaje de error
                Text(
                    text = error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (lessons.isEmpty()) {
                // Mensaje vacío
                Text(
                    text = "No hay lecciones disponibles.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // 3. Renderizar la lista
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        if (!currentCourse?.description.isNullOrEmpty()) {
                            Text(
                                text = currentCourse?.description ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            // Usamos HorizontalDivider en Material3
                            HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                        }
                        Text(
                            text = "Lecciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // ✅ Ahora 'items' reconocerá la lista de lecciones correctamente
                    items(lessons) { lesson ->
                        LessonItem(lesson = lesson, onClick = {
                            navController.navigate("lesson_detail/${lesson.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun LessonItem(lesson: Lesson, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de estado
            Icon(
                imageVector = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (lesson.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "${lesson.orderIndex}. ${lesson.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${lesson.durationMinutes} min • ${lesson.xpReward} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}