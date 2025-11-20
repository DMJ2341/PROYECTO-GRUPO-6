package com.example.cyberlearnapp.ui.screens.lessons.fundamentos

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.network.models.Category
import com.example.cyberlearnapp.ui.screens.lessons.shared.*
import com.example.cyberlearnapp.ui.screens.lessons.templates.*
import com.example.cyberlearnapp.ui.screens.lessons.simulators.*
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel

/**
 * 📱 LECCIÓN 2: INGENIERÍA SOCIAL Y ENGAÑO
 * 6 pantallas interactivas
 */
@Composable
fun Leccion02Screen(
    lessonId: Int,
    onComplete: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    val currentScreenIndex by viewModel.currentScreenIndex.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    when (currentScreenIndex) {
        0 -> Screen01_Equifax(viewModel)
        1 -> Screen02_PhishingSimulator(viewModel)
        2 -> Screen03_TiposIngenieriaSocial(viewModel)
        3 -> Screen04_ShoulderSurfing(viewModel)
        4 -> Screen05_Pretexting(viewModel)
        5 -> Screen06_SummaryL2(viewModel, onComplete)
    }
}

// ============================================
// PANTALLA 1: STORY HOOK - EQUIFAX
// ============================================
@Composable
fun Screen01_Equifax(viewModel: InteractiveLessonViewModel) {
    StoryHookScreen(
        caseTitle = "🚨 CASO REAL: EQUIFAX (2017)",
        date = "Marzo 2017 - Oficina de Atlanta",
        description = "Un empleado recibe email de \"recursos humanos\"",
        impactCards = listOf(
            ImpactCard(
                icon = "👤",
                value = "147M",
                label = "Personas Afectadas",
                detail = "44% de la población adulta de EE.UU. expuesta"
            ),
            ImpactCard(
                icon = "💳",
                value = "209K",
                label = "Tarjetas Robadas",
                detail = "Números de tarjetas de crédito comprometidos"
            ),
            ImpactCard(
                icon = "💰",
                value = "$700M",
                label = "Multa Record",
                detail = "La mayor multa por violación de datos en la historia"
            )
        ),
        hookQuestion = "🔍 ¿CÓMO UN SOLO CLIC CAUSÓ ESTO?",
        screenNumber = 1,
        totalScreens = 6,
        buttonText = "🎯 ANALIZAR EL ATAQUE",
        onNext = { viewModel.nextScreen() }
    )
}

// ============================================
// PANTALLA 2: SIMULADOR DE PHISHING
// ============================================
@Composable
fun Screen02_PhishingSimulator(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🔍 SIMULADOR: ENCUENTRA LOS 3 ERRORES",
        screenNumber = 2,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "Siguiente"
    ) {
        PhishingSimulator(
            emailData = EQUIFAX_PHISHING_DATA,
            onComplete = { success ->
                viewModel.recordAnswer(2, success)
            }
        )
    }
}

// ============================================
// PANTALLA 3: TIPOS DE INGENIERÍA SOCIAL
// ============================================
@Composable
fun Screen03_TiposIngenieriaSocial(viewModel: InteractiveLessonViewModel) {
    ClassifierScreen(
        title = "🎯 CLASIFICADOR DE TÁCTICAS",
        scenario = "Hombre con chaleco de mantenimiento pide acceso al servidor 'para reparaciones'",
        categories = listOf(
            Category(
                id = "phishing",
                name = "PHISHING",
                icon = "📧",
                color = "#FF006E"
            ),
            Category(
                id = "pretexting",
                name = "PRETEXTING",
                icon = "🎭",
                color = "#00D9FF"
            ),
            Category(
                id = "vishing",
                name = "VISHING",
                icon = "📞",
                color = "#00FF41"
            )
        ),
        correctCategoryId = "pretexting",
        correctFeedback = "✅ EXACTO! Pretexting - Crear un escenario falso para ganar confianza",
        incorrectFeedback = "❌ Piensa: ¿Es un email, una llamada o una persona física con un rol falso?",
        screenNumber = 3,
        totalScreens = 6,
        onAnswerRecorded = { isCorrect -> viewModel.recordAnswer(3, isCorrect) },
        onNext = { viewModel.nextScreen() }
    )
}

// ============================================
// PANTALLA 4: SHOULDER SURFING
// ============================================
@Composable
fun Screen04_ShoulderSurfing(viewModel: InteractiveLessonViewModel) {
    var selectedPerson by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    val correctAnswer = "man_coffee"
    val isCorrect = selectedPerson == correctAnswer

    ScreenContainer(
        title = "👀 SIMULADOR: DETECTA EL ESPÍA",
        screenNumber = 4,
        totalScreens = 6,
        onNext = {
            viewModel.recordAnswer(4, isCorrect)
            viewModel.nextScreen()
        },
        buttonText = "Siguiente",
        buttonEnabled = showFeedback
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "ESCENARIO: Cafetería - 2:30 PM",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            Text(
                text = "\"Estás trabajando en tu laptop\"",
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "👤 Personas en la cafetería:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            // Grid de personas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PersonCard(
                    emoji = "😊",
                    description = "Mujer leyendo",
                    id = "woman_reading",
                    isSelected = selectedPerson == "woman_reading",
                    onClick = {
                        selectedPerson = "woman_reading"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )

                PersonCard(
                    emoji = "😐",
                    description = "Hombre con café",
                    id = "man_coffee",
                    isSelected = selectedPerson == "man_coffee",
                    onClick = {
                        selectedPerson = "man_coffee"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PersonCard(
                    emoji = "👮",
                    description = "Guardia de seguridad",
                    id = "guard",
                    isSelected = selectedPerson == "guard",
                    onClick = {
                        selectedPerson = "guard"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )

                PersonCard(
                    emoji = "🎒",
                    description = "Estudiante con audífonos",
                    id = "student",
                    isSelected = selectedPerson == "student",
                    onClick = {
                        selectedPerson = "student"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Feedback
            if (showFeedback && selectedPerson != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FeedbackMessage(
                    isCorrect = isCorrect,
                    message = if (isCorrect) {
                        "✅ CORRECTO! 😐 Hombre con café - Está mirando tu pantalla cada vez que tipeas contraseña. Shoulder Surfing es observar información confidencial por encima del hombro."
                    } else {
                        "❌ Incorrecto. Observa quién tiene línea de visión directa hacia tu pantalla."
                    }
                )
            }
        }
    }
}

/**
 * Componente: Person Card
 */
@Composable
fun PersonCard(
    emoji: String,
    description: String,
    id: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                CyberColors.NeonBlue.copy(alpha = 0.3f)
            else
                CyberColors.CardBg
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ============================================
// PANTALLA 5: CASOS DE PRETEXTING
// ============================================
@Composable
fun Screen05_Pretexting(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🎭 PRETEXTING EN ACCIÓN",
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
                text = "\"Pretexting es crear un escenario falso para manipular a la víctima\"",
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 18.sp
            )

            // Casos reales
            CaseExampleCard(
                icon = "🔧",
                title = "Técnico de soporte",
                description = "Llama diciendo que hay un problema urgente con el servidor y necesita credenciales para \"solucionarlo\"",
                realCase = "Caso Ubiquiti (2015): $46.7M robados"
            )

            CaseExampleCard(
                icon = "👔",
                title = "Ejecutivo de banco",
                description = "Email que parece del CEO pidiendo transferencia urgente para \"adquisición confidencial\"",
                realCase = "Caso FACC (2016): €50M perdidos"
            )

            CaseExampleCard(
                icon = "📦",
                title = "Empleado de envíos",
                description = "Persona con uniforme de mensajería pide acceso a oficina para \"entregar paquete urgente al CEO\"",
                realCase = "Táctica común en ataques de penetración física"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "💡 PROTECCIÓN:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = "• Verificar SIEMPRE la identidad\n• Llamar al número oficial, no al proporcionado\n• No compartir información sin autorización\n• Reportar solicitudes sospechosas",
                fontSize = 13.sp,
                color = Color.White,
                lineHeight = 17.sp
            )
        }
    }
}

/**
 * Componente: Case Example Card
 */
@Composable
fun CaseExampleCard(
    icon: String,
    title: String,
    description: String,
    realCase: String
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
            Text(
                text = icon,
                fontSize = 32.sp
            )

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
                    color = Color.White,
                    lineHeight = 15.sp
                )

                Text(
                    text = "📌 $realCase",
                    fontSize = 11.sp,
                    color = CyberColors.NeonBlue
                )
            }
        }
    }
}

// ============================================
// PANTALLA 6: RESUMEN
// ============================================
@Composable
fun Screen06_SummaryL2(viewModel: InteractiveLessonViewModel, onComplete: () -> Unit) {
    val xpEarned by viewModel.xpEarned.collectAsState()

    SummaryScreen(
        lessonTitle = "LECCIÓN 2",
        achievements = listOf(
            "📧 Phishing (Equifax - 147M afectados)",
            "🎭 Pretexting (Rol falso para acceso)",
            "👀 Shoulder Surfing (Observación directa)",
            "📞 Vishing (Engaño por llamadas)"
        ),
        statistics = listOf(
            StatisticItem("📧", "60%", "Phishing"),
            StatisticItem("🎭", "25%", "Pretexting"),
            StatisticItem("📞", "15%", "Otros")
        ),
        xpEarned = xpEarned,
        badgeName = "Cazador de Phishing",
        nextLessonTitle = "Ataques Cibernéticos Básicos",
        screenNumber = 6,
        totalScreens = 6,
        onComplete = onComplete
    )
}