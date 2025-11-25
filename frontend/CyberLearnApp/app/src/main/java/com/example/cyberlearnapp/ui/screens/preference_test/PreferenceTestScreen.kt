package com.example.cyberlearnapp.ui.screens.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.components.CircularProgress
import com.example.cyberlearnapp.viewmodel.PreferenceTestViewModel
import kotlinx.coroutines.launch

@Composable
fun PreferenceTestScreen(
    navController: NavController,
    viewModel: PreferenceTestViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 25 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadQuestions()
    }

    // Manejo de navegación post-submit
    LaunchedEffect(state.isSubmissionSuccess) {
        if (state.isSubmissionSuccess) {
            val profileSlug = state.resultProfile?.lowercase()?.replace(" ", "_") ?: "red_team"
            navController.navigate("preference_result/$profileSlug") {
                popUpTo("preference_test") { inclusive = true }
            }
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            Button(onClick = { viewModel.loadQuestions() }) { Text("Reintentar") }
        }
        return
    }

    if (state.questions.isEmpty()) {
        return // Esperando carga
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {

        // Header
        PreferenceHeader(
            currentPage = pagerState.currentPage,
            totalPages = 25,
            onClose = { navController.popBackStack() }
        )

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false // Bloqueamos scroll manual para obligar a responder
        ) { page ->
            // Protegemos contra índice fuera de rango si questions no cargó completo
            if (page < state.questions.size) {
                PreferenceQuestionScreen(
                    question = state.questions[page],
                    selectedOption = state.answers[page],
                    onOptionSelected = { viewModel.selectAnswer(page, it) }
                )
            }
        }

        // Botones Inferiores
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón Anterior
            if (pagerState.currentPage > 0) {
                OutlinedButton(onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }) {
                    Text("Anterior")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }

            // Botón Siguiente / Finalizar
            Button(
                onClick = {
                    if (pagerState.currentPage == 24) {
                        viewModel.submitTest()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                // Deshabilitado si no ha respondido la pregunta actual
                enabled = state.answers[pagerState.currentPage] != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pagerState.currentPage == 24) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(if (pagerState.currentPage == 24) "Finalizar" else "Siguiente")
                }
            }
        }
    }
}

@Composable
fun PreferenceHeader(currentPage: Int, totalPages: Int, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgress(
                progress = (currentPage + 1).toFloat() / totalPages,
                size = 50.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Test Vocacional",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "Pregunta ${currentPage + 1} de $totalPages",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar")
        }
    }
}