package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            containerColor = Color.Transparent, // ✅ Scaffold transparente
            floatingActionButton = {
                if (mode == "practice" && terms.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = { showPracticeModeSelector = true },
                        containerColor = Color(0xFF00D9FF),
                        modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎮", fontSize = 24.sp)
                            Text(
                                "Iniciar Práctica",
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // ✅ HEADER CON COLOR
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
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📚", fontSize = 36.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Glosario Ciberseguridad",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        if (stats != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✨ ${stats!!.learnedCount}/${stats!!.totalTerms} términos dominados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(0.8f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF00D9FF).copy(0.3f)
                                    ),
                                    border = BorderStroke(1.5.dp, Color(0xFF00D9FF)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "${stats!!.progressPercentage.toInt()}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF00D9FF),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { (stats!!.progressPercentage / 100).toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = Color(0xFF00D9FF),
                                trackColor = Color(0xFF2D3748),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ✅ SELECTOR DE MODO MEJORADO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        onClick = { viewModel.setMode("learn") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(if (mode == "learn") 10.dp else 4.dp, RoundedCornerShape(16.dp)),
                        color = if (mode == "learn") Color(0xFF00D9FF).copy(0.25f) else Color(0xFF1A2332),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, if (mode == "learn") Color(0xFF00D9FF) else Color(0xFF2D3748))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "📖",
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "APRENDER",
                                fontWeight = FontWeight.Black,
                                color = if (mode == "learn") Color(0xFF00D9FF) else Color.White.copy(0.6f),
                                fontSize = 16.sp
                            )
                            if (mode == "learn") {
                                Spacer(Modifier.width(8.dp))
                                Text("✓", fontSize = 20.sp, color = Color(0xFF00D9FF))
                            }
                        }
                    }

                    Surface(
                        onClick = { viewModel.setMode("practice") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(if (mode == "practice") 10.dp else 4.dp, RoundedCornerShape(16.dp)),
                        color = if (mode == "practice") Color(0xFFFBBF24).copy(0.25f) else Color(0xFF1A2332),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, if (mode == "practice") Color(0xFFFBBF24) else Color(0xFF2D3748))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "🎮",
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "PRACTICAR",
                                fontWeight = FontWeight.Black,
                                color = if (mode == "practice") Color(0xFFFBBF24) else Color.White.copy(0.6f),
                                fontSize = 16.sp
                            )
                            if (mode == "practice") {
                                Spacer(Modifier.width(8.dp))
                                Text("✓", fontSize = 20.sp, color = Color(0xFFFBBF24))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                if (mode == "learn") {
                    if (dailyTerm != null) {
                        DailyTermCard(
                            term = dailyTerm,
                            onClick = { selectedTerm = dailyTerm }
                        )
                        Spacer(Modifier.height(20.dp))
                    }

                    // ✅ BUSCADOR MEJORADO
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.loadTerms(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "🔍 Buscar término...",
                                color = Color.White.copy(0.5f),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                null,
                                tint = Color(0xFF00D9FF),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    viewModel.loadTerms("")
                                }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        "Limpiar",
                                        tint = Color(0xFFEF4444)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00D9FF),
                            unfocusedBorderColor = Color(0xFF2D3748),
                            focusedContainerColor = Color(0xFF1A2332),
                            unfocusedContainerColor = Color(0xFF1A2332),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(20.dp))
                }

                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = Color(0xFF00D9FF),
                        trackColor = Color(0xFF2D3748)
                    )
                    Spacer(Modifier.height(12.dp))
                }

                if (terms.isEmpty() && !isLoading) {
                    EmptyStateCard(mode = mode, searchQuery = searchQuery)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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

            // ✅ DIÁLOGO SELECCIÓN PRÁCTICA MEJORADO
            if (showPracticeModeSelector) {
                AlertDialog(
                    onDismissRequest = { showPracticeModeSelector = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎮", fontSize = 32.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Elige modo de práctica",
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // FLASHCARDS
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .clickable {
                                        showPracticeModeSelector = false
                                        viewModel.startPracticeSession(PracticeMode.FLASHCARD) {
                                            navController.navigate("flashcard")
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF6366F1).copy(0.2f)
                                ),
                                border = BorderStroke(2.dp, Color(0xFF6366F1)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF6366F1)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            "🎴",
                                            fontSize = 32.sp,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            "Flashcards",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            fontSize = 20.sp
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Repasa términos volteando tarjetas",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(0.8f),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            // QUIZ
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .clickable {
                                        showPracticeModeSelector = false
                                        viewModel.startPracticeSession(PracticeMode.QUIZ) {
                                            navController.navigate("quiz")
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFBBF24).copy(0.2f)
                                ),
                                border = BorderStroke(2.dp, Color(0xFFFBBF24)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFFBBF24)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            "🎯",
                                            fontSize = 32.sp,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            "Quiz",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            fontSize = 20.sp
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Responde preguntas de opción múltiple",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(0.8f),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showPracticeModeSelector = false }) {
                            Text(
                                "Cancelar",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                fontSize = 16.sp
                            )
                        }
                    },
                    containerColor = Color(0xFF1A2332),
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }
    }
}

// ✅ DAILY TERM CARD MEJORADA
@Composable
fun DailyTermCard(
    term: GlossaryTerm,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFBBF24).copy(0.2f)
        ),
        border = BorderStroke(3.dp, Color(0xFFFBBF24)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFBBF24).copy(0.2f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💡", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Término del Día",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (term.category != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF00D9FF).copy(0.3f)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF00D9FF)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                term.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = term.termEs,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = term.definitionEs,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.85f),
                    maxLines = 2,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👆", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Toca para ver más detalles",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ✅ TERM CARD MEJORADA
@Composable
fun TermCard(
    term: GlossaryTerm,
    onClick: () -> Unit,
    onToggleLearned: () -> Unit
) {
    val cardColor = if (term.isLearned) Color(0xFF10B981) else Color(0xFF00D9FF)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (term.isLearned)
                Color(0xFF10B981).copy(alpha = 0.15f)
            else
                Color(0xFF1A2332)
        ),
        border = BorderStroke(2.dp, cardColor.copy(0.5f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (term.isLearned) Color(0xFF10B981).copy(0.3f) else Color(0xFF2D3748)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (term.isLearned) Icons.Default.CheckCircle else Icons.Default.Circle,
                    contentDescription = null,
                    tint = if (term.isLearned) Color(0xFF10B981) else Color.White.copy(0.5f),
                    modifier = Modifier
                        .size(28.dp)
                        .padding(6.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = term.termEs,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp
                )
                if (term.category != null) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "📂",
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = term.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF00D9FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            IconButton(
                onClick = onToggleLearned,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (term.isLearned) Color(0xFFFBBF24).copy(0.2f) else Color(0xFF2D3748),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    if (term.isLearned) "⭐" else "☆",
                    fontSize = 24.sp
                )
            }
        }
    }
}

// ✅ TERM DETAIL MEJORADO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermDetailFullScreen(
    term: GlossaryTerm,
    onDismiss: () -> Unit,
    onToggleLearned: () -> Unit
) {
    var languageIsEnglish by remember { mutableStateOf(true) }

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
                        Text(
                            if (languageIsEnglish) term.termEn else term.termEs,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF00D9FF).copy(0.2f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                "Volver",
                                tint = Color(0xFF00D9FF)
                            )
                        }
                    },
                    actions = {
                        Surface(
                            onClick = { languageIsEnglish = true },
                            modifier = Modifier
                                .height(40.dp)
                                .shadow(if (languageIsEnglish) 6.dp else 0.dp, RoundedCornerShape(10.dp)),
                            color = if (languageIsEnglish) Color(0xFF00D9FF).copy(0.3f) else Color(0xFF2D3748),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, if (languageIsEnglish) Color(0xFF00D9FF) else Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "EN",
                                    fontWeight = FontWeight.Black,
                                    color = if (languageIsEnglish) Color(0xFF00D9FF) else Color.White.copy(0.5f),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        Surface(
                            onClick = { languageIsEnglish = false },
                            modifier = Modifier
                                .height(40.dp)
                                .shadow(if (!languageIsEnglish) 6.dp else 0.dp, RoundedCornerShape(10.dp)),
                            color = if (!languageIsEnglish) Color(0xFF00D9FF).copy(0.3f) else Color(0xFF2D3748),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, if (!languageIsEnglish) Color(0xFF00D9FF) else Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "ES",
                                    fontWeight = FontWeight.Black,
                                    color = if (!languageIsEnglish) Color(0xFF00D9FF) else Color.White.copy(0.5f),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        IconButton(
                            onClick = onToggleLearned,
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (term.isLearned) Color(0xFFFBBF24).copy(0.2f) else Color(0xFF2D3748),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                if (term.isLearned) "⭐" else "☆",
                                fontSize = 24.sp
                            )
                        }

                        Spacer(Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A2332),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
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
                // TÍTULO
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00D9FF).copy(0.2f)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF00D9FF)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (languageIsEnglish) term.termEn else term.termEs,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(24.dp),
                        fontSize = 28.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                // METADATA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (term.category != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF6366F1).copy(0.2f)
                            ),
                            border = BorderStroke(1.5.dp, Color(0xFF6366F1)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("📂", fontSize = 18.sp)
                                Text(
                                    term.category,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    if (term.difficulty != null) {
                        val (difficultyEmoji, difficultyColor) = when (term.difficulty) {
                            "beginner" -> "🌱" to Color(0xFF10B981)
                            "intermediate" -> "⚡" to Color(0xFFF59E0B)
                            "advanced" -> "🔥" to Color(0xFFEF4444)
                            else -> "📚" to Color(0xFF6366F1)
                        }
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = difficultyColor.copy(0.2f)
                            ),
                            border = BorderStroke(1.5.dp, difficultyColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(difficultyEmoji, fontSize = 18.sp)
                                Text(
                                    term.difficulty,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // DEFINICIÓN
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📖", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = if (languageIsEnglish) "Definition" else "Definición",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00D9FF),
                        fontSize = 22.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A2332)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF2D3748)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (languageIsEnglish) term.definitionEn else term.definitionEs,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(0.9f),
                        fontSize = 17.sp,
                        lineHeight = 26.sp
                    )
                }

                // FUENTE
                val sourceText = term.reference ?: term.whereYouHearIt
                if (!sourceText.isNullOrBlank()) {
                    Spacer(Modifier.height(28.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📚", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = if (languageIsEnglish) "Source" else "Fuente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFBBF24),
                            fontSize = 20.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = sourceText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(0.7f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }

                Spacer(Modifier.height(36.dp))

                Button(
                    onClick = onToggleLearned,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (term.isLearned) Color(0xFF10B981) else Color(0xFF00D9FF)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        if (term.isLearned) "⭐ Aprendido" else "Marcar como aprendido",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ✅ EMPTY STATE MEJORADO
@Composable
fun EmptyStateCard(mode: String, searchQuery: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        ),
        border = BorderStroke(2.dp, Color(0xFF2D3748))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (mode == "practice") "🎮" else "📚",
                fontSize = 72.sp
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = if (mode == "practice")
                    "Marca términos como aprendidos para practicar"
                else
                    "No hay términos",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (mode == "practice")
                    "Aprende primero para desbloquear prácticas"
                else
                    "Intenta con otra búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.White.copy(0.7f),
                fontSize = 16.sp
            )
        }
    }
}