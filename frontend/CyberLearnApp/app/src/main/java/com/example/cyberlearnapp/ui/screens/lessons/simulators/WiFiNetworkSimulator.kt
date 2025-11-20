package com.example.cyberlearnapp.ui.screens.lessons.simulators

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * 📶 SIMULADOR: SELECTOR DE REDES WI-FI
 * Simula la selección de redes Wi-Fi seguras en un aeropuerto/cafetería
 *
 * Usado en:
 * - Lección 4: Redes Wi-Fi disponibles
 */
@Composable
fun WiFiNetworkSimulator(
    scenario: String = "Aeropuerto",
    networks: List<WiFiNetwork>,
    correctNetworkId: String,
    onComplete: (Boolean) -> Unit
) {
    var selectedNetwork by remember { mutableStateOf<WiFiNetwork?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    val isCorrect = selectedNetwork?.id == correctNetworkId

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado del escenario
        ScenarioHeader(scenario = scenario)

        // Lista de redes disponibles
        Text(
            text = "ESCENARIO: $scenario - Redes Disponibles",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonBlue
        )

        LazyColumn(
            modifier = Modifier.height(300.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(networks) { network ->
                WiFiNetworkCard(
                    network = network,
                    isSelected = selectedNetwork?.id == network.id,
                    showResult = showFeedback,
                    isCorrect = network.id == correctNetworkId,
                    onClick = {
                        if (!showFeedback) {
                            selectedNetwork = network
                            showFeedback = true
                            onComplete(network.id == correctNetworkId)
                        }
                    }
                )
            }
        }

        // Feedback
        if (showFeedback && selectedNetwork != null) {
            Spacer(modifier = Modifier.height(8.dp))

            if (isCorrect) {
                FeedbackMessage(
                    isCorrect = true,
                    message = selectedNetwork!!.correctFeedback
                )
            } else {
                FeedbackMessage(
                    isCorrect = false,
                    message = selectedNetwork!!.incorrectFeedback
                )
            }
        }
    }
}

/**
 * Componente: Scenario Header
 */
@Composable
fun ScenarioHeader(scenario: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.CardBg
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = when (scenario) {
                    "Aeropuerto" -> "✈️"
                    "Cafetería" -> "☕"
                    "Hotel" -> "🏨"
                    else -> "📍"
                },
                fontSize = 32.sp
            )

            Column {
                Text(
                    text = "Ubicación: $scenario",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonGreen
                )
                Text(
                    text = "Selecciona la red MÁS SEGURA",
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Componente: WiFi Network Card
 */
@Composable
fun WiFiNetworkCard(
    network: WiFiNetwork,
    isSelected: Boolean,
    showResult: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showResult && isSelected && isCorrect -> CyberColors.NeonGreen.copy(alpha = 0.2f)
        showResult && isSelected && !isCorrect -> CyberColors.NeonPink.copy(alpha = 0.2f)
        isSelected -> CyberColors.NeonBlue.copy(alpha = 0.2f)
        else -> CyberColors.CardBg
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Icono de seguridad
                Text(
                    text = if (network.isSecure) "🔒" else "📶",
                    fontSize = 28.sp
                )

                Column {
                    // Nombre de la red
                    Text(
                        text = network.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Estado de seguridad
                    Text(
                        text = if (network.isSecure) "Protegida" else "Abierta",
                        fontSize = 12.sp,
                        color = if (network.isSecure)
                            CyberColors.NeonGreen
                        else
                            CyberColors.NeonPink.copy(alpha = 0.7f)
                    )

                    // Información adicional
                    if (network.additionalInfo != null) {
                        Text(
                            text = network.additionalInfo,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Indicador de señal
            SignalStrengthIndicator(strength = network.signalStrength)

            // Resultado (si está visible)
            if (showResult && isSelected) {
                Text(
                    text = if (isCorrect) "✅" else "❌",
                    fontSize = 28.sp
                )
            }
        }
    }
}

/**
 * Componente: Signal Strength Indicator
 */
@Composable
fun SignalStrengthIndicator(strength: Int) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = androidx.compose.ui.Alignment.Bottom
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height((8 + index * 4).dp)
                        .then(
                            if (index < strength / 25) {
                                Modifier.background(CyberColors.NeonGreen)
                            } else {
                                Modifier.background(Color.White.copy(alpha = 0.2f))
                            }
                        )
                )
            }
        }

        Text(
            text = "$strength%",
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

/**
 * Modelo de datos: WiFi Network
 */
data class WiFiNetwork(
    val id: String,
    val name: String,
    val isSecure: Boolean,
    val signalStrength: Int, // 0-100
    val additionalInfo: String? = null,
    val correctFeedback: String,
    val incorrectFeedback: String
)

/**
 * Datos de ejemplo: Aeropuerto
 */
val AIRPORT_NETWORKS = listOf(
    WiFiNetwork(
        id = "free_wifi",
        name = "Free_Airport_WiFi",
        isSecure = false,
        signalStrength = 85,
        additionalInfo = "Sin contraseña",
        correctFeedback = "",
        incorrectFeedback = "❌ Las redes abiertas pueden ser Evil Twins (redes falsas). Podrían interceptar tu información."
    ),
    WiFiNetwork(
        id = "official",
        name = "Airport_Official",
        isSecure = true,
        signalStrength = 75,
        additionalInfo = "Requiere contraseña",
        correctFeedback = "✅ EXCELENTE! Redes oficiales con contraseña son más seguras que las abiertas. Siempre verifica con el personal que sea la red legítima.",
        incorrectFeedback = ""
    ),
    WiFiNetwork(
        id = "starbucks",
        name = "Starbucks_Free",
        isSecure = false,
        signalStrength = 60,
        additionalInfo = "Sin contraseña",
        correctFeedback = "",
        incorrectFeedback = "❌ Aunque parezca legítima, esta red no está asociada con el aeropuerto. Podría ser un Evil Twin."
    ),
    WiFiNetwork(
        id = "guest",
        name = "Guest_Airport",
        isSecure = false,
        signalStrength = 90,
        additionalInfo = "Sin contraseña",
        correctFeedback = "",
        incorrectFeedback = "❌ Nombre genérico + red abierta = Alta probabilidad de ser un Evil Twin creado por atacantes."
    )
)

/**
 * Datos de ejemplo: Cafetería (Evil Twin attack)
 */
val CAFE_NETWORKS = listOf(
    WiFiNetwork(
        id = "free_mall",
        name = "Free_Mall_WiFi",
        isSecure = false,
        signalStrength = 95,
        additionalInfo = "Sin contraseña - Señal muy fuerte",
        correctFeedback = "",
        incorrectFeedback = "❌ ¡Evil Twin! Señal anormalmente fuerte cerca de tu ubicación. Los atacantes crean redes falsas con nombres atractivos."
    ),
    WiFiNetwork(
        id = "mall_secure",
        name = "Mall_Official_5G",
        isSecure = true,
        signalStrength = 70,
        additionalInfo = "Contraseña disponible en tiendas",
        correctFeedback = "✅ CORRECTO! Red oficial con contraseña. Siempre confirma con el personal antes de conectarte.",
        incorrectFeedback = ""
    ),
    WiFiNetwork(
        id = "public",
        name = "Public_Internet",
        isSecure = false,
        signalStrength = 80,
        additionalInfo = "Sin contraseña",
        correctFeedback = "",
        incorrectFeedback = "❌ Nombre muy genérico. Probable Evil Twin. Los atacantes usan nombres atractivos para engañar usuarios."
    )
)

/**
 * Datos de ejemplo: Hotel
 */
val HOTEL_NETWORKS = listOf(
    WiFiNetwork(
        id = "hotel_guest",
        name = "Hotel_Guest_WiFi",
        isSecure = false,
        signalStrength = 85,
        additionalInfo = "Sin contraseña",
        correctFeedback = "",
        incorrectFeedback = "❌ Red abierta sin autenticación. Verifica con recepción la red oficial del hotel."
    ),
    WiFiNetwork(
        id = "hotel_official",
        name = "GrandHotel_Secure",
        isSecure = true,
        signalStrength = 75,
        additionalInfo = "Código en tu habitación",
        correctFeedback = "✅ PERFECTO! Red protegida del hotel. Código único por habitación = Mayor seguridad.",
        incorrectFeedback = ""
    ),
    WiFiNetwork(
        id = "hotel_lobby",
        name = "Lobby_Free_WiFi",
        isSecure = false,
        signalStrength = 90,
        additionalInfo = "Sin contraseña - Señal muy fuerte",
        correctFeedback = "",
        incorrectFeedback = "❌ Posible Evil Twin. Señal muy fuerte y nombre genérico son banderas rojas."
    )
)