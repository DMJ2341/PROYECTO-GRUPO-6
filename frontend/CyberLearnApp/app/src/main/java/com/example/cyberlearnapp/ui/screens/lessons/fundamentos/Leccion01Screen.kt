package com.example.cyberlearnapp.ui.screens.lessons.fundamentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.ui.screens.lessons.shared.*
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel

/**
 * 🎯 LECCIÓN 1: INTRODUCCIÓN A LAS AMENAZAS CIBERNÉTICAS
 * 6 pantallas interactivas según el diseño propuesto
 */
@Composable
fun Leccion01Screen(
    lessonId: Int,
    onComplete: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    val currentScreenIndex by viewModel.currentScreenIndex.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    when (currentScreenIndex) {
        0 -> Screen01_StoryHook(viewModel)
        1 -> Screen02_GlobalMap(viewModel)
        2 -> Screen03_Classifier(viewModel)
        3 -> Screen04_InternalVsExternal(viewModel)
        4 -> Screen05_Hero(viewModel)
        5 -> Screen06_SummaryL1(viewModel, onComplete)
    }
}

// ============================================
// PANTALLA 1: STORY HOOK - WANNACRY
// ============================================
@Composable
fun Screen01_StoryHook(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "CASO REAL: WANNACRY (2017)",
        screenNumber = 1,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "🎯 ANALIZAR LA AMENAZA"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hook textual
            Text(
                text = "\"12 de mayo de 2017 - 10:00 AM\"",
                fontSize = 14.sp,
                color = CyberColors.NeonBlue,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Hospitales en Reino Unido comienzan a colapsar",
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "👆 TOCA PARA VER EL IMPACTO:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            // Tarjetas de impacto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImpactCard(
                    icon = "🏥",
                    value = "200k",
                    label = "Computadoras Infectadas",
                    detail = "Sistemas en 150 países afectados simultáneamente",
                    modifier = Modifier.weight(1f)
                )

                ImpactCard(
                    icon = "💀",
                    value = "600+",
                    label = "Cirugías Canceladas",
                    detail = "Pacientes en quirófano transferidos a otros hospitales mientras sistemas colapsaban",
                    modifier = Modifier.weight(1f)
                )
            }

            ImpactCard(
                icon = "💰",
                value = "\$4B",
                label = "Pérdidas Globales",
                detail = "Daños económicos a nivel mundial",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pregunta gancho
            Text(
                text = "🔍 ¿CÓMO UN VIRUS PARALIZÓ LA SALUD MUNDIAL?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )
        }
    }
}

// ============================================
// PANTALLA 2: MAPA DE INFECCIÓN GLOBAL
// ============================================
@Composable
fun Screen02_GlobalMap(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🌍 PANORAMA GLOBAL DE AMENAZAS",
        screenNumber = 2,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "🕵️ CLASIFICAR AMENAZAS SIMILARES"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "\"WannaCry demostró 3 verdades:\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Estadísticas clave
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        "VELOCIDAD: 150 países en 24 horas",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        "IMPACTO: Desde hospitales hasta empresas",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        "FACILIDAD: Se propagó sola",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🎯 TIPOS DE AMENAZAS DETECTADAS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            // Grid de tipos de amenazas
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(300.dp)
            ) {
                item {
                    StatDisplay(
                        icon = "🦠",
                        percentage = "85%",
                        label = "MALWARE\n(Como WannaCry)"
                    )
                }

                item {
                    StatDisplay(
                        icon = "📧",
                        percentage = "10%",
                        label = "SOCIAL\n(Engaño)"
                    )
                }

                item {
                    StatDisplay(
                        icon = "🌐",
                        percentage = "5%",
                        label = "WEB\n(Ataques)"
                    )
                }
            }
        }
    }
}

// ============================================
// PANTALLA 3: CLASIFICADOR DE DOMINIOS
// ============================================
@Composable
fun Screen03_Classifier(viewModel: InteractiveLessonViewModel) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    val correctAnswer = "SOCIAL"  // SMiShing es ingeniería social

    ScreenContainer(
        title = "🎮 CLASIFICADOR DE AMENAZAS",
        screenNumber = 3,
        totalScreens = 6,
        onNext = {
            viewModel.recordAnswer(3, selectedCategory == correctAnswer)
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
                text = "\"WannaCry fue MALWARE. Clasifica estos casos:\"",
                fontSize = 14.sp,
                color = Color.White
            )

            // Caso a clasificar
            Text(
                text = "CASO #1:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = "\"Empleado recibe SMS: 'Su paquete no se entregó. Confirme datos: bit.ly/paquete123'\"",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Opciones de categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryChip(
                    name = "MALWARE",
                    icon = "🦠",
                    color = CyberColors.NeonPink,
                    isSelected = selectedCategory == "MALWARE",
                    onClick = {
                        selectedCategory = "MALWARE"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )

                CategoryChip(
                    name = "SOCIAL",
                    icon = "📧",
                    color = CyberColors.NeonBlue,
                    isSelected = selectedCategory == "SOCIAL",
                    onClick = {
                        selectedCategory = "SOCIAL"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )

                CategoryChip(
                    name = "WEB",
                    icon = "🌐",
                    color = CyberColors.NeonGreen,
                    isSelected = selectedCategory == "WEB",
                    onClick = {
                        selectedCategory = "WEB"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Feedback
            if (showFeedback && selectedCategory != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FeedbackMessage(
                    isCorrect = selectedCategory == correctAnswer,
                    message = if (selectedCategory == correctAnswer) {
                        "✅ CORRECTO! SMiShing - Engaño por SMS. Similar a casos reales de bancos en México"
                    } else {
                        "❌ Intenta de nuevo. Piensa: ¿hay código malicioso o solo engaño?"
                    }
                )
            }
        }
    }
}

// ============================================
// PANTALLA 4: INTERNO vs EXTERNO - CASO TARGET
// ============================================
@Composable
fun Screen04_InternalVsExternal(viewModel: InteractiveLessonViewModel) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    val correctAnswer = "EXTERNO"

    ScreenContainer(
        title = "🔍 DETECTIVE: ¿INTERNO O EXTERNO?",
        screenNumber = 4,
        totalScreens = 6,
        onNext = {
            viewModel.recordAnswer(4, selectedOption == correctAnswer)
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
                text = "CASO REAL: TARGET (2013)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = "\"40 millones de tarjetas de crédito robadas\"",
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "EVIDENCIAS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            // Lista de evidencias
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "• Entraron por sistema de AIRE ACONDICIONADO",
                    "• Contraseña: \"password1234\"",
                    "• Estuvieron 19 DÍAS sin detección",
                    "• Robaron datos de 70 millones de personas"
                ).forEach { evidence ->
                    Text(
                        text = evidence,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryChip(
                    name = "INTERNO\n(Empleado)",
                    icon = "👨‍💼",
                    color = CyberColors.NeonBlue,
                    isSelected = selectedOption == "INTERNO",
                    onClick = {
                        selectedOption = "INTERNO"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )

                CategoryChip(
                    name = "EXTERNO\n(Hacker)",
                    icon = "🦹",
                    color = CyberColors.NeonPink,
                    isSelected = selectedOption == "EXTERNO",
                    onClick = {
                        selectedOption = "EXTERNO"
                        showFeedback = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Feedback
            if (showFeedback && selectedOption != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FeedbackMessage(
                    isCorrect = selectedOption == correctAnswer,
                    message = if (selectedOption == correctAnswer) {
                        "✅ EXACTO! Hackers externos usaron credenciales de un proveedor (contratista de aire acondicionado). Esto muestra: Las amenazas pueden venir de cualquier punto de la cadena"
                    } else {
                        "❌ Piensa bien: ¿Quién instaló los sistemas de aire acondicionado?"
                    }
                )
            }
        }
    }
}

// ============================================
// PANTALLA 5: EL HÉROE DE WANNACRY
// ============================================
@Composable
fun Screen05_Hero(viewModel: InteractiveLessonViewModel) {
    ScreenContainer(
        title = "🦸 LECCIÓN CLAVE: TODOS PODEMOS AYUDAR",
        screenNumber = 5,
        totalScreens = 6,
        onNext = { viewModel.nextScreen() },
        buttonText = "🚀 APRENDER MÁS AMENAZAS"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "\"¿Cómo se detuvo WannaCry?\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Perfil del héroe
            Text(
                text = "👤 Marcus Hutchins (22 años)",
                fontSize = 14.sp,
                color = CyberColors.NeonGreen,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "\"Analista de seguridad británico\"",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🔍 DESCUBRIÓ EL 'KILL SWITCH':",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            Text(
                text = "\"El virus consultaba un dominio web. Si el dominio existía, se detenía. Marcus lo registró por \$10.69 y salvó miles de sistemas.\"",
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Moraleja
            Text(
                text = "🎯 MORALEJA:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = "\"Un solo analista puede cambiar el curso de un ataque global. Tu conocimiento importa.\"",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }
    }
}

// ============================================
// PANTALLA 6: RESUMEN Y RECOMPENSA
// ============================================
@Composable
fun Screen06_SummaryL1(viewModel: InteractiveLessonViewModel, onComplete: () -> Unit) {
    val xpEarned by viewModel.xpEarned.collectAsState()

    ScreenContainer(
        title = "🏆 MISIÓN CUMPLIDA - LECCIÓN 1",
        screenNumber = 6,
        totalScreens = 6,
        onNext = {
            viewModel.nextScreen()
            onComplete()
        },
        buttonText = "🚀 CONTINUAR A INGENIERÍA SOCIAL"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "✅ APRENDISTE SOBRE:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            // Lista de conceptos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "🦠 Malware (WannaCry)",
                    "📧 Amenazas Sociales (SMiShing)",
                    "👥 Interno vs Externo (Target)",
                    "🦸 Tu papel como defensor"
                ).forEach { item ->
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📊 ESTADÍSTICAS DE LA LECCIÓN:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonBlue
            )

            // Grid de estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatDisplay(
                    icon = "🦠",
                    percentage = "70%",
                    label = "Malware"
                )

                StatDisplay(
                    icon = "📧",
                    percentage = "20%",
                    label = "Social"
                )

                StatDisplay(
                    icon = "👥",
                    percentage = "10%",
                    label = "Interno/\nExterno"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🎁 RECOMPENSAS:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            Text(
                text = "⭐ +$xpEarned XP | 🛡️ Insignia \"Primer Respondedor\"",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}