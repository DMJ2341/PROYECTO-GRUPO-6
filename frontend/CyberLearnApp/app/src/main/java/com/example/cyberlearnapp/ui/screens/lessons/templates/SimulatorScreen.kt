package com.example.cyberlearnapp.ui.screens.lessons.templates

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * 🎮 PLANTILLA: SIMULATOR
 * Pantalla wrapper que carga diferentes simuladores según el tipo
 *
 * Tipos soportados:
 * - phishing: Email/SMS falso
 * - ddos: Ataque DDoS visual
 * - wifi: Selector de redes
 * - ransomware: Cifrado de archivos
 * - password: Medidor de fortaleza
 */
@Composable
fun SimulatorScreen(
    title: String,
    simulatorType: String,
    initialState: Map<String, Any>,
    instruction: String? = null,
    screenNumber: Int,
    totalScreens: Int,
    onSimulationComplete: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    var simulationCompleted by remember { mutableStateOf(false) }
    var simulationSuccess by remember { mutableStateOf(false) }

    ScreenContainer(
        title = title,
        screenNumber = screenNumber,
        totalScreens = totalScreens,
        onNext = {
            onSimulationComplete(simulationSuccess)
            onNext()
        },
        buttonText = "Continuar",
        buttonEnabled = simulationCompleted
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Instrucción (si existe)
            if (instruction != null) {
                Text(
                    text = instruction,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Cargar simulador según tipo
            when (simulatorType.lowercase()) {
                "phishing" -> {
                    // El simulador específico se implementará en simulators/
                    Text(
                        text = "🎯 SIMULADOR DE PHISHING",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonGreen
                    )

                    Text(
                        text = "Analiza el siguiente email y marca las señales de peligro:",
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    // Aquí se integraría PhishingSimulator.kt
                    SimulatorPlaceholder(
                        type = "Email Phishing",
                        onComplete = { success ->
                            simulationCompleted = true
                            simulationSuccess = success
                        }
                    )
                }

                "ddos" -> {
                    Text(
                        text = "🌊 SIMULADOR DE ATAQUE DDoS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonGreen
                    )

                    // Aquí se integraría DDoSSimulator.kt
                    SimulatorPlaceholder(
                        type = "DDoS Attack",
                        onComplete = { success ->
                            simulationCompleted = true
                            simulationSuccess = success
                        }
                    )
                }

                "wifi" -> {
                    Text(
                        text = "📶 SIMULADOR DE REDES WI-FI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonGreen
                    )

                    // Aquí se integraría WiFiNetworkSimulator.kt
                    SimulatorPlaceholder(
                        type = "WiFi Network",
                        onComplete = { success ->
                            simulationCompleted = true
                            simulationSuccess = success
                        }
                    )
                }

                "ransomware" -> {
                    Text(
                        text = "🦠 SIMULADOR DE RANSOMWARE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonGreen
                    )

                    // Aquí se integraría RansomwareSimulator.kt
                    SimulatorPlaceholder(
                        type = "Ransomware",
                        onComplete = { success ->
                            simulationCompleted = true
                            simulationSuccess = success
                        }
                    )
                }

                "password" -> {
                    Text(
                        text = "🔐 MEDIDOR DE CONTRASEÑAS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonGreen
                    )

                    // Aquí se integraría PasswordStrengthMeter.kt
                    SimulatorPlaceholder(
                        type = "Password Strength",
                        onComplete = { success ->
                            simulationCompleted = true
                            simulationSuccess = success
                        }
                    )
                }

                else -> {
                    Text(
                        text = "⚠️ Simulador no disponible: $simulatorType",
                        fontSize = 14.sp,
                        color = CyberColors.NeonPink
                    )
                }
            }
        }
    }
}

/**
 * Componente temporal: Placeholder para simuladores
 * Esto se reemplazará con los simuladores reales
 */
@Composable
fun SimulatorPlaceholder(
    type: String,
    onComplete: (Boolean) -> Unit
) {
    var interacted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = CyberColors.CardBg
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "🎮 Simulador: $type\n(En desarrollo)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        if (!interacted) {
            CyberButton(
                text = "🎯 INICIAR SIMULACIÓN",
                onClick = {
                    interacted = true
                    onComplete(true)
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            FeedbackMessage(
                isCorrect = true,
                message = "✅ Simulación completada correctamente"
            )
        }
    }
}