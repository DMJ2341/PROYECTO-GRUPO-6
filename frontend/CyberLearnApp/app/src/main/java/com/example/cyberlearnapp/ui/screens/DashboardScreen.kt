package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cyberlearnapp.ui.theme.*
import com.example.cyberlearnapp.ui.components.ProgressCard
import com.example.cyberlearnapp.ui.components.CourseCard
import com.example.cyberlearnapp.viewmodel.UserViewModel

@Composable
fun DashboardScreen(
    userViewModel: UserViewModel,
    onCourseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProgress by userViewModel.userProgress.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    // ✅ Cargar datos cuando se abre la pantalla
    LaunchedEffect(Unit) {
        println("🔹 Dashboard - Cargando progreso del usuario...")
        userViewModel.loadUserProgress()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryDark)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan)
                    Text(
                        text = "Cargando...",
                        color = TextWhite,
                        modifier = Modifier.padding(top = 80.dp)
                    )
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "❌ Error",
                            color = TextWhite,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = TextGray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { userViewModel.loadUserProgress() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            userProgress != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "CyberLearn",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Aprende. Hackea. Protege.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentCyan,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    ProgressCard(
                        progress = userProgress!!,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "Continúa aprendiendo",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    CourseCard(
                        title = "Fundamentos de Ciberseguridad",
                        description = "Principiante • +155 XP",
                        progress = 0f,
                        onClick = { onCourseClick("Fundamentos de Ciberseguridad") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CourseCard(
                        title = "Phishing e Ingeniería Social",
                        description = "Principiante • +185 XP",
                        progress = 0f,
                        onClick = { onCourseClick("Phishing e Ingeniería Social") }
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            else -> {
                // Estado inicial - mostrar algo mientras carga
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            }
        }
    }
}