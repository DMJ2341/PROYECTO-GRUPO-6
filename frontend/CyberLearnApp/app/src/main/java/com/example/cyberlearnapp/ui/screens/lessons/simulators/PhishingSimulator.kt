package com.example.cyberlearnapp.ui.screens.lessons.simulators

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * 📧 SIMULADOR: PHISHING DETECTOR
 * Permite analizar emails/SMS falsos y marcar señales de peligro
 *
 * Usado en:
 * - Lección 2: Simulador de Equifax
 * - Lección 4: Detector de SMS falsos
 */
@Composable
fun PhishingSimulator(
    emailData: PhishingEmailData,
    onComplete: (Boolean) -> Unit
) {
    var selectedFlags by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showResult by remember { mutableStateOf(false) }

    val allFlagsFound = selectedFlags.containsAll(
        emailData.redFlags.map { it.id }.toSet()
    )

    val incorrectFlags = selectedFlags.filter { flagId ->
        emailData.redFlags.none { it.id == flagId }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Email/SMS a analizar
        EmailCard(
            from = emailData.from,
            subject = emailData.subject,
            body = emailData.body,
            link = emailData.link
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "👆 SEÑALES DE PELIGRO:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonGreen
        )

        // Lista de posibles señales
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            emailData.redFlags.forEach { flag ->
                CheckableFlag(
                    flag = flag,
                    isSelected = selectedFlags.contains(flag.id),
                    showResult = showResult,
                    onToggle = {
                        selectedFlags = if (selectedFlags.contains(flag.id)) {
                            selectedFlags - flag.id
                        } else {
                            selectedFlags + flag.id
                        }
                    }
                )
            }

            // Opciones incorrectas (señales que NO están)
            emailData.incorrectOptions?.forEach { option ->
                CheckableFlag(
                    flag = option,
                    isSelected = selectedFlags.contains(option.id),
                    showResult = showResult,
                    isIncorrect = true,
                    onToggle = {
                        selectedFlags = if (selectedFlags.contains(option.id)) {
                            selectedFlags - option.id
                        } else {
                            selectedFlags + option.id
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de verificación
        if (!showResult) {
            CyberButton(
                text = "🔍 VERIFICAR ANÁLISIS",
                onClick = {
                    showResult = true
                    onComplete(allFlagsFound && incorrectFlags.isEmpty())
                },
                enabled = selectedFlags.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Resultado
            val correctCount = selectedFlags.count { flagId ->
                emailData.redFlags.any { it.id == flagId }
            }
            val totalFlags = emailData.redFlags.size

            FeedbackMessage(
                isCorrect = allFlagsFound && incorrectFlags.isEmpty(),
                message = if (allFlagsFound && incorrectFlags.isEmpty()) {
                    "✅ $correctCount/$totalFlags SEÑALES - ¡ES PHISHING!\n${emailData.conclusionMessage}"
                } else if (incorrectFlags.isNotEmpty()) {
                    "❌ Marcaste señales que no están presentes. Revisa el mensaje nuevamente."
                } else {
                    "⚠️ Encontraste $correctCount de $totalFlags señales. Revisa con más detalle."
                }
            )
        }
    }
}

/**
 * 📱 VARIANTE: SMS Phishing (SMiShing)
 */
@Composable
fun SMiShingSimulator(
    smsData: SMSData,
    onComplete: (Boolean) -> Unit
) {
    var selectedFlags by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showResult by remember { mutableStateOf(false) }

    val allFlagsFound = selectedFlags.containsAll(
        smsData.redFlags.map { it.id }.toSet()
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SMS a analizar
        SMSCard(
            from = smsData.from,
            message = smsData.message
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "👆 MARCA LOS ERRORES:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonGreen
        )

        // Checkboxes de señales
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            smsData.redFlags.forEach { flag ->
                CheckableFlag(
                    flag = flag,
                    isSelected = selectedFlags.contains(flag.id),
                    showResult = showResult,
                    onToggle = {
                        selectedFlags = if (selectedFlags.contains(flag.id)) {
                            selectedFlags - flag.id
                        } else {
                            selectedFlags + flag.id
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!showResult) {
            CyberButton(
                text = "🔍 VERIFICAR",
                onClick = {
                    showResult = true
                    onComplete(allFlagsFound)
                },
                enabled = selectedFlags.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            FeedbackMessage(
                isCorrect = allFlagsFound,
                message = if (allFlagsFound) {
                    "✅ ${smsData.redFlags.size}/${smsData.redFlags.size} - ¡ES SMISHING!\n${smsData.advice}"
                } else {
                    "⚠️ Encontraste ${selectedFlags.size} de ${smsData.redFlags.size}. Revisa nuevamente."
                }
            )
        }
    }
}

/**
 * Componente: Email Card
 */
@Composable
fun EmailCard(
    from: String,
    subject: String,
    body: String,
    link: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.CardBg
        ),
        border = BorderStroke(1.dp, CyberColors.BorderGlow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header del email
            Text(
                text = "De: $from",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Text(
                text = "Asunto: $subject",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Divider(color = CyberColors.BorderGlow, thickness = 1.dp)

            // Cuerpo del email
            Text(
                text = body,
                fontSize = 13.sp,
                color = Color.White,
                lineHeight = 17.sp
            )

            // Link (si existe)
            if (link != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "👇 $link",
                    fontSize = 12.sp,
                    color = CyberColors.NeonBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Componente: SMS Card
 */
@Composable
fun SMSCard(
    from: String,
    message: String
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
                text = "De: $from",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            Text(
                text = message,
                fontSize = 13.sp,
                color = Color.White,
                lineHeight = 17.sp
            )
        }
    }
}

/**
 * Componente: Checkable Flag (CORREGIDO)
 */
@Composable
fun CheckableFlag(
    flag: RedFlag,
    isSelected: Boolean,
    showResult: Boolean,
    isIncorrect: Boolean = false,
    onToggle: () -> Unit
) {
    val backgroundColor = when {
        showResult && isSelected && !isIncorrect -> CyberColors.NeonGreen.copy(alpha = 0.2f)
        showResult && isSelected && isIncorrect -> CyberColors.NeonPink.copy(alpha = 0.2f)
        isSelected -> CyberColors.NeonBlue.copy(alpha = 0.2f)
        else -> CyberColors.CardBg
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (!showResult) {
                onToggle()
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) CyberColors.NeonGreen else CyberColors.BorderGlow
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isSelected) "☑️" else "⬜",
                    fontSize = 20.sp
                )

                Text(
                    text = flag.description,
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }

            if (showResult && isSelected) {
                Text(
                    text = if (isIncorrect) "❌" else "✅",
                    fontSize = 20.sp
                )
            }
        }
    }
}

/**
 * Modelos de datos
 */
data class PhishingEmailData(
    val from: String,
    val subject: String,
    val body: String,
    val link: String? = null,
    val redFlags: List<RedFlag>,
    val incorrectOptions: List<RedFlag>? = null,
    val conclusionMessage: String
)

data class SMSData(
    val from: String,
    val message: String,
    val redFlags: List<RedFlag>,
    val advice: String
)

data class RedFlag(
    val id: String,
    val description: String
)

/**
 * Datos de ejemplo para Equifax (Lección 2)
 */
val EQUIFAX_PHISHING_DATA = PhishingEmailData(
    from = "rh@equifax-com.tk",
    subject = "URGENTE: Actualización de nómina",
    body = """
        Hola Carlos,
        
        Necesitamos que verifique sus datos de nómina antes del viernes.
        
        👇 Haga clic aquí:
    """.trimIndent(),
    link = "http://equifax-payroll.tk/update",
    redFlags = listOf(
        RedFlag("domain", "Dominio .tk (no .com)"),
        RedFlag("url", "URL sospechosa (HTTP no HTTPS)"),
        RedFlag("urgency", "Urgencia artificial")
    ),
    incorrectOptions = listOf(
        RedFlag("logo", "Falta logo oficial")
    ),
    conclusionMessage = "Dominio, URL y urgencia son señales claras de phishing"
)

/**
 * Datos de ejemplo para SMiShing (Lección 1)
 */
val SMISHING_EXAMPLE_DATA = SMSData(
    from = "Banco-Ofiicial",
    message = "ALERTA: Actividad sospechosa en su cuenta. Bloquearemos su tarjeta. Verifique ahora: http://banco-ofiicial.com/secure",
    redFlags = listOf(
        RedFlag("typo", "\"Ofiicial\" con 1 'f' (Error ortográfico)"),
        RedFlag("http", "Enlace HTTP (no HTTPS)"),
        RedFlag("urgency", "Urgencia artificial"),
        RedFlag("sender", "Remitente no oficial")
    ),
    advice = "Reporta el mensaje y elimínalo. Nunca hagas clic en enlaces sospechosos."
)