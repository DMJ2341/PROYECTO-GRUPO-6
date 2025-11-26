package com.example.cyberlearnapp.ui.screens.final_exam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.cyberlearnapp.R
import com.example.cyberlearnapp.viewmodel.FinalExamViewModel

@Composable
fun FinalExamResultScreen(
    navController: NavController,
    vm: FinalExamViewModel = hiltViewModel()
) {
    // ← CORRECTO: collectAsState() fuera de cualquier condición
    val state by vm.state.collectAsState()
    val result = state.result

    // Si no hay resultado, redirigir
    if (result == null) {
        LaunchedEffect(Unit) {
            navController.navigate("dashboard") {
                popUpTo(0)
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Confetti si aprobó
        if (result.passed) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
            val progress by animateLottieCompositionAsState(composition, iterations = 1)

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (result.passed) "¡FELICIDADES!" else "INTÉNTALO DE NUEVO",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (result.passed) Color(0xFF4CAF50) else Color.Red
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Tu calificación: ${result.percentage.toInt()}%",
                style = MaterialTheme.typography.displayMedium
            )

            Text(
                text = "Nivel: ${result.grade}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            if (result.passed && result.new_badge != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("NUEVA INSIGNIA DESBLOQUEADA", fontWeight = FontWeight.Bold, color = Color(0xFFFFA000))
                        Spacer(Modifier.height(8.dp))
                        Text(result.new_badge, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("VOLVER AL DASHBOARD")
            }
        }
    }
}