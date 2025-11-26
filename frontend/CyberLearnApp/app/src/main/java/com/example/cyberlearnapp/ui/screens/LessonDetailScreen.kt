package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.screens.lessons.LessonScreenRender
import com.example.cyberlearnapp.viewmodel.LessonViewModel

@Composable
fun LessonDetailScreen(
    lessonId: String,
    navController: NavController,
    viewModel: LessonViewModel = hiltViewModel()
) {
    LaunchedEffect(lessonId) { viewModel.loadLesson(lessonId) }
    val lessonState by viewModel.lesson.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var currentScreenIndex by remember { mutableIntStateOf(0) }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val lesson = lessonState
    if (lesson == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No se pudo cargar la lección")
            Button(onClick = { viewModel.loadLesson(lessonId) }) { Text("Reintentar") }
        }
        return
    }

    val screens = lesson.screens
    val currentScreenData = screens.getOrNull(currentScreenIndex)
    val progress = if (screens.isNotEmpty()) (currentScreenIndex + 1).toFloat() / screens.size else 0f

    Scaffold(
        topBar = {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp))
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (currentScreenIndex > 0) {
                        OutlinedButton(onClick = { currentScreenIndex-- }) { Text("Anterior") }
                    } else { Spacer(Modifier.width(1.dp)) }

                    Button(onClick = {
                        if (currentScreenIndex < screens.size - 1) currentScreenIndex++
                        else {
                            viewModel.completeLesson(lessonId)
                            navController.popBackStack()
                        }
                    }) {
                        Text(if (currentScreenIndex < screens.size - 1) "Siguiente" else "Finalizar")
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (currentScreenData != null) {
                LessonScreenRender(screenData = currentScreenData, onQuizAnswer = {})
            }
        }
    }
}