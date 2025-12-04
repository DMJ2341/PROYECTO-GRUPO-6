package com.example.cyberlearnapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.models.*
import com.example.cyberlearnapp.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestRecommendationsScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel()
) {
    val recommendations by viewModel.recommendations.collectAsState()
    val result by viewModel.result.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("📜 Certificaciones", "🧪 Laboratorios", "🛤️ Rutas")

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
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 24.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Recomendaciones",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            result?.let {
                                val role = CyberRole.fromString(it.recommendedRole)
                                Text(
                                    role?.displayName ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFF00D9FF))
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("test_skills") }) {
                            Icon(Icons.Default.Star, "Skills", tint = Color(0xFFFBBF24))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A2332)
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // REFERENCIA ACADÉMICA
                if (recommendations?.academicReference != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF00D9FF).copy(0.15f)
                        ),
                        border = BorderStroke(2.dp, Color(0xFF00D9FF)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "🎓",
                                fontSize = 32.sp
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Basado en:",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Holland Code (RIASEC) y NIST SP 800-181",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // ✅ TABS CON COLORES VIBRANTES (SIN INDICADOR PERSONALIZADO)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1A2332),
                    contentColor = Color(0xFF00D9FF)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                            },
                            selectedContentColor = Color(0xFF00D9FF),
                            unselectedContentColor = Color.White.copy(0.6f)
                        )
                    }
                }

                // CONTENIDO SEGÚN TAB
                if (recommendations == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00D9FF))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (selectedTab) {
                            0 -> { // CERTIFICACIONES
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF10B981).copy(0.15f)
                                        ),
                                        border = BorderStroke(1.5.dp, Color(0xFF10B981)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("💡", fontSize = 24.sp)
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                "Empieza por las gratuitas para ganar experiencia",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                                items(recommendations!!.certifications) { cert ->
                                    CertificationCardCompact(cert)
                                }
                            }
                            1 -> { // LABS
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF8B5CF6).copy(0.15f)
                                        ),
                                        border = BorderStroke(1.5.dp, Color(0xFF8B5CF6)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🎯", fontSize = 24.sp)
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                "Practica en estos laboratorios reales",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                                items(recommendations!!.labs) { lab ->
                                    LabCardCompact(lab)
                                }
                            }
                            2 -> { // RUTAS
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFFBBF24).copy(0.15f)
                                        ),
                                        border = BorderStroke(1.5.dp, Color(0xFFFBBF24)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("📚", fontSize = 24.sp)
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                "Rutas completas de aprendizaje estructurado",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                                items(recommendations!!.learningPaths) { path ->
                                    LearningPathCardCompact(path)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ✅ CARDS COMPACTAS CON COLORES VIBRANTES
@Composable
fun CertificationCardCompact(cert: Certification) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cert.url))
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        ),
        border = BorderStroke(2.dp, Color(0xFF00D9FF).copy(0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (cert.isFree) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF10B981)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "✨ GRATIS",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    Text(
                        cert.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.width(12.dp))

                val (diffColor, diffText) = when(cert.difficulty.lowercase()) {
                    "beginner" -> Color(0xFF10B981) to "🌱 Principiante"
                    "intermediate" -> Color(0xFFFBBF24) to "⚡ Intermedio"
                    "advanced" -> Color(0xFFEF4444) to "🔥 Avanzado"
                    else -> Color(0xFF00D9FF) to cert.difficulty
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = diffColor.copy(0.2f)
                    ),
                    border = BorderStroke(1.5.dp, diffColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        diffText,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏢", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    cert.provider,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                cert.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(0.9f),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            if (cert.priceInfo != null && !cert.isFree) {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFBBF24).copy(0.2f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFBBF24)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💰", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            cert.priceInfo,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LabCardCompact(lab: Lab) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lab.url))
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        ),
        border = BorderStroke(2.dp, Color(0xFF8B5CF6).copy(0.4f)),
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
                    containerColor = Color(0xFF8B5CF6)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "🧪",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        lab.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 17.sp
                    )
                    if (lab.isFree) {
                        Text(
                            "⭐",
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    lab.platform,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.7f),
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    lab.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun LearningPathCardCompact(path: LearningPath) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(path.url))
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        ),
        border = BorderStroke(2.dp, Color(0xFFFBBF24).copy(0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFBBF24)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "📈",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    path.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        path.platform,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.7f),
                        fontSize = 13.sp
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFBBF24).copy(0.2f)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFFBBF24)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⏱️", fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${path.estimatedHours}h",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    path.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}