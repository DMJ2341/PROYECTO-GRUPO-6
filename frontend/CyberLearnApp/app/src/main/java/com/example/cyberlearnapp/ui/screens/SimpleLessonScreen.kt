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

    val pagerState = rememberPagerState(
        pageCount = { state.lesson?.totalScreens ?: 1 }
    )

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
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
                    IconButton(onClick = onNavigateBack) {
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
                state.isLoading -> LoadingScreen()
                state.errorMessage != null -> ErrorScreen(
                    message = state.errorMessage!!,
                    onRetry = { viewModel.loadLesson(lessonId) },
                    onBack = onNavigateBack
                )
                state.lesson != null -> LessonContent(
                    lesson = state.lesson!!,
                    pagerState = pagerState,
                    onNext = {
                        if (pagerState.currentPage < state.lesson!!.totalScreens - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onLessonCompleted()
                        }
                    },
                    onBack = onNavigateBack
                )
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

            if (screen != null) {
                DynamicScreenContent(
                    screen = screen,
                    onNext = onNext
                )
            } else {
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
            // Página actual
            Text(
                text = "${currentPage + 1}/$totalPages",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Indicadores de progreso
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

            // Botón salir (opcional)
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
            verticalArrangement = Arrangement.Center
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
                else -> DefaultContent(screen.content)
            }

            // Instrucción para continuar
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Toca para continuar →",
                color = Color(0xFF60A5FA),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StoryHookContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Hook principal
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

        // Tarjetas de impacto (si existen)
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

@Composable
fun InteractiveScenarioContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Escenario
        content["scenario"]?.let { scenario ->
            Text(
                text = scenario.toString(),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Personajes (si existen)
        val characters = content["characters"] as? List<Map<String, String>>
        characters?.let { chars ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                chars.forEach { character ->
                    Text(
                        text = "${character["emoji"] ?: "👤"} ${character["name"] ?: ""}",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Opciones (si existen)
        val choices = content["choices"] as? List<Map<String, Any>>
        choices?.let { chs ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                chs.forEach { choice ->
                    ChoiceCard(choice)
                }
            }
        }
    }
}

@Composable
fun ChoiceCard(choice: Map<String, Any>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${choice["id"] ?: "A"})",
                color = Color(0xFF60A5FA),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = choice["text"].toString(),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun HeroIntroContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Emoji/icono grande
        content["hero_image"]?.let { heroImage ->
            Text(
                text = heroImage.toString(),
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Subtítulo
        content["subtitle"]?.let { subtitle ->
            Text(
                text = subtitle.toString(),
                color = Color(0xFFFBBF24),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Estadísticas
        val stats = content["stats"] as? List<Map<String, String>>
        stats?.let { sts ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sts.forEach { stat ->
                    Text(
                        text = "${stat["icon"] ?: "📊"} ${stat["text"] ?: ""}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveConceptContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Definición
        content["definition"]?.let { definition ->
            Text(
                text = definition.toString(),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Ejemplos
        val examples = content["examples"] as? List<Map<String, Any>>
        examples?.let { exs ->
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                exs.forEach { example ->
                    ExampleCard(example)
                }
            }
        }
    }
}

@Composable
fun ExampleCard(example: Map<String, Any>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = example["icon"]?.toString() ?: "💡",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = example["text"].toString(),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun InteractiveMapContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Descripción
        content["description"]?.let { description ->
            Text(
                text = description.toString(),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Hechos clave
        val keyFacts = content["key_facts"] as? List<String>
        keyFacts?.let { facts ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                facts.forEach { fact ->
                    Text(
                        text = fact,
                        color = Color(0xFF60A5FA),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ClassificationGameContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Instrucción
        content["instruction"]?.let { instruction ->
            Text(
                text = instruction.toString(),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Casos
        val cases = content["cases"] as? List<Map<String, String>>
        cases?.let { cs ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cs.forEach { case ->
                    Text(
                        text = "• ${case["text"] ?: ""}",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
fun MiniChallengeContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "🧠 DESAFÍO RÁPIDO",
            color = Color(0xFFFBBF24),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Preguntas (primera pregunta como ejemplo)
        val questions = content["questions"] as? List<Map<String, Any>>
        questions?.firstOrNull()?.let { question ->
            question["question"]?.let { q ->
                Text(
                    text = q.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CompletionContent(content: Map<String, Any>, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // XP ganada
        content["xp_earned"]?.let { xp ->
            Text(
                text = "⭐ +$xp XP",
                color = Color(0xFFFBBF24),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Badge desbloqueado
        content["badge_unlocked"]?.let { badge ->
            Text(
                text = "🏆 $badge",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Resumen
        content["summary"]?.let { summary ->
            Text(
                text = summary.toString(),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Botón especial para completar
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
        ) {
            Text("¡Completar Lección!")
        }
    }
}

@Composable
fun DefaultContent(content: Map<String, Any>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Mostrar todo el contenido como texto simple
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