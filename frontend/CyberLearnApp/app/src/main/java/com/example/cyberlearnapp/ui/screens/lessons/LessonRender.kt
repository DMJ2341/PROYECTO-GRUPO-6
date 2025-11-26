package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cyberlearnapp.network.models.LessonScreen
import com.example.cyberlearnapp.utils.getIconByName
import kotlinx.serialization.json.*

@Composable
fun LessonScreenRender(
    screenData: LessonScreen,
    onQuizAnswer: (Boolean) -> Unit
) {
    val content = screenData.content
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 100.dp) // Espacio extra para el botón inferior
    ) {
        // Header del Tipo de Pantalla
        SuggestionChip(
            onClick = {},
            label = { Text(screenData.type.replace("_", " ").uppercase()) },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = screenData.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- SWITCH MAESTRO ---
        when (screenData.type) {
            "story_hook" -> RenderStoryHook(content)
            "theory_tabs" -> RenderTheoryTabs(content)
            "theory_section" -> RenderTheorySection(content)
            "interactive_concept" -> RenderInteractiveConcepts(content)
            "practical_lab" -> RenderPracticalLab(content)
            "practical_scenario", "practical_exercise" -> RenderPracticalScenario(content)
            "quiz" -> RenderQuiz(content, onQuizAnswer)
            else -> Text("Contenido en desarrollo: ${screenData.type}")
        }
    }
}

// 1. STORY HOOK (Caso Real)
@Composable
fun RenderStoryHook(content: JsonObject) {
    val caseName = content["case_name"]?.jsonPrimitive?.content ?: ""
    val date = content["date"]?.jsonPrimitive?.content ?: ""
    val narrative = content["narrative"]?.jsonPrimitive?.content ?: ""
    val imageUrl = content["image_url"]?.jsonPrimitive?.content
    val stats = content["stats"]?.jsonArray

    Card(elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(getIconByName("history"), null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(date, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                }
                Spacer(Modifier.height(8.dp))
                Text(caseName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text(narrative, style = MaterialTheme.typography.bodyLarge, lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5)

                if (stats != null) {
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        stats.forEach { stat ->
                            val s = stat.jsonObject
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(getIconByName(s["icon"]?.jsonPrimitive?.content ?: ""), null, tint = MaterialTheme.colorScheme.primary)
                                Text(s["value"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                                Text(s["label"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 2. THEORY TABS
@Composable
fun RenderTheoryTabs(content: JsonObject) {
    val tabs = content["tabs"]?.jsonArray ?: return
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    text = { Text(tab.jsonObject["label"]?.jsonPrimitive?.content ?: "") },
                    icon = { Icon(getIconByName(tab.jsonObject["icon"]?.jsonPrimitive?.content ?: ""), null) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        val current = tabs[selectedIndex].jsonObject
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(current["title"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(current["text"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

// 3. THEORY SECTION (Listas complejas: puertos, roles, componentes)
@Composable
fun RenderTheorySection(content: JsonObject) {
    // Soporte para 'common_ports' (Curso 2)
    val ports = content["common_ports"]?.jsonArray
    if (ports != null) {
        ports.forEach { p ->
            val port = p.jsonObject
            ListItem(
                headlineContent = { Text("${port["port"]}: ${port["service"]?.jsonPrimitive?.content}") },
                supportingContent = { Text("Vuln: ${port["vuln"]?.jsonPrimitive?.content}", color = Color.Red) },
                leadingContent = { Badge { Text(port["protocol"]?.jsonPrimitive?.content ?: "") } }
            )
            HorizontalDivider()
        }
        return
    }

    // Soporte para 'components' (Curso 5 - Risk)
    val components = content["components"]?.jsonArray
    if (components != null) {
        components.forEach { c ->
            val comp = c.jsonObject
            Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(getIconByName(comp["icon"]?.jsonPrimitive?.content ?: ""), null)
                        Spacer(Modifier.width(8.dp))
                        Text(comp["component"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                    }
                    comp["description"]?.jsonPrimitive?.content?.let {
                        Text(it, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

// 4. INTERACTIVE CONCEPTS (Tarjetas simples)
@Composable
fun RenderInteractiveConcepts(content: JsonObject) {
    val concepts = content["concepts"]?.jsonArray ?: return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        concepts.forEach { c ->
            val item = c.jsonObject
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(getIconByName(item["icon"]?.jsonPrimitive?.content ?: ""), null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(item["name"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                        Text(item["definition"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

// 5. PRACTICAL LAB (PCAP View)
@Composable
fun RenderPracticalLab(content: JsonObject) {
    val instruction = content["instruction"]?.jsonPrimitive?.content ?: ""
    val pcap = content["pcap_simulation"]?.jsonArray

    Text(instruction, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(12.dp))

    if (pcap != null) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B))) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("NETWORK TRAFFIC CAPTURE", color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = MaterialTheme.typography.labelSmall.fontSize)
                HorizontalDivider(color = Color.Gray)
                pcap.forEach { pkt ->
                    val p = pkt.jsonObject
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("[${p["packet"]}] ", color = Color.Yellow, fontFamily = FontFamily.Monospace)
                        Text("${p["src"]} -> ${p["dst"]} ", color = Color.White, fontFamily = FontFamily.Monospace)
                        Text("[${p["flags"]}]", color = Color.Cyan, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }

    // Preguntas del lab
    val questions = content["questions"]?.jsonArray
    if (questions != null) {
        Spacer(Modifier.height(16.dp))
        questions.forEach { q ->
            var revealed by remember { mutableStateOf(false) }
            val quest = q.jsonObject
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { revealed = !revealed },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("❓ " + (quest["q"]?.jsonPrimitive?.content ?: ""), fontWeight = FontWeight.Bold)
                    if (revealed) {
                        Spacer(Modifier.height(8.dp))
                        Text("💡 " + (quest["a"]?.jsonPrimitive?.content ?: ""), color = MaterialTheme.colorScheme.primary)
                    } else {
                        Text("Toca para ver la respuesta", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// 6. PRACTICAL SCENARIO (Decisiones / Risk Calc)
@Composable
fun RenderPracticalScenario(content: JsonObject) {
    val scenario = content["scenario"]?.jsonPrimitive?.content ?: ""

    Text("Escenario:", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
    Text(scenario, style = MaterialTheme.typography.bodyLarge)
    Spacer(Modifier.height(16.dp))

    // Caso Risk Calculator (Curso 5)
    val factors = content["factors"]?.jsonObject
    if (factors != null) {
        factors.entries.forEach { (key, value) ->
            val factor = value.jsonObject
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(key.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(factor["question"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    // Mostrar status o likelihood
                    val status = factor["status"]?.jsonPrimitive?.content
                        ?: factor["likelihood"]?.jsonPrimitive?.content
                        ?: factor["magnitude"]?.jsonPrimitive?.content

                    Text(status ?: "", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    // Caso Scenarios simples
    val scenarios = content["scenarios"]?.jsonArray
    scenarios?.forEach { s ->
        val sc = s.jsonObject
        var revealed by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { revealed = !revealed },
            colors = CardDefaults.cardColors(containerColor = if(revealed) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(sc["description"]?.jsonPrimitive?.content ?: "")
                if (revealed) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Respuesta: " + (sc["correct_answer"]?.jsonPrimitive?.content ?: ""), fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text(sc["explanation"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// 7. QUIZ (Interactivo)
@Composable
fun RenderQuiz(content: JsonObject, onAnswer: (Boolean) -> Unit) {
    val question = content["question"]?.jsonPrimitive?.content ?: ""
    val options = content["options"]?.jsonArray ?: return

    var selectedId by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    Text("Evaluación Rápida", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(8.dp))
    Text(question, style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(16.dp))

    options.forEach { opt ->
        val o = opt.jsonObject
        val id = o["id"]?.jsonPrimitive?.content ?: ""
        val text = o["text"]?.jsonPrimitive?.content ?: ""
        val isCorrect = o["is_correct"]?.jsonPrimitive?.boolean ?: false
        val feedback = o["feedback"]?.jsonPrimitive?.content ?: ""

        val isSelected = selectedId == id
        val cardColor = when {
            isSubmitted && isCorrect -> Color(0xFFC8E6C9) // Verde si es la correcta
            isSubmitted && isSelected && !isCorrect -> Color(0xFFFFCDD2) // Rojo si elegiste mal
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable(enabled = !isSubmitted) {
                    selectedId = id
                    isSubmitted = true
                    onAnswer(isCorrect)
                },
            colors = CardDefaults.cardColors(containerColor = cardColor),
            border = BorderStroke(1.dp, if(isSelected) MaterialTheme.colorScheme.primary else Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(text)
                }
                // Feedback solo si ya se envió y (es la seleccionada o es la correcta para mostrar cuál era)
                if (isSubmitted && (isSelected || isCorrect)) {
                    if (isCorrect) {
                        Text("✅ $feedback", modifier = Modifier.padding(start = 40.dp, top = 4.dp), style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
                    } else if (isSelected) {
                        Text("❌ $feedback", modifier = Modifier.padding(start = 40.dp, top = 4.dp), style = MaterialTheme.typography.bodySmall, color = Color.Red)
                    }
                }
            }
        }
    }
}