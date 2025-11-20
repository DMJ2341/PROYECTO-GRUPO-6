package com.example.cyberlearnapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.ui.screens.lessons.fundamentos.*
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Router principal para lecciones interactivas
 * Carga la lección correspondiente según el ID
 */
@Composable
fun InteractiveLessonScreen(
    lessonId: Int,
    onComplete: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    when (lessonId) {
        // CURSO 1: Fundamentos de Ciberseguridad
        1 -> Leccion01Screen(lessonId, onComplete, viewModel)
        2 -> Leccion02Screen(lessonId, onComplete, viewModel)
        3 -> Leccion03Screen(lessonId, onComplete, viewModel)
        4 -> Leccion04Screen(lessonId, onComplete, viewModel)
        5 -> Leccion05Screen(lessonId, onComplete, viewModel)
        6 -> Leccion06Screen(lessonId, onComplete, viewModel)

        // CURSO 2: Seguridad de Redes (placeholder)
        7, 8, 9, 10, 11, 12 -> PlaceholderLessonScreen(lessonId, onComplete)

        // CURSO 3: Seguridad de Sistemas Operativos (placeholder)
        13, 14, 15, 16, 17, 18 -> PlaceholderLessonScreen(lessonId, onComplete)

        // CURSO 4: Ciberseguridad Avanzada (placeholder)
        19, 20, 21, 22, 23, 24 -> PlaceholderLessonScreen(lessonId, onComplete)

        // CURSO 5: Operaciones de Ciberseguridad (placeholder)
        25, 26, 27, 28, 29, 30 -> PlaceholderLessonScreen(lessonId, onComplete)

        else -> PlaceholderLessonScreen(lessonId, onComplete)
    }
}

/**
 * Placeholder para lecciones no implementadas aún
 */
@Composable
fun PlaceholderLessonScreen(
    lessonId: Int,
    onComplete: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        androidx.compose.material3.Text(
            text = "🚧 Lección $lessonId",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

        androidx.compose.material3.Text(
            text = "Esta lección estará disponible próximamente",
            fontSize = 16.sp
        )

        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))

        androidx.compose.material3.Button(onClick = onComplete) {
            androidx.compose.material3.Text("Volver")
        }
    }
}