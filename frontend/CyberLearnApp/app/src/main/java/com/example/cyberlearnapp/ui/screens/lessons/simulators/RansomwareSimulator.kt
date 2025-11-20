package com.example.cyberlearnapp.ui.screens.lessons.simulators

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * 🦠 SIMULADOR: RANSOMWARE ATTACK
 * Simula el proceso de cifrado de archivos por ransomware
 *
 * Usado en:
 * - Lección 3: Infografía de Ransomware
 * - Lección 6: Detección de Ransomware
 */
@Composable
fun RansomwareSimulator(
    totalFiles: Int = 1200,
    encryptionTimeMs: Long = 2000,
    ransom: String = "0.5 BTC",
    onComplete: (Boolean) -> Unit
) {
    var attackPhase by remember { mutableStateOf(RansomwarePhase.IDLE) }
    var filesEncrypted by remember { mutableStateOf(0) }
    var showContainment by remember { mutableStateOf(false) }

    // Animación de cifrado
    LaunchedEffect(attackPhase) {
        if (attackPhase == RansomwarePhase.ENCRYPTING) {
            val incrementStep = totalFiles / 20

            for (i in 0..totalFiles step incrementStep) {
                filesEncrypted = i
                delay(encryptionTimeMs / 20)
            }

            filesEncrypted = totalFiles
            delay(500)
            attackPhase = RansomwarePhase.RANSOM_DEMAND
            showContainment = true
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Alerta crítica
        if (attackPhase != RansomwarePhase.IDLE) {
            CriticalAlert(phase = attackPhase)
        }

        // Estado de archivos
        FileEncryptionStatus(
            totalFiles = totalFiles,
            filesEncrypted = filesEncrypted,
            phase = attackPhase
        )

        // Evidencias del ataque
        if (attackPhase != RansomwarePhase.IDLE) {
            AttackEvidenceCard(
                timeOfInfection = "Hace 2 horas",
                origin = "Dispositivo infectado vía Wi-Fi",
                ransomAmount = ransom
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Controles
        when (attackPhase) {
            RansomwarePhase.IDLE -> {
                CyberButton(
                    text = "🚨 DETECTAR ATAQUE RANSOMWARE",
                    onClick = { attackPhase = RansomwarePhase.ENCRYPTING },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            RansomwarePhase.ENCRYPTING -> {
                Text(
                    text = "⚠️ Cifrando archivos...",
                    fontSize = 14.sp,
                    color = CyberColors.NeonPink,
                    fontWeight = FontWeight.Bold
                )
            }

            RansomwarePhase.RANSOM_DEMAND -> {
                if (showContainment) {
                    ContainmentPlan(
                        onPlanExecuted = { success ->
                            onComplete(success)
                        }
                    )
                } else {
                    RansomMessage(ransom = ransom)
                }
            }
        }
    }
}

/**
 * Componente: Critical Alert
 */
@Composable
fun CriticalAlert(phase: RansomwarePhase) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.NeonPink.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚨",
                fontSize = 32.sp,
                modifier = Modifier.alpha(alpha)
            )

            Column {
                Text(
                    text = "ALERTA CRÍTICA:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonPink
                )

                Text(
                    text = when (phase) {
                        RansomwarePhase.ENCRYPTING -> "Patrón de ransomware detectado"
                        RansomwarePhase.RANSOM_DEMAND -> "Sistema comprometido"
                        else -> ""
                    },
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Componente: File Encryption Status
 */
@Composable
fun FileEncryptionStatus(
    totalFiles: Int,
    filesEncrypted: Int,
    phase: RansomwarePhase
) {
    val progress = filesEncrypted.toFloat() / totalFiles

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.CardBg
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ESTADO DE ARCHIVOS:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (phase == RansomwarePhase.IDLE) {
                        "Sistema normal"
                    } else {
                        "🔍 $filesEncrypted archivos .txt siendo encriptados"
                    },
                    fontSize = 14.sp,
                    color = if (phase == RansomwarePhase.IDLE)
                        CyberColors.NeonGreen
                    else
                        CyberColors.NeonPink
                )
            }

            if (phase != RansomwarePhase.IDLE) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberColors.NeonPink,
                    trackColor = CyberColors.CardBg
                )

                Text(
                    text = "${(progress * 100).toInt()}% completado",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Componente: Attack Evidence Card
 */
@Composable
fun AttackEvidenceCard(
    timeOfInfection: String,
    origin: String,
    ransomAmount: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.CardBg
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "EVIDENCIAS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            EvidenceItem(
                icon = "⏰",
                label = "Tiempo de infección:",
                value = timeOfInfection
            )

            EvidenceItem(
                icon = "📍",
                label = "Origen:",
                value = origin
            )

            EvidenceItem(
                icon = "💰",
                label = "Rescate demandado:",
                value = ransomAmount
            )
        }
    }
}

/**
 * Componente: Evidence Item
 */
@Composable
fun EvidenceItem(icon: String, label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 20.sp)

        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Componente: Ransom Message
 */
@Composable
fun RansomMessage(ransom: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "💀",
                fontSize = 48.sp
            )

            Text(
                text = "TUS ARCHIVOS HAN SIDO CIFRADOS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonPink,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = "Pague $ransom en 24 horas o serán borrados",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Componente: Containment Plan
 */
@Composable
fun ContainmentPlan(
    onPlanExecuted: (Boolean) -> Unit
) {
    var selectedSteps by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showResult by remember { mutableStateOf(false) }

    val correctSteps = setOf("isolate", "backup", "notify")
    val allCorrectSelected = selectedSteps.containsAll(correctSteps) && !selectedSteps.contains("pay")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🎯 PLAN DE CONTENCIÓN:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonGreen
        )

        Text(
            text = "Selecciona las acciones correctas en orden:",
            fontSize = 13.sp,
            color = Color.White
        )

        ContainmentStepCard(
            stepNumber = 1,
            action = "Aislar dispositivo infectado de la red",
            id = "isolate",
            isSelected = selectedSteps.contains("isolate"),
            showResult = showResult,
            isCorrect = true,
            onToggle = {
                selectedSteps = if (selectedSteps.contains("isolate")) {
                    selectedSteps - "isolate"
                } else {
                    selectedSteps + "isolate"
                }
            }
        )

        ContainmentStepCard(
            stepNumber = 2,
            action = "Activar copias de seguridad",
            id = "backup",
            isSelected = selectedSteps.contains("backup"),
            showResult = showResult,
            isCorrect = true,
            onToggle = {
                selectedSteps = if (selectedSteps.contains("backup")) {
                    selectedSteps - "backup"
                } else {
                    selectedSteps + "backup"
                }
            }
        )

        ContainmentStepCard(
            stepNumber = 3,
            action = "Notificar a autoridades",
            id = "notify",
            isSelected = selectedSteps.contains("notify"),
            showResult = showResult,
            isCorrect = true,
            onToggle = {
                selectedSteps = if (selectedSteps.contains("notify")) {
                    selectedSteps - "notify"
                } else {
                    selectedSteps + "notify"
                }
            }
        )

        ContainmentStepCard(
            stepNumber = 4,
            action = "Pagar el rescate inmediatamente",
            id = "pay",
            isSelected = selectedSteps.contains("pay"),
            showResult = showResult,
            isCorrect = false,
            onToggle = {
                selectedSteps = if (selectedSteps.contains("pay")) {
                    selectedSteps - "pay"
                } else {
                    selectedSteps + "pay"
                }
            }
        )

        if (!showResult) {
            CyberButton(
                text = "🚨 EJECUTAR CONTENCIÓN",
                onClick = {
                    showResult = true
                    onPlanExecuted(allCorrectSelected)
                },
                enabled = selectedSteps.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            FeedbackMessage(
                isCorrect = allCorrectSelected,
                message = if (allCorrectSelected) {
                    "✅ PERFECTO! Contención exitosa: Aislaste el sistema, recuperaste backups y notificaste autoridades. NUNCA pagar rescate."
                } else if (selectedSteps.contains("pay")) {
                    "❌ Pagar el rescate financia a criminales y no garantiza recuperar archivos. Usa backups y notifica autoridades."
                } else {
                    "⚠️ Faltan pasos clave en tu plan de contención. Revisa las mejores prácticas."
                }
            )
        }
    }
}

/**
 * Componente: Containment Step Card
 */
@Composable
fun ContainmentStepCard(
    stepNumber: Int,
    action: String,
    id: String,
    isSelected: Boolean,
    showResult: Boolean,
    isCorrect: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor = when {
        showResult && isSelected && isCorrect -> CyberColors.NeonGreen.copy(alpha = 0.2f)
        showResult && isSelected && !isCorrect -> CyberColors.NeonPink.copy(alpha = 0.2f)
        isSelected -> CyberColors.NeonBlue.copy(alpha = 0.2f)
        else -> CyberColors.CardBg
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (!showResult) onToggle else {},
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (isSelected) "☑️" else "⬜",
                    fontSize = 24.sp
                )

                Column {
                    Text(
                        text = "[$stepNumber]",
                        fontSize = 12.sp,
                        color = CyberColors.NeonBlue
                    )
                    Text(
                        text = action,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            if (showResult && isSelected) {
                Text(
                    text = if (isCorrect) "✅" else "❌",
                    fontSize = 24.sp
                )
            }
        }
    }
}

/**
 * Fases del ransomware
 */
enum class RansomwarePhase {
    IDLE,
    ENCRYPTING,
    RANSOM_DEMAND
}