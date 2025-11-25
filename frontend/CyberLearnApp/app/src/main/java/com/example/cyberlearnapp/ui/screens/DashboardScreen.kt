package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.network.DailyTerm
import com.example.cyberlearnapp.ui.components.BadgeCard
import com.example.cyberlearnapp.ui.components.XpLevelBar
import com.example.cyberlearnapp.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
        viewModel.loadDailyTerm()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // 1. DAILY TERM CARD
        state.dailyTerm?.let { term ->
            DailyTermCard(term = term)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2. PROGRESS BAR
        XpLevelBar(
            currentXp = state.userXp,
            level = state.userLevel,
            xpToNext = 100 // Simplificado
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. CALL TO ACTION (TEST VOCACIONAL)
        // Verifica si el usuario ya tiene un perfil asignado. Si no, muestra el botón.
        if (!state.hasPreferenceResult) {
            Button(
                onClick = { navController.navigate("preference_test") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "🎯 Descubre tu Rol en Ciberseguridad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Test de 5 minutos para saber si eres Red, Blue o Purple Team",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            // Si ya tiene resultado, botón para verlo
            OutlinedButton(
                onClick = { navController.navigate("preference_result") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver mi Perfil Profesional")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 4. BADGES
        Text("Mis Logros", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (state.badges.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.DarkGray)) {
                Text(
                    "Completa lecciones para ganar medallas.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.LightGray
                )
            }
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

@Composable
fun DailyTermCard(term: DailyTerm) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252530))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Término del Día", style = MaterialTheme.typography.labelLarge, color = Color(0xFFBB86FC))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = term.term,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "[${term.category}]",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = term.definition,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFEEEEEE)
            )
        }
    }
}