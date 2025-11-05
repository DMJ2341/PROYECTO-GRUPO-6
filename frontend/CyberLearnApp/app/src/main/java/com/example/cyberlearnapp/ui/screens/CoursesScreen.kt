package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cyberlearnapp.ui.components.CourseCard
import com.example.cyberlearnapp.viewmodel.CourseViewModel

@Composable
fun CoursesScreen(
    courseViewModel: CourseViewModel,
    onCourseClick: (String, String, String, String, Int, String) -> Unit
) {
    // Observa el estado del ViewModel
    val uiState by courseViewModel.uiState.collectAsState()

    // Llama a la API solo una vez cuando la pantalla aparece
    LaunchedEffect(Unit) {
        courseViewModel.loadAllCourses()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Cursos Disponibles",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Manejo de estados (Cargando, Error, Éxito)
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                // Estado de éxito: Muestra la lista de cursos de la API
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.courseList) { course ->
                        CourseCard(
                            emoji = course.image_url,
                            title = course.title,
                            description = course.description,
                            level = course.level,
                            xp = course.xp_reward,
                            progress = 0, // El progreso real vendría del UserViewModel
                            onCourseClick = {
                                onCourseClick(
                                    course.id,
                                    course.title,
                                    course.description,
                                    course.level,
                                    course.xp_reward,
                                    course.image_url
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}