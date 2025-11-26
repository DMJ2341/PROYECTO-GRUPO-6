package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.* // ✅ IMPORTANTE: Incluye getValue, setValue, collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.screens.lessons.LessonScreenRender
import com.example.cyberlearnapp.viewmodel.LessonViewModel
// No necesitamos importar 'Lesson' aquí porque usamos 'LessonResponse' que viene del VM

@Composable
fun LessonDetailScreen(
    navController: NavController,
    viewModel: LessonViewModel = hiltViewModel(),
    lessonId: String
) {
    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    // Ahora 'by' funcionará porque importamos androidx.compose.runtime.*
    val lessonResponse by viewModel.lesson.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var currentScreenIndex by remember { mutableIntStateOf(0) }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    if (error?.contains("403") == true || error?.contains("bloqueada") == true) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Lección Bloqueada", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Debes completar las lecciones anteriores.", textAlign = TextAlign.Center)
                    } else {
                        Text(text = error ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            } else if (lessonResponse != null) {
                // Obtenemos el total de pantallas real desde la respuesta
                val totalScreens = lessonResponse!!.screens.size

                LessonScreenRender(
                    lesson = lessonResponse!!, // Pasamos LessonResponse (coincide con el cambio en Render)
                    screenIndex = currentScreenIndex,
                    onNext = {
                        if (currentScreenIndex < totalScreens - 1) {
                            currentScreenIndex++
                        } else {
                            // Fin de la lección
                            viewModel.completeLesson(lessonId)
                            navController.popBackStack()
                        }
                    },
                    onPrev = {
                        if (currentScreenIndex > 0) currentScreenIndex--
                    },
                    isLastScreen = currentScreenIndex == totalScreens - 1
                )
            }
        }
    }
}