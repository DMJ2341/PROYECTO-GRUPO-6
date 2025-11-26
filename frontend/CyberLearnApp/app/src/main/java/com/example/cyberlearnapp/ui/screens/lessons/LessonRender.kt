package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.LessonResponse
import com.example.cyberlearnapp.utils.getIconByName
import kotlinx.serialization.json.*

@Composable
fun LessonScreenRender(
    lesson: LessonResponse, // ✅ CAMBIO: Recibe LessonResponse
    screenIndex: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    isLastScreen: Boolean
) {
    // ✅ LÓGICA SIMPLIFICADA: Usamos la lista 'screens' que ya viene en el objeto
    val currentScreen = lesson.screens.getOrNull(screenIndex)

    if (currentScreen == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando contenido o fin de lección...")
        }
        return
    }

    val type = currentScreen.type
    // El contenido ya es un JsonObject, no hace falta parsear string
    val contentData = currentScreen.content
    val title = currentScreen.title

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            when (type) {
                "story_hook" -> RenderStoryHook(contentData)
                "theory_tabs" -> RenderTheoryTabs(contentData)
                "theory_section" -> RenderTheorySection(contentData)
                "interactive_concept" -> RenderInteractiveConcept(contentData)
                "quiz" -> RenderQuiz(contentData)
                // NUEVAS INTERACTIVAS
                "memory_cards" -> RenderMemoryCards(contentData)
                "accordion_list" -> RenderAccordionList(contentData)
                "progress_checklist" -> RenderProgressChecklist(contentData)
                // ---
                else -> RenderStoryHook(contentData)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onPrev, enabled = screenIndex > 0) {
                Text("Anterior")
            }
            Button(onClick = onNext) {
                Text(if (isLastScreen) "Finalizar" else "Siguiente")
            }
        }
    }
}

// =========================================================================
// RENDERERS EXISTENTES
// =========================================================================

@Composable
fun RenderStoryHook(content: JsonObject) {
    LazyColumn {
        item {
            content["image_url"]?.jsonPrimitive?.content?.let { url ->
                Text("Imagen: $url", color = Color.Blue, modifier = Modifier.padding(bottom = 8.dp))
            }
            Text(content["narrative"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.bodyLarge)

            val stats = content["stats"]?.jsonArray
            if (stats != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    stats.forEach { statElement ->
                        val stat = statElement.jsonObject
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(getIconByName(stat["icon"]?.jsonPrimitive?.content ?: ""), null)
                            Text(stat["value"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                            Text(stat["label"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.bodySmall)
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

    LazyColumn {
        if (intro != null) item { Text(intro, modifier = Modifier.padding(bottom = 8.dp)) }
        items(tabs?.size ?: 0) { index ->
            val tab = tabs!![index].jsonObject
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(getIconByName(tab["icon"]?.jsonPrimitive?.content ?: ""), null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(tab["title"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tab["text"]?.jsonPrimitive?.content ?: "")
                }
            }
        }
    }
}

@Composable
fun RenderTheorySection(content: JsonObject) {
    LazyColumn {
        content["intro"]?.jsonPrimitive?.contentOrNull?.let {
            item { Text(it, modifier = Modifier.padding(bottom = 16.dp)) }
        }

        val components = content["components"]?.jsonArray
        if (components != null) {
            item { Text("Componentes Clave:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp)) }
            items(components.size) { index ->
                val element = components[index]
                if (element is JsonObject) {
                    ListItem(
                        headlineContent = { Text(element["name"]?.jsonPrimitive?.content ?: "") },
                        supportingContent = { Text(element["description"]?.jsonPrimitive?.content ?: "") },
                        leadingContent = { Icon(Icons.Default.CheckCircle, null) }
                    )
                } else if (element is JsonPrimitive) {
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.ArrowRight, contentDescription = null)
                        Text(text = element.content, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RenderInteractiveConcept(content: JsonObject) {
    val concepts = content["concepts"]?.jsonArray
    LazyColumn {
        item { Text(content["description"]?.jsonPrimitive?.content ?: "", modifier = Modifier.padding(bottom = 16.dp)) }
        items(concepts?.size ?: 0) { index ->
            val c = concepts!![index].jsonObject
            OutlinedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(c["name"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Def: ${c["definition"]?.jsonPrimitive?.content ?: ""}")
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
        Text(question, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
        options?.forEach { element ->
            val opt = element.jsonObject
            val id = opt["id"]?.jsonPrimitive?.content
            val text = opt["text"]?.jsonPrimitive?.content
            val isCorrect = opt["is_correct"]?.jsonPrimitive?.boolean == true
            val optFeedback = opt["feedback"]?.jsonPrimitive?.content ?: ""

            Button(
                onClick = { selectedOption = id; feedback = optFeedback },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption == id) (if (isCorrect) Color.Green else Color.Red) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text ?: "")
            }
        }
        if (feedback.isNotEmpty()) Text(feedback, modifier = Modifier.padding(top = 16.dp))
    }
}

// =========================================================================
// NUEVAS INTERACTIVAS
// =========================================================================

// --- IDEA 1: "Memory Cards" ---
@Composable
fun RenderMemoryCards(content: JsonObject) {
    val cards = content["cards"]?.jsonArray ?: return

    LazyColumn {
        items(cards.size) { index ->
            val card = cards[index].jsonObject
            var flipped by remember { mutableStateOf(false) }

            // Función de ayuda para parsear el color de forma segura
            fun parseColorHex(hex: String?, default: Long): Color {
                return try {
                    val cleanHex = hex?.removePrefix("#")
                    // Combina 0xFF (alpha opaco) con el valor RGB de 6 dígitos
                    Color(cleanHex?.toLong(16)?.or(0xFF00000000) ?: default)
                } catch (e: Exception) {
                    Color(default)
                }
            }

            val frontColorString = card["front"]?.jsonObject?.get("color")?.jsonPrimitive?.content
            val backColorString = card["back"]?.jsonObject?.get("color")?.jsonPrimitive?.content

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(180.dp)
                    .clickable { flipped = !flipped },
                colors = CardDefaults.cardColors(
                    containerColor = if (flipped)
                        parseColorHex(backColorString, 0xFF4CAF50) // Default: Green
                    else
                        parseColorHex(frontColorString, 0xFF1976D2) // Default: Blue
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!flipped) {
                        // FRENTE
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                getIconByName(card["front"]?.jsonObject?.get("icon")?.jsonPrimitive?.content ?: ""),
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
                            modifier = Modifier.padding(20.dp)
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
                                fontSize = 16.sp
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
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- IDEA 2: "Accordion List" ---

@Composable
fun RenderAccordionList(content: JsonObject) {
    val sections = content["sections"]?.jsonArray ?: return
    var expandedSection by remember { mutableStateOf<String?>(null) }
    var expandedItem by remember { mutableStateOf<String?>(null) }

    LazyColumn {
        items(sections.toList()) { section ->
            val sec = section.jsonObject
            val sectionId = sec["id"]?.jsonPrimitive?.content ?: ""
            val isExpanded = expandedSection == sectionId

            // Función de ayuda para parsear el color de forma segura
            fun parseColorHex(hex: String?, default: Long): Color {
                return try {
                    val cleanHex = hex?.removePrefix("#")
                    // Combina 0xFF (alpha opaco) con el valor RGB de 6 dígitos
                    Color(cleanHex?.toLong(16)?.or(0xFF00000000) ?: default)
                } catch (e: Exception) {
                    Color(default)
                }
            }

            val sectionColor = parseColorHex(sec["color"]?.jsonPrimitive?.content, 0xFFD32F2F)

            // Sección principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        expandedSection = if (isExpanded) null else sectionId
                        expandedItem = null
                    },
                colors = CardDefaults.cardColors(
                    containerColor = sectionColor.copy(alpha = 0.2f)
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
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null
                    )
                }
            }

            // Items de la sección (expandible)
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    sec["items"]?.jsonArray?.forEach { item ->
                        val it = item.jsonObject
                        val itemId = it["id"]?.jsonPrimitive?.content ?: ""
                        val isItemExpanded = expandedItem == itemId

                        // Item
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable {
                                    expandedItem = if (isItemExpanded) null else itemId
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        getIconByName(it["icon"]?.jsonPrimitive?.content ?: ""),
                                        null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        it["title"]?.jsonPrimitive?.content ?: "",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        if (isItemExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Detalles (expandible)
                                AnimatedVisibility(visible = isItemExpanded) {
                                    Column(modifier = Modifier.padding(top = 12.dp)) {
                                        val details = it["details"]?.jsonObject

                                        // Definición
                                        DetailRow(
                                            label = "Definición",
                                            value = details?.get("definition")?.jsonPrimitive?.content ?: "",
                                            color = Color(0xFF1976D2)
                                        )

                                        // Tipos
                                        val types = details?.get("types")?.jsonArray?.joinToString(", ") {
                                            it.jsonPrimitive.content
                                        }
                                        if (types != null) {
                                            DetailRow(
                                                label = "Tipos",
                                                value = types,
                                                color = Color(0xFFF57C00)
                                            )
                                        }

                                        // Caso real
                                        DetailRow(
                                            label = "Caso Real",
                                            value = details?.get("real_case")?.jsonPrimitive?.content ?: "",
                                            color = Color(0xFFD32F2F)
                                        )

                                        // Prevención
                                        val prevention = details?.get("prevention")?.jsonArray
                                        if (prevention != null) {
                                            Text(
                                                "Prevención:",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF388E3C),
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                            prevention.forEach { prev ->
                                                Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp)) {
                                                    Text("✓", color = Color(0xFF388E3C))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(
                                                        prev.jsonPrimitive.content,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }

                                        // Severidad badge
                                        val severity = details?.get("severity")?.jsonPrimitive?.content
                                        if (severity != null) {
                                            Spacer(Modifier.height(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        when(severity) {
                                                            "CRÍTICA" -> Color(0xFFD32F2F)
                                                            "ALTA" -> Color(0xFFF57C00)
                                                            "MEDIA" -> Color(0xFFFBC02D)
                                                            else -> Color(0xFF388E3C)
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
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// --- IDEA 3: "Progress Checklist" ---

@Composable
fun RenderProgressChecklist(content: JsonObject) {
    val items = content["items"]?.jsonArray ?: return
    var completedItems by remember { mutableStateOf(setOf<String>()) }

    Column {
        // Barra de progreso global
        val progress = if (items.isNotEmpty()) completedItems.size.toFloat() / items.size else 0f

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
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
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    "${completedItems.size} de ${items.size} completados",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Items del checklist
        LazyColumn {
            items(items.toList()) { item ->
                val it = item.jsonObject
                val itemId = it["id"]?.jsonPrimitive?.content ?: ""
                val isCompleted = completedItems.contains(itemId)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            completedItems = if (isCompleted) {
                                completedItems - itemId
                            } else {
                                completedItems + itemId
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted)
                            Color(0xFFE8F5E9) // Very light green
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        2.dp,
                        if (isCompleted) Color(0xFF388E3C) else Color.LightGray
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Tarea principal
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF388E3C)
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Icon(
                                getIconByName(it["icon"]?.jsonPrimitive?.content ?: ""),
                                null,
                                tint = if (isCompleted) Color(0xFF388E3C) else MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                it["task"]?.jsonPrimitive?.content ?: "",
                                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Contenido desbloqueado
                        AnimatedVisibility(visible = isCompleted) {
                            Column(
                                modifier = Modifier
                                    .padding(start = 56.dp, top = 12.dp)
                                    .background(
                                        Color(0xFF388E3C).copy(alpha = 0.1f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎉", fontSize = 24.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        it["unlock_content"]?.jsonObject?.get("title")?.jsonPrimitive?.content ?: "¡Desbloqueado!",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32) // Darker green
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                // Puntos clave
                                it["unlock_content"]?.jsonObject?.get("points")?.jsonArray?.forEach { point ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Text("•", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            point.jsonPrimitive.content,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                // Bonus fact
                                val bonusFact = it["unlock_content"]?.jsonObject?.get("bonus_fact")?.jsonPrimitive?.content
                                if (bonusFact != null) {
                                    Spacer(Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFFFF9C4) // Light yellow
                                        )
                                    ) {
                                        Row(modifier = Modifier.padding(12.dp)) {
                                            Icon(
                                                Icons.Default.Lightbulb,
                                                null,
                                                tint = Color(0xFFF57C00), // Orange
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                bonusFact,
                                                fontSize = 12.sp,
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

        // Mensaje de completitud
        if (completedItems.size == items.size && items.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50) // Solid green
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