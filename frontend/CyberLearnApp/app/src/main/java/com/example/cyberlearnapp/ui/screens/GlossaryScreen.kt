package com.example.cyberlearnapp.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.network.models.GlossaryTerm
import com.example.cyberlearnapp.viewmodel.GlossaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(
    viewModel: GlossaryViewModel = hiltViewModel()
) {
    val terms by viewModel.terms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val stats by viewModel.stats.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTerm by remember { mutableStateOf<GlossaryTerm?>(null) }
    var showCategoryFilter by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ✅ HEADER CON ESTADÍSTICAS
        Text(
            text = "📚 Glosario Ciberseguridad",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        // Progreso
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

        // ✅ SELECTOR DE MODO
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

        // ✅ BUSCADOR
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

        // ✅ LOADING
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }

        // ✅ LISTA DE TÉRMINOS
        if (terms.isEmpty() && !isLoading) {
            EmptyStateCard(
                mode = mode,
                searchQuery = searchQuery
            )
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

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    // ✅ DETALLE DEL TÉRMINO (Bottom Sheet)
    if (selectedTerm != null) {
        TermDetailSheet(
            term = selectedTerm!!,
            onDismiss = { selectedTerm = null },
            onToggleLearned = {
                viewModel.toggleLearned(selectedTerm!!.id, selectedTerm!!.isLearned)
                selectedTerm = null
            }
        )
    }
}

@Composable
fun TermCard(
    term: GlossaryTerm,
    onClick: () -> Unit,
    onToggleLearned: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            // Indicador de estado
            Icon(
                imageVector = if (term.isLearned) Icons.Default.CheckCircle else Icons.Default.Circle,
                contentDescription = null,
                tint = if (term.isLearned)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = term.term,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (term.acronym != null) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = term.acronym,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = term.definition,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (term.category != null) {
                    Spacer(Modifier.height(8.dp))
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
                    contentDescription = if (term.isLearned) "Desmarcar" else "Marcar como aprendido",
                    tint = if (term.isLearned) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermDetailSheet(
    term: GlossaryTerm,
    onDismiss: () -> Unit,
    onToggleLearned: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = term.term,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    if (term.termEn != term.term) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = term.termEn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onToggleLearned) {
                    Icon(
                        imageVector = if (term.isLearned) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (term.isLearned) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            if (term.acronym != null) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Acrónimo: ${term.acronym}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Definición
            Text(
                text = "Definición",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = term.definition,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )

            // Ejemplo
            if (term.example != null) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Ejemplo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "💡 ${term.example}",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Metadata
            Spacer(Modifier.height(16.dp))

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
                    val difficultyColor = when (term.difficulty) {
                        "beginner" -> Color(0xFF4CAF50)
                        "intermediate" -> Color(0xFFFFA726)
                        "advanced" -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.primary
                    }

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                when (term.difficulty) {
                                    "beginner" -> "Principiante"
                                    "intermediate" -> "Intermedio"
                                    "advanced" -> "Avanzado"
                                    else -> term.difficulty
                                }
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = difficultyColor.copy(alpha = 0.2f),
                            labelColor = difficultyColor
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón de acción
            Button(
                onClick = onToggleLearned,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (term.isLearned)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (term.isLearned) Icons.Default.Check else Icons.Default.Star,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (term.isLearned) "✓ Aprendido" else "Marcar como aprendido",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun EmptyStateCard(mode: String, searchQuery: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "🔍"
                    mode == "practice" -> "🎮"
                    else -> "📚"
                },
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "No se encontraron resultados para \"$searchQuery\""
                    mode == "practice" -> "Aún no has marcado términos como aprendidos"
                    else -> "No hay términos disponibles"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (mode == "practice") {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Primero aprende algunos términos en modo APRENDER",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}