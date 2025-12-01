package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.network.models.GlossaryTerm
import com.example.cyberlearnapp.viewmodel.GlossaryViewModel
import com.example.cyberlearnapp.viewmodel.PracticeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(
    navController: NavController,
    viewModel: GlossaryViewModel = hiltViewModel()
) {
    val terms by viewModel.terms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val stats by viewModel.stats.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTerm by remember { mutableStateOf<GlossaryTerm?>(null) }
    var showPracticeModeSelector by remember { mutableStateOf(false) }

    val dailyTerm = remember(terms) {
        if (terms.isNotEmpty()) {
            terms.firstOrNull { !it.isLearned } ?: terms.random()
        } else null
    }

    Scaffold(
        floatingActionButton = {
            if (mode == "practice" && terms.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { showPracticeModeSelector = true },
                    icon = { Icon(Icons.Default.PlayArrow, "Iniciar práctica") },
                    text = { Text("Iniciar Práctica", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "📚 Glosario Ciberseguridad",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (stats != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stats!!.learnedCount}/${stats!!.totalTerms} términos dominados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${stats!!.progressPercentage.toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = (stats!!.progressPercentage / 100).toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(20.dp))

            // SELECTOR DE MODO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = mode == "learn",
                    onClick = { viewModel.setMode("learn") },
                    label = { Text("📖 APRENDER") },
                    leadingIcon = if (mode == "learn") {
                        { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                    } else null,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = mode == "practice",
                    onClick = { viewModel.setMode("practice") },
                    label = { Text("🎮 PRACTICAR") },
                    leadingIcon = if (mode == "practice") {
                        { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                    } else null,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            if (mode == "learn") {
                if (dailyTerm != null) {
                    DailyTermCard(
                        term = dailyTerm,
                        onClick = { selectedTerm = dailyTerm }
                    )
                    Spacer(Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.loadTerms(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar término...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.loadTerms("")
                            }) {
                                Icon(Icons.Default.Clear, "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(Modifier.height(16.dp))
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }

            if (terms.isEmpty() && !isLoading) {
                EmptyStateCard(mode = mode, searchQuery = searchQuery)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(terms) { term ->
                        TermCard(
                            term = term,
                            onClick = { selectedTerm = term },
                            onToggleLearned = { viewModel.toggleLearned(term.id, term.isLearned) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        // DETALLE TÉRMINO
        if (selectedTerm != null) {
            TermDetailFullScreen(
                term = selectedTerm!!,
                onDismiss = { selectedTerm = null },
                onToggleLearned = {
                    viewModel.toggleLearned(selectedTerm!!.id, selectedTerm!!.isLearned)
                }
            )
        }

        // DIÁLOGO SELECCIÓN PRÁCTICA (CORREGIDO)
        if (showPracticeModeSelector) {
            AlertDialog(
                onDismissRequest = { showPracticeModeSelector = false },
                title = { Text("🎮 Elige modo de práctica", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // FLASHCARDS
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showPracticeModeSelector = false
                                    // ✅ CORRECCIÓN: Callback directo
                                    viewModel.startPracticeSession(PracticeMode.FLASHCARD) {
                                        navController.navigate("flashcard")
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎴", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text("Flashcards", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Repasa términos volteando tarjetas", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        // QUIZ
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showPracticeModeSelector = false
                                    // ✅ CORRECCIÓN: Callback directo
                                    viewModel.startPracticeSession(PracticeMode.QUIZ) {
                                        navController.navigate("quiz")
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎯", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text("Quiz", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Responde preguntas de opción múltiple", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showPracticeModeSelector = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

// ... (DailyTermCard y TermCard siguen igual) ...

@Composable
fun DailyTermCard(
    term: GlossaryTerm,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("💡", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Término del Día", style = MaterialTheme.typography.labelMedium, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                if (term.category != null) {
                    Badge { Text(term.category, style = MaterialTheme.typography.labelSmall) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = term.termEs,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = term.definitionEs,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB0BEC5),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "👆 Toca para ver más detalles",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64B5F6),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TermCard(
    term: GlossaryTerm,
    onClick: () -> Unit,
    onToggleLearned: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (term.isLearned)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (term.isLearned) Icons.Default.CheckCircle else Icons.Default.Circle,
                contentDescription = null,
                tint = if (term.isLearned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = term.termEs,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (term.category != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "📂 ${term.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onToggleLearned) {
                Icon(
                    imageVector = if (term.isLearned) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (term.isLearned) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

// ... (TermDetailFullScreen ya lo corregiste antes, mantenlo como está o úsalo de abajo) ...

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermDetailFullScreen(
    term: GlossaryTerm,
    onDismiss: () -> Unit,
    onToggleLearned: () -> Unit
) {
    var languageIsEnglish by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (languageIsEnglish) term.termEn else term.termEs) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.ArrowBack, "Volver") }
                    },
                    actions = {
                        FilterChip(
                            selected = languageIsEnglish,
                            onClick = { languageIsEnglish = true },
                            label = { Text("EN", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FilterChip(
                            selected = !languageIsEnglish,
                            onClick = { languageIsEnglish = false },
                            label = { Text("ES", fontWeight = FontWeight.Bold) }
                        )
                        IconButton(onClick = onToggleLearned) {
                            Icon(
                                imageVector = if (term.isLearned) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (term.isLearned) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = if (languageIsEnglish) term.termEn else term.termEs,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                // Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (term.category != null) {
                        AssistChip(
                            onClick = {},
                            label = { Text(term.category) },
                            leadingIcon = { Icon(Icons.Default.Category, null, Modifier.size(16.dp)) }
                        )
                    }
                    if (term.difficulty != null) {
                        // ... (Lógica de colores de dificultad se mantiene igual)
                        val difficultyColor = when (term.difficulty) {
                            "beginner" -> Color(0xFF4CAF50)
                            "intermediate" -> Color(0xFFFFA726)
                            "advanced" -> Color(0xFFF44336)
                            else -> MaterialTheme.colorScheme.primary
                        }
                        AssistChip(
                            onClick = {},
                            label = { Text(term.difficulty) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = difficultyColor.copy(alpha = 0.2f), labelColor = difficultyColor)
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = if (languageIsEnglish) "📖 Definition" else "📖 Definición",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = if (languageIsEnglish) term.definitionEn else term.definitionEs,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // FUENTE (CORREGIDO PARA USAR reference)
                val sourceText = term.reference ?: term.whereYouHearIt
                if (!sourceText.isNullOrBlank()) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = if (languageIsEnglish) "📚 Source" else "📚 Fuente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = sourceText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onToggleLearned,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (term.isLearned) "Aprendido" else "Marcar como aprendido")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ... (EmptyStateCard sigue igual)
@Composable
fun EmptyStateCard(mode: String, searchQuery: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = if (mode == "practice") "🎮" else "📚", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (mode == "practice") "Marca términos como aprendidos para practicar" else "No hay términos",
                style = MaterialTheme.typography.titleMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}