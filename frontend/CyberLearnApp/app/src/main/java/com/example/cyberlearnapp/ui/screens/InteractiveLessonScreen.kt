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
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.network.models.InteractiveLesson
import com.example.cyberlearnapp.network.models.LessonProgressRequest
import com.example.cyberlearnapp.ui.screens.interactive.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveLessonScreen(
    lessonId: String,
    lessonTitle: String,
    token: String,
    onNavigateBack: () -> Unit,
    onLessonCompleted: () -> Unit
) {
    var lesson by remember { mutableStateOf<InteractiveLesson?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Estados para tracking
    var signalsFound by remember { mutableStateOf<List<Int>>(emptyList()) }
    var quizAnswers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var completedScreens by remember { mutableStateOf<List<Int>>(emptyList()) }
    var totalXpEarned by remember { mutableStateOf(0) }

    // Cargar lección
    LaunchedEffect(lessonId) {
        scope.launch {
            try {
                isLoading = true
                val response = RetrofitInstance.api.getInteractiveLesson(
                    lessonId = lessonId,
                    token = "Bearer $token"
                )
                if (response.isSuccessful && response.body() != null) {
                    lesson = response.body()!!.lesson
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error cargando lección"
                isLoading = false
            }
        }
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
                isLoading -> {
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

                errorMessage != null -> {
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
                                    text = errorMessage ?: "Error desconocido",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                lesson != null -> {
                    val pagerState = rememberPagerState(pageCount = { lesson!!.totalScreens })

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
                                    text = "${pagerState.currentPage + 1}/${lesson!!.totalScreens}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    repeat(lesson!!.totalScreens) { index ->
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
                            val screen = lesson!!.screens[page]

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
                                                // Guardar progreso final
                                                try {
                                                    RetrofitInstance.api.saveLessonProgress(
                                                        lessonId = lessonId,
                                                        token = "Bearer $token",
                                                        progress = LessonProgressRequest(
                                                            currentScreen = lesson!!.totalScreens,
                                                            completedScreens = completedScreens + page,
                                                            signalsFound = signalsFound,
                                                            quizAnswers = quizAnswers
                                                        )
                                                    )
                                                } catch (e: Exception) {
                                                    println("Error guardando progreso: ${e.message}")
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
                            pagerState.currentPage < lesson!!.totalScreens - 1) {
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