package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.network.models.LessonProgressRequest
import com.example.cyberlearnapp.ui.screens.interactive.*
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveLessonScreen(
    lessonId: String,
    lessonTitle: String,
    onNavigateBack: () -> Unit,
    onLessonCompleted: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Estados para tracking
    var signalsFound by remember { mutableStateOf<List<Int>>(emptyList()) }
    var quizAnswers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var completedScreens by remember { mutableStateOf<List<Int>>(emptyList()) }
    var totalXpEarned by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // Cargar lección
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A)
                )
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF60A5FA))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando lección interactiva...",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFDC2626).copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "❌", fontSize = 64.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Error al cargar",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.errorMessage ?: "Error desconocido",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        viewModel.clearError()
                                        onNavigateBack()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDC2626)
                                    )
                                ) {
                                    Text("Volver")
                                }
                            }
                        }
                    }
                }

                state.lesson != null -> {
                    val pagerState = rememberPagerState(pageCount = { state.lesson!!.totalScreens })

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Progress indicator
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
                                    text = "${pagerState.currentPage + 1}/${state.lesson!!.totalScreens}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    repeat(state.lesson!!.totalScreens) { index ->
                                        Box(
                                            modifier = Modifier
                                                .width(if (index == pagerState.currentPage) 24.dp else 8.dp)
                                                .height(8.dp)
                                                .background(
                                                    color = if (index <= pagerState.currentPage)
                                                        Color(0xFF60A5FA)
                                                    else
                                                        Color.White.copy(alpha = 0.3f),
                                                    shape = MaterialTheme.shapes.small
                                                )
                                        )
                                    }
                                }

                                Text(
                                    text = "⭐ $totalXpEarned XP",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Pager con pantallas
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.weight(1f),
                            userScrollEnabled = false
                        ) { page ->
                            val screen = state.lesson!!.screens[page]

                            when (screen.type) {
                                "story_hook" -> {
                                    StoryHookScreen(
                                        screen = screen,
                                        onNext = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(page + 1)
                                                if (!completedScreens.contains(page)) {
                                                    completedScreens = completedScreens + page
                                                }
                                            }
                                        }
                                    )
                                }

                                "interactive_email" -> {
                                    InteractiveEmailScreen(
                                        screen = screen,
                                        signalsFound = signalsFound,
                                        onSignalFound = { signalId, xp ->
                                            if (!signalsFound.contains(signalId)) {
                                                signalsFound = signalsFound + signalId
                                                totalXpEarned += xp
                                            }
                                        },
                                        onNext = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(page + 1)
                                                if (!completedScreens.contains(page)) {
                                                    completedScreens = completedScreens + page
                                                }
                                            }
                                        }
                                    )
                                }

                                "checklist" -> {
                                    ChecklistScreen(
                                        screen = screen,
                                        signalsFound = signalsFound,
                                        onNext = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(page + 1)
                                                if (!completedScreens.contains(page)) {
                                                    completedScreens = completedScreens + page
                                                }
                                            }
                                        }
                                    )
                                }

                                "action_plan" -> {
                                    ActionPlanScreen(
                                        screen = screen,
                                        onNext = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(page + 1)
                                                if (!completedScreens.contains(page)) {
                                                    completedScreens = completedScreens + page
                                                }
                                            }
                                        }
                                    )
                                }

                                "quiz" -> {
                                    QuizScreen(
                                        screen = screen,
                                        quizAnswers = quizAnswers,
                                        onAnswerSelected = { questionId, answerId, isCorrect, xp ->
                                            quizAnswers = quizAnswers + (questionId.toString() to answerId)
                                            if (isCorrect) {
                                                totalXpEarned += xp
                                            }
                                        },
                                        onComplete = {
                                            scope.launch {
                                                // ✅ CORREGIDO: Usar la función del ViewModel
                                                val token = viewModel.getToken()
                                                if (token.isNotEmpty()) {
                                                    try {
                                                        RetrofitInstance.api.saveLessonProgress(
                                                            lessonId = lessonId,
                                                            token = "Bearer $token",
                                                            progress = LessonProgressRequest(
                                                                currentScreen = state.lesson!!.totalScreens,
                                                                completedScreens = completedScreens + page,
                                                                signalsFound = signalsFound,
                                                                quizAnswers = quizAnswers
                                                            )
                                                        )
                                                    } catch (e: Exception) {
                                                        println("Error guardando progreso: ${e.message}")
                                                    }
                                                }

                                                onLessonCompleted()
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        // Botones de navegación
                        if (pagerState.currentPage > 0 &&
                            pagerState.currentPage < state.lesson!!.totalScreens - 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E293B))
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }
                                ) {
                                    Text("← Anterior", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}