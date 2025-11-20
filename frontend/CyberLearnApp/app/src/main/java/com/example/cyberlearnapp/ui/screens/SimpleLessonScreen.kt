package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleLessonScreen(
    lessonId: String,
    lessonTitle: String,
    onNavigateBack: () -> Unit,
    onLessonCompleted: () -> Unit
) {
    val viewModel: InteractiveLessonViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    println("🎬 [SIMPLE-LESSON] Iniciando con lessonId: $lessonId")

    val pagerState = rememberPagerState(
        pageCount = { state.lesson?.totalScreens ?: 1 }
    )

    LaunchedEffect(lessonId) {
        println("🔄 [SIMPLE-LESSON] LaunchedEffect ejecutándose para: $lessonId")
        viewModel.loadLesson(lessonId)
    }

    // 🔍 Debug: Monitorear cambios en el estado
    LaunchedEffect(state) {
        println("📊 [STATE-DEBUG] isLoading: ${state.isLoading}")
        println("📊 [STATE-DEBUG] errorMessage: ${state.errorMessage}")
        println("📊 [STATE-DEBUG] lesson: ${state.lesson?.id}")
        println("📊 [STATE-DEBUG] screens count: ${state.lesson?.screens?.size ?: 0}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = lessonTitle,
                        color = Color.White,
                        fontSize = 16.sp,
                        maxLines = 2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        println("⬅️ [SIMPLE-LESSON] Navegando hacia atrás")
                        onNavigateBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A)
                )
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    println("⏳ [SIMPLE-LESSON] Mostrando pantalla de carga")
                    LoadingScreen()
                }

                state.errorMessage != null -> {
                    println("❌ [SIMPLE-LESSON] Mostrando error: ${state.errorMessage}")
                    ErrorScreen(
                        message = state.errorMessage!!,
                        onRetry = {
                            println("🔄 [SIMPLE-LESSON] Reintentando cargar lección")
                            viewModel.loadLesson(lessonId)
                        },
                        onBack = {
                            println("⬅️ [SIMPLE-LESSON] Volviendo desde pantalla de error")
                            onNavigateBack()
                        }
                    )
                }

                state.lesson != null -> {
                    val lesson = state.lesson!!

                    if (lesson.screens.isEmpty()) {
                        println("⚠️ [SIMPLE-LESSON] Lección sin pantallas")
                        ErrorScreen(
                            message = "Esta lección no tiene contenido disponible",
                            onRetry = { viewModel.loadLesson(lessonId) },
                            onBack = onNavigateBack
                        )
                    } else {
                        println("✅ [SIMPLE-LESSON] Mostrando contenido de lección: ${lesson.screens.size} pantallas")
                        LessonContent(
                            lesson = lesson,
                            pagerState = pagerState,
                            onNext = {
                                if (pagerState.currentPage < lesson.totalScreens - 1) {
                                    scope.launch {
                                        println("➡️ [SIMPLE-LESSON] Avanzando a pantalla ${pagerState.currentPage + 2}")
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                } else {
                                    println("🏁 [SIMPLE-LESSON] Completando lección")
                                    onLessonCompleted()
                                }
                            },
                            onBack = onNavigateBack
                        )
                    }
                }

                else -> {
                    println("⚠️ [SIMPLE-LESSON] Estado indefinido")
                    LoadingScreen()
                }
            }
        }
    }
}

@Composable
fun LessonContent(
    lesson: com.example.cyberlearnapp.viewmodel.InteractiveLesson,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Progress Header
        LessonProgressHeader(
            currentPage = pagerState.currentPage,
            totalPages = lesson.totalScreens,
            onBack = onBack
        )

        // Pager con pantallas
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false
        ) { page ->
            val screen = lesson.screens.getOrNull(page)

            println("📄 [PAGER] Renderizando página $page")

            if (screen != null) {
                println("✅ [PAGER] Screen encontrada: type=${screen.type}, title=${screen.title}")
                DynamicScreenContent(
                    screen = screen,
                    onNext = onNext
                )
            } else {
                println("❌ [PAGER] Screen no encontrada en índice $page")
                ErrorScreen(
                    message = "Pantalla no disponible",
                    onRetry = { },
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
fun LessonProgressHeader(
    currentPage: Int,
    totalPages: Int,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentPage + 1}/$totalPages",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(totalPages) { index ->
                    Box(
                        modifier = Modifier
                            .width(if (index == currentPage) 24.dp else 8.dp)
                            .height(8.dp)
                            .background(
                                color = if (index <= currentPage)
                                    Color(0xFF60A5FA)
                                else
                                    Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Text(
                text = "Salir",
                color = Color(0xFF60A5FA),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}

@Composable
fun DynamicScreenContent(
    screen: com.example.cyberlearnapp.viewmodel.InteractiveScreen,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .clickable { onNext() }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Título
            Text(
                text = screen.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Subtítulo (si existe)
            screen.subtitle?.let { subtitle ->
                Text(
                    text = subtitle,
                    color = Color(0xFFFBBF24),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Contenido dinámico según el tipo
            when (screen.type) {
                "story_hook" -> StoryHookContent(screen.content)
                "interactive_scenario" -> InteractiveScenarioContent(screen.content)
                "hero_intro" -> HeroIntroContent(screen.content)
                "interactive_concept" -> InteractiveConceptContent(screen.content)
                "interactive_map" -> InteractiveMapContent(screen.content)
                "classification_game" -> ClassificationGameContent(screen.content)
                "mini_challenge" -> MiniChallengeContent(screen.content)
                "completion" -> CompletionContent(screen.content, onNext)
                "phishing_simulator" -> PhishingSimulatorContent(screen.content)
                "timeline" -> TimelineContent(screen.content)
                "ddos_simulator" -> DdosSimulatorContent(screen.content)
                "wifi_simulator" -> WifiSimulatorContent(screen.content)
                "smishing_detector" -> SmishingDetectorContent(screen.content)
                "cia_triangle" -> CiaTriangleContent(screen.content)
                "scenario_analyzer" -> ScenarioAnalyzerContent(screen.content)
                "mission_brief" -> MissionBriefContent(screen.content)
                "phishing_analysis" -> PhishingAnalysisContent(screen.content)
                "network_audit" -> NetworkAuditContent(screen.content)
                "ransomware_response" -> RansomwareResponseContent(screen.content)
                "budget_defense" -> BudgetDefenseContent(screen.content)
                "final_certificate" -> FinalCertificateContent(screen.content, onNext)
                "error" -> ErrorContent(screen.content)
                else -> DefaultContent(screen.content)
            }

            // Instrucción para continuar
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = screen.ctaButton ?: "Toca para continuar →",
                color = Color(0xFF60A5FA),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Funciones de renderizado de contenido específico por tipo
@Composable
fun StoryHookContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        content["hook"]?.let { hook ->
            Text(
                text = hook.toString(),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        @Suppress("UNCHECKED_CAST")
        val impactCards = content["impact_cards"] as? List<Map<String, String>>
        impactCards?.let { cards ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                cards.forEach { card ->
                    ImpactCard(card)
                }
            }
        }
    }
}

@Composable
fun ImpactCard(card: Map<String, String>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card["icon"] ?: "📊",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = card["value"] ?: "",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = card["label"] ?: "",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

// Implementaciones simplificadas para los demás tipos (placeholder para mantener compatibilidad)
@Composable fun InteractiveScenarioContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun HeroIntroContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun InteractiveConceptContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun InteractiveMapContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun ClassificationGameContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun MiniChallengeContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun PhishingSimulatorContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun TimelineContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun DdosSimulatorContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun WifiSimulatorContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun SmishingDetectorContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun CiaTriangleContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun ScenarioAnalyzerContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun MissionBriefContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun PhishingAnalysisContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun NetworkAuditContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun RansomwareResponseContent(content: Map<String, Any>) { DefaultContent(content) }
@Composable fun BudgetDefenseContent(content: Map<String, Any>) { DefaultContent(content) }

@Composable
fun CompletionContent(content: Map<String, Any>, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        content["xp_earned"]?.let { xp ->
            Text(
                text = "⭐ +$xp XP",
                color = Color(0xFFFBBF24),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        content["badge_unlocked"]?.let { badge ->
            Text(
                text = "🏆 $badge",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        content["summary"]?.let { summary ->
            Text(
                text = summary.toString(),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
        ) {
            Text("¡Completar Lección!")
        }
    }
}

@Composable
fun FinalCertificateContent(content: Map<String, Any>, onNext: () -> Unit) {
    CompletionContent(content, onNext)
}

@Composable
fun ErrorContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "❌", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = content["error"]?.toString() ?: "Error desconocido",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DefaultContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        content.forEach { (key, value) ->
            if (key != "type" && value is String) {
                Text(
                    text = value,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF60A5FA))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando lección...",
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "❌", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280))
                ) {
                    Text("Volver")
                }
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA))
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}