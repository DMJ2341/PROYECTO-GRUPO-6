package com.example.cyberlearnapp.ui.screens.lessons.fundamentos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.network.models.Category
import com.example.cyberlearnapp.network.models.QuizOption
import com.example.cyberlearnapp.ui.screens.lessons.shared.*
import com.example.cyberlearnapp.ui.screens.lessons.templates.*
import com.example.cyberlearnapp.ui.screens.lessons.simulators.*
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel

@Composable
fun Leccion05Screen(
    lessonId: Int,
    onComplete: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    val currentScreenIndex by viewModel.currentScreenIndex.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    when (currentScreenIndex) {
        0 -> Screen01_TargetCase(viewModel)
        1 -> Screen02_CIATriad(viewModel)
        2 -> Screen03_CIADecisions(viewModel)
        3 -> Screen04_PasswordStrength(viewModel)
        4 -> Screen05_SecurityPrinciples(viewModel)
        5 -> Screen06_SummaryL5(viewModel, onComplete)
    }
}

@Composable
fun Screen01_TargetCase(viewModel: InteractiveLessonViewModel) {
    StoryHookScreen(
        caseTitle = "🚨 CASO REAL: TARGET (2013)",
        date = "Diciembre 2013 - Temporada Navideña",
        description = "Clientas descubren cargos no reconocidos",
        impactCards = listOf(
            ImpactCard("💳", "40M", "Tarjetas Robadas", "Información completa de pago comprometida"),
            ImpactCard("👤", "70M", "Clientes Expuestos", "Datos personales filtrados"),
            ImpactCard("🔓", "1", "Contraseña Débil", "Contratista usaba 'password1234' para acceso remoto")
        ),
        hookQuestion = "🔍 ¿QUÉ PRINCIPIOS SE VIOLARON?",
        screenNumber = 1,
        totalScreens = 6,
        buttonText = "🎯 ANALIZAR LA TRÍADA CIA",
        onNext = { viewModel.nextScreen() }
    )
}

@Composable
fun Screen02_CIATriad(viewModel: InteractiveLessonViewModel) {
    var selectedPrinciple by remember { mutableStateOf<String?>(null) }

    ScreenContainer(
        title = "🛡️ LA TRÍADA CIA - BASE DE LA SEGURIDAD",
        screenNumber = 2,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "🎯 PRACTICAR DECISIONES"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "👆 TOCA CADA PRINCIPIO:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            CIAPrincipleCard(
                icon = "🔒",
                title = "CONFIDENCIALIDAD",
                description = "Datos solo para autorizados",
                example = "Target: Hackers accedieron a datos privados de clientes",
                isSelected = selectedPrinciple == "confidentiality",
                onClick = { selectedPrinciple = "confidentiality" }
            )

            CIAPrincipleCard(
                icon = "📊",
                title = "INTEGRIDAD",
                description = "Datos exactos y sin modificar",
                example = "Target: No modificaron datos, pero los robaron",
                isSelected = selectedPrinciple == "integrity",
                onClick = { selectedPrinciple = "integrity" }
            )

            CIAPrincipleCard(
                icon = "⏰",
                title = "DISPONIBILIDAD",
                description = "Acceso cuando se necesita",
                example = "Target: Sistemas funcionaban durante el robo",
                isSelected = selectedPrinciple == "availability",
                onClick = { selectedPrinciple = "availability" }
            )

            if (selectedPrinciple != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = CyberColors.NeonGreen.copy(alpha = 0.2f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎯 EN TARGET: FALLÓ LA CONFIDENCIALIDAD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberColors.NeonGreen
                        )
                        Text(
                            text = "Datos expuestos = Confidencialidad vulnerada",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CIAPrincipleCard(
    icon: String,
    title: String,
    description: String,
    example: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                CyberColors.NeonGreen.copy(alpha = 0.2f)
            else
                CyberColors.CardBg
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = icon, fontSize = 32.sp)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) CyberColors.NeonGreen else Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White
                )
                if (isSelected) {
                    Text(
                        text = "→ $example",
                        fontSize = 11.sp,
                        color = CyberColors.NeonBlue
                    )
                }
            }
        }
    }
}

@Composable
fun Screen03_CIADecisions(viewModel: InteractiveLessonViewModel) {
    QuizScreen(
        title = "🎯 SIMULADOR: ¿QUÉ PRINCIPIO PROTEGER?",
        question = "ESCENARIO 1: Hospital - Sistema de Pacientes\n\n¿Qué es MÁS importante?",
        options = listOf(
            QuizOption(
                id = "confidentiality",
                text = "🔒 Confidencialidad - Privacidad de datos médicos",
                isCorrect = false,
                feedback = "❌ Importante, pero en hospitales hay algo más crítico"
            ),
            QuizOption(
                id = "integrity",
                text = "📊 Integridad - Datos médicos correctos",
                isCorrect = true,
                feedback = "✅ CORRECTO! En hospitales, datos médicos incorrectos pueden ser mortales. Un medicamento mal registrado = muerte."
            ),
            QuizOption(
                id = "availability",
                text = "⏰ Disponibilidad - Acceso inmediato",
                isCorrect = false,
                feedback = "❌ Importante, pero la integridad es más crítica"
            )
        ),
        explanation = "En bancos: 🔒 Confidencialidad | En emergencias: ⏰ Disponibilidad",
        screenNumber = 3,
        totalScreens = 6,
        onAnswerRecorded = { isCorrect -> viewModel.recordAnswer(3, isCorrect) },
        onNext = { viewModel.nextScreen() }
    )
}

@Composable
fun Screen04_PasswordStrength(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🔐 MEDIDOR DE CONTRASEÑAS",
        screenNumber = 4,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "Siguiente"
    ) {
        PasswordStrengthMeter(
            targetCase = TARGET_PASSWORD_CASE,
            onComplete = { success ->
                viewModel.recordAnswer(4, success)
            }
        )
    }
}

@Composable
fun Screen05_SecurityPrinciples(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🛡️ OTROS PRINCIPIOS CLAVE",
        screenNumber = 5,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "🚀 VER RESUMEN"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Más allá de CIA:",
                fontSize = 14.sp,
                color = Color.White
            )

            SecurityPrincipleCard(
                icon = "👤",
                title = "NO REPUDIO",
                description = "Prueba de que una acción ocurrió",
                example = "Firmas digitales, logs de auditoría"
            )

            SecurityPrincipleCard(
                icon = "🔑",
                title = "AUTENTICACIÓN",
                description = "Verificar identidad del usuario",
                example = "Contraseñas, biometría, 2FA"
            )

            SecurityPrincipleCard(
                icon = "🚪",
                title = "AUTORIZACIÓN",
                description = "Permisos de acceso a recursos",
                example = "Roles, ACLs, políticas de acceso"
            )

            SecurityPrincipleCard(
                icon = "👁️",
                title = "AUDITORÍA",
                description = "Registro de eventos de seguridad",
                example = "Logs, monitoreo, análisis forense"
            )

            SecurityPrincipleCard(
                icon = "🔄",
                title = "DEFENSA EN PROFUNDIDAD",
                description = "Múltiples capas de protección",
                example = "Firewall + Antivirus + IDS + Backups"
            )

            SecurityPrincipleCard(
                icon = "🔒",
                title = "MÍNIMO PRIVILEGIO",
                description = "Solo permisos necesarios",
                example = "Usuario estándar vs Administrador"
            )
        }
    }
}

@Composable
fun SecurityPrincipleCard(
    icon: String,
    title: String,
    description: String,
    example: String
) {
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
            Text(text = icon, fontSize = 28.sp)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonGreen
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = "Ej: $example",
                    fontSize = 11.sp,
                    color = CyberColors.NeonBlue
                )
            }
        }
    }
}

@Composable
fun Screen06_SummaryL5(viewModel: InteractiveLessonViewModel, onComplete: () -> Unit) {
    val xpEarned by viewModel.xpEarned.collectAsState()

    SummaryScreen(
        lessonTitle = "LECCIÓN 5",
        achievements = listOf(
            "🔒 Tríada CIA (Confidencialidad, Integridad, Disponibilidad)",
            "🎯 Decisiones de seguridad contextuales",
            "🔐 Contraseñas fuertes y medición",
            "🛡️ 6 principios adicionales de seguridad"
        ),
        statistics = listOf(
            StatisticItem("🔒", "33%", "Confiden-\ncialidad"),
            StatisticItem("📊", "33%", "Integridad"),
            StatisticItem("⏰", "34%", "Disponi-\nbilidad")
        ),
        xpEarned = xpEarned,
        badgeName = "Estratega CIA",
        nextLessonTitle = "Evaluación Final",
        screenNumber = 6,
        totalScreens = 6,
        onComplete = onComplete
    )
}