// app/src/main/java/com/example/cyberlearnapp/ui/screens/TestRecommendationsScreen.kt
package com.example.cyberlearnapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.models.*
import com.example.cyberlearnapp.ui.theme.*
import com.example.cyberlearnapp.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestRecommendationsScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel()
) {
    val recommendations by viewModel.recommendations.collectAsState()
    val result by viewModel.result.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("📜 Certificaciones", "🧪 Laboratorios", "🛤️ Rutas")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Recomendaciones")
                        result?.let {
                            val role = CyberRole.fromString(it.recommendedRole)
                            Text(
                                role?.displayName ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Botón para ver Skills
                    IconButton(onClick = { navController.navigate("test_skills") }) {
                        Icon(Icons.Default.Star, "Skills")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundMain
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Referencia académica (compacta)
            if (recommendations?.academicReference != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.School,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Basado en Holland Code (RIASEC) y NIST SP 800-181",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                }
            }

            // Contenido según tab
            if (recommendations == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (selectedTab) {
                        0 -> { // Certificaciones
                            item {
                                Text(
                                    "💡 Empieza por las gratuitas para ganar experiencia",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(recommendations!!.certifications) { cert ->
                                CertificationCardCompact(cert)
                            }
                        }
                        1 -> { // Labs
                            item {
                                Text(
                                    "🎯 Practica en estos laboratorios reales",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(recommendations!!.labs) { lab ->
                                LabCardCompact(lab)
                            }
                        }
                        2 -> { // Learning Paths
                            item {
                                Text(
                                    "📚 Rutas completas de aprendizaje estructurado",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
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

@Composable
fun CertificationCardCompact(cert: Certification) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cert.url))
                context.startActivity(intent)
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (cert.isFree) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    "GRATIS",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF4CAF50),
                                labelColor = Color.White
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Text(
                        cert.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Badge de dificultad
                val (diffColor, diffText) = when(cert.difficulty.lowercase()) {
                    "beginner" -> Color(0xFF4CAF50) to "Principiante"
                    "intermediate" -> Color(0xFFFF9800) to "Intermedio"
                    "advanced" -> Color(0xFFE53935) to "Avanzado"
                    else -> MaterialTheme.colorScheme.primary to cert.difficulty
                }

                AssistChip(
                    onClick = {},
                    label = { Text(diffText, fontWeight = FontWeight.Medium) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = diffColor.copy(alpha = 0.15f),
                        labelColor = diffColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Provider
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Business,
                    null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    cert.provider,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                cert.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )

            // Precio
            if (cert.priceInfo != null && !cert.isFree) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AttachMoney,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        cert.priceInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
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
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lab.url))
                context.startActivity(intent)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono
            Icon(
                Icons.Default.Science,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        lab.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (lab.isFree) {
                        Icon(
                            Icons.Default.StarRate,
                            "Gratis",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    lab.platform,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    lab.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
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
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(path.url))
                context.startActivity(intent)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono
            Icon(
                Icons.Default.TrendingUp,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    path.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        path.platform,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                "${path.estimatedHours}h",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Schedule,
                                null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.height(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    path.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }
        }
    }
}