@file:OptIn(ExperimentalLayoutApi::class)
package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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
            CircularProgressIndicator(color = PrimaryCyan)
            Spacer(Modifier.height(16.dp))
            Text("⏳ Cargando contenido...", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMain)
            .padding(20.dp)
    ) {
        // ✅ TÍTULO CON COLOR Y EMOJI
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryCyan.copy(0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "📚",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = currentScreen.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = PrimaryCyan,
                fontSize = 26.sp
            )
        }

        // Contenido dinámico
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
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
                is UnknownScreen -> Text("⚠️ Tipo no soportado", color = WarningOrange, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ✅ NAVEGACIÓN MEJORADA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onPrev,
                enabled = screenIndex > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryCyan,
                    disabledContentColor = Color.White.copy(0.3f)
                ),
                border = BorderStroke(2.dp, if (screenIndex > 0) PrimaryCyan else Color.White.copy(0.2f))
            ) {
                Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Anterior", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(start = 8.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text(
                    text = if (isLastScreen) "✅ Finalizar" else "Siguiente",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    if (isLastScreen) Icons.Default.CheckCircle else Icons.Default.ArrowForward,
                    null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
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
    } catch (e: Exception) {
        default
    }
}

// =========================================================================
// STORY INTRO - MÁS COLORIDO Y CON EMOJIS
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
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D0A0A) // Rojo oscuro más intenso
                ),
                border = BorderStroke(4.dp, ErrorRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ErrorRed.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "🚨",
                            fontSize = 64.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            content.headline,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = ErrorRed,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            lineHeight = 36.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            content.subheadline,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (content.stats.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    content.stats.forEach { stat ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(12.dp, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryCyan.copy(0.25f)
                            ),
                            border = BorderStroke(3.dp, PrimaryCyan),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    stat.value,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryCyan,
                                    fontSize = 36.sp
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    stat.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// STORY SLIDE - CON CAJAS DE COLOR Y EMOJIS
// =========================================================================

@Composable
fun RenderStorySlide(content: StorySlideContent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ✅ HEADER CON ÍCONO Y COLOR DE FONDO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = parseColorHex(content.iconColor, PrimaryCyan).copy(0.25f)
            ),
            border = BorderStroke(3.dp, parseColorHex(content.iconColor, PrimaryCyan)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(20.dp)
            ) {
                Icon(
                    getIconByName(content.icon),
                    null,
                    tint = parseColorHex(content.iconColor, PrimaryCyan),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    content.headline,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 24.sp,
                    lineHeight = 30.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ✅ TEXTO EN CAJA CON COLOR DE FONDO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A2332) // Azul oscuro
            ),
            border = BorderStroke(2.dp, PrimaryCyan.copy(0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                content.body,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(24.dp)
            )
        }

        content.highlightBox?.let { box ->
            Spacer(Modifier.height(24.dp))
            val boxColor = when (box.type) {
                "danger" -> ErrorRed
                "warning" -> WarningOrange
                "success" -> SuccessGreen
                else -> AccentGold
            }

            val emoji = when (box.type) {
                "danger" -> "⚠️"
                "warning" -> "⚡"
                "success" -> "✅"
                else -> "💡"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = boxColor.copy(alpha = 0.2f)
                ),
                border = BorderStroke(3.dp, boxColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        emoji,
                        fontSize = 32.sp
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        box.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
fun RenderCrisisTimeline(content: CrisisTimelineContent) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        items(content.events) { event ->
            val severityColor = when (event.severity) {
                "critical" -> ErrorRed
                "warning" -> WarningOrange
                else -> PrimaryCyan
            }

            val severityEmoji = when (event.severity) {
                "critical" -> "🔴"
                "warning" -> "🟠"
                else -> "🔵"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = severityColor.copy(alpha = 0.2f)
                ),
                border = BorderStroke(3.dp, severityColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp)) {
                    // ✅ TIMESTAMP CON EMOJI
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(80.dp)
                    ) {
                        Text(
                            severityEmoji,
                            fontSize = 28.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = severityColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                event.time,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Icon(
                            getIconByName(event.icon),
                            null,
                            tint = severityColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(Modifier.width(20.dp))

                    Column {
                        Text(
                            event.title,
                            fontWeight = FontWeight.Black,
                            color = severityColor,
                            fontSize = 19.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// CRISIS DECISION - MÁS VIDA Y COLOR
// =========================================================================

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
        // ✅ TIMER MÁS DRAMÁTICO CON EMOJI
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (timeLeft <= 10) ErrorRed.copy(0.3f) else WarningOrange.copy(0.3f)
            ),
            border = BorderStroke(
                4.dp,
                if (timeLeft <= 10) ErrorRed else WarningOrange
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (timeLeft <= 10) "🚨" else "⏱️",
                        fontSize = 36.sp
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "DECISIÓN CRÍTICA",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
                Text(
                    "${timeLeft}s",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = if (timeLeft <= 10) ErrorRed else WarningOrange,
                    fontSize = 42.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ✅ ESCENARIO EN CAJA CON COLOR
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A2332)
            ),
            border = BorderStroke(2.dp, PrimaryCyan.copy(0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                content.scenario,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(content.options) { option ->
                val isSelected = selectedOption == option.id
                val showResult = selectedOption != null
                val color = if (showResult && isSelected) {
                    if (option.isCorrect) SuccessGreen else ErrorRed
                } else PrimaryCyan

                Card(
                    onClick = { if (!showResult) selectedOption = option.id },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isSelected) 12.dp else 6.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) color.copy(0.25f) else Color(0xFF1A2332)
                    ),
                    border = BorderStroke(
                        if (isSelected) 4.dp else 2.dp,
                        if (isSelected) color else PrimaryCyan.copy(0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = color.copy(0.3f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    getIconByName(option.icon),
                                    null,
                                    tint = color,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .padding(8.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Text(
                                option.text,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                lineHeight = 24.sp
                            )
                        }

                        if (isSelected && showResult) {
                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(color = color.copy(0.5f), thickness = 2.dp)
                            Spacer(Modifier.height(16.dp))

                            Text(
                                if (option.isCorrect) "✅ " + option.consequence.title else "❌ " + option.consequence.title,
                                fontWeight = FontWeight.Black,
                                color = color,
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                option.consequence.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// FLIP CARD - MÁS COLORIDO
// =========================================================================

@Composable
fun RenderFlipCardFull(content: FlipCardContent) {
    var flipped by remember { mutableStateOf(false) }

    val cardColor = if (!flipped) {
        parseColorHex(content.card.front.color, PrimaryCyan)
    } else {
        parseColorHex(content.card.back.color, PrimaryPurple)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .clickable { flipped = !flipped }
            .shadow(20.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor.copy(0.3f)),
        border = BorderStroke(4.dp, cardColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(0.15f),
                            Color.Transparent
                        )
                    )
                )
                .padding(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!flipped) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(0.2f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            getIconByName(content.card.front.icon),
                            null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(20.dp)
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                    Text(
                        content.card.front.term,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )
                    content.card.front.subtitle?.let {
                        Spacer(Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                it,
                                color = Color.White,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(36.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "👆",
                            fontSize = 24.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Toca para voltear",
                            color = Color.White.copy(0.8f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "📖 DEFINICIÓN",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 16.sp,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        content.card.back.definition ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 19.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// =========================================================================
// SWIPE CARDS - MÁS COLORIDO
// =========================================================================

@Composable
fun RenderSwipeCards(content: SwipeCardsContent) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var answers by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    if (currentIndex >= content.cards.size) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(0.25f)
                ),
                border = BorderStroke(4.dp, SuccessGreen),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🎉",
                        fontSize = 80.sp
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "¡COMPLETADO!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = AccentGold.copy(0.3f)
                        ),
                        border = BorderStroke(2.dp, AccentGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "✨ ${answers.values.count { it }} de ${content.cards.size} correctas",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
        return
    }

    val card = content.cards[currentIndex]

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // ✅ INSTRUCCIÓN EN CAJA CON COLOR
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = AccentGold.copy(0.2f)
            ),
            border = BorderStroke(2.dp, AccentGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "💭 " + content.instruction,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(Modifier.height(36.dp))

        // ✅ CARD DE ESCENARIO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .shadow(16.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryCyan.copy(0.25f)
            ),
            border = BorderStroke(4.dp, PrimaryCyan),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(0.1f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    card.scenario,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 19.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(44.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(
                onClick = {
                    answers = answers + (card.id to (card.correctAnswer == "left"))
                    currentIndex++
                },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "❌",
                        fontSize = 32.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        content.labels["left"] ?: "Malo",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = {
                    answers = answers + (card.id to (card.correctAnswer == "right"))
                    currentIndex++
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "✅",
                        fontSize = 32.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        content.labels["right"] ?: "Bueno",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = PrimaryCyan.copy(0.3f)
            ),
            border = BorderStroke(2.dp, PrimaryCyan),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                "📊 ${currentIndex + 1} / ${content.cards.size}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
        }
    }
}

// =========================================================================
// DRAG DROP MATCH - YA NO QUEDA BIEN, AHORA SÍ
// =========================================================================

@Composable
fun RenderDragDropMatch(content: DragDropMatchContent) {
    var matches by remember { mutableStateOf(mapOf<Int, String>()) }
    var selectedScenario by remember { mutableStateOf<Int?>(null) }
    var showResults by remember { mutableStateOf(false) }

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = AccentGold.copy(0.2f)
            ),
            border = BorderStroke(2.dp, AccentGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "🎯 " + content.instruction,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content.targets.forEach { target ->
                Button(
                    onClick = {
                        selectedScenario?.let { scenarioId ->
                            matches = matches + (scenarioId to target.id)
                            selectedScenario = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = parseColorHex(target.color, PrimaryCyan)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .shadow(if (selectedScenario != null) 16.dp else 8.dp, RoundedCornerShape(16.dp)),
                    enabled = selectedScenario != null,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        target.icon?.let {
                            Icon(
                                getIconByName(it),
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                        Text(
                            target.label,
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(content.scenarios) { scenario ->
                val isSelected = selectedScenario == scenario.id
                val matchedTarget = matches[scenario.id]
                val isCorrect = matchedTarget == scenario.correctTarget

                Card(
                    onClick = { if (!showResults) selectedScenario = scenario.id },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            if (isSelected) 12.dp else 6.dp,
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            showResults && matchedTarget != null -> if (isCorrect) SuccessGreen.copy(0.25f) else ErrorRed.copy(0.25f)
                            isSelected -> PrimaryCyan.copy(0.25f)
                            else -> Color(0xFF1A2332)
                        }
                    ),
                    border = BorderStroke(
                        3.dp,
                        when {
                            showResults && matchedTarget != null -> if (isCorrect) SuccessGreen else ErrorRed
                            isSelected -> PrimaryCyan
                            else -> PrimaryCyan.copy(0.3f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            scenario.text,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            fontSize = 17.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (matchedTarget != null) {
                            val target = content.targets.find { it.id == matchedTarget }
                            target?.let {
                                Spacer(Modifier.width(16.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = parseColorHex(it.color, PrimaryCyan)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        it.label,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!showResults && matches.size == content.scenarios.size) {
                item {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { showResults = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "🔍",
                            fontSize = 24.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "VERIFICAR RESPUESTAS",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// DRAG DROP BUILDER
// =========================================================================

@Composable
fun RenderDragDropBuilder(content: DragDropBuilderContent) {
    var selectedComponents by remember { mutableStateOf(listOf<String>()) }

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = AccentGold.copy(0.2f)
            ),
            border = BorderStroke(2.dp, AccentGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "🔧 " + content.instruction,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "📦 COMPONENTES DISPONIBLES:",
            color = PrimaryCyan,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content.availableComponents.forEach { component ->
                Button(
                    onClick = { selectedComponents = selectedComponents + component.id },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryCyan.copy(0.3f)
                    ),
                    border = BorderStroke(2.dp, PrimaryCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        null,
                        modifier = Modifier.size(20.dp),
                        tint = PrimaryCyan
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        component.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2332)),
            border = BorderStroke(4.dp, PrimaryCyan),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (selectedComponents.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "🏗️",
                            fontSize = 64.sp
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "ÁREA DE CONSTRUCCIÓN",
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Selecciona componentes arriba",
                            color = Color.White.copy(0.7f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(selectedComponents) { componentId ->
                            val component = content.availableComponents.find { it.id == componentId }
                            component?.let {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = PrimaryCyan.copy(0.2f)
                                    ),
                                    border = BorderStroke(2.dp, PrimaryCyan),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.DragHandle,
                                            null,
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(Modifier.width(20.dp))
                                        Text(
                                            it.name,
                                            color = Color.White,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedComponents.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { selectedComponents = emptyList() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🗑️", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("LIMPIAR", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("✅", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("VALIDAR", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

// =========================================================================
// LOG HUNTER - MEJORADO CON MÁS CONTRASTE
// =========================================================================

@Composable
fun RenderLogHunter(content: LogHunterContent) {
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }
    var showResults by remember { mutableStateOf(false) }
    val logs = content.allLogs

    Column {
        content.instruction?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = AccentGold.copy(0.2f)
                ),
                border = BorderStroke(2.dp, AccentGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "🔍 " + it,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        content.scenario?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A2332)
                ),
                border = BorderStroke(2.dp, PrimaryCyan.copy(0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    it,
                    color = Color.White,
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(20.dp)
                )
            }
            Spacer(Modifier.height(28.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(logs) { log ->
                val isSelected = selectedIds.contains(log.id)
                val isSuspicious = log.isSuspiciousCombined

                val borderColor = when {
                    showResults && isSuspicious -> SuccessGreen
                    showResults && isSelected && !isSuspicious -> ErrorRed
                    isSelected -> Color(0xFF00FF41)
                    else -> Color(0xFF2D3748)
                }

                Card(
                    onClick = {
                        if (!showResults) {
                            selectedIds = if (isSelected) selectedIds - log.id else selectedIds + log.id
                        }
                    },
                    border = BorderStroke(3.dp, borderColor),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            showResults && isSuspicious -> SuccessGreen.copy(0.2f)
                            showResults && isSelected && !isSuspicious -> ErrorRed.copy(0.2f)
                            isSelected -> Color(0xFF00FF41).copy(0.15f)
                            else -> Color.Black
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            log.text,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold, // ✅ BOLD PARA CONTRASTE
                            color = if (isSelected) Color(0xFF00FF41) else Color(0xFFCCCCCC), // ✅ MÁS CLARO
                            lineHeight = 22.sp
                        )

                        if (showResults && isSuspicious) {
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = SuccessGreen.copy(0.5f), thickness = 2.dp)
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "⚠️",
                                    fontSize = 24.sp
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "AMENAZA DETECTADA",
                                    color = SuccessGreen,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            log.reason?.let { reason ->
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    reason,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                if (!showResults) {
                    Button(
                        onClick = { showResults = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "🔍",
                            fontSize = 24.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "ANALIZAR LOGS",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                    }
                } else {
                    val correctCount = logs.count { it.isSuspiciousCombined && selectedIds.contains(it.id) }
                    val totalSuspicious = logs.count { it.isSuspiciousCombined }

                    Card(
                        modifier = Modifier.shadow(16.dp, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (correctCount == totalSuspicious) SuccessGreen.copy(0.25f) else WarningOrange.copy(0.25f)
                        ),
                        border = BorderStroke(
                            4.dp,
                            if (correctCount == totalSuspicious) SuccessGreen else WarningOrange
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                if (correctCount == totalSuspicious) "🎯" else "📊",
                                fontSize = 48.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Resultado: $correctCount / $totalSuspicious amenazas detectadas",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center
                            )
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A2332)
            ),
            border = BorderStroke(2.dp, PrimaryCyan.copy(0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "⚖️ " + content.scenario,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(Modifier.height(36.dp))

        // ✅ OPCIONES CON MÁS COLOR Y EMOJIS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(10.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = ErrorRed.copy(0.2f)
                ),
                border = BorderStroke(3.dp, ErrorRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            getIconByName(content.leftOption.icon),
                            null,
                            tint = ErrorRed,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            content.leftOption.label,
                            color = ErrorRed,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    content.leftOption.consequences.forEach { consequence ->
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("⚠️", fontSize = 18.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                consequence,
                                fontSize = 15.sp,
                                color = Color.White,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(20.dp))

            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(10.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(0.2f)
                ),
                border = BorderStroke(3.dp, SuccessGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            content.rightOption.label,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            getIconByName(content.rightOption.icon),
                            null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    content.rightOption.consequences.forEach { consequence ->
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                consequence,
                                fontSize = 15.sp,
                                color = Color.White,
                                textAlign = TextAlign.End,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("✅", fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        // ✅ SLIDER MÁS VISIBLE
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..100f,
            modifier = Modifier.height(56.dp),
            colors = SliderDefaults.colors(
                thumbColor = AccentGold,
                activeTrackColor = AccentGold,
                inactiveTrackColor = Color(0xFF2D3748)
            )
        )

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = AccentGold.copy(0.25f)
            ),
            border = BorderStroke(3.dp, AccentGold),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "⚖️ Balance: ${sliderValue.toInt()}%",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { showExplanation = !showExplanation },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "💡",
                fontSize = 24.sp
            )
            Spacer(Modifier.width(12.dp))
            Text(
                if (showExplanation) "Ocultar Análisis" else "Ver Análisis Óptimo",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = Color.Black
            )
        }

        if (showExplanation) {
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryCyan.copy(0.2f)
                ),
                border = BorderStroke(3.dp, PrimaryCyan),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp)) {
                    Text(
                        "📊 ANÁLISIS ÓPTIMO",
                        fontWeight = FontWeight.Black,
                        color = PrimaryCyan,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        content.explanation,
                        color = Color.White,
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// =========================================================================
// TIMED QUIZ - MÁS DINÁMICO
// =========================================================================

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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (timeLeft <= 10) ErrorRed.copy(0.3f) else AccentGold.copy(0.3f)
            ),
            border = BorderStroke(
                4.dp,
                if (timeLeft <= 10) ErrorRed else AccentGold
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (timeLeft <= 10) "⏰" else "⏱️",
                        fontSize = 36.sp
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "TIEMPO",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
                Text(
                    "${timeLeft}s",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = if (timeLeft <= 10) ErrorRed else AccentGold,
                    fontSize = 42.sp
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryCyan.copy(0.2f)
            ),
            border = BorderStroke(3.dp, PrimaryCyan),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "❓ " + content.question,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(content.options) { option ->
                val isSelected = selectedOption == option.id
                val showResult = selectedOption != null
                val color = if (showResult && isSelected) {
                    if (option.isCorrect) SuccessGreen else ErrorRed
                } else AccentGold

                Card(
                    onClick = { if (!showResult) selectedOption = option.id },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            if (isSelected) 12.dp else 6.dp,
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected && showResult) color.copy(0.25f) else Color(0xFF1A2332)
                    ),
                    border = BorderStroke(
                        if (isSelected) 4.dp else 2.dp,
                        if (isSelected) color else PrimaryCyan.copy(0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showResult && isSelected) {
                            Text(
                                if (option.isCorrect) "✅" else "❌",
                                fontSize = 28.sp
                            )
                            Spacer(Modifier.width(16.dp))
                        }
                        Text(
                            option.text,
                            color = Color.White,
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            if (selectedOption != null && !showExplanation) {
                item {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { showExplanation = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("💡", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "VER EXPLICACIÓN",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            if (showExplanation) {
                item {
                    Card(
                        modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryCyan.copy(0.2f)
                        ),
                        border = BorderStroke(3.dp, PrimaryCyan),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(28.dp)) {
                            Text(
                                "📚 EXPLICACIÓN",
                                fontWeight = FontWeight.Black,
                                color = PrimaryCyan,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                content.explanation,
                                color = Color.White,
                                fontSize = 17.sp,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// BUDGET ALLOCATION - MÁS VISUAL
// =========================================================================

@Composable
fun RenderBudgetAllocation(content: BudgetAllocationContent) {
    var allocations by remember {
        mutableStateOf(content.categories.associate { it.id to false }.toMutableMap())
    }
    val spent = content.categories.filter { allocations[it.id] == true }.sumOf { it.cost }
    val remaining = content.totalBudget - spent
    var showResults by remember { mutableStateOf(false) }

    Column {
        // ✅ HEADER DE PRESUPUESTO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    remaining < 0 -> ErrorRed.copy(0.3f)
                    remaining == 0 -> SuccessGreen.copy(0.3f)
                    else -> AccentGold.copy(0.3f)
                }
            ),
            border = BorderStroke(
                4.dp,
                when {
                    remaining < 0 -> ErrorRed
                    remaining == 0 -> SuccessGreen
                    else -> AccentGold
                }
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "💰 PRESUPUESTO",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$${content.totalBudget / 1000}K",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "💵 RESTANTE",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$${remaining / 1000}K",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = when {
                            remaining < 0 -> ErrorRed
                            remaining == 0 -> SuccessGreen
                            else -> AccentGold
                        },
                        fontSize = 40.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            items(content.categories) { category ->
                val isSelected = allocations[category.id] == true
                val isOptimal = content.optimalAllocation.find { it.id == category.id }?.selected == true

                Card(
                    onClick = {
                        if (!showResults) {
                            allocations = allocations.apply {
                                this[category.id] = !(this[category.id] ?: false)
                            }.toMutableMap()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            if (isSelected) 12.dp else 6.dp,
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            showResults && isSelected && isOptimal -> SuccessGreen.copy(0.25f)
                            showResults && isSelected && !isOptimal -> ErrorRed.copy(0.25f)
                            showResults && !isSelected && isOptimal -> WarningOrange.copy(0.25f)
                            isSelected -> AccentGold.copy(0.25f)
                            else -> Color(0xFF1A2332)
                        }
                    ),
                    border = BorderStroke(
                        3.dp,
                        when {
                            showResults && isSelected && isOptimal -> SuccessGreen
                            showResults && isSelected && !isOptimal -> ErrorRed
                            showResults && !isSelected && isOptimal -> WarningOrange
                            isSelected -> AccentGold
                            else -> PrimaryCyan.copy(0.3f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ✅ CHECKBOX CON EMOJI
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (isSelected) AccentGold else Color(0xFF2D3748),
                                    RoundedCornerShape(12.dp)
                                )
                                .shadow(if (isSelected) 8.dp else 0.dp, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isSelected) "✅" else "",
                                fontSize = 24.sp
                            )
                        }

                        Spacer(Modifier.width(20.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    category.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )

                                if (category.mandatory) {
                                    Spacer(Modifier.width(12.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = ErrorRed
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            "⚠️ OBLIGATORIO",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                "📊 Impacto: ${category.impact}",
                                fontSize = 16.sp,
                                color = Color.White.copy(0.9f),
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium
                            )

                            if (showResults && isOptimal && !isSelected) {
                                Spacer(Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = WarningOrange.copy(0.3f)
                                    ),
                                    border = BorderStroke(1.dp, WarningOrange),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "💡 Se recomienda incluir",
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(20.dp))

                        // ✅ PRECIO DESTACADO
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AccentGold.copy(0.3f) else Color(0xFF2D3748)
                            ),
                            border = BorderStroke(2.dp, if (isSelected) AccentGold else Color.Transparent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "$${category.cost / 1000}K",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) AccentGold else Color.White.copy(0.7f),
                                fontSize = 26.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                if (!showResults) {
                    Button(
                        onClick = { showResults = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        enabled = remaining >= 0,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "📊",
                            fontSize = 24.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "EVALUAR ASIGNACIÓN",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// CODE PRACTICE - MÁS COLORIDO
// =========================================================================

@Composable
fun RenderCodePractice(content: CodePracticeContent) {
    var revealedSnippets by remember { mutableStateOf(setOf<Int>()) }

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = AccentGold.copy(0.2f)
            ),
            border = BorderStroke(2.dp, AccentGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "💻 " + content.instruction,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            items(content.codeSnippets) { snippet ->
                val isRevealed = revealedSnippets.contains(snippet.id)

                Card(
                    onClick = {
                        revealedSnippets = if (isRevealed) {
                            revealedSnippets - snippet.id
                        } else {
                            revealedSnippets + snippet.id
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            if (isRevealed) 16.dp else 8.dp,
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isRevealed) {
                            if (snippet.isVulnerable) ErrorRed.copy(0.25f) else SuccessGreen.copy(0.25f)
                        } else Color(0xFF1A2332)
                    ),
                    border = BorderStroke(
                        3.dp,
                        if (isRevealed) {
                            if (snippet.isVulnerable) ErrorRed else SuccessGreen
                        } else PrimaryCyan.copy(0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = AccentGold
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    "⌨️ " + snippet.language.uppercase(),
                                    color = Color.Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    letterSpacing = 1.sp
                                )
                            }

                            if (!isRevealed) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "👆",
                                        fontSize = 22.sp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Toca para analizar",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(0.7f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // ✅ CÓDIGO CON FONDO NEGRO Y TEXTO EN BOLD
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                snippet.code,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold, // ✅ BOLD
                                color = Color(0xFF00FF41),
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(20.dp)
                            )
                        }

                        if (isRevealed) {
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider(
                                color = if (snippet.isVulnerable) ErrorRed.copy(0.5f) else SuccessGreen.copy(0.5f),
                                thickness = 2.dp
                            )
                            Spacer(Modifier.height(20.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    if (snippet.isVulnerable) "🚨" else "🛡️",
                                    fontSize = 32.sp
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    if (snippet.isVulnerable) "CÓDIGO VULNERABLE" else "CÓDIGO SEGURO",
                                    fontWeight = FontWeight.Black,
                                    color = if (snippet.isVulnerable) ErrorRed else SuccessGreen,
                                    fontSize = 20.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            snippet.explanation?.let { explanation ->
                                Spacer(Modifier.height(20.dp))
                                Text(
                                    explanation,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    lineHeight = 26.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// EVIDENCE LAB - MÁS COLORIDO
// =========================================================================

@Composable
fun RenderEvidenceLab(content: EvidenceLabContent) {
    var selectedItems by remember { mutableStateOf(listOf<String>()) }
    var showResults by remember { mutableStateOf(false) }

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = AccentGold.copy(0.2f)
            ),
            border = BorderStroke(2.dp, AccentGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "🔬 " + content.instruction,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 26.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "📋 Selecciona las evidencias en el orden correcto",
                    color = Color.White.copy(0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ✅ DROP ZONES MÁS VISUALES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content.dropZones.sortedBy { it.order ?: 0 }.forEach { zone ->
                val itemInZone = selectedItems.getOrNull((zone.order ?: 1) - 1)

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .shadow(if (itemInZone != null) 12.dp else 6.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (itemInZone != null) AccentGold.copy(0.25f) else Color(0xFF1A2332)
                    ),
                    border = BorderStroke(
                        4.dp,
                        if (itemInZone != null) AccentGold else PrimaryCyan.copy(0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (itemInZone != null) AccentGold else Color(0xFF2D3748)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    zone.label ?: "📍 Paso ${zone.order}",
                                    fontSize = 14.sp,
                                    color = if (itemInZone != null) Color.Black else Color.White.copy(0.7f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            if (itemInZone != null) {
                                val item = content.evidenceItems.find { it.id == itemInZone }
                                item?.let {
                                    Icon(
                                        getIconByName(it.icon),
                                        null,
                                        tint = AccentGold,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        it.name,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 18.sp
                                    )
                                }
                            } else {
                                Text(
                                    "➕",
                                    fontSize = 36.sp,
                                    color = Color.White.copy(0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        Text(
            "🔍 EVIDENCIAS DISPONIBLES:",
            color = PrimaryCyan,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(content.evidenceItems.filter { !selectedItems.contains(it.id) }) { item ->
                Card(
                    onClick = {
                        if (!showResults && selectedItems.size < content.dropZones.size) {
                            selectedItems = selectedItems + item.id
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2332)),
                    border = BorderStroke(2.dp, PrimaryCyan.copy(0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryCyan.copy(0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                getIconByName(item.icon),
                                null,
                                tint = PrimaryCyan,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                            )
                        }
                        Spacer(Modifier.width(20.dp))
                        Text(
                            item.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }
            }

            if (selectedItems.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { selectedItems = emptyList() },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("🗑️", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("LIMPIAR", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                        }

                        if (selectedItems.size == content.dropZones.size && !showResults) {
                            Button(
                                onClick = { showResults = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                                    .shadow(12.dp, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("✅", fontSize = 24.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("VERIFICAR", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(20.dp, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) SuccessGreen.copy(0.25f) else WarningOrange.copy(0.25f)
                        ),
                        border = BorderStroke(
                            4.dp,
                            if (isCorrect) SuccessGreen else WarningOrange
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                if (isCorrect) "🎯" else "🔄",
                                fontSize = 48.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                if (isCorrect) "¡ORDEN CORRECTO!" else "REVISA EL ORDEN",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 22.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// ACHIEVEMENT UNLOCK - MÁS ÉPICO
// =========================================================================

@Composable
fun RenderAchievementUnlock(content: AchievementUnlockContent) {
    val achievement = content.achievement
    val rarityColor = when (achievement.rarity.lowercase()) {
        "legendary" -> AccentGold
        "epic" -> PrimaryPurple
        "rare" -> PrimaryCyan
        else -> SuccessGreen
    }

    val rarityEmoji = when (achievement.rarity.lowercase()) {
        "legendary" -> "👑"
        "epic" -> "💜"
        "rare" -> "💎"
        else -> "⭐"
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(24.dp, RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(
                containerColor = rarityColor.copy(0.25f)
            ),
            border = BorderStroke(5.dp, rarityColor),
            shape = RoundedCornerShape(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                rarityColor.copy(0.4f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = rarityColor.copy(0.3f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            getIconByName(achievement.icon),
                            null,
                            tint = rarityColor,
                            modifier = Modifier
                                .size(110.dp)
                                .padding(24.dp)
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = rarityColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "🏆 LOGRO DESBLOQUEADO",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (rarityColor == AccentGold) Color.Black else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        achievement.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 36.sp,
                        lineHeight = 42.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        achievement.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(0.9f),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(36.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = rarityColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            rarityEmoji + " " + achievement.rarity.uppercase(),
                            color = if (rarityColor == AccentGold) Color.Black else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 14.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// LESSON COMPLETE - MÁS CELEBRATORIO
// =========================================================================

@Composable
fun RenderLessonComplete(content: LessonCompleteContent) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(0.25f)
                ),
                border = BorderStroke(5.dp, SuccessGreen),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SuccessGreen.copy(0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(44.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "🎉",
                            fontSize = 80.sp
                        )

                        Spacer(Modifier.height(28.dp))

                        Text(
                            "¡LECCIÓN COMPLETADA!",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = SuccessGreen,
                            fontSize = 32.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = AccentGold
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 36.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "✨",
                                    fontSize = 32.sp
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "+${content.xpEarned} XP",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    fontSize = 40.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        if (content.keyTakeaways.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2332)),
                    border = BorderStroke(3.dp, PrimaryCyan),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(32.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "💡",
                                fontSize = 36.sp
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "PUNTOS CLAVE",
                                fontWeight = FontWeight.Black,
                                color = PrimaryCyan,
                                fontSize = 22.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        content.keyTakeaways.forEach { takeaway ->
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    "✅",
                                    fontSize = 24.sp
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    takeaway,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    lineHeight = 26.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        content.nextLesson?.let { next ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = AccentGold.copy(0.2f)),
                    border = BorderStroke(4.dp, AccentGold),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(32.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "🚀",
                                fontSize = 32.sp
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "SIGUIENTE LECCIÓN",
                                style = MaterialTheme.typography.labelLarge,
                                color = AccentGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            next.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 24.sp,
                            lineHeight = 30.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            next.preview,
                            color = Color.White.copy(0.9f),
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// SUMMARY STATS - MÁS COLORIDO
// =========================================================================

@Composable
fun RenderSummaryStats(content: SummaryStatsContent) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        items(content.stats) { stat ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = parseColorHex(stat.color, PrimaryCyan).copy(0.25f)
                ),
                border = BorderStroke(3.dp, parseColorHex(stat.color, PrimaryCyan)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = parseColorHex(stat.color, PrimaryCyan).copy(0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            getIconByName(stat.icon),
                            null,
                            tint = parseColorHex(stat.color, PrimaryCyan),
                            modifier = Modifier
                                .size(56.dp)
                                .padding(12.dp)
                        )
                    }

                    Spacer(Modifier.width(24.dp))

                    Column {
                        Text(
                            stat.value,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = parseColorHex(stat.color, PrimaryCyan),
                            fontSize = 40.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stat.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        content.keyInsight?.let { insight ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = AccentGold.copy(0.25f)
                    ),
                    border = BorderStroke(4.dp, AccentGold),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(32.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "💡",
                                fontSize = 36.sp
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "INSIGHT CLAVE",
                                fontWeight = FontWeight.Black,
                                color = AccentGold,
                                fontSize = 20.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            insight,
                            color = Color.White,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        content.lessons?.let { lessons ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2332)),
                    border = BorderStroke(3.dp, PrimaryCyan),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(32.dp)) {
                        Text(
                            "📚 LECCIONES APRENDIDAS",
                            fontWeight = FontWeight.Black,
                            color = PrimaryCyan,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(Modifier.height(20.dp))

                        lessons.forEach { lesson ->
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    "✅",
                                    fontSize = 22.sp
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    lesson,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    lineHeight = 26.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}