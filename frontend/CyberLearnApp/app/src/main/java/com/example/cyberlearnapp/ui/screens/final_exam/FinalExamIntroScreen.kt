package com.example.cyberlearnapp.ui.screens.final_exam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun FinalExamIntroScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎓", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text("Examen Final Integrador", style = MaterialTheme.typography.headlineMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text(
            "Este examen evalúa todo lo aprendido en los 5 cursos. Tienes 60 minutos para completarlo. Necesitas 70% para aprobar y obtener la insignia 'CyberLearn Expert'.",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("final_exam/take") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("COMENZAR AHORA")
        }
    }
}