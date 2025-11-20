package com.example.cyberlearnapp.ui.screens.lessons.fundamentos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.ui.screens.lessons.shared.*
import com.example.cyberlearnapp.ui.screens.lessons.templates.*
import com.example.cyberlearnapp.ui.screens.lessons.simulators.*
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel

/**
 * 🦠 LECCIÓN 3: ATAQUES CIBERNÉTICOS BÁSICOS
 * 6 pantallas interactivas con simuladores
 */
@Composable
fun Leccion03Screen(
    lessonId: Int,
    onComplete: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    val currentScreenIndex by viewModel.currentScreenIndex.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    when (currentScreenIndex) {
        0 -> Screen01_ColonialPipeline(viewModel)
        1 -> Screen02_RansomwareProcess(viewModel)
        2 -> Screen03_DDoSSimulator(viewModel)
        3 -> Screen04_MalwareTypes(viewModel)
        4 -> Screen05_DefenseStrategies(viewModel)
        5 -> Screen06_SummaryL3(viewModel, onComplete)
    }
}

// ============================================
// PANTALLA 1: STORY HOOK - COLONIAL PIPELINE
// ============================================
@Composable
fun Screen01_ColonialPipeline(viewModel: InteractiveLessonViewModel) {
    StoryHookScreen(
        caseTitle = "🚨 CASO REAL: COLONIAL PIPELINE (2021)",
        date = "7 de mayo de 2021 - Costa Este EE.UU.",
        description = "La mayor tubería de gasolina se detiene",
        impactCards = listOf(
            ImpactCardData(
                icon = "⛽",
                value = "5 Días",
                label = "Paralizada",
                detail = "45% de la gasolina de la Costa Este afectada"
            ),
            ImpactCardData(
                icon = "🚗",
                value = "45%",
                label = "Aumento Precios",
                detail = "Pánico en estaciones de servicio, largas filas"
            ),
            ImpactCardData(
                icon = "💰",
                value = "$4.4M",
                label = "Rescate Pagado",
                detail = "Pagado en Bitcoin a hackers rusos (grupo DarkSide)"
            )
        ),
        hookQuestion = "🔍 ¿CÓMO UN ANUNCIO FALSO PARALIZÓ EE.UU.?",
        screenNumber = 1,
        totalScreens = 6,
        buttonText = "🎯 ANALIZAR EL RANSOMWARE",
        onNext = { viewModel.nextScreen() }
    )
}

// ============================================
// PANTALLA 2: INFOGRAFÍA RANSOMWARE
// ============================================
@Composable
fun Screen02_RansomwareProcess(viewModel: InteractiveLessonViewModel) {
    ProcessInfographicScreen(
        title = "🔄 CÓMO FUNCIONA UN RANSOMWARE",
        processSteps = listOf(
            ProcessStepData(
                stepNumber = 1,
                title = "INFECCIÓN",
                description = "Empleado hace clic en anuncio 'Actualizar Windows' - Descarga DarkSide",
                icon = "🦠"
            ),
            ProcessStepData(
                stepNumber = 2,
                title = "PROPAGACIÓN",
                description = "El ransomware busca conexiones de red y se expande a otros sistemas",
                icon = "🌐"
            ),
            ProcessStepData(
                stepNumber = 3,
                title = "CIFRADO",
                description = "Encripta 100 GB de datos en 2 horas - Archivos cambian a .locked",
                icon = "🔒"
            ),
            ProcessStepData(
                stepNumber = 4,
                title = "EXTORSIÓN",
                description = "Muestra mensaje: 'Pague $4.4M o borramos todo'",
                icon = "💰"
            )
        ),
        impactSummary = "5 días sin gasolina en la Costa Este de EE.UU.",
        screenNumber = 2,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() }
    )
}

// ============================================
// PANTALLA 3: SIMULADOR DDoS
// ============================================
@Composable
fun Screen03_DDoSSimulator(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🌊 SIMULADOR DE ATAQUE DDoS",
        screenNumber = 3,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "Siguiente"
    ) {
        DDoSSimulator(
            normalTraffic = 50,
            attackTraffic = 10000,
            onComplete = { success ->
                viewModel.recordAnswer(3, success)
            }
        )
    }
}

// ============================================
// PANTALLA 4: TIPOS DE MALWARE
// ============================================
@Composable
fun Screen04_MalwareTypes(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🦠 TIPOS DE MALWARE",
        screenNumber = 4,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "🛡️ VER DEFENSAS"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Conoce los principales tipos de software malicioso:",
                fontSize = 14.sp,
                color = Color.White
            )

            MalwareTypeCard(
                icon = "🔒",
                name = "RANSOMWARE",
                description = "Cifra archivos y exige rescate",
                examples = "WannaCry, DarkSide, REvil",
                damage = "Pérdida de datos + dinero"
            )

            MalwareTypeCard(
                icon = "🐴",
                name = "TROYANO",
                description = "Se disfraza de software legítimo",
                examples = "Emotet, Zeus, TrickBot",
                damage = "Robo de credenciales y datos bancarios"
            )

            MalwareTypeCard(
                icon = "🪱",
                name = "GUSANO",
                description = "Se replica automáticamente en red",
                examples = "Conficker, Stuxnet, ILOVEYOU",
                damage = "Colapso de redes, propagación masiva"
            )

            MalwareTypeCard(
                icon = "👁️",
                name = "SPYWARE",
                description = "Espía actividades sin conocimiento",
                examples = "Pegasus, FinFisher, DarkHotel",
                damage = "Vigilancia, robo de información personal"
            )

            MalwareTypeCard(
                icon = "📢",
                name = "ADWARE",
                description = "Muestra anuncios no deseados",
                examples = "Fireball, Gator, DollarRevenue",
                damage = "Ralentización, rastreo de navegación"
            )
        }
    }
}

/**
 * Componente: Malware Type Card
 */
@Composable
fun MalwareTypeCard(
    icon: String,
    name: String,
    description: String,
    examples: String,
    damage: String
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
                    text = name,
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
                    text = "Ejemplos: $examples",
                    fontSize = 11.sp,
                    color = CyberColors.NeonBlue
                )

                Text(
                    text = "⚠️ $damage",
                    fontSize = 11.sp,
                    color = CyberColors.NeonPink.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ============================================
// PANTALLA 5: ESTRATEGIAS DE DEFENSA
// ============================================
@Composable
fun Screen05_DefenseStrategies(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🛡️ ESTRATEGIAS DE DEFENSA",
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
                text = "CAPAS DE PROTECCIÓN:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            DefenseLayerCard(
                layer = 1,
                icon = "🔄",
                title = "Backups Regulares",
                description = "Copias de seguridad automáticas (3-2-1: 3 copias, 2 medios, 1 offsite)",
                effectiveness = "CRÍTICA vs Ransomware"
            )

            DefenseLayerCard(
                layer = 2,
                icon = "🛡️",
                title = "Antivirus/EDR",
                description = "Detección de malware conocido y comportamiento anómalo",
                effectiveness = "ALTA vs Troyanos y Gusanos"
            )

            DefenseLayerCard(
                layer = 3,
                icon = "🔥",
                title = "Firewall + IDS/IPS",
                description = "Bloqueo de tráfico malicioso y detección de intrusiones",
                effectiveness = "ALTA vs DDoS y accesos no autorizados"
            )

            DefenseLayerCard(
                layer = 4,
                icon = "🔐",
                title = "Actualizaciones",
                description = "Parches de seguridad para vulnerabilidades conocidas",
                effectiveness = "ESENCIAL - Equifax falló aquí"
            )

            DefenseLayerCard(
                layer = 5,
                icon = "🎓",
                title = "Capacitación",
                description = "Entrenamiento continuo en ciberseguridad",
                effectiveness = "FUNDAMENTAL - El humano es el eslabón más débil"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CyberColors.NeonBlue.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "💡 PRINCIPIO CLAVE:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonBlue
                    )

                    Text(
                        text = "\"Defensa en profundidad\" - Múltiples capas de seguridad. Si una falla, las demás protegen.",
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

/**
 * Componente: Defense Layer Card
 */
@Composable
fun DefenseLayerCard(
    layer: Int,
    icon: String,
    title: String,
    description: String,
    effectiveness: String
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
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text(
                    text = icon,
                    fontSize = 28.sp
                )
                Text(
                    text = "[$layer]",
                    fontSize = 11.sp,
                    color = CyberColors.NeonGreen
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 15.sp
                )

                Text(
                    text = "✅ $effectiveness",
                    fontSize = 11.sp,
                    color = CyberColors.NeonGreen
                )
            }
        }
    }
}

// ============================================
// PANTALLA 6: RESUMEN
// ============================================
@Composable
fun Screen06_SummaryL3(viewModel: InteractiveLessonViewModel, onComplete: () -> Unit) {
    val xpEarned by viewModel.xpEarned.collectAsState()

    SummaryScreen(
        lessonTitle = "LECCIÓN 3",
        achievements = listOf(
            "🔒 Ransomware (Colonial Pipeline - $4.4M)",
            "🌊 DDoS (Saturación de servidores)",
            "🦠 Tipos de Malware (5 categorías)",
            "🛡️ Defensa en profundidad (5 capas)"
        ),
        statistics = listOf(
            StatisticItemData("🔒", "40%", "Ransomware"),
            StatisticItemData("🌊", "30%", "DDoS"),
            StatisticItemData("🪱", "30%", "Otros")
        ),
        xpEarned = xpEarned,
        badgeName = "Contenedor de Ransomware",
        nextLessonTitle = "Dispositivos Móviles e Inalámbricos",
        screenNumber = 6,
        totalScreens = 6,
        onComplete = onComplete
    )
}