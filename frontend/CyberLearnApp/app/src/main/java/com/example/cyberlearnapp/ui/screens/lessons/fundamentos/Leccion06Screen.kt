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
fun Leccion06Screen(
    lessonId: Int,
    onComplete: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    val currentScreenIndex by viewModel.currentScreenIndex.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    when (currentScreenIndex) {
        0 -> Screen01_MissionBriefing(viewModel)
        1 -> Screen02_PhishingAnalysis(viewModel)
        2 -> Screen03_NetworkAudit(viewModel)
        3 -> Screen04_RansomwareDetection(viewModel)
        4 -> Screen05_DefenseImplementation(viewModel)
        5 -> Screen06_CommunicationClosure(viewModel)
        6 -> Screen07_MissionResults(viewModel)
        7 -> Screen08_CourseCompletion(viewModel, onComplete)
    }
}

@Composable
fun Screen01_MissionBriefing(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🚨 OPERACIÓN: ESCUDO CIUDADANO",
        screenNumber = 1,
        totalScreens = 8,
        onNext = { viewModel.nextScreen() },
        buttonText = "🚀 ACEPTAR MISIÓN"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "\"CyberLearn recibe alerta de PYME local\"",
                fontSize = 14.sp,
                color = CyberColors.NeonBlue
            )

            Text(
                text = "Tienda 'TecnoShop' - 15 empleados",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CyberColors.CardBg
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👤 CLIENTE: María González, dueña",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonGreen
                    )
                    Text(
                        text = "\"Recibimos emails extraños y el sistema va lento\"",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🎯 TU MISIÓN:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = "• Proteger a TecnoShop en 24 horas\n• Presupuesto: \$2,000\n• Tiempo estimado: 45 minutos",
                fontSize = 13.sp,
                color = Color.White,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🔍 EVIDENCIAS INICIALES:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                EvidenceItem("• Emails sospechosos a empleados")
                EvidenceItem("• Wi-Fi público sin seguridad")
                EvidenceItem("• Sistema lento últimas 48 horas")
            }
        }
    }
}

@Composable
fun EvidenceItem(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.9f)
    )
}

@Composable
fun Screen02_PhishingAnalysis(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🔍 FASE 1: INVESTIGACIÓN DE EMAILS",
        screenNumber = 2,
        totalScreens = 8,
        onNext = { viewModel.nextScreen() },
        buttonText = "📨 ANALIZAR SIGUIENTE"
    ) {
        PhishingSimulator(
            emailData = PhishingEmailData(
                from = "soporte@tecno-shop.com",
                subject = "URGENTE: Actualizar credenciales",
                body = """
                    Estimado empleado,
                    
                    Por seguridad, debe actualizar sus credenciales en el siguiente enlace:
                    
                    Tiene 24 horas o será suspendido.
                """.trimIndent(),
                link = "http://tecno-shop-update.com/login",
                redFlags = listOf(
                    RedFlag("domain", "Dominio diferente (tecno-shop-update.com)"),
                    RedFlag("http", "HTTP en lugar de HTTPS"),
                    RedFlag("threat", "Amenaza de suspensión"),
                    RedFlag("generic", "Falta de información específica")
                ),
                conclusionMessage = "4 señales claras de phishing empresarial"
            ),
            onComplete = { success ->
                viewModel.recordAnswer(2, success)
            }
        )
    }
}

@Composable
fun Screen03_NetworkAudit(viewModel: InteractiveLessonViewModel) {
    var selectedActions by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showResult by remember { mutableStateOf(false) }

    val correctActions = setOf("wpa3", "disconnect", "close_port")
    val allCorrect = selectedActions.containsAll(correctActions) && !selectedActions.contains("block_internet")

    ScreenContainer(
        title = "🌐 FASE 2: AUDITORÍA DE RED",
        screenNumber = 3,
        totalScreens = 8,
        onNext = {
            viewModel.recordAnswer(3, allCorrect)
            viewModel.nextScreen()
        },
        buttonText = "🔍 INVESTIGAR TRÁFICO",
        buttonEnabled = showResult
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "ESCANEOS ENCONTRADOS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

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
                    NetworkFinding("📶 Red Wi-Fi: \"TecnoShop_Free\" (Abierta)")
                    NetworkFinding("💻 3 dispositivos desconocidos conectados")
                    NetworkFinding("📊 Tráfico anormal: 2 GB/hora (Normal: 200MB)")
                    NetworkFinding("⚠️ Puerto 3389 (Remote Desktop) ABIERTO")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🎯 SELECCIONA ACCIONES INMEDIATAS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            ActionCheckbox(
                action = "Cambiar a WPA3 con contraseña fuerte",
                id = "wpa3",
                isSelected = selectedActions.contains("wpa3"),
                onToggle = {
                    selectedActions = if (selectedActions.contains("wpa3")) {
                        selectedActions - "wpa3"
                    } else {
                        selectedActions + "wpa3"
                    }
                    showResult = selectedActions.isNotEmpty()
                }
            )

            ActionCheckbox(
                action = "Desconectar dispositivos no autorizados",
                id = "disconnect",
                isSelected = selectedActions.contains("disconnect"),
                onToggle = {
                    selectedActions = if (selectedActions.contains("disconnect")) {
                        selectedActions - "disconnect"
                    } else {
                        selectedActions + "disconnect"
                    }
                    showResult = selectedActions.isNotEmpty()
                }
            )

            ActionCheckbox(
                action = "Cerrar puerto 3389",
                id = "close_port",
                isSelected = selectedActions.contains("close_port"),
                onToggle = {
                    selectedActions = if (selectedActions.contains("close_port")) {
                        selectedActions - "close_port"
                    } else {
                        selectedActions + "close_port"
                    }
                    showResult = selectedActions.isNotEmpty()
                }
            )

            ActionCheckbox(
                action = "Bloquear todo el internet",
                id = "block_internet",
                isSelected = selectedActions.contains("block_internet"),
                onToggle = {
                    selectedActions = if (selectedActions.contains("block_internet")) {
                        selectedActions - "block_internet"
                    } else {
                        selectedActions + "block_internet"
                    }
                    showResult = selectedActions.isNotEmpty()
                }
            )

            if (showResult) {
                FeedbackMessage(
                    isCorrect = allCorrect,
                    message = if (allCorrect) {
                        "✅ Correcto! Protegiste la red sin interrumpir operaciones"
                    } else {
                        "⚠️ Revisa: bloquear internet paraliza el negocio"
                    }
                )
            }
        }
    }
}

@Composable
fun NetworkFinding(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color.White
    )
}

@Composable
fun ActionCheckbox(
    action: String,
    id: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                CyberColors.NeonBlue.copy(alpha = 0.2f)
            else
                CyberColors.CardBg
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isSelected) "☑️" else "⬜",
                fontSize = 20.sp
            )
            Text(
                text = action,
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun Screen04_RansomwareDetection(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🦠 FASE 3: ANÁLISIS DEL SISTEMA",
        screenNumber = 4,
        totalScreens = 8,
        onNext = { viewModel.nextScreen() },
        buttonText = "Siguiente"
    ) {
        RansomwareSimulator(
            totalFiles = 1200,
            encryptionTimeMs = 2000,
            ransom = "0.5 BTC",
            onComplete = { success ->
                viewModel.recordAnswer(4, success)
            }
        )
    }
}

@Composable
fun Screen05_DefenseImplementation(viewModel: InteractiveLessonViewModel) {
    var budget by remember { mutableStateOf(2000) }
    var selectedItems by remember { mutableStateOf<Set<String>>(emptySet()) }
    var totalSpent by remember { mutableStateOf(0) }

    ScreenContainer(
        title = "🛡️ FASE 4: FORTIFICACIÓN",
        screenNumber = 5,
        totalScreens = 8,
        onNext = { viewModel.nextScreen() },
        buttonText = "💾 IMPLEMENTAR"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PRESUPUESTO RESTANTE: $$budget",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            BudgetItem(
                name = "Antivirus Empresarial",
                cost = 400,
                period = "/año",
                isSelected = selectedItems.contains("antivirus"),
                onToggle = { /* Logic */ }
            )

            BudgetItem(
                name = "Capacitación Empleados",
                cost = 600,
                period = "(una vez)",
                isSelected = selectedItems.contains("training"),
                onToggle = { /* Logic */ }
            )

            BudgetItem(
                name = "Backup Automático",
                cost = 150,
                period = "/mes",
                isSelected = selectedItems.contains("backup"),
                onToggle = { /* Logic */ }
            )
        }
    }
}

@Composable
fun BudgetItem(
    name: String,
    cost: Int,
    period: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                CyberColors.NeonGreen.copy(alpha = 0.2f)
            else
                CyberColors.CardBg
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = name, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "$$cost $period", fontSize = 12.sp, color = CyberColors.NeonBlue)
            }
            Text(text = if (isSelected) "✅" else "⬜", fontSize = 24.sp)
        }
    }
}

@Composable
fun Screen06_CommunicationClosure(viewModel: InteractiveLessonViewModel) {
    ClassifierScreen(
        title = "📊 FASE 5: REPORTE FINAL",
        scenario = "Después de tu intervención, ¿qué principio CIA fue MÁS afectado en TecnoShop?",
        categories = listOf(
            Category("confidentiality", "CONFIDENCIALIDAD", "🔒", "#00FF41"),
            Category("integrity", "INTEGRIDAD", "📊", "#00D9FF"),
            Category("availability", "DISPONIBILIDAD", "⏰", "#FF006E")
        ),
        correctCategoryId = "confidentiality",
        correctFeedback = "✅ CORRECTO! Datos de clientes estaban en riesgo de exposición",
        incorrectFeedback = "❌ Piensa: ¿qué estaba siendo comprometido?",
        screenNumber = 6,
        totalScreens = 8,
        onAnswerRecorded = { isCorrect -> viewModel.recordAnswer(6, isCorrect) },
        onNext = { viewModel.nextScreen() }
    )
}

@Composable
fun Screen07_MissionResults(viewModel: InteractiveLessonViewModel) {
    val answers by viewModel.userAnswers.collectAsState()
    val correctCount = answers.count { it.value }
    val totalQuestions = answers.size
    val score = if (totalQuestions > 0) (correctCount * 100 / totalQuestions) else 100

    SummaryWithScoreScreen(
        lessonTitle = "EVALUACIÓN FINAL",
        score = score,
        correctAnswers = correctCount,
        totalQuestions = totalQuestions,
        achievements = listOf(
            "🔍 Fase 1 (Phishing): Detectado",
            "🌐 Fase 2 (Red): Asegurada",
            "🦠 Fase 3 (Ransomware): Contenido",
            "🛡️ Fase 4 (Defensas): Implementadas"
        ),
        xpEarned = score * 3,
        badgeName = "Analista Certificado",
        strengths = if (score >= 80) listOf(
            "Detección temprana de amenazas",
            "Toma de decisiones efectiva"
        ) else null,
        improvements = if (score < 80) listOf(
            "Revisa principios de la Tríada CIA",
            "Practica más análisis de phishing"
        ) else null,
        screenNumber = 7,
        totalScreens = 8,
        onComplete = { viewModel.nextScreen() }
    )
}

@Composable
fun Screen08_CourseCompletion(viewModel: InteractiveLessonViewModel, onComplete: () -> Unit) {
    val xpEarned by viewModel.xpEarned.collectAsState()

    CourseCompletionScreen(
        courseTitle = "FUNDAMENTOS DE CIBERSEGURIDAD",
        totalXpEarned = xpEarned,
        totalLessonsCompleted = 6,
        badges = listOf(
            BadgeInfo("🛡️", "Primer Respondedor"),
            BadgeInfo("📧", "Cazador de Phishing"),
            BadgeInfo("🔒", "Contenedor de Ransomware"),
            BadgeInfo("📱", "Guardian Inalámbrico"),
            BadgeInfo("🎯", "Estratega CIA"),
            BadgeInfo("🏆", "Analista Certificado")
        ),
        overallScore = 85,
        nextCourseTitle = "Seguridad de Redes",
        screenNumber = 8,
        totalScreens = 8,
        onComplete = onComplete
    )
}