package com.example.cyberlearnapp.ui.screens.lessons.simulators

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.ui.screens.lessons.shared.*

/**
 * 🔐 SIMULADOR: PASSWORD STRENGTH METER
 * Medidor interactivo de fortaleza de contraseñas
 *
 * Usado en:
 * - Lección 5: Principios de Seguridad
 * - Lección 6: Evaluación de contraseñas
 */
@Composable
fun PasswordStrengthMeter(
    targetCase: PasswordCase,
    onComplete: (Boolean) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var analyzed by remember { mutableStateOf(false) }

    val strength = calculatePasswordStrength(password)
    val meetsRequirements = strength.score >= targetCase.minimumScore

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Caso de prueba
        CaseCard(targetCase = targetCase)

        // Input de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                analyzed = false
            },
            label = { Text("Ingresa la contraseña", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Text(
                        text = if (showPassword) "👁️" else "🔒",
                        fontSize = 20.sp
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberColors.NeonGreen,
                unfocusedBorderColor = CyberColors.BorderGlow,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        // Medidor visual
        if (password.isNotEmpty()) {
            StrengthMeterVisual(strength = strength)

            // Requisitos
            RequirementsChecklist(
                password = password,
                requirements = strength.requirements
            )

            // Tiempo para crackear
            TimeToCrackCard(strength = strength)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de análisis
        if (!analyzed) {
            CyberButton(
                text = "🔍 ANALIZAR CONTRASEÑA",
                onClick = {
                    analyzed = true
                    onComplete(meetsRequirements)
                },
                enabled = password.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            FeedbackMessage(
                isCorrect = meetsRequirements,
                message = if (meetsRequirements) {
                    "✅ Contraseña ${strength.level.name}: ${strength.feedback}"
                } else {
                    "❌ Contraseña ${strength.level.name}: ${strength.feedback}\n\nObjetivo: ${targetCase.explanation}"
                }
            )
        }
    }
}

/**
 * Componente: Case Card
 */
@Composable
fun CaseCard(targetCase: PasswordCase) {
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
                text = "🎯 CASO: ${targetCase.title}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = targetCase.description,
                fontSize = 13.sp,
                color = Color.White,
                lineHeight = 17.sp
            )

            Text(
                text = "Requisito: ${targetCase.requirement}",
                fontSize = 12.sp,
                color = CyberColors.NeonBlue
            )
        }
    }
}

/**
 * Componente: Strength Meter Visual
 */
@Composable
fun StrengthMeterVisual(strength: PasswordStrength) {
    val progress = strength.score / 100f

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Fortaleza:",
                fontSize = 13.sp,
                color = Color.White
            )

            Text(
                text = strength.level.displayName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = strength.level.color
            )
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = strength.level.color,
            trackColor = CyberColors.CardBg
        )
    }
}

/**
 * Componente: Requirements Checklist
 */
@Composable
fun RequirementsChecklist(
    password: String,
    requirements: List<PasswordRequirement>
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
                text = "Requisitos:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            requirements.forEach { req ->
                RequirementItem(
                    requirement = req,
                    isMet = checkRequirement(password, req)
                )
            }
        }
    }
}

/**
 * Componente: Requirement Item
 */
@Composable
fun RequirementItem(requirement: PasswordRequirement, isMet: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = if (isMet) "✅" else "⬜",
            fontSize = 16.sp
        )

        Text(
            text = requirement.description,
            fontSize = 12.sp,
            color = if (isMet) CyberColors.NeonGreen else Color.White.copy(alpha = 0.7f)
        )
    }
}

/**
 * Componente: Time To Crack Card
 */
@Composable
fun TimeToCrackCard(strength: PasswordStrength) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (strength.level) {
                PasswordLevel.WEAK -> CyberColors.NeonPink.copy(alpha = 0.2f)
                PasswordLevel.MODERATE -> CyberColors.NeonBlue.copy(alpha = 0.2f)
                PasswordLevel.STRONG -> CyberColors.NeonGreen.copy(alpha = 0.2f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "⏱️",
                fontSize = 32.sp
            )

            Column {
                Text(
                    text = "Tiempo para crackear:",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Text(
                    text = strength.timeToCrack,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Función: Calcular fortaleza de contraseña
 */
fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) {
        return PasswordStrength(
            score = 0,
            level = PasswordLevel.WEAK,
            feedback = "Ingresa una contraseña",
            timeToCrack = "N/A",
            requirements = emptyList()
        )
    }

    var score = 0
    val requirements = mutableListOf<PasswordRequirement>()

    // Longitud
    val lengthReq = PasswordRequirement("Al menos 8 caracteres")
    requirements.add(lengthReq)
    if (password.length >= 8) score += 20
    if (password.length >= 12) score += 10
    if (password.length >= 16) score += 10

    // Mayúsculas
    val upperReq = PasswordRequirement("Al menos 1 mayúscula")
    requirements.add(upperReq)
    if (password.any { it.isUpperCase() }) score += 15

    // Minúsculas
    val lowerReq = PasswordRequirement("Al menos 1 minúscula")
    requirements.add(lowerReq)
    if (password.any { it.isLowerCase() }) score += 15

    // Números
    val digitReq = PasswordRequirement("Al menos 1 número")
    requirements.add(digitReq)
    if (password.any { it.isDigit() }) score += 15

    // Símbolos
    val symbolReq = PasswordRequirement("Al menos 1 símbolo (!@#$%)")
    requirements.add(symbolReq)
    if (password.any { !it.isLetterOrDigit() }) score += 15

    // Penalizaciones
    if (password.matches(Regex("^[0-9]+$"))) score -= 20 // Solo números
    if (password.matches(Regex("^[a-z]+$"))) score -= 20 // Solo minúsculas
    if (password.contains("123") || password.contains("abc")) score -= 10 // Secuencias

    score = score.coerceIn(0, 100)

    val level = when {
        score < 40 -> PasswordLevel.WEAK
        score < 70 -> PasswordLevel.MODERATE
        else -> PasswordLevel.STRONG
    }

    val timeToCrack = when {
        score < 30 -> "Segundos"
        score < 50 -> "Minutos a horas"
        score < 70 -> "Días a meses"
        else -> "Años a siglos"
    }

    val feedback = when (level) {
        PasswordLevel.WEAK -> "Muy vulnerable. Agrega longitud y variedad de caracteres."
        PasswordLevel.MODERATE -> "Aceptable, pero puede mejorar. Considera más longitud."
        PasswordLevel.STRONG -> "Excelente! Difícil de crackear."
    }

    return PasswordStrength(
        score = score,
        level = level,
        feedback = feedback,
        timeToCrack = timeToCrack,
        requirements = requirements
    )
}

/**
 * Función: Verificar requisito
 */
fun checkRequirement(password: String, requirement: PasswordRequirement): Boolean {
    return when {
        requirement.description.contains("8 caracteres") -> password.length >= 8
        requirement.description.contains("mayúscula") -> password.any { it.isUpperCase() }
        requirement.description.contains("minúscula") -> password.any { it.isLowerCase() }
        requirement.description.contains("número") -> password.any { it.isDigit() }
        requirement.description.contains("símbolo") -> password.any { !it.isLetterOrDigit() }
        else -> false
    }
}

/**
 * Modelos de datos
 */
data class PasswordStrength(
    val score: Int,
    val level: PasswordLevel,
    val feedback: String,
    val timeToCrack: String,
    val requirements: List<PasswordRequirement>
)

enum class PasswordLevel(val displayName: String, val color: Color) {
    WEAK("DÉBIL", CyberColors.NeonPink),
    MODERATE("MODERADA", CyberColors.NeonBlue),
    STRONG("FUERTE", CyberColors.NeonGreen)
}

data class PasswordRequirement(val description: String)

data class PasswordCase(
    val title: String,
    val description: String,
    val requirement: String,
    val minimumScore: Int,
    val explanation: String
)

/**
 * Casos de ejemplo
 */
val TARGET_PASSWORD_CASE = PasswordCase(
    title = "Acceso a Sistema de Salud",
    description = "Crea una contraseña segura para acceder a historiales médicos sensibles",
    requirement = "Contraseña FUERTE (70+ puntos)",
    minimumScore = 70,
    explanation = "Datos médicos requieren contraseñas fuertes con mínimo 12 caracteres, mayúsculas, minúsculas, números y símbolos"
)

val BANK_PASSWORD_CASE = PasswordCase(
    title = "Banca en Línea",
    description = "Configura contraseña para tu cuenta bancaria",
    requirement = "Contraseña FUERTE (70+ puntos)",
    minimumScore = 70,
    explanation = "Finanzas requieren máxima seguridad: 16+ caracteres, variedad completa"
)

val EMAIL_PASSWORD_CASE = PasswordCase(
    title = "Correo Electrónico",
    description = "Contraseña para tu email personal",
    requirement = "Contraseña MODERADA (50+ puntos)",
    minimumScore = 50,
    explanation = "Email es punto de recuperación de otras cuentas, necesita protección adecuada"
)