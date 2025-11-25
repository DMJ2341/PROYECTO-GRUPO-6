package com.example.cyberlearnapp.ui.screens.final_exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.airbnb.lottie.compose.*
import com.example.cyberlearnapp.R
import com.example.cyberlearnapp.network.models.assessments.FinalExamQuestion
import com.example.cyberlearnapp.viewmodel.FinalExamViewModel
import kotlinx.coroutines.delay

// --- PANTALLA 1: INTRODUCCIÓN ---
@Composable
fun FinalExamIntroScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎓", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            "Examen Final Integrador",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("• Duración: 60 minutos")
                Text("• Intentos permitidos: 3")
                Text("• Aprobación: ≥ 70%")
                Text("• Recompensa: Badge 'Expert'")
            }
        }

        Spacer(Modifier.height(48.dp))
        Button(
            onClick = { navController.navigate("final_exam/take") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Comenzar Examen")
        }
    }
}

// --- PANTALLA 2: EL EXAMEN (TIMER + PREGUNTAS) ---
@Composable
fun FinalExamScreen(navController: NavController, vm: FinalExamViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    var secondsLeft by remember { mutableIntStateOf(3600) } // 60 min

    LaunchedEffect(Unit) {
        vm.startExam()
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        // Si el tiempo se acaba, enviar automáticamente
        if (!state.submitting && state.result == null) {
            vm.submit(navController)
        }
    }

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header con Timer
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Intento ${state.attempt}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60),
                style = MaterialTheme.typography.headlineSmall,
                color = if (secondsLeft < 300) Color.Red else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.questions) { q ->
                ExamQuestionItem(
                    question = q,
                    selectedOptionId = state.answers[q.id.toString()],
                    onOptionSelected = { optionId ->
                        vm.selectAnswer(q.id.toString(), optionId)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.submit(navController) },
            enabled = !state.submitting,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (state.submitting) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Finalizar Examen")
            }
        }

        if (state.error != null) {
            Text(state.error!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

// --- COMPONENTE VISUAL DE PREGUNTA (FALTABA ESTE) ---
@Composable
fun ExamQuestionItem(
    question: FinalExamQuestion,
    selectedOptionId: String?,
    onOptionSelected: (String) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question.question_text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            question.options?.forEach { option ->
                val isSelected = selectedOptionId == option.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onOptionSelected(option.id) }
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null // El click lo maneja la Row
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option.text)
                }
            }
        }
    }
}

// --- PANTALLA 3: RESULTADO ---
@Composable
fun FinalExamResultScreen(navController: NavController, vm: FinalExamViewModel = hiltViewModel()) {
    val result = vm.state.collectAsState().value.result ?: return

    // Lottie Confetti
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    val progress by animateLottieCompositionAsState(composition)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (result.passed) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }

        Card(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (result.passed) "¡APROBADO!" else "No Aprobado",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (result.passed) Color(0xFF4CAF50) else Color.Red,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Calificación: ${result.grade}", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${(result.percentage).toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )

                if (result.passed && result.new_badge != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("🏅 Nuevo Badge Desbloqueado:", style = MaterialTheme.typography.labelLarge)
                    Text(result.new_badge, style = MaterialTheme.typography.titleLarge, color = Color(0xFFFFC107))
                } else if (!result.passed) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Puedes reintentarlo en 48hs.", textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }) {
                    Text("Volver al Dashboard")
                }
            }
        }
    }
}