package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.LessonResponse
import com.example.cyberlearnapp.utils.getIconByName
import kotlinx.serialization.json.*

import com.example.cyberlearnapp.ui.theme.*

// =========================================================================
// FUNCIÓN PRINCIPAL DE RENDERIZADO DE PANTALLAS DE LECCIÓN
// =========================================================================

@Composable
fun LessonScreenRender(
    lesson: LessonResponse,
    screenIndex: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    isLastScreen: Boolean
) {
    val currentScreen = lesson.screens.getOrNull(screenIndex)

    if (currentScreen == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Cargando contenido o fin de lección...",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    val type = currentScreen.type
    val contentData = currentScreen.content
    val title = currentScreen.title

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la pantalla
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Contenido principal (con scroll)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (type) {
                "step_revelation" -> RenderStepRevelation(contentData)
                "story_hook" -> RenderStoryHook(contentData)
                "theory_tabs" -> RenderTheoryTabs(contentData)
                "theory_section" -> RenderTheorySection(contentData)
                "interactive_concept" -> RenderInteractiveConcept(contentData)
                "quiz" -> RenderQuiz(contentData)
                "memory_cards" -> RenderMemoryCards(contentData)
                "accordion_list" -> RenderAccordionList(contentData)
                "progress_checklist" -> RenderProgressChecklist(contentData)
                else -> {
                    // Mensaje informativo para tipos no implementados
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = WarningOrange.copy(alpha = 0.2f)
                        ),
                        border = BorderStroke(2.dp, WarningOrange)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Tipo de contenido no soportado: $type",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Este tipo de pantalla aún no ha sido implementado.",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Botones de navegación
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onPrev,
                enabled = screenIndex > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Anterior", color = MaterialTheme.colorScheme.onSecondary)
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    if (isLastScreen) "Finalizar" else "Siguiente",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// =========================================================================
// FUNCIÓN DE UTILIDAD: PARSING SEGURO DE COLORES HEXADECIMALES
// =========================================================================

fun parseColorHex(hex: String?, default: Color): Color {
    return try {
        if (hex.isNullOrBlank()) return default
        val cleanHex = hex.removePrefix("#")
        if (cleanHex.length != 6 && cleanHex.length != 8) return default

        val colorValue = cleanHex.toLong(16)
        val finalValue = if (cleanHex.length == 6) {
            0xFF000000 or colorValue
        } else {
            colorValue
        }
        Color(finalValue)
    } catch (e: Exception) {
        default
    }
}

// =========================================================================
// RENDERER: STEP REVELATION
// Para pantallas tipo "Caso Real" con pasos revelables
// =========================================================================

@Composable
fun RenderStepRevelation(content: JsonObject) {
    val storyTitle = content["story_title"]?.jsonPrimitive?.content
    val storyIntro = content["story_intro"]?.jsonPrimitive?.content
    val steps = content["steps"]?.jsonArray

    var currentStep by remember { mutableStateOf(0) }
    var showInsight by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título de la historia
        if (storyTitle != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryPurple.copy(alpha = 0.2f)
                    ),
                    border = BorderStroke(2.dp, PrimaryPurple)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            storyTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Introducción
        if (storyIntro != null) {
            item {
                Text(
                    storyIntro,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 24.sp
                )
            }
        }

        // Pasos de la historia
        if (steps != null && steps.isNotEmpty()) {
            items(steps.size) { index ->
                if (index <= currentStep) {
                    val step = steps[index].jsonObject
                    val stepTitle = step["title"]?.jsonPrimitive?.content ?: ""
                    val stepContent = step["content"]?.jsonPrimitive?.content ?: ""
                    val stepInsight = step["insight"]?.jsonPrimitive?.content
                    val stepIcon = step["icon"]?.jsonPrimitive?.content ?: "info"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceCard
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (index == currentStep) PrimaryCyan else SurfaceElevated
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header del paso
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    getIconByName(stepIcon),
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Paso ${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary
                                    )
                                    Text(
                                        stepTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Contenido del paso
                            Text(
                                stepContent,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                lineHeight = 22.sp
                            )

                            // Insight (revelable)
                            if (stepInsight != null && index == currentStep) {
                                Spacer(Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { showInsight = !showInsight },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = AccentGold
                                    ),
                                    border = BorderStroke(1.dp, AccentGold)
                                ) {
                                    Icon(
                                        Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        if (showInsight) "Ocultar Insight" else "Ver Insight Clave",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                AnimatedVisibility(visible = showInsight) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = AccentGold.copy(alpha = 0.15f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("💡", fontSize = 20.sp)
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                stepInsight,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimary,
                                                fontStyle = FontStyle.Italic,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botón para revelar siguiente paso
            if (currentStep < steps.size - 1) {
                item {
                    Button(
                        onClick = {
                            currentStep++
                            showInsight = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryCyan
                        )
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Continuar Historia")
                    }
                }
            } else {
                // Mensaje final
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SuccessGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Historia completada. Continúa a la siguiente sección.",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// RENDERERS DE TIPOS DE CONTENIDO
// =========================================================================

@Composable
fun RenderStoryHook(content: JsonObject) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            content["image_url"]?.jsonPrimitive?.content?.let { url ->
                Text(
                    "Imagen: $url",
                    color = PrimaryCyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                content["narrative"]?.jsonPrimitive?.content ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            val stats = content["stats"]?.jsonArray
            if (stats != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    stats.forEach { statElement ->
                        val stat = statElement.jsonObject
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                getIconByName(stat["icon"]?.jsonPrimitive?.content ?: ""),
                                null,
                                tint = AccentGold
                            )
                            Text(
                                stat["value"]?.jsonPrimitive?.content ?: "",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                stat["label"]?.jsonPrimitive?.content ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderTheoryTabs(content: JsonObject) {
    val intro = content["intro"]?.jsonPrimitive?.content
    val tabs = content["tabs"]?.jsonArray

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (intro != null) {
            item {
                Text(
                    intro,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        items(tabs?.size ?: 0) { index ->
            val tab = tabs!![index].jsonObject
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            getIconByName(tab["icon"]?.jsonPrimitive?.content ?: ""),
                            null,
                            tint = PrimaryCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            tab["title"]?.jsonPrimitive?.content ?: "",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        tab["text"]?.jsonPrimitive?.content ?: "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RenderTheorySection(content: JsonObject) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content["intro"]?.jsonPrimitive?.contentOrNull?.let {
            item {
                Text(
                    it,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        val components = content["components"]?.jsonArray
        if (components != null) {
            item {
                Text(
                    "Componentes Clave:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(components.size) { index ->
                val element = components[index]
                if (element is JsonObject) {
                    ListItem(
                        headlineContent = {
                            Text(
                                element["name"]?.jsonPrimitive?.content ?: "",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        supportingContent = {
                            Text(
                                element["description"]?.jsonPrimitive?.content ?: "",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingContent = {
                            Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen)
                        }
                    )
                } else if (element is JsonPrimitive) {
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(
                            Icons.Default.ArrowRight,
                            contentDescription = null,
                            tint = PrimaryCyan
                        )
                        Text(
                            text = element.content,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderInteractiveConcept(content: JsonObject) {
    val concepts = content["concepts"]?.jsonArray

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                content["description"]?.jsonPrimitive?.content ?: "",
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(concepts?.size ?: 0) { index ->
            val c = concepts!![index].jsonObject
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        c["name"]?.jsonPrimitive?.content ?: "",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        "Def: ${c["definition"]?.jsonPrimitive?.content ?: ""}",
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RenderQuiz(content: JsonObject) {
    val question = content["question"]?.jsonPrimitive?.content ?: ""
    val options = content["options"]?.jsonArray
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("") }

    Column {
        Text(
            question,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 24.sp
        )

        options?.forEach { element ->
            val opt = element.jsonObject
            val id = opt["id"]?.jsonPrimitive?.content
            val text = opt["text"]?.jsonPrimitive?.content
            val isCorrect = opt["is_correct"]?.jsonPrimitive?.boolean == true
            val optFeedback = opt["feedback"]?.jsonPrimitive?.content ?: ""

            val containerColor = when {
                selectedOption != null && selectedOption == id ->
                    if (isCorrect) SuccessGreen.copy(alpha = 0.2f)
                    else ErrorRed.copy(alpha = 0.2f)
                selectedOption == id -> SurfaceActive
                else -> MaterialTheme.colorScheme.surface
            }

            val borderColor = when {
                selectedOption != null && selectedOption == id ->
                    if (isCorrect) SuccessGreen else ErrorRed
                selectedOption == id -> PrimaryCyan
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            Card(
                onClick = {
                    selectedOption = id
                    feedback = optFeedback
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = containerColor
                ),
                border = BorderStroke(2.dp, borderColor)
            ) {
                Text(
                    text ?: "",
                    modifier = Modifier.padding(16.dp),
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            }
        }

        if (feedback.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceElevated
                )
            ) {
                Text(
                    feedback,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun RenderMemoryCards(content: JsonObject) {
    val cards = content["cards"]?.jsonArray ?: return

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards.size) { index ->
            val card = cards[index].jsonObject
            var flipped by remember { mutableStateOf(false) }

            val frontColorString = card["front"]?.jsonObject?.get("color")?.jsonPrimitive?.content
            val backColorString = card["back"]?.jsonObject?.get("color")?.jsonPrimitive?.content

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { flipped = !flipped },
                colors = CardDefaults.cardColors(
                    containerColor = if (flipped)
                        parseColorHex(backColorString, SurfaceActive)
                    else
                        parseColorHex(frontColorString, PrimaryCyan)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!flipped) {
                        // FRENTE
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                getIconByName(
                                    card["front"]?.jsonObject?.get("icon")?.jsonPrimitive?.content ?: ""
                                ),
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                card["front"]?.jsonObject?.get("term")?.jsonPrimitive?.content ?: "",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Toca para ver definición",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        // REVERSO
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                "Definición:",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                card["back"]?.jsonObject?.get("definition")?.jsonPrimitive?.content ?: "",
                                color = Color.White,
                                fontSize = 15.sp,
                                lineHeight = 20.sp
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "Ejemplo:",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                card["back"]?.jsonObject?.get("example")?.jsonPrimitive?.content ?: "",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderAccordionList(content: JsonObject) {
    val sections = content["sections"]?.jsonArray ?: return
    var expandedSection by remember { mutableStateOf<String?>(null) }
    var expandedItem by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sections.toList()) { section ->
            val sec = section.jsonObject
            val sectionId = sec["id"]?.jsonPrimitive?.content ?: ""
            val isExpanded = expandedSection == sectionId

            val sectionColor = parseColorHex(
                sec["color"]?.jsonPrimitive?.content,
                PrimaryCyan
            )

            // Sección principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedSection = if (isExpanded) null else sectionId
                        expandedItem = null
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    2.dp,
                    if (isExpanded) sectionColor else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        getIconByName(sec["icon"]?.jsonPrimitive?.content ?: ""),
                        null,
                        tint = sectionColor
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        sec["title"]?.jsonPrimitive?.content ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Items de la sección (expandible)
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                    sec["items"]?.jsonArray?.forEach { item ->
                        val it = item.jsonObject
                        val itemId = it["id"]?.jsonPrimitive?.content ?: ""
                        val isItemExpanded = expandedItem == itemId

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    expandedItem = if (isItemExpanded) null else itemId
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceElevated
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        getIconByName(it["icon"]?.jsonPrimitive?.content ?: ""),
                                        null,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        it["title"]?.jsonPrimitive?.content ?: "",
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        if (isItemExpanded) Icons.Default.ExpandLess
                                        else Icons.Default.ExpandMore,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                AnimatedVisibility(visible = isItemExpanded) {
                                    Column(modifier = Modifier.padding(top = 12.dp)) {
                                        val details = it["details"]?.jsonObject

                                        DetailRow(
                                            label = "Definición",
                                            value = details?.get("definition")?.jsonPrimitive?.content ?: "",
                                            color = PrimaryCyan
                                        )

                                        val types = details?.get("types")?.jsonArray?.joinToString(", ") {
                                            it.jsonPrimitive.content
                                        }
                                        if (types != null) {
                                            DetailRow(
                                                label = "Tipos",
                                                value = types,
                                                color = WarningOrange
                                            )
                                        }

                                        DetailRow(
                                            label = "Caso Real",
                                            value = details?.get("real_case")?.jsonPrimitive?.content ?: "",
                                            color = ErrorRed
                                        )

                                        val prevention = details?.get("prevention")?.jsonArray
                                        if (prevention != null) {
                                            Text(
                                                "Prevención:",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = SuccessGreen,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                            prevention.forEach { prev ->
                                                Row(
                                                    modifier = Modifier.padding(
                                                        start = 8.dp,
                                                        top = 2.dp
                                                    )
                                                ) {
                                                    Text("✓", color = SuccessGreen)
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(
                                                        prev.jsonPrimitive.content,
                                                        fontSize = 12.sp,
                                                        lineHeight = 18.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }

                                        val severity = details?.get("severity")?.jsonPrimitive?.content
                                        if (severity != null) {
                                            Spacer(Modifier.height(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        when (severity) {
                                                            "CRÍTICA" -> ErrorRed.copy(alpha = 0.8f)
                                                            "ALTA" -> WarningOrange.copy(alpha = 0.8f)
                                                            "MEDIA" -> AccentGold.copy(alpha = 0.8f)
                                                            else -> SuccessGreen.copy(alpha = 0.8f)
                                                        },
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    "Severidad: $severity",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
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
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            "$label:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = color
        )
        Text(
            value,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = TextPrimary,
            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
        )
    }
}

@Composable
fun RenderProgressChecklist(content: JsonObject) {
    val items = content["items"]?.jsonArray ?: return
    var completedItems by remember { mutableStateOf(setOf<String>()) }

    Column {
        val progress = if (items.isNotEmpty()) {
            completedItems.size.toFloat() / items.size
        } else {
            0f
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = SurfaceCard
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Progreso del Módulo",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryCyan
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryCyan,
                    trackColor = SurfaceElevated
                )
                Text(
                    "${completedItems.size} de ${items.size} completados",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                val it = item.jsonObject
                val itemId = it["id"]?.jsonPrimitive?.content ?: ""
                val isCompleted = completedItems.contains(itemId)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            completedItems = if (isCompleted) {
                                completedItems - itemId
                            } else {
                                completedItems + itemId
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted)
                            SuccessGreen.copy(alpha = 0.1f)
                        else
                            SurfaceCard
                    ),
                    border = BorderStroke(
                        2.dp,
                        if (isCompleted) SuccessGreen else SurfaceElevated
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = SuccessGreen,
                                    uncheckedColor = TextSecondary
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Icon(
                                getIconByName(it["icon"]?.jsonPrimitive?.content ?: ""),
                                null,
                                tint = if (isCompleted) SuccessGreen else PrimaryCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                it["task"]?.jsonPrimitive?.content ?: "",
                                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp,
                                color = if (isCompleted) TextSecondary else TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        AnimatedVisibility(visible = isCompleted) {
                            Column(
                                modifier = Modifier
                                    .padding(start = 56.dp, top = 12.dp)
                                    .background(
                                        SuccessGreen.copy(alpha = 0.15f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎉", fontSize = 24.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        it["unlock_content"]?.jsonObject?.get("title")?.jsonPrimitive?.content
                                            ?: "¡Desbloqueado!",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = SuccessGreen
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                it["unlock_content"]?.jsonObject?.get("points")?.jsonArray?.forEach { point ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Text("•", color = SuccessGreen, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            point.jsonPrimitive.content,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = TextPrimary
                                        )
                                    }
                                }

                                val bonusFact = it["unlock_content"]?.jsonObject?.get("bonus_fact")
                                    ?.jsonPrimitive?.content
                                if (bonusFact != null) {
                                    Spacer(Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = SurfaceElevated
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Lightbulb,
                                                null,
                                                tint = AccentGold,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                bonusFact,
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                                color = TextPrimary,
                                                fontStyle = FontStyle.Italic
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (completedItems.size == items.size && items.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "¡Módulo Completado!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Has dominado todos los conceptos",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}