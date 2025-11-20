package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.LessonDetailResponse
import com.example.cyberlearnapp.network.RetrofitInstance
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,  // ✅ String ahora
    lessonTitle: String,
    onNavigateBack: () -> Unit,
    onLessonCompleted: () -> Unit
) {
    var lessonContent by remember { mutableStateOf<LessonDetailResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCompleting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(lessonId) {
        scope.launch {
            try {
                println("🔍 [LESSON] Cargando lección: $lessonId")
                isLoading = true
                val response = RetrofitInstance.api.getLessonContent(lessonId = lessonId)

                println("📡 [LESSON] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    lessonContent = response.body()!!
                    println("✅ [LESSON] Lección cargada: ${lessonContent?.title}")
                } else {
                    errorMessage = "Error: ${response.code()}"
                    println("❌ [LESSON] Error: ${response.code()}")
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error cargando lección"
                println("💥 [LESSON] Exception: ${e.message}")
                e.printStackTrace()
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
                        maxLines = 2,
                        fontSize = 16.sp,
                        lineHeight = 20.sp
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
                                text = "Cargando lección...",
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
                            shape = RoundedCornerShape(20.dp),
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
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                lessonContent != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Header con stats
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF3B82F6),
                                            Color(0xFF8B5CF6)
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .padding(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text(text = "⏱️", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${lessonContent!!.duration_minutes} min",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFBBF24).copy(alpha = 0.2f))
                                        .padding(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text(text = "⭐", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "+${lessonContent!!.xp_reward} XP",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Contenido formateado
                        FormattedLessonContent(
                            content = lessonContent!!.content ?: "Contenido no disponible",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón completar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF10B981),
                                            Color(0xFF059669)
                                        )
                                    )
                                )
                        ) {
                            Button(
                                onClick = {
                                    if (!isCompleting) {
                                        isCompleting = true
                                        scope.launch {
                                            kotlinx.coroutines.delay(500)
                                            onLessonCompleted()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                elevation = ButtonDefaults.buttonElevation(0.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                if (isCompleting) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.5.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Completar Lección",
                                            color = Color.White,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

// ✅ Mantén las funciones FormattedLessonContent, SectionType, RenderSection y SectionCard
// exactamente como las tenías antes (no las cambies)

@Composable
fun FormattedLessonContent(
    content: String,
    modifier: Modifier = Modifier
) {
    val lines = content.lines()

    Column(modifier = modifier) {
        var currentSection: SectionType? = null
        val sectionLines = mutableListOf<String>()

        for (line in lines) {
            when {
                line.contains("LECCIÓN") && line.contains("📚") -> {
                    if (sectionLines.isNotEmpty()) {
                        RenderSection(currentSection, sectionLines.toList())
                        sectionLines.clear()
                    }
                    currentSection = SectionType.MAIN_TITLE
                    sectionLines.add(line)
                }
                line.contains("🔒 CONFIDENCIALIDAD") -> {
                    if (sectionLines.isNotEmpty()) {
                        RenderSection(currentSection, sectionLines.toList())
                        sectionLines.clear()
                    }
                    currentSection = SectionType.CONFIDENTIALITY
                    sectionLines.add(line)
                }
                line.contains("✅ INTEGRIDAD") -> {
                    if (sectionLines.isNotEmpty()) {
                        RenderSection(currentSection, sectionLines.toList())
                        sectionLines.clear()
                    }
                    currentSection = SectionType.INTEGRITY
                    sectionLines.add(line)
                }
                line.contains("🚀 DISPONIBILIDAD") -> {
                    if (sectionLines.isNotEmpty()) {
                        RenderSection(currentSection, sectionLines.toList())
                        sectionLines.clear()
                    }
                    currentSection = SectionType.AVAILABILITY
                    sectionLines.add(line)
                }
                line.startsWith("━━━━") -> {
                    if (sectionLines.isNotEmpty()) {
                        RenderSection(currentSection, sectionLines.toList())
                        sectionLines.clear()
                        currentSection = null
                    }
                }
                else -> {
                    sectionLines.add(line)
                }
            }
        }

        if (sectionLines.isNotEmpty()) {
            RenderSection(currentSection, sectionLines.toList())
        }
    }
}

enum class SectionType {
    MAIN_TITLE,
    CONFIDENTIALITY,
    INTEGRITY,
    AVAILABILITY,
    NORMAL
}

@Composable
fun RenderSection(type: SectionType?, lines: List<String>) {
    when (type) {
        SectionType.MAIN_TITLE -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3B82F6),
                                Color(0xFF1E3A8A)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = lines.joinToString("\n"),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        SectionType.CONFIDENTIALITY -> {
            SectionCard(
                color = Color(0xFF3B82F6),
                lines = lines
            )
        }

        SectionType.INTEGRITY -> {
            SectionCard(
                color = Color(0xFF10B981),
                lines = lines
            )
        }

        SectionType.AVAILABILITY -> {
            SectionCard(
                color = Color(0xFF8B5CF6),
                lines = lines
            )
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .padding(16.dp)
            ) {
                Text(
                    text = lines.joinToString("\n"),
                    color = Color(0xFFE2E8F0),
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SectionCard(color: Color, lines: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.15f))
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(color)
                .align(Alignment.CenterStart)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = lines.firstOrNull() ?: "",
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lines.drop(1).joinToString("\n"),
                color = Color(0xFFE2E8F0),
                fontSize = 15.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}