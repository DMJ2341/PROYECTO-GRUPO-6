package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape // <-- ¡IMPORTACIÓN AÑADIDA!
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// --- IMPORTACIONES DE COLOR ---
import com.example.cyberlearnapp.ui.theme.AccentCyan
import com.example.cyberlearnapp.ui.theme.CardBg
import com.example.cyberlearnapp.ui.theme.Danger
import com.example.cyberlearnapp.ui.theme.PrimaryDark
import com.example.cyberlearnapp.ui.theme.Success
import com.example.cyberlearnapp.ui.theme.TextGray
import com.example.cyberlearnapp.ui.theme.TextWhite
import com.example.cyberlearnapp.ui.theme.Warning
// --- FIN DE IMPORTACIONES ---
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.viewmodel.UserViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.Progress

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val userProgress by userViewModel.userProgress.collectAsState()
    val userBadges by userViewModel.userBadges.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    // CARGAR DATOS AL INICIAR LA PANTALLA
    LaunchedEffect(Unit) {
        userViewModel.loadUserProgress()
        userViewModel.loadUserBadges()
    }

    // --- CÁLCULOS DE PROGRESO CORREGIDOS ---
    val currentLevel = userProgress?.level ?: 1
    val currentXpTotal = userProgress?.xpTotal ?: 0
    // Asumiendo 100 XP por nivel
    val currentLevelXp = currentXpTotal % 100
    val xpToNextLevel = 100 - currentLevelXp
    val progressFraction = currentLevelXp / 100f
    // ----------------------------------------

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
                text = "Tu información y configuración",
                style = MaterialTheme.typography.bodyMedium,
                color = AccentCyan,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Mostrar loading si está cargando
            if (isLoading && userProgress == null) { // Mostrar solo si no hay datos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            }

            // Tarjeta de información del usuario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar del usuario
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape, // <-- Ahora funciona
                        color = AccentCyan
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👤", // Emoji de avatar
                                fontSize = 40.sp // Tamaño ajustado
                            )
                        }
                    }

                    Text(
                        text = currentUser?.name ?: "Usuario",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text(
                        text = currentUser?.email ?: "usuario@ejemplo.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentCyan,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Nivel y XP
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = AccentCyan.copy(alpha = 0.1f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = currentLevel.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = AccentCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Nivel",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = currentXpTotal.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = AccentCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "XP Total",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = userProgress?.streak?.toString() ?: "0",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Warning,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Racha",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray
                                )
                            }
                        }
                    }

                    // Barra de progreso de nivel
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nivel $currentLevel",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextWhite
                            )
                            Text(
                                text = "Nivel ${currentLevel + 1}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Barra de progreso dinámica
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.small),
                            color = AccentCyan,
                            trackColor = TextGray.copy(alpha = 0.3f)
                        )

                        Text(
                            text = "Faltan $xpToNextLevel XP para el nivel ${currentLevel + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Button(
                        onClick = onEditProfile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentCyan,
                            contentColor = TextWhite
                        )
                    ) {
                        Text("Editar perfil")
                    }
                }
            }

            // Estadísticas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📊 Estadísticas",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Lista de estadísticas
                    Column {
                        StatItem(
                            label = "Lecciones completadas",
                            value = userProgress?.lessonsCompleted?.toString() ?: "0",
                            color = AccentCyan
                        )
                        StatItem(
                            label = "Insignias obtenidas",
                            value = "${userBadges.size} / 15", // Asumiendo 15 totales
                            color = Success
                        )
                        StatItem(
                            label = "Progreso Nivel Actual",
                            value = "$currentLevelXp / 100 XP",
                            color = Warning
                        )
                        StatItem(
                            label = "Cursos completados",
                            value = userProgress?.coursesCompleted?.toString() ?: "0",
                            color = Success
                        )
                    }
                }
            }

            // Actividad reciente (Datos estáticos por ahora)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📚 Actividad Reciente",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Lista de actividad
                    Column {
                        ActivityItem(
                            course = "Fundamentos de Ciberseguridad",
                            activity = "Última actividad: Hoy"
                        )
                    }
                }
            }

            // Configuración
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "⚙️ Configuración",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Danger,
                            contentColor = TextWhite
                        )
                    ) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}

// Componente de item de estadística
@Composable
fun StatItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Bold
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