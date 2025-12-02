@file:OptIn(ExperimentalLayoutApi::class)
package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.*
import com.example.cyberlearnapp.utils.getIconByName
import kotlinx.coroutines.delay
import com.example.cyberlearnapp.ui.theme.*

// =========================================================================
// DISPATCHER PRINCIPAL
// =========================================================================

@Composable
fun LessonScreenRender(
    lesson: LessonDetailResponse,
    screenIndex: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    isLastScreen: Boolean
) {
    val currentScreen = lesson.screens.getOrNull(screenIndex)

    if (currentScreen == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando...", color = TextPrimary)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Título
        Text(
            text = currentScreen.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Contenido dinámico
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (currentScreen) {
                is StoryIntro -> RenderStoryIntro(currentScreen.content)
                is StorySlide -> RenderStorySlide(currentScreen.content)
                is CrisisTimeline -> RenderCrisisTimeline(currentScreen.content)
                is CrisisDecision -> RenderCrisisDecision(currentScreen.content, onNext)
                is FlipCard -> RenderFlipCardFull(currentScreen.content)
                is SwipeCards -> RenderSwipeCards(currentScreen.content)
                is DragDropMatch -> RenderDragDropMatch(currentScreen.content)
                is DragDropBuilder -> RenderDragDropBuilder(currentScreen.content)
                is LogHunter -> RenderLogHunter(currentScreen.content)
                is SliderBalance -> RenderSliderBalance(currentScreen.content)
                is TimedQuiz -> RenderTimedQuiz(currentScreen.content)
                is BudgetAllocation -> RenderBudgetAllocation(currentScreen.content)
                is CodePractice -> RenderCodePractice(currentScreen.content)
                is EvidenceLab -> RenderEvidenceLab(currentScreen.content)
                is AchievementUnlock -> RenderAchievementUnlock(currentScreen.content)
                is LessonComplete -> RenderLessonComplete(currentScreen.content)
                is SummaryStats -> RenderSummaryStats(currentScreen.content)
                is UnknownScreen -> Text("Tipo no soportado", color = WarningOrange)
            }
        }

        // Navegación
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onPrev,
                enabled = screenIndex > 0,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated)
            ) {
                Text("Anterior", color = TextSecondary)
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text(if (isLastScreen) "Finalizar" else "Siguiente", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =========================================================================
// UTILS
// =========================================================================

fun parseColorHex(hex: String?, default: Color): Color {
    return try {
        if (hex.isNullOrBlank()) return default
        val cleanHex = hex.removePrefix("#")
        if (cleanHex.length != 6 && cleanHex.length != 8) return default
        val colorValue = cleanHex.toLong(16)
        val finalValue = if (cleanHex.length == 6) 0xFF000000 or colorValue else colorValue
        Color(finalValue)
    } catch (e: Exception) { default }
}

// =========================================================================
// RENDERERS PARTE 1: STORY + CRISIS + FLIP
// =========================================================================

@Composable
fun RenderStoryIntro(content: StoryIntroContent) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                border = BorderStroke(2.dp, ErrorRed)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, null, tint = ErrorRed, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(content.headline, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(content.subheadline, style = MaterialTheme.typography.bodyLarge, color = TextSecondary, textAlign = TextAlign.Center)
                }
            }
        }

        if (content.stats.isNotEmpty()) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    content.stats.forEach { stat ->
                        Card(
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryCyan.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stat.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                Spacer(Modifier.height(4.dp))
                                Text(stat.label, style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderStorySlide(content: StorySlideContent) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(getIconByName(content.icon), null, tint = parseColorHex(content.iconColor, PrimaryCyan), modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Text(content.headline, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(Modifier.height(16.dp))
        Text(content.body, style = MaterialTheme.typography.bodyLarge, color = TextSecondary, lineHeight = 24.sp)

        content.highlightBox?.let { box ->
            Spacer(Modifier.height(24.dp))
            val boxColor = when (box.type) {
                "danger" -> ErrorRed
                "warning" -> WarningOrange
                "success" -> SuccessGreen
                else -> PrimaryCyan
            }

            Card(colors = CardDefaults.cardColors(containerColor = boxColor.copy(alpha = 0.15f)), border = BorderStroke(2.dp, boxColor)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Lightbulb, null, tint = boxColor, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(box.text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, lineHeight = 22.sp)
                }
            }
        }
    }
}

@Composable
fun RenderCrisisTimeline(content: CrisisTimelineContent) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(content.events) { event ->
            val severityColor = when (event.severity) {
                "critical" -> ErrorRed
                "warning" -> WarningOrange
                else -> PrimaryCyan
            }

            Card(colors = CardDefaults.cardColors(containerColor = SurfaceCard), border = BorderStroke(1.dp, severityColor.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
                        Text(event.time, style = MaterialTheme.typography.labelSmall, color = severityColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(4.dp))
                        Icon(getIconByName(event.icon), null, tint = severityColor)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(event.title, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(event.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun RenderCrisisDecision(content: CrisisDecisionContent, onNext: () -> Unit) {
    var timeLeft by remember { mutableIntStateOf(content.timerSeconds) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0 && selectedOption == null) {
            delay(1000)
            timeLeft--
        }
    }

    Column {
        Card(colors = CardDefaults.cardColors(containerColor = if (timeLeft <= 10) ErrorRed.copy(0.2f) else WarningOrange.copy(0.2f))) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("DECISIÓN CRÍTICA", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("${timeLeft}s", fontWeight = FontWeight.Black, color = if (timeLeft <= 10) ErrorRed else WarningOrange)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(content.scenario, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(content.options) { option ->
                val isSelected = selectedOption == option.id
                val showResult = selectedOption != null
                val color = if (showResult && isSelected) (if (option.isCorrect) SuccessGreen else ErrorRed) else PrimaryCyan

                Card(
                    onClick = { if (!showResult) selectedOption = option.id },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) color.copy(0.2f) else SurfaceCard),
                    border = BorderStroke(1.dp, if (isSelected) color else SurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(getIconByName(option.icon), null, tint = color)
                            Spacer(Modifier.width(12.dp))
                            Text(option.text, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        if (isSelected && showResult) {
                            Spacer(Modifier.height(8.dp))
                            Text(option.consequence.title, fontWeight = FontWeight.Bold, color = color)
                            Text(option.consequence.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderFlipCardFull(content: FlipCardContent) {
    var flipped by remember { mutableStateOf(false) }

    // ✅ CORRECCIÓN: Acceso explícito al color
    val cardColor = if (!flipped) {
        parseColorHex(content.card.front.color, PrimaryCyan)
    } else {
        parseColorHex(content.card.back.color, PrimaryCyan)
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(350.dp).clickable { flipped = !flipped },
        colors = CardDefaults.cardColors(containerColor = cardColor)  // ✅ Usar cardColor
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!flipped) {
                    // FRENTE
                    Icon(getIconByName(content.card.front.icon), null, tint = Color.White, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(content.card.front.term, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White)
                    content.card.front.subtitle?.let { Text(it, color = Color.White.copy(0.8f)) }
                    Spacer(Modifier.height(16.dp))
                    Text("Toca para voltear", color = Color.White.copy(0.6f), fontSize = 12.sp)
                } else {
                    // REVERSO
                    Text("DEFINICIÓN", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White.copy(0.6f))
                    Spacer(Modifier.height(8.dp))
                    Text(content.card.back.definition ?: "", style = MaterialTheme.typography.bodyLarge, color = Color.White, textAlign = TextAlign.Center)
                }
            }
        }
    }
}


@Composable
fun RenderSwipeCards(content: SwipeCardsContent) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var answers by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    if (currentIndex >= content.cards.size) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(0.15f)),
            border = BorderStroke(2.dp, SuccessGreen)
        ) {
            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("¡Completado!", style = MaterialTheme.typography.headlineMedium, color = SuccessGreen, fontWeight = FontWeight.Bold)
                Text("${answers.values.count { it }} de ${content.cards.size} correctas", color = TextSecondary)
            }
        }
        return
    }

    val card = content.cards[currentIndex]

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(content.instruction, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(2.dp, PrimaryCyan)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(card.scenario, modifier = Modifier.padding(24.dp), textAlign = TextAlign.Center, color = TextPrimary, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    answers = answers + (card.id to (card.correctAnswer == "left"))
                    currentIndex++
                },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Close, null)
                Spacer(Modifier.width(8.dp))
                Text(content.labels["left"] ?: "Malo")
            }
            Button(
                onClick = {
                    answers = answers + (card.id to (card.correctAnswer == "right"))
                    currentIndex++
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text(content.labels["right"] ?: "Bueno")
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("${currentIndex + 1} / ${content.cards.size}", color = TextTertiary, fontSize = 14.sp)
    }
}

@Composable
fun RenderDragDropMatch(content: DragDropMatchContent) {
    var matches by remember { mutableStateOf(mapOf<Int, String>()) }
    var selectedScenario by remember { mutableStateOf<Int?>(null) }
    var showResults by remember { mutableStateOf(false) }

    Column {
        Text(content.instruction, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            content.targets.forEach { target ->
                Button(
                    onClick = {
                        selectedScenario?.let { scenarioId ->
                            matches = matches + (scenarioId to target.id)
                            selectedScenario = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = parseColorHex(target.color, PrimaryCyan)),
                    modifier = Modifier.weight(1f),
                    enabled = selectedScenario != null
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        target.icon?.let { Icon(getIconByName(it), null, tint = Color.White) }
                        Text(target.label, color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(content.scenarios) { scenario ->
                val isSelected = selectedScenario == scenario.id
                val matchedTarget = matches[scenario.id]
                val isCorrect = matchedTarget == scenario.correctTarget

                Card(
                    onClick = { if (!showResults) selectedScenario = scenario.id },
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            showResults && matchedTarget != null -> if (isCorrect) SuccessGreen.copy(0.2f) else ErrorRed.copy(0.2f)
                            isSelected -> PrimaryCyan.copy(0.2f)
                            else -> SurfaceCard
                        }
                    ),
                    border = BorderStroke(
                        2.dp,
                        when {
                            showResults && matchedTarget != null -> if (isCorrect) SuccessGreen else ErrorRed
                            isSelected -> PrimaryCyan
                            else -> SurfaceElevated
                        }
                    )
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(scenario.text, color = TextPrimary, modifier = Modifier.weight(1f))
                        if (matchedTarget != null) {
                            val target = content.targets.find { it.id == matchedTarget }
                            target?.let {
                                Spacer(Modifier.width(8.dp))
                                Card(colors = CardDefaults.cardColors(containerColor = parseColorHex(it.color, PrimaryCyan))) {
                                    Text(it.label, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }

            if (!showResults && matches.size == content.scenarios.size) {
                item {
                    Button(
                        onClick = { showResults = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("VERIFICAR RESPUESTAS", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RenderDragDropBuilder(content: DragDropBuilderContent) {
    var selectedComponents by remember { mutableStateOf(listOf<String>()) }

    Column {
        Text(content.instruction, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Text("COMPONENTES DISPONIBLES:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content.availableComponents.forEach { component ->
                SuggestionChip(
                    onClick = { selectedComponents = selectedComponents + component.id },
                    label = { Text(component.name, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(2.dp, PrimaryCyan)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (selectedComponents.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Build, null, tint = TextTertiary, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("ÁREA DE CONSTRUCCIÓN", color = TextTertiary)
                        Text("Selecciona componentes arriba", color = TextTertiary, fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedComponents) { componentId ->
                            val component = content.availableComponents.find { it.id == componentId }
                            component?.let {
                                Card(colors = CardDefaults.cardColors(containerColor = SurfaceElevated)) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DragHandle, null, tint = PrimaryCyan)
                                        Spacer(Modifier.width(12.dp))
                                        Text(it.name, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedComponents.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { selectedComponents = emptyList() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("LIMPIAR")
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("VALIDAR")
                }
            }
        }
    }
}

@Composable
fun RenderLogHunter(content: LogHunterContent) {
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }
    var showResults by remember { mutableStateOf(false) }
    val logs = content.allLogs

    Column {
        content.instruction?.let {
            Text(it, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }
        content.scenario?.let {
            Text(it, color = TextSecondary, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(logs) { log ->
                val isSelected = selectedIds.contains(log.id)
                val isSuspicious = log.isSuspiciousCombined

                val borderColor = when {
                    showResults && isSuspicious -> SuccessGreen
                    showResults && isSelected && !isSuspicious -> ErrorRed
                    isSelected -> PrimaryCyan
                    else -> SurfaceElevated
                }

                Card(
                    onClick = {
                        if (!showResults) {
                            selectedIds = if (isSelected) selectedIds - log.id else selectedIds + log.id
                        }
                    },
                    border = BorderStroke(1.dp, borderColor),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            showResults && isSuspicious -> SuccessGreen.copy(0.1f)
                            showResults && isSelected && !isSuspicious -> ErrorRed.copy(0.1f)
                            isSelected -> PrimaryCyan.copy(0.1f)
                            else -> SurfaceCard
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(log.text, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
                        if (showResults && isSuspicious) {
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("AMENAZA DETECTADA", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            log.reason?.let { reason ->
                                Text(reason, fontSize = 11.sp, color = TextPrimary, fontStyle = FontStyle.Italic)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                if (!showResults) {
                    Button(
                        onClick = { showResults = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Icon(Icons.Default.Search, null)
                        Spacer(Modifier.width(8.dp))
                        Text("ANALIZAR LOGS", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    val correctCount = logs.count { it.isSuspiciousCombined && selectedIds.contains(it.id) }
                    val totalSuspicious = logs.count { it.isSuspiciousCombined }

                    Card(colors = CardDefaults.cardColors(containerColor = if (correctCount == totalSuspicious) SuccessGreen.copy(0.2f) else WarningOrange.copy(0.2f))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Resultado: $correctCount / $totalSuspicious amenazas detectadas", fontWeight = FontWeight.Bold, color = if (correctCount == totalSuspicious) SuccessGreen else WarningOrange)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderSliderBalance(content: SliderBalanceContent) {
    var sliderValue by remember { mutableFloatStateOf(50f) }
    var showExplanation by remember { mutableStateOf(false) }

    Column {
        Text(content.scenario, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(getIconByName(content.leftOption.icon), null, tint = ErrorRed)
                    Spacer(Modifier.width(8.dp))
                    Text(content.leftOption.label, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                content.leftOption.consequences.forEach { consequence ->
                    Text("• $consequence", fontSize = 12.sp, color = TextSecondary)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(content.rightOption.label, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(getIconByName(content.rightOption.icon), null, tint = SuccessGreen)
                }
                content.rightOption.consequences.forEach { consequence ->
                    Text("• $consequence", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.End)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = PrimaryCyan, activeTrackColor = PrimaryCyan, inactiveTrackColor = SurfaceElevated)
        )

        Text("Balance: ${sliderValue.toInt()}%", color = PrimaryCyan, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showExplanation = !showExplanation },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
        ) {
            Icon(Icons.Default.Lightbulb, null)
            Spacer(Modifier.width(8.dp))
            Text(if (showExplanation) "Ocultar Análisis" else "Ver Análisis Óptimo")
        }

        if (showExplanation) {
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = AccentGold.copy(0.15f)), border = BorderStroke(1.dp, AccentGold)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ANÁLISIS", fontWeight = FontWeight.Bold, color = AccentGold)
                    Spacer(Modifier.height(8.dp))
                    Text(content.explanation, color = TextPrimary, lineHeight = 22.sp)
                }
            }
        }
    }
}

@Composable
fun RenderTimedQuiz(content: TimedQuizContent) {
    var timeLeft by remember { mutableIntStateOf(content.timerSeconds) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showExplanation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0 && selectedOption == null) {
            delay(1000)
            timeLeft--
        }
    }

    Column {
        Card(colors = CardDefaults.cardColors(containerColor = if (timeLeft <= 10) ErrorRed.copy(0.2f) else PrimaryCyan.copy(0.2f))) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, tint = if (timeLeft <= 10) ErrorRed else PrimaryCyan)
                    Spacer(Modifier.width(8.dp))
                    Text("TIEMPO", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text("${timeLeft}s", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = if (timeLeft <= 10) ErrorRed else PrimaryCyan)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(content.question, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(content.options) { option ->
                val isSelected = selectedOption == option.id
                val showResult = selectedOption != null
                val color = if (showResult && isSelected) (if (option.isCorrect) SuccessGreen else ErrorRed) else PrimaryCyan

                Card(
                    onClick = { if (!showResult) selectedOption = option.id },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected && showResult) color.copy(0.2f) else SurfaceCard),
                    border = BorderStroke(2.dp, if (isSelected) color else SurfaceElevated)
                ) {
                    Text(option.text, modifier = Modifier.padding(16.dp), color = TextPrimary, lineHeight = 22.sp)
                }
            }

            if (selectedOption != null && !showExplanation) {
                item {
                    Button(
                        onClick = { showExplanation = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
                    ) {
                        Text("VER EXPLICACIÓN")
                    }
                }
            }

            if (showExplanation) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = SurfaceElevated)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("EXPLICACIÓN", fontWeight = FontWeight.Bold, color = AccentGold)
                            Spacer(Modifier.height(8.dp))
                            Text(content.explanation, color = TextPrimary, lineHeight = 22.sp)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun RenderBudgetAllocation(content: BudgetAllocationContent) {
    var allocations by remember { mutableStateOf(content.categories.associate { it.id to false }.toMutableMap()) }
    val spent = content.categories.filter { allocations[it.id] == true }.sumOf { it.cost }
    val remaining = content.totalBudget - spent
    var showResults by remember { mutableStateOf(false) }

    Column {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = when {
                    remaining < 0 -> ErrorRed.copy(0.2f)
                    remaining == 0 -> SuccessGreen.copy(0.2f)
                    else -> PrimaryCyan.copy(0.2f)
                }
            ),
            border = BorderStroke(2.dp, when {
                remaining < 0 -> ErrorRed
                remaining == 0 -> SuccessGreen
                else -> PrimaryCyan
            })
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("PRESUPUESTO", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 12.sp)
                    Text("Total: $${content.totalBudget / 1000}K", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("RESTANTE", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 12.sp)
                    Text("$${remaining / 1000}K", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = when {
                        remaining < 0 -> ErrorRed
                        remaining == 0 -> SuccessGreen
                        else -> PrimaryCyan
                    })
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(content.categories) { category ->
                val isSelected = allocations[category.id] == true
                val isOptimal = content.optimalAllocation.find { it.id == category.id }?.selected == true

                Card(
                    onClick = {
                        if (!showResults) {
                            allocations = allocations.apply { this[category.id] = !(this[category.id] ?: false) }.toMutableMap()
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            showResults && isSelected && isOptimal -> SuccessGreen.copy(0.15f)
                            showResults && isSelected && !isOptimal -> ErrorRed.copy(0.15f)
                            showResults && !isSelected && isOptimal -> WarningOrange.copy(0.15f)
                            isSelected -> PrimaryCyan.copy(0.15f)
                            else -> SurfaceCard
                        }
                    ),
                    border = BorderStroke(2.dp, when {
                        showResults && isSelected && isOptimal -> SuccessGreen
                        showResults && isSelected && !isOptimal -> ErrorRed
                        showResults && !isSelected && isOptimal -> WarningOrange
                        isSelected -> PrimaryCyan
                        else -> SurfaceElevated
                    })
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).background(if (isSelected) PrimaryCyan else SurfaceElevated, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(category.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                if (category.mandatory) {
                                    Spacer(Modifier.width(8.dp))
                                    Card(colors = CardDefaults.cardColors(containerColor = ErrorRed)) {
                                        Text("OBLIGATORIO", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Impacto: ${category.impact}", fontSize = 13.sp, color = TextSecondary)
                            if (showResults && isOptimal && !isSelected) {
                                Spacer(Modifier.height(4.dp))
                                Text("⚠️ Se recomienda incluir", fontSize = 12.sp, color = WarningOrange, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Text("$${category.cost / 1000}K", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isSelected) PrimaryCyan else TextSecondary)
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                if (!showResults) {
                    Button(
                        onClick = { showResults = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        enabled = remaining >= 0
                    ) {
                        Icon(Icons.Default.Assessment, null)
                        Spacer(Modifier.width(8.dp))
                        Text("EVALUAR ASIGNACIÓN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RenderCodePractice(content: CodePracticeContent) {
    var revealedSnippets by remember { mutableStateOf(setOf<Int>()) }

    Column {
        Text(content.instruction, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(content.codeSnippets) { snippet ->
                val isRevealed = revealedSnippets.contains(snippet.id)

                Card(
                    onClick = {
                        revealedSnippets = if (isRevealed) revealedSnippets - snippet.id else revealedSnippets + snippet.id
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isRevealed) {
                            if (snippet.isVulnerable) ErrorRed.copy(0.15f) else SuccessGreen.copy(0.15f)
                        } else SurfaceCard
                    ),
                    border = BorderStroke(2.dp, if (isRevealed) {
                        if (snippet.isVulnerable) ErrorRed else SuccessGreen
                    } else SurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Card(colors = CardDefaults.cardColors(containerColor = PrimaryCyan.copy(0.3f))) {
                                Text(snippet.language.uppercase(), color = PrimaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            if (!isRevealed) {
                                Text("Toca para analizar", fontSize = 12.sp, color = TextTertiary, fontStyle = FontStyle.Italic)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Card(colors = CardDefaults.cardColors(containerColor = BackgroundMain)) {
                            Text(snippet.code, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 12.sp, color = TextPrimary, lineHeight = 18.sp, modifier = Modifier.padding(12.dp))
                        }

                        if (isRevealed) {
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = SurfaceElevated)
                            Spacer(Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (snippet.isVulnerable) Icons.Default.Dangerous else Icons.Default.VerifiedUser, null, tint = if (snippet.isVulnerable) ErrorRed else SuccessGreen, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(if (snippet.isVulnerable) "CÓDIGO VULNERABLE" else "CÓDIGO SEGURO", fontWeight = FontWeight.Bold, color = if (snippet.isVulnerable) ErrorRed else SuccessGreen, fontSize = 16.sp)
                            }

                            snippet.explanation?.let { explanation ->
                                Spacer(Modifier.height(12.dp))
                                Text(explanation, color = TextPrimary, lineHeight = 22.sp, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderEvidenceLab(content: EvidenceLabContent) {
    var selectedItems by remember { mutableStateOf(listOf<String>()) }
    var showResults by remember { mutableStateOf(false) }

    Column {
        Text(content.instruction, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Selecciona las evidencias en el orden correcto", color = TextSecondary, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            content.dropZones.sortedBy { it.order ?: 0 }.forEach { zone ->
                val itemInZone = selectedItems.getOrNull((zone.order ?: 1) - 1)
                Card(
                    modifier = Modifier.weight(1f).height(100.dp),
                    colors = CardDefaults.cardColors(containerColor = if (itemInZone != null) PrimaryCyan.copy(0.15f) else SurfaceCard),
                    border = BorderStroke(2.dp, if (itemInZone != null) PrimaryCyan else SurfaceElevated)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(zone.label ?: "Paso ${zone.order}", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
                            if (itemInZone != null) {
                                Spacer(Modifier.height(4.dp))
                                val item = content.evidenceItems.find { it.id == itemInZone }
                                item?.let {
                                    Icon(getIconByName(it.icon), null, tint = PrimaryCyan)
                                    Text(it.name, fontSize = 11.sp, color = TextPrimary, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Text("EVIDENCIAS DISPONIBLES:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(content.evidenceItems.filter { !selectedItems.contains(it.id) }) { item ->
                Card(
                    onClick = {
                        if (!showResults && selectedItems.size < content.dropZones.size) {
                            selectedItems = selectedItems + item.id
                        }
                    },
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(getIconByName(item.icon), null, tint = AccentGold, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(16.dp))
                        Text(item.name, color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            if (selectedItems.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { selectedItems = emptyList() },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("LIMPIAR")
                        }
                        if (selectedItems.size == content.dropZones.size && !showResults) {
                            Button(
                                onClick = { showResults = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("VERIFICAR")
                            }
                        }
                    }
                }
            }

            if (showResults) {
                item {
                    val isCorrect = selectedItems.withIndex().all { (index, itemId) ->
                        content.evidenceItems.find { it.id == itemId }?.correctOrder == index + 1
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isCorrect) SuccessGreen.copy(0.2f) else WarningOrange.copy(0.2f)),
                        border = BorderStroke(2.dp, if (isCorrect) SuccessGreen else WarningOrange)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Info, null, tint = if (isCorrect) SuccessGreen else WarningOrange, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(if (isCorrect) "¡ORDEN CORRECTO!" else "REVISA EL ORDEN", fontWeight = FontWeight.Bold, color = if (isCorrect) SuccessGreen else WarningOrange, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderAchievementUnlock(content: AchievementUnlockContent) {
    val achievement = content.achievement
    val rarityColor = when (achievement.rarity.lowercase()) {
        "legendary" -> AccentGold
        "epic" -> PrimaryPurple
        "rare" -> PrimaryCyan
        else -> SuccessGreen
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.fillMaxWidth(0.9f), colors = CardDefaults.cardColors(containerColor = rarityColor.copy(0.15f)), border = BorderStroke(3.dp, rarityColor)) {
            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(getIconByName(achievement.icon), null, tint = rarityColor, modifier = Modifier.size(80.dp))
                Spacer(Modifier.height(24.dp))
                Text("LOGRO DESBLOQUEADO", style = MaterialTheme.typography.labelLarge, color = rarityColor, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(achievement.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(achievement.description, style = MaterialTheme.typography.bodyLarge, color = TextSecondary, textAlign = TextAlign.Center, lineHeight = 24.sp)
                Spacer(Modifier.height(24.dp))
                Card(colors = CardDefaults.cardColors(containerColor = rarityColor)) {
                    Text(achievement.rarity.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                }
            }
        }
    }
}

@Composable
fun RenderLessonComplete(content: LessonCompleteContent) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(0.15f)), border = BorderStroke(3.dp, SuccessGreen)) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.EmojiEvents, null, tint = SuccessGreen, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("¡LECCIÓN COMPLETADA!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    Spacer(Modifier.height(8.dp))
                    Text("+${content.xpEarned} XP", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = AccentGold)
                }
            }
        }

        if (content.keyTakeaways.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, null, tint = AccentGold)
                            Spacer(Modifier.width(12.dp))
                            Text("PUNTOS CLAVE", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        content.keyTakeaways.forEach { takeaway ->
                            Row(modifier = Modifier.padding(vertical = 6.dp)) {
                                Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(takeaway, color = TextPrimary, lineHeight = 22.sp)
                            }
                        }
                    }
                }
            }
        }

        content.nextLesson?.let { next ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceCard), border = BorderStroke(2.dp, PrimaryCyan)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("SIGUIENTE LECCIÓN", style = MaterialTheme.typography.labelMedium, color = PrimaryCyan, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(next.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(Modifier.height(8.dp))
                        Text(next.preview, color = TextSecondary, lineHeight = 22.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RenderSummaryStats(content: SummaryStatsContent) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(content.stats) { stat ->
            Card(colors = CardDefaults.cardColors(containerColor = parseColorHex(stat.color, PrimaryCyan).copy(0.15f)), border = BorderStroke(1.dp, parseColorHex(stat.color, PrimaryCyan))) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(getIconByName(stat.icon), null, tint = parseColorHex(stat.color, PrimaryCyan), modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(stat.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = parseColorHex(stat.color, PrimaryCyan))
                        Text(stat.label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }
        }

        content.keyInsight?.let { insight ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = AccentGold.copy(0.15f)), border = BorderStroke(2.dp, AccentGold)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, null, tint = AccentGold, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("INSIGHT CLAVE", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(insight, color = TextPrimary, lineHeight = 24.sp, fontSize = 15.sp)
                    }
                }
            }
        }

        content.lessons?.let { lessons ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("LECCIONES APRENDIDAS", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        lessons.forEach { lesson ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text("•", color = PrimaryCyan, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Text(lesson, color = TextSecondary, lineHeight = 22.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}































