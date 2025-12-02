package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.screens.lessons.LessonScreenRender
import com.example.cyberlearnapp.viewmodel.LessonViewModel
import com.example.cyberlearnapp.ui.theme.SuccessGreen

@Composable
fun LessonDetailScreen(
    navController: NavController,
    viewModel: LessonViewModel = hiltViewModel(),
    lessonId: String
) {
    // Cargar lección al entrar
    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    // ✅ CAMBIO: lessonResponse ahora es LessonDetailResponse
    val lessonResponse by viewModel.lesson.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val completionResult by viewModel.completionResult.collectAsState()

    var currentScreenIndex by remember { mutableIntStateOf(0) }

    // Dialog de lección completada
    if (completionResult != null) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "¡Lección Completada!",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "¡Excelente trabajo!",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "+${completionResult?.xp_earned ?: 0} XP",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    completionResult?.course_progress?.let { progress ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Progreso del curso: ${progress.percentage}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // ✅ ELIMINADO: Ya no mostramos new_badges porque no existe en el modelo
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetCompletionResult()
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("refresh_dashboard", true)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
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
            when {
                // Estado de carga
                isLoading && lessonResponse == null -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                // Estado de error
                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Acceso Bloqueado",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            error ?: "Error desconocido",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                viewModel.clearError()
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Volver al Dashboard")
                        }
                    }
                }

                // ✅ Estado de contenido cargado
                lessonResponse != null -> {
                    val lesson = lessonResponse!!
                    val totalScreens = lesson.totalScreens

                    LessonScreenRender(
                        lesson = lesson,
                        screenIndex = currentScreenIndex,
                        onNext = {
                            if (currentScreenIndex < totalScreens - 1) {
                                currentScreenIndex++
                            } else {
                                // Última pantalla: completar lección
                                if (completionResult == null) {
                                    viewModel.completeLesson(lessonId)
                                }
                            }
                        },
                        onPrev = {
                            if (currentScreenIndex > 0) {
                                currentScreenIndex--
                            }
                        },
                        isLastScreen = currentScreenIndex == totalScreens - 1
                    )
                }
            }
        }
    }
}