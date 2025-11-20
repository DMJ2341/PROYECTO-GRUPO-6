package com.example.cyberlearnapp.ui.screens.lessons.fundamentos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.ui.screens.lessons.shared.*
import com.example.cyberlearnapp.ui.screens.lessons.templates.*
import com.example.cyberlearnapp.ui.screens.lessons.simulators.*
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel

/**
 * 📱 LECCIÓN 4: DISPOSITIVOS MÓVILES E INALÁMBRICOS
 * 6 pantallas interactivas con simuladores WiFi y SMiShing
 */
@Composable
fun Leccion04Screen(
    lessonId: Int,
    onComplete: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    val currentScreenIndex by viewModel.currentScreenIndex.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    when (currentScreenIndex) {
        0 -> Screen01_EvilTwinAttack(viewModel)
        1 -> Screen02_WiFiSimulator(viewModel)
        2 -> Screen03_SMiShingDetector(viewModel)
        3 -> Screen04_MobileSecurity(viewModel)
        4 -> Screen05_BluetoothThreats(viewModel)
        5 -> Screen06_SummaryL4(viewModel, onComplete)
    }
}

// ============================================
// PANTALLA 1: STORY HOOK - EVIL TWIN
// ============================================
@Composable
fun Screen01_EvilTwinAttack(viewModel: InteractiveLessonViewModel) {
    StoryHookScreen(
        caseTitle = "🚨 CASO REAL: CADENA DE TIENDAS (2022)",
        date = "Diciembre 2022 - Centro Comercial",
        description = "Clientas reportan fraudes en sus tarjetas",
        impactCards = listOf(
            ImpactCardData(
                icon = "👤",
                value = "2,300",
                label = "Clientes Afectados",
                detail = "Datos de tarjetas y contraseñas comprometidos"
            ),
            ImpactCardData(
                icon = "💳",
                value = "$1.2M",
                label = "Fraudes Detectados",
                detail = "Transacciones no autorizadas en 3 días"
            ),
            ImpactCardData(
                icon = "📱",
                value = "1",
                label = "Router Falso",
                detail = "Evil Twin: Wi-Fi 'Free_Mall_WiFi' era falso"
            )
        ),
        hookQuestion = "🔍 ¿CÓMO EL WIFI GRATUITO ROBÓ DATOS?",
        screenNumber = 1,
        totalScreens = 6,
        buttonText = "🎯 ANALIZAR REDES FALSAS",
        onNext = { viewModel.nextScreen() }
    )
}

// ============================================
// PANTALLA 2: SIMULADOR DE REDES WI-FI
// ============================================
@Composable
fun Screen02_WiFiSimulator(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "📶 SIMULADOR: ELIGE TU RED SEGURA",
        screenNumber = 2,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "Siguiente"
    ) {
        WiFiNetworkSimulator(
            scenario = "Aeropuerto",
            networks = AIRPORT_NETWORKS,
            correctNetworkId = "official",
            onComplete = { success ->
                viewModel.recordAnswer(2, success)
            }
        )
    }
}

// ============================================
// PANTALLA 3: DETECTOR DE SMS FALSOS
// ============================================
@Composable
fun Screen03_SMiShingDetector(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "📱 ANALIZADOR DE SMISHING",
        screenNumber = 3,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "Siguiente"
    ) {
        SMiShingSimulator(
            smsData = SMISHING_EXAMPLE_DATA,
            onComplete = { success ->
                viewModel.recordAnswer(3, success)
            }
        )
    }
}

// ============================================
// PANTALLA 4: SEGURIDAD MÓVIL
// ============================================
@Composable
fun Screen04_MobileSecurity(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🔐 MEJORES PRÁCTICAS MÓVILES",
        screenNumber = 4,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "🔵 VER BLUETOOTH"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Protege tu dispositivo móvil:",
                fontSize = 14.sp,
                color = Color.White
            )

            SecurityPracticeCard(
                icon = "🔒",
                title = "Bloqueo de Pantalla",
                description = "PIN de 6+ dígitos o biometría",
                risk = "ALTO",
                stat = "80% de robos físicos evitables"
            )

            SecurityPracticeCard(
                icon = "📲",
                title = "Actualizaciones Automáticas",
                description = "Sistema operativo y apps al día",
                risk = "CRÍTICO",
                stat = "60% de malware móvil usa vulnerabilidades viejas"
            )

            SecurityPracticeCard(
                icon = "🏪",
                title = "Tiendas Oficiales",
                description = "Solo Google Play Store o App Store",
                risk = "ALTO",
                stat = "98% de malware Android viene de tiendas no oficiales"
            )

            SecurityPracticeCard(
                icon = "🌐",
                title = "VPN en Wi-Fi Público",
                description = "Cifra tu conexión en redes abiertas",
                risk = "MEDIO",
                stat = "1 de cada 4 Wi-Fi públicos no son seguros"
            )

            SecurityPracticeCard(
                icon = "📍",
                title = "Permisos de Aplicaciones",
                description = "Revisa y limita accesos innecesarios",
                risk = "MEDIO",
                stat = "70% de apps piden más permisos de los necesarios"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CyberColors.NeonGreen.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚡ CONFIGURACIÓN RÁPIDA:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonGreen
                    )

                    Text(
                        text = "• Activar Encontrar mi Dispositivo\n• Habilitar cifrado completo\n• Desactivar Bluetooth/WiFi cuando no uses\n• No hacer jailbreak/root",
                        fontSize = 12.sp,
                        color = Color.White,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Componente: Security Practice Card
 */
@Composable
fun SecurityPracticeCard(
    icon: String,
    title: String,
    description: String,
    risk: String,
    stat: String
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
                fontSize = 28.sp
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = risk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (risk) {
                            "CRÍTICO" -> CyberColors.NeonPink
                            "ALTO" -> Color(0xFFFF9800)
                            else -> CyberColors.NeonBlue
                        }
                    )
                }

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Text(
                    text = "📊 $stat",
                    fontSize = 11.sp,
                    color = CyberColors.NeonGreen
                )
            }
        }
    }
}

// ============================================
// PANTALLA 5: AMENAZAS BLUETOOTH
// ============================================
@Composable
fun Screen05_BluetoothThreats(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🔵 AMENAZAS BLUETOOTH",
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
                text = "Bluetooth también tiene riesgos:",
                fontSize = 14.sp,
                color = Color.White
            )

            BluetoothThreatCard(
                icon = "📱➡️💻",
                name = "BLUEJACKING",
                description = "Envío no solicitado de mensajes via Bluetooth",
                severity = "BAJA",
                realCase = "Spam publicitario en espacios públicos"
            )

            BluetoothThreatCard(
                icon = "🔓",
                name = "BLUESNARFING",
                description = "Acceso no autorizado a datos del dispositivo",
                severity = "ALTA",
                realCase = "Robo de contactos, fotos y mensajes (2003-2005)"
            )

            BluetoothThreatCard(
                icon = "🎧",
                name = "BLUEBUGGING",
                description = "Control remoto completo del dispositivo",
                severity = "CRÍTICA",
                realCase = "Llamadas, SMS y escuchas sin autorización"
            )

            BluetoothThreatCard(
                icon = "🔐",
                name = "BLUEBORNE",
                description = "Explotación de vulnerabilidades Bluetooth",
                severity = "CRÍTICA",
                realCase = "2017: 5.3 mil millones de dispositivos vulnerables"
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
                        text = "🛡️ PROTECCIÓN:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonBlue
                    )

                    Text(
                        text = "• Desactivar Bluetooth cuando no lo uses\n• Modo \"No Detectable\" activado\n• No emparejar con dispositivos desconocidos\n• Mantener sistema actualizado",
                        fontSize = 12.sp,
                        color = Color.White,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Componente: Bluetooth Threat Card
 */
@Composable
fun BluetoothThreatCard(
    icon: String,
    name: String,
    description: String,
    severity: String,
    realCase: String
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = icon, fontSize = 24.sp)
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.NeonGreen
                    )
                }

                Text(
                    text = severity,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (severity) {
                        "CRÍTICA" -> CyberColors.NeonPink
                        "ALTA" -> Color(0xFFFF9800)
                        else -> CyberColors.NeonBlue
                    }
                )
            }

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.White
            )

            Text(
                text = "📌 $realCase",
                fontSize = 11.sp,
                color = CyberColors.NeonBlue
            )
        }
    }
}

// ============================================
// PANTALLA 6: RESUMEN
// ============================================
@Composable
fun Screen06_SummaryL4(viewModel: InteractiveLessonViewModel, onComplete: () -> Unit) {
    val xpEarned by viewModel.xpEarned.collectAsState()

    SummaryScreen(
        lessonTitle = "LECCIÓN 4",
        achievements = listOf(
            "📶 Evil Twin (Wi-Fi falso - 2,300 afectados)",
            "📱 SMiShing (SMS fraudulentos)",
            "🔐 Seguridad Móvil (5 mejores prácticas)",
            "🔵 Amenazas Bluetooth (4 tipos)"
        ),
        statistics = listOf(
            StatisticItemData("📶", "50%", "Wi-Fi"),
            StatisticItemData("📱", "30%", "SMS"),
            StatisticItemData("🔵", "20%", "Bluetooth")
        ),
        xpEarned = xpEarned,
        badgeName = "Guardian Inalámbrico",
        nextLessonTitle = "Principios de la Ciberseguridad",
        screenNumber = 6,
        totalScreens = 6,
        onComplete = onComplete
    )
}