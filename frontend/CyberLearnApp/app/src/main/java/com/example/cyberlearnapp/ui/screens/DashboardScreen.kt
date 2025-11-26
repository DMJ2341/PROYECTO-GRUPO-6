package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.components.BadgeCard
import com.example.cyberlearnapp.ui.components.DailyTermCard
import com.example.cyberlearnapp.ui.components.XpLevelBar
import com.example.cyberlearnapp.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Término del día
        state.dailyTerm?.let {
            DailyTermCard(wrapper = it)
            Spacer(Modifier.height(16.dp))
        }

        // Nivel
        XpLevelBar(currentXp = state.userXp, level = state.userLevel)
        Spacer(Modifier.height(24.dp))

        // Botón de Acción Principal (Test o Examen)
        if (!state.hasPreferenceResult) {
            Button(
                onClick = { navController.navigate("preference_test") },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🎯 Descubre tu Rol (Test Vocacional)")
            }
        } else if (state.completedCourses >= 5) {
            Button(
                onClick = { navController.navigate("final_exam/intro") },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🏆 Examen Final Integrador")
            }
        } else {
            OutlinedButton(onClick = { navController.navigate("preference_result") }, modifier = Modifier.fillMaxWidth()) {
                Text("Ver mi Perfil Profesional")
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Mis Insignias", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        if (state.badges.isEmpty()) {
            Text("Completa lecciones para ganar insignias.", color = Color.Gray)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.badges.size) { index ->
                    BadgeCard(badge = state.badges[index])
                }
            }
        }
    }
}