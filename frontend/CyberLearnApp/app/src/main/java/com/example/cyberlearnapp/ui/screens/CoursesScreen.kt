package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cyberlearnapp.ui.components.CourseCard
import com.example.cyberlearnapp.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    navController: NavController,
    viewModel: CourseViewModel
) {
    val courses by viewModel.courses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        if (courses.isEmpty()) {
            viewModel.loadCourses()
        }
    }

    // ✅ FONDO CON GRADIENTE OSCURO
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1419), // Negro azulado
                        Color(0xFF1A2332)  // Azul oscuro
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // ✅ HEADER CON COLOR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00D9FF).copy(0.15f) // Cyan con transparencia
                ),
                border = BorderStroke(2.dp, Color(0xFF00D9FF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📚",
                                fontSize = 32.sp
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Cursos Disponibles",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 26.sp
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF00D9FF).copy(0.3f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "✨ ${courses.size} cursos para dominar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Botón de filtros
                    IconButton(
                        onClick = { /* TODO: Implement filters */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color(0xFF00D9FF).copy(0.2f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtrar cursos",
                            tint = Color(0xFF00D9FF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ✅ ESTADOS: Loading, Error, Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.shadow(16.dp, RoundedCornerShape(20.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A2332)
                            ),
                            border = BorderStroke(2.dp, Color(0xFF00D9FF)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(56.dp),
                                    color = Color(0xFF00D9FF),
                                    strokeWidth = 5.dp
                                )
                                Text(
                                    text = "⏳ Cargando cursos...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }

                error != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEF4444).copy(0.2f)
                        ),
                        border = BorderStroke(3.dp, Color(0xFFEF4444))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🚨",
                                fontSize = 56.sp
                            )

                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = "Error al cargar cursos",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFEF4444),
                                fontSize = 22.sp
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = error ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontSize = 15.sp
                            )

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = { viewModel.loadCourses() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF4444)
                                ),
                                modifier = Modifier
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Reintentar",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                courses.isEmpty() -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A2332)
                        ),
                        border = BorderStroke(2.dp, Color(0xFF00D9FF).copy(0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📚",
                                    fontSize = 72.sp
                                )
                                Spacer(Modifier.height(20.dp))
                                Text(
                                    text = "No hay cursos disponibles",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Vuelve pronto para nuevos contenidos",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(0.7f),
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                else -> {
                    // ✅ LISTA DE CURSOS
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(courses) { course ->
                            CourseCard(
                                course = course,
                                onClick = {
                                    navController.navigate("course_detail/${course.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}