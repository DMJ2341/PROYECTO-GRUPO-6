// app/src/main/java/com/example/cyberlearnapp/ui/screens/TestResultScreen.kt
package com.example.cyberlearnapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.models.*
import com.example.cyberlearnapp.ui.theme.*
import com.example.cyberlearnapp.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel()
) {
    val result by viewModel.result.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("📜 Certs", "🧪 Labs", "🛤️ Paths", "💡 Skills")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados del Test") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundMain
                )
            )
        }
    ) { padding ->

        if (result == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Calculando resultado...")
                }
            }
        } else {
            val role = CyberRole.fromString(result!!.recommendedRole)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card del resultado principal
                item {
                    RoleResultCard(
                        role = role!!,
                        confidence = result!!.confidence
                    )
                }

                // Dimensiones principales
                item {
                    PersonalityDimensionsCard(
                        topDimensions = result!!.topDimensions
                    )
                }

                // Referencia académica
                if (recommendations?.academicReference != null) {
                    item {
                        AcademicReferenceCard(
                            reference = recommendations!!.academicReference!!.reference
                        )
                    }
                }

                // Tabs
                item {
                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }
                }

                // Contenido según tab seleccionado
                when (selectedTab) {
                    0 -> { // Certificaciones
                        if (recommendations != null) {
                            items(recommendations!!.certifications) { cert ->
                                CertificationCard(cert)
                            }
                        }
                    }
                    1 -> { // Labs
                        if (recommendations != null) {
                            items(recommendations!!.labs) { lab ->
                                LabCard(lab)
                            }
                        }
                    }
                    2 -> { // Learning Paths
                        if (recommendations != null) {
                            items(recommendations!!.learningPaths) { path ->
                                LearningPathCard(path)
                            }
                        }
                    }
                    3 -> { // Skills
                        if (recommendations != null) {
                            item {
                                SkillsGrid(recommendations!!.skills)
                            }
                        }
                    }
                }

                // Botón retomar test
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.resetTest()
                            navController.navigate("test") {
                                popUpTo("test_result") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retomar Test")
                    }
                }
            }
        }
    }
}

@Composable
fun RoleResultCard(role: CyberRole, confidence: Float) {
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value),
        colors = CardDefaults.cardColors(
            containerColor = Color(role.color).copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji del rol
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(role.color)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = role.emoji,
                    style = MaterialTheme.typography.displayLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Tu perfil ideal es:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                role.displayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(role.color)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                role.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de confianza
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Confianza:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${(confidence * 100).toInt()}%",
                        fontWeight = FontWeight.Bold,
                        color = Color(role.color)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { confidence },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(role.color)
                )
            }
        }
    }
}

@Composable
fun PersonalityDimensionsCard(topDimensions: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "🧩 Tus Dimensiones Principales",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            topDimensions.take(3).forEach { dimension ->
                val displayName = when(dimension) {
                    "INVESTIGATIVE" -> "Investigativo - Análisis y resolución"
                    "REALISTIC" -> "Realista - Técnico y práctico"
                    "SOCIAL" -> "Social - Colaboración y comunicación"
                    "CONVENTIONAL" -> "Convencional - Procesos y documentación"
                    "ENTERPRISING" -> "Emprendedor - Liderazgo y estrategia"
                    "ARTISTIC" -> "Artístico - Creatividad e innovación"
                    else -> dimension
                }

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(displayName)
                }
            }
        }
    }
}

@Composable
fun AcademicReferenceCard(reference: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "📚 Respaldo Académico",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                reference,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CertificationCard(cert: Certification) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (cert.isFree) {
                        AssistChip(
                            onClick = {},
                            label = { Text("GRATIS", fontSize = MaterialTheme.typography.labelSmall.fontSize) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Text(
                        cert.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        cert.provider,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Badge de dificultad
                val diffColor = when(cert.difficulty.lowercase()) {
                    "beginner" -> Color(0xFF4CAF50)
                    "intermediate" -> Color(0xFFFF9800)
                    "advanced" -> Color(0xFFE53935)
                    else -> MaterialTheme.colorScheme.primary
                }

                AssistChip(
                    onClick = {},
                    label = { Text(cert.difficulty) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = diffColor.copy(alpha = 0.2f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                cert.description,
                style = MaterialTheme.typography.bodyMedium
            )

            if (cert.priceInfo != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "💰 ${cert.priceInfo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LabCard(lab: Lab) {
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
            Icon(
                Icons.Default.Science,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        lab.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (lab.isFree) {
                        Spacer(modifier = Modifier.width(8.dp))
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

                Text(
                    lab.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun LearningPathCard(path: LearningPath) {
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
            Icon(
                Icons.Default.TrendingUp,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    path.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        path.platform,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AssistChip(
                        onClick = {},
                        label = { Text("${path.estimatedHours}h") },
                        leadingIcon = {
                            Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    path.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SkillsGrid(skills: List<RoleSkill>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        skills.forEach { skill ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        skill.skill,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}