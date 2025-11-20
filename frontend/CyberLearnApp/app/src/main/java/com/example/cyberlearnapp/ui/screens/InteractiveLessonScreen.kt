package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.viewmodel.InteractiveLessonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveLessonScreen(
    lessonId: String,
    onNavigateBack: () -> Unit,
    onLessonCompleted: () -> Unit,
    viewModel: InteractiveLessonViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var currentScreenIndex by remember { mutableIntStateOf(0) }

    // Cargar lección al iniciar
    LaunchedEffect(lessonId) {
        println("🎬 [INTERACTIVE-LESSON] Cargando lección: $lessonId")
        viewModel.loadLesson(lessonId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.lesson?.title ?: "Lección Interactiva",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
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
                            shape = RoundedCornerShape(20.dp)
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
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                state.lesson != null -> {
                    val lesson = state.lesson!!
                    val screens = lesson.screens

                    if (screens.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay contenido disponible",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Progress indicator
                            LinearProgressIndicator(
                                progress = { (currentScreenIndex + 1).toFloat() / screens.size },
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF3B82F6),
                                trackColor = Color(0xFF1E293B)
                            )

                            // Screen content
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp)
                            ) {
                                InteractiveScreenContent(
                                    screen = screens[currentScreenIndex]
                                )
                            }

                            // Navigation buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Previous button
                                if (currentScreenIndex > 0) {
                                    Button(
                                        onClick = { currentScreenIndex-- },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF374151)
                                        )
                                    ) {
                                        Text("Anterior")
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                // Next/Complete button
                                Button(
                                    onClick = {
                                        if (currentScreenIndex < screens.size - 1) {
                                            currentScreenIndex++
                                        } else {
                                            onLessonCompleted()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreenIndex == screens.size - 1) {
                                            Color(0xFF10B981)
                                        } else {
                                            Color(0xFF3B82F6)
                                        }
                                    )
                                ) {
                                    Text(
                                        text = if (currentScreenIndex == screens.size - 1) {
                                            "Completar"
                                        } else {
                                            "Siguiente"
                                        }
                                    )
                                    if (currentScreenIndex < screens.size - 1) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveScreenContent(
    screen: com.example.cyberlearnapp.viewmodel.InteractiveScreen
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Screen number badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF3B82F6).copy(alpha = 0.2f)
            ) {
                Text(
                    text = "Pantalla ${screen.screenNumber}",
                    color = Color(0xFF3B82F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = screen.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            // Subtitle
            screen.subtitle?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Content (simple display for now)
            if (screen.content.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(16.dp)
                ) {
                    Column {
                        screen.content.forEach { (key, value) ->
                            Text(
                                text = "$key: $value",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Hint
            screen.hint?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFBBF24).copy(alpha = 0.2f))
                        .padding(12.dp)
                ) {
                    Row {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            color = Color(0xFFFBBF24),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}