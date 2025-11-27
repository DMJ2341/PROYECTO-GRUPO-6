package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.screens.lessons.LessonScreenRender
import com.example.cyberlearnapp.viewmodel.LessonViewModel

// NUEVAS IMPORTACIONES CLAVE PARA SCROLLING
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

// ✅ IMPORTACIÓN CLAVE PARA EL COLOR DE ÉXITO
import com.example.cyberlearnapp.ui.theme.SuccessGreen


@Composable
fun LessonDetailScreen(
    navController: NavController,
    viewModel: LessonViewModel = hiltViewModel(),
    lessonId: String
) {
    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    val lessonResponse by viewModel.lesson.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val completionResult by viewModel.completionResult.collectAsState()

    var currentScreenIndex by remember { mutableIntStateOf(0) }

    // ✅ Diálogo de Victoria / XP
    if (completionResult != null) {
        AlertDialog(
            onDismissRequest = { },
            // ❌ CORRECCIÓN: Usamos SuccessGreen en lugar de MaterialTheme.colorScheme.success
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(48.dp)) },
            title = { Text("¡Lección Completada!", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("¡Excelente trabajo!", color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "+${completionResult?.xp_earned} XP",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Continuar", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && lessonResponse == null) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else if (error != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
                    Text("Acceso Bloqueado", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    Text("Completa las lecciones anteriores primero.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) { Text("Volver") }
                }
            } else if (lessonResponse != null) {
                val totalScreens = lessonResponse!!.screens.size

                // MODIFICACIÓN CLAVE: Envuelve LessonScreenRender en un Column con verticalScroll
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // <<-- SOLUCIÓN AL TEXTO CORTADO
                ) {
                    LessonScreenRender(
                        lesson = lessonResponse!!,
                        screenIndex = currentScreenIndex,
                        onNext = {
                            if (currentScreenIndex < totalScreens - 1) {
                                currentScreenIndex++
                            } else {
                                if (completionResult == null) {
                                    viewModel.completeLesson(lessonId)
                                }
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
}