package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.cyberlearnapp.network.models.Lesson
import com.example.cyberlearnapp.viewmodel.CourseViewModel
import com.example.cyberlearnapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    navController: NavController,
    courseId: Int,
    viewModel: CourseViewModel = hiltViewModel()
) {
    val course by viewModel.selectedCourse.collectAsState()
    val lessons by viewModel.courseLessons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadCourseLessons(courseId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(courseId) {
        viewModel.loadCourseDetail(courseId)
        viewModel.loadCourseLessons(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        course?.title ?: "Cargando...",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Volver",
                            tint = PrimaryCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundMain
                )
            )
        },
        containerColor = BackgroundMain
    ) { padding ->
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryCyan,
                    strokeWidth = 4.dp
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER DEL CURSO
            item {
                course?.let { c ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceCard
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            PrimaryCyan.copy(alpha = 0.15f),
                                            SurfaceCard
                                        )
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Column {
                                Text(
                                    text = c.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    lineHeight = 32.sp
                                )

                                Spacer(Modifier.height(12.dp))

                                Text(
                                    text = c.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    lineHeight = 22.sp
                                )

                                Spacer(Modifier.height(20.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoChip(
                                        icon = "📊",
                                        text = "Nivel: ${c.level}"
                                    )
                                    InfoChip(
                                        icon = "📚",
                                        text = "${lessons.size} lecciones"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TÍTULO DE LECCIONES
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📖",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Lecciones",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // LISTA DE LECCIONES
            items(lessons) { lesson ->
                LessonItem(
                    lesson = lesson,
                    onClick = {
                        if (!lesson.isLocked) {
                            navController.navigate("lesson_detail/${lesson.id}")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LessonItem(
    lesson: Lesson,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (lesson.isLocked) 0.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !lesson.isLocked, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                lesson.isCompleted -> SuccessGreen.copy(alpha = 0.15f)
                lesson.isLocked -> SurfaceElevated.copy(alpha = 0.5f)
                else -> SurfaceCard
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ICONO DE ESTADO
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        when {
                            lesson.isCompleted -> SuccessGreen
                            lesson.isLocked -> SurfaceElevated
                            else -> PrimaryCyan
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        lesson.isCompleted -> Icons.Default.CheckCircle
                        lesson.isLocked -> Icons.Default.Lock
                        else -> Icons.Default.PlayArrow
                    },
                    contentDescription = null,
                    tint = when {
                        lesson.isCompleted -> BackgroundMain
                        lesson.isLocked -> TextTertiary
                        else -> BackgroundMain
                    },
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // CONTENIDO
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (lesson.isLocked) TextTertiary else TextPrimary,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LessonStat(
                        icon = "⏱️",
                        text = "${lesson.durationMinutes} min",
                        color = if (lesson.isLocked) TextTertiary else TextSecondary
                    )
                    LessonStat(
                        icon = "⭐",
                        text = "${lesson.xpReward} XP",
                        color = if (lesson.isLocked) TextTertiary else WarningOrange
                    )
                }
            }

            // BADGE DE COMPLETADA
            if (lesson.isCompleted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SuccessGreen,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "✓ Completada",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundMain,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: String,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceElevated,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = icon,
                fontSize = 14.sp
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun LessonStat(
    icon: String,
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 14.sp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = color,
            fontSize = 12.sp
        )
    }
}