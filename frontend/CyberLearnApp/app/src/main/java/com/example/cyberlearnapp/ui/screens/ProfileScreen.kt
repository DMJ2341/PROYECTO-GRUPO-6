package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cyberlearnapp.ui.theme.*
import com.example.cyberlearnapp.viewmodel.UserViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val userProgress by userViewModel.userProgress.collectAsState()
    val userBadges by userViewModel.userBadges.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    // Cargar datos cuando se abre la pantalla
    LaunchedEffect(Unit) {
        userViewModel.loadUserProgress()
        userViewModel.loadUserBadges()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Tu progreso en CyberLearn",
                style = MaterialTheme.typography.bodyMedium,
                color = AccentCyan,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentCyan)
                    }
                }
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        color = Danger,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                userProgress != null -> {
                    ProfileStats(
                        userProgress = userProgress!!,
                        userBadges = userBadges
                    )
                }
                else -> {
                    Text(
                        text = "No hay datos de progreso",
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStats(
    userProgress: com.example.cyberlearnapp.viewmodel.UserProgress,
    userBadges: List<com.example.cyberlearnapp.network.Badge>
) {
    val currentLevel = (userProgress.totalXp / 100) + 1
    val currentXpTotal = userProgress.totalXp
    val xpForNextLevel = currentLevel * 100
    val xpInCurrentLevel = currentXpTotal % 100

    val lessonsCompleted = userProgress.coursesProgress.sumOf { it.completedLessons }
    val coursesCompleted = userProgress.coursesProgress.count { it.progressPercent >= 100 }

    Column {
        // Tarjeta de nivel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nivel $currentLevel",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$currentXpTotal XP totales",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentCyan,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Barra de progreso de nivel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(TextGray.copy(alpha = 0.3f), MaterialTheme.shapes.small)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(xpInCurrentLevel / 100f)
                            .height(8.dp)
                            .background(AccentCyan, MaterialTheme.shapes.small)
                    )
                }

                Text(
                    text = "$xpInCurrentLevel/$xpForNextLevel XP para el siguiente nivel",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Estadísticas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Estadísticas",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        title = "Racha actual",
                        value = userProgress.currentStreak.toString(),
                        icon = "🔥"
                    )
                    StatItem(
                        title = "Insignias",
                        value = userProgress.badgesCount.toString(),
                        icon = "🛡️"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        title = "Lecciones completadas",
                        value = lessonsCompleted.toString(),
                        icon = "📚"
                    )
                    StatItem(
                        title = "Cursos completados",
                        value = coursesCompleted.toString(),
                        icon = "🎓"
                    )
                }
            }
        }

        // Insignias
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Tus Insignias",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (userBadges.isNotEmpty()) {
                    // Mostrar insignias del usuario
                    // (Implementar grid de insignias aquí)
                    Text(
                        text = "${userBadges.size} insignias desbloqueadas",
                        color = TextGray
                    )
                } else {
                    Text(
                        text = "Aún no tienes insignias. ¡Completa lecciones para ganarlas!",
                        color = TextGray
                    )
                }
            }
        }
    }
}

// Componente de item de estadística
@Composable
fun StatItem(title: String, value: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = TextGray
        )
    }
}

// Componente de item de actividad
@Composable
fun ActivityItem(course: String, activity: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = course,
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhite,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = activity,
            style = MaterialTheme.typography.bodySmall,
            color = TextGray,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}