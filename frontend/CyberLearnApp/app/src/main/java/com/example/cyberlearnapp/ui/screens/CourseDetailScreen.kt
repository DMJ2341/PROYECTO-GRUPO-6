package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.viewmodel.CourseViewModel

@Composable
fun CourseDetailScreen(
    courseId: Int,
    navController: NavController,
    viewModel: CourseViewModel = hiltViewModel()
) {
    LaunchedEffect(courseId) {
        viewModel.loadCourseDetails(courseId)
    }

    val state by viewModel.state.collectAsState()
    val course = state.selectedCourse
    val lessons = state.lessons

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    if (course == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se pudo cargar el curso")
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Cabecera
        item {
            Text(course.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(course.description, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Spacer(Modifier.height(24.dp))

            // Progreso
            val completedCount = lessons.count { it.isCompleted }
            val progress = if (lessons.isNotEmpty()) completedCount.toFloat() / lessons.size else 0f

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progreso", fontWeight = FontWeight.Bold)
                Text("$completedCount / ${lessons.size} lecciones")
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(32.dp))
        }

        // Lista de Lecciones
        items(lessons.sortedBy { it.orderIndex }) { lesson ->
            val isUnlocked = !lesson.isLocked || lesson.isCompleted

            ListItem(
                headlineContent = {
                    Text(lesson.title, fontWeight = if (lesson.isCompleted) FontWeight.Bold else FontWeight.Normal)
                },
                supportingContent = {
                    Text("+${lesson.xpReward} XP • ${lesson.durationMinutes} min", color = MaterialTheme.colorScheme.primary)
                },
                leadingContent = {
                    Icon(
                        imageVector = when {
                            lesson.isCompleted -> Icons.Default.CheckCircle
                            isUnlocked -> Icons.Default.PlayArrow
                            else -> Icons.Default.Lock
                        },
                        contentDescription = null,
                        tint = if (lesson.isCompleted) Color(0xFF4CAF50) else if (isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isUnlocked) {
                        navController.navigate("lesson/${lesson.id}")
                    }
                    .alpha(if (isUnlocked) 1f else 0.5f)
            )
            HorizontalDivider()
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}