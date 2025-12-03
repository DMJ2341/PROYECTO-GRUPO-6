package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.cyberlearnapp.navigation.Screens
import com.example.cyberlearnapp.network.models.CourseProgress
import com.example.cyberlearnapp.ui.components.DailyTermCard
import com.example.cyberlearnapp.ui.components.StatCard
import com.example.cyberlearnapp.ui.components.XpLevelBar
import com.example.cyberlearnapp.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // ✅ REFRESH AUTOMÁTICO
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshDashboard()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // ✅ FONDO CON GRADIENTE
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1419),
                        Color(0xFF1A2332)
                    )
                )
            )
    ) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00D9FF))
            }
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // ✅ HEADER CON SALUDO Y RACHA REAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A2332)
                ),
                border = BorderStroke(2.dp, Color(0xFF00D9FF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👋", fontSize = 32.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "¡Hola, Hacker!",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 26.sp
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Sigue aprendiendo hoy",
                            color = Color.White.copy(0.7f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // ✅ RACHA REAL - CORREGIDO
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFBBF24).copy(0.2f)
                        ),
                        border = BorderStroke(2.dp, Color(0xFFFBBF24)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔥", fontSize = 28.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${state.currentStreak} días",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24),
                                fontSize = 16.sp
                            )
                            Text(
                                "racha",
                                color = Color.White.copy(0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ✅ XP LEVEL BAR CON DATOS REALES
            XpLevelBar(
                currentXp = state.userXp,
                level = state.userLevel
            )

            Spacer(Modifier.height(24.dp))

            // ✅ STATS GRID CON DATOS REALES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    emoji = "🎓",
                    value = state.userXp.toString(),
                    label = "XP Total",
                    color = Color(0xFF8B5CF6),
                    onClick = null,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    emoji = "🏆",
                    value = "#${state.userLevel}",
                    label = "Nivel",
                    color = Color(0xFF00D9FF),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    emoji = "📚",
                    value = "${state.completedCourses}/${state.totalCourses}",
                    label = "Cursos",
                    color = Color(0xFF10B981),
                    onClick = { navController.navigate(Screens.Courses.route) },
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    emoji = "📖",
                    value = state.glossaryStats?.learnedCount?.toString() ?: "0",
                    label = "Términos",
                    color = Color(0xFFFBBF24),
                    onClick = { navController.navigate(Screens.Glossary.route) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(28.dp))

            // ✅ CURSOS EN PROGRESO CON DATOS REALES
            if (state.coursesProgress.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📚 Cursos en Progreso",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    TextButton(onClick = { navController.navigate(Screens.Courses.route) }) {
                        Text(
                            "Ver todos",
                            color = Color(0xFF00D9FF),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("→", color = Color(0xFF00D9FF), fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ✅ MAPEAR CURSOS REALES
                state.coursesProgress.take(2).forEach { course ->
                    CourseProgressCard(
                        course = course,
                        onClick = {
                            navController.navigate(Screens.CourseDetail.createRoute(course.courseId))
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ✅ GLOSARIO CON LAYOUT ARREGLADO Y DATOS REALES
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(20.dp))
                    .clickable { navController.navigate(Screens.Glossary.route) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFBBF24).copy(0.15f)
                ),
                border = BorderStroke(2.dp, Color(0xFFFBBF24)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFBBF24)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "📖",
                                fontSize = 32.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                "Glosario",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Términos de Ciberseguridad",
                                color = Color.White.copy(0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    // ✅ STATS EN COLUMNA (NO APILADOS)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFBBF24).copy(0.3f)
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFFFBBF24)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "${state.glossaryStats?.learnedCount ?: 0}/${state.glossaryStats?.totalTerms ?: 262}",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24),
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${state.glossaryStats?.progressPercentage?.toInt() ?: 0}% completado",
                                color = Color.White.copy(0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ✅ TÉRMINO DEL DÍA
            state.dailyTerm?.let { dailyTermWrapper ->
                Text(
                    "💡 Término del Día",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(16.dp))

                DailyTermCard(
                    term = dailyTermWrapper.term,
                    onClick = { navController.navigate(Screens.Glossary.route) }
                )

                Spacer(Modifier.height(24.dp))
            }

            // ✅ SIGUIENTE PASO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B5CF6).copy(0.15f)
                ),
                border = BorderStroke(2.dp, Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎯", fontSize = 32.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Siguiente Paso",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    if (!state.hasPreferenceResult) {
                        Button(
                            onClick = { navController.navigate(Screens.PreferenceTest.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8B5CF6)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("🧪", fontSize = 28.sp)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Descubre tu Rol (Test Vocacional)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    } else if (state.completedCourses >= 5) {
                        Button(
                            onClick = { navController.navigate("final_exam/intro") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏆", fontSize = 28.sp)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Examen Final Integrador",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { navController.navigate(Screens.PreferenceTest.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF8B5CF6)
                            ),
                            border = BorderStroke(2.dp, Color(0xFF8B5CF6)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Ver mi Perfil Profesional",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { state.completedCourses.toFloat() / 5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                            color = Color(0xFF8B5CF6),
                            trackColor = Color(0xFF2D3748),
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "${state.completedCourses}/5 cursos completados",
                            color = Color.White.copy(0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ✅ COMPONENTE AUXILIAR
@Composable
fun CourseProgressCard(
    course: CourseProgress,
    onClick: () -> Unit
) {
    val courseColor = when {
        "fundamento" in course.title.lowercase() -> Color(0xFF6366F1)
        "network" in course.title.lowercase() || "red" in course.title.lowercase() -> Color(0xFF10B981)
        "web" in course.title.lowercase() -> Color(0xFF06B6D4)
        "crypto" in course.title.lowercase() -> Color(0xFF14B8A6)
        else -> Color(0xFF8B5CF6)
    }

    val courseIcon = when {
        "fundamento" in course.title.lowercase() -> "🛡️"
        "network" in course.title.lowercase() || "red" in course.title.lowercase() -> "🌐"
        "web" in course.title.lowercase() -> "🌍"
        "crypto" in course.title.lowercase() -> "🔐"
        else -> "📚"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = courseColor.copy(0.15f)
        ),
        border = BorderStroke(2.dp, courseColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = courseColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    courseIcon,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    course.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { course.percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = courseColor,
                    trackColor = Color(0xFF2D3748)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "⏱️ Continuar",
                    color = courseColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = courseColor.copy(0.3f)
                ),
                border = BorderStroke(1.5.dp, courseColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "${course.percentage}%",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}