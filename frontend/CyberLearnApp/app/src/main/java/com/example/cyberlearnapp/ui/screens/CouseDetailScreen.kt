package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cyberlearnapp.network.Lesson
import com.example.cyberlearnapp.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseViewModel: CourseViewModel,
    courseId: String,
    courseTitle: String,
    courseDescription: String,
    courseLevel: String,
    courseXp: Int,
    courseEmoji: String,
    token: String, // Mantenemos el token para la navegación a lecciones
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    // Observa el estado del ViewModel
    val uiState by courseViewModel.uiState.collectAsState()

    // Llama a la API para cargar las lecciones de ESTE curso
    LaunchedEffect(courseId) {
        courseViewModel.loadLessonsForCourse(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(courseTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Cabecera del Curso
            item {
                CourseHeader(
                    emoji = courseEmoji,
                    title = courseTitle,
                    description = courseDescription,
                    level = courseLevel,
                    xp = courseXp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Lecciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Manejo de estados de carga de lecciones
            when {
                uiState.isLoading -> {
                    item {
                        Box(modifier = Modifier.fillParentMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                uiState.error != null -> {
                    item {
                        Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {
                    // Estado de éxito: Muestra la lista de lecciones de la API
                    items(uiState.lessonList) { lesson ->
                        LessonItem(
                            lesson = lesson,
                            isLocked = false, // Aquí iría la lógica de progreso del usuario
                            onClick = {
                                // Navegar a la lección correcta
                                // (Aquí asumo que tu app.py decide si es interactiva por el ID)
                                // O podrías añadir "lesson_type" a la API de getCourseLessons

                                // Esta es la lección interactiva
                                if (lesson.id == "phishing_anatomia_interactivo") {
                                    navController.navigate("interactive_lesson/${lesson.id}/${lesson.title}")
                                } else {
                                    // Para futuras lecciones de texto
                                    navController.navigate("lesson/${lesson.id}/${lesson.title}")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CourseHeader(emoji: String, title: String, description: String, level: String, xp: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(description, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            InfoChip(text = level)
            InfoChip(text = "$xp XP")
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LessonItem(
    lesson: Lesson,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick, enabled = !isLocked),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLocked) Color.Gray.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Filled.Lock else Icons.Filled.PlayCircle,
                    contentDescription = null,
                    tint = if (isLocked) Color.Gray else MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${lesson.duration_minutes} min • ${lesson.xp_reward} XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}