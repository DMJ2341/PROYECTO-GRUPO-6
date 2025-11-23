package com.example.cyberlearnapp.ui.screens.lessons.simulators

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.ui.screens.lessons.shared.*
import kotlinx.coroutines.delay

/**
 * 🌊 SIMULADOR: ATAQUE DDoS
 * Simula visualmente el colapso de un servidor bajo ataque DDoS
 *
 * Usado en:
 * - Lección 3: Simulador DDoS
 */
@Composable
fun DDoSSimulator(
    normalTraffic: Int = 50,
    attackTraffic: Int = 10000,
    onComplete: (Boolean) -> Unit
) {
    var serverState by remember { mutableStateOf(ServerState.OPERATIONAL) }
    var currentTraffic by remember { mutableStateOf(normalTraffic) }
    var attackStarted by remember { mutableStateOf(false) }
    var showDefense by remember { mutableStateOf(false) }

    LaunchedEffect(attackStarted) {
        if (attackStarted && serverState != ServerState.DOWN) {
            // Incrementar tráfico gradualmente
            for (i in normalTraffic..attackTraffic step 500) {
                currentTraffic = i
                delay(50)

                // Cambiar estado del servidor según tráfico
                serverState = when {
                    currentTraffic < 1000 -> ServerState.OPERATIONAL
                    currentTraffic < 5000 -> ServerState.OVERLOADED
                    else -> ServerState.DOWN
                }
            }

            // Mostrar opciones de defensa después del ataque
            delay(1000)
            showDefense = true
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Estado del servidor
        ServerStatusCard(
            state = serverState,
            traffic = currentTraffic,
            normalTraffic = normalTraffic
        )

        // Indicador de ancho de banda
        BandwidthIndicator(
            currentTraffic = currentTraffic,
            maxTraffic = attackTraffic,
            state = serverState
        )

        // Estadísticas en tiempo real
        TrafficStatsRow(
            normalTraffic = normalTraffic,
            currentTraffic = currentTraffic,
            botsActive = if (attackStarted) 10000 else 0
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Controles
        if (!attackStarted) {
            CyberButton(
                text = "🚨 INICIAR ATAQUE DDoS",
                onClick = { attackStarted = true },
                modifier = Modifier.fillMaxWidth()
            )
        } else if (showDefense) {
            DefenseOptions(
                onDefenseSelected = { success ->
                    if (success) {
                        serverState = ServerState.OPERATIONAL
                        currentTraffic = normalTraffic
                    }
                    onComplete(success)
                }
            )
        } else {
            // Mensaje durante el ataque
            Text(
                text = "⚠️ Ataque en progreso...",
                fontSize = 14.sp,
                color = CyberColors.NeonPink,
                fontWeight = FontWeight.Bold
            )
        }

        // Explicación
        if (serverState == ServerState.DOWN) {
            Spacer(modifier = Modifier.height(8.dp))
            FeedbackMessage(
                isCorrect = false,
                message = "💀 Servidor CAÍDO - 10,000 bots enviaron peticiones simultáneas saturando el ancho de banda"
            )
        }
    }
}

/**
 * Componente: Server Status Card
 */
@Composable
fun ServerStatusCard(
    state: ServerState,
    traffic: Int,
    normalTraffic: Int
) {
    val (icon, statusText, statusColor) = when (state) {
        ServerState.OPERATIONAL -> Triple("🟢", "OPERATIVO", CyberColors.NeonGreen)
        ServerState.OVERLOADED -> Triple("🟡", "SOBRECARGADO", CyberColors.NeonBlue)
        ServerState.DOWN -> Triple("🔴", "CAÍDO", CyberColors.NeonPink)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.CardBg
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "SERVIDOR WEB:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Text(
                text = "$icon $statusText",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            Text(
                text = "Tu sitio web recibe $traffic visitas/minuto",
                fontSize = 13.sp,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            if (traffic > normalTraffic * 2) {
                Text(
                    text = "⚠️ Tráfico ${traffic / normalTraffic}x superior al normal",
                    fontSize = 12.sp,
                    color = CyberColors.NeonPink
                )
            }
        }
    }
}

/**
 * Componente: Bandwidth Indicator
 */
@Composable
fun BandwidthIndicator(
    currentTraffic: Int,
    maxTraffic: Int,
    state: ServerState
) {
    val progress = (currentTraffic.toFloat() / maxTraffic).coerceIn(0f, 1f)

    val color = when (state) {
        ServerState.OPERATIONAL -> CyberColors.NeonGreen
        ServerState.OVERLOADED -> CyberColors.NeonBlue
        ServerState.DOWN -> CyberColors.NeonPink
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ancho de banda:",
                fontSize = 13.sp,
                color = Color.White
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = CyberColors.CardBg
        )

        if (state == ServerState.DOWN) {
            Text(
                text = "📈 Ancho de banda saturado: 100%",
                fontSize = 12.sp,
                color = CyberColors.NeonPink,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Componente: Traffic Stats Row
 */
@Composable
fun TrafficStatsRow(
    normalTraffic: Int,
    currentTraffic: Int,
    botsActive: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatDisplay(
            icon = "📊",
            percentage = "$normalTraffic",
            label = "Tráfico\nNormal"
        )

        StatDisplay(
            icon = "🌊",
            percentage = "$currentTraffic",
            label = "Tráfico\nActual"
        )

        StatDisplay(
            icon = "🤖",
            percentage = "$botsActive",
            label = "Bots\nActivos"
        )
    }
}

/**
 * Componente: Defense Options
 */
@Composable
fun DefenseOptions(
    onDefenseSelected: (Boolean) -> Unit
) {
    var selectedDefense by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🛡️ ¿CÓMO DEFENDERSE?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonGreen
        )

        DefenseOptionCard(
            title = "Filtros de tráfico",
            description = "Bloquear IPs maliciosas",
            isCorrect = true,
            isSelected = selectedDefense == "filters",
            onClick = {
                selectedDefense = "filters"
                onDefenseSelected(true)
            }
        )

        DefenseOptionCard(
            title = "Apagar el servidor",
            description = "Desconectar todo",
            isCorrect = false,
            isSelected = selectedDefense == "shutdown",
            onClick = {
                selectedDefense = "shutdown"
                onDefenseSelected(false)
            }
        )

        DefenseOptionCard(
            title = "Servicios de mitigación",
            description = "Cloudflare, AWS Shield",
            isCorrect = true,
            isSelected = selectedDefense == "mitigation",
            onClick = {
                selectedDefense = "mitigation"
                onDefenseSelected(true)
            }
        )

        if (selectedDefense != null) {
            val isCorrect = selectedDefense == "filters" || selectedDefense == "mitigation"

            FeedbackMessage(
                isCorrect = isCorrect,
                message = if (isCorrect) {
                    "✅ Correcto! Los filtros y servicios de mitigación distribuyen el tráfico y bloquean bots"
                } else {
                    "❌ Apagar el servidor es exactamente lo que busca el atacante"
                }
            )
        }
    }
}

/**
 * Componente: Defense Option Card
 */
@Composable
fun DefenseOptionCard(
    title: String,
    description: String,
    isCorrect: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                if (isCorrect) CyberColors.NeonGreen.copy(alpha = 0.2f)
                else CyberColors.NeonPink.copy(alpha = 0.2f)
            } else {
                CyberColors.CardBg
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            if (isSelected) {
                Text(
                    text = if (isCorrect) "✅" else "❌",
                    fontSize = 24.sp
                )
            }
        }
    }
}

/**
 * Estados del servidor
 */
enum class ServerState {
    OPERATIONAL,
    OVERLOADED,
    DOWN
}