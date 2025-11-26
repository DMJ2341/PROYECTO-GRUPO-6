package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
            .padding(bottom = 100.dp)
    ) {
        // Header del Tipo de Pantalla
        SuggestionChip(
            onClick = {},
            label = { Text(screenData.type.replace("_", " ").uppercase()) },
            modifier = Modifier.padding(bottom = 8.dp),
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )

        // Título Principal
        Text(
            text = screenData.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- SWITCH MAESTRO COMPLETO ---
        // Ahora maneja TODOS los tipos definidos en tus JSONs
        when (screenData.type) {
            // Básicos
            "story_hook" -> RenderStoryHook(content)
            "theory_tabs" -> RenderTheoryTabs(content)
            "theory_section" -> RenderTheorySection(content)
            "interactive_concept" -> RenderInteractiveConcepts(content)

            // Gráficos y Sistemas
            "interactive_diagram" -> RenderInteractiveDiagram(content)
            "interactive_system" -> RenderInteractiveSystem(content)
            "kill_chain_analysis" -> RenderKillChain(content)
            "incident_timeline" -> RenderIncidentTimeline(content)

            // Ejercicios Prácticos (NUEVOS)
            "practical_lab" -> RenderPracticalLab(content)
            "practical_scenario", "practical_exercise" -> RenderPracticalExerciseWrapper(content) // Unificamos lógica
            "architecture_design" -> RenderArchitectureDesign(content)
            "interactive_exercise" -> RenderInteractiveExercise(content)

            // Evaluaciones y Análisis
            "analysis_questions" -> RenderAnalysisQuestions(content)
            "control_assessment" -> RenderControlAssessment(content)
            "theoretical_questions" -> RenderTheoreticalQuestions(content)
            "pcap_analysis" -> RenderPcapAnalysis(content)

            // Exámenes y Entregables
            "final_exam_intro" -> RenderFinalExamIntro(content)
            "final_deliverable", "final_deliverables" -> RenderFinalDeliverable(content)
            "quiz" -> RenderQuiz(content, onQuizAnswer)

            // Fallback para tipos incidentales o muy específicos no mapeados
            else -> {
                // Intentamos renderizar como ejercicio práctico genérico si tiene tareas
                if (content.containsKey("tasks") || content.containsKey("questions")) {
                    RenderPracticalExerciseWrapper(content)
                } else {
                    Text(
                        "Contenido: ${screenData.type}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// ===================================================================
// RENDERERS IMPLEMENTADOS
// ===================================================================

// 1. STORY HOOK
@Composable
fun RenderStoryHook(content: JsonObject) {
    val caseName = content["case_name"]?.jsonPrimitive?.content ?: ""
    val date = content["date"]?.jsonPrimitive?.content ?: ""
    val narrative = content["narrative"]?.jsonPrimitive?.content ?: ""
    val imageUrl = content["image_url"]?.jsonPrimitive?.content
    val stats = content["stats"]?.jsonArray

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(8.dp))
                Text(caseName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
                Text(narrative, style = MaterialTheme.typography.bodyLarge)

                if (stats != null) {
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        stats.forEach { stat ->
                            val s = stat.jsonObject
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(getIconByName(s["icon"]?.jsonPrimitive?.content ?: ""), null, tint = MaterialTheme.colorScheme.secondary)
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
    val intro = content["intro"]?.jsonPrimitive?.content
    val tabs = content["tabs"]?.jsonArray ?: return
    var selectedIndex by remember { mutableIntStateOf(0) }

    if (intro != null) {
        Text(intro, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
    }

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            val t = tab.jsonObject
            Tab(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                text = { Text(t["label"]?.jsonPrimitive?.content ?: "") },
                icon = if (t["icon"] != null) {
                    { Icon(getIconByName(t["icon"]?.jsonPrimitive?.content ?: ""), null) }
                } else null
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    if (selectedIndex < tabs.size) {
        val current = tabs[selectedIndex].jsonObject
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(current["title"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(current["text"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

// 3. PRACTICAL EXERCISE / SCENARIO (Unificado para máxima cobertura)
@Composable
fun RenderPracticalExerciseWrapper(content: JsonObject) {
    // Detectamos qué tipo de contenido es y mostramos todo lo disponible
    val instruction = content["instruction"]?.jsonPrimitive?.content
        ?: content["scenario"]?.jsonPrimitive?.content ?: ""

    val techniques = content["techniques"]?.jsonArray
    val tasks = content["tasks"]?.jsonArray
    val questions = content["questions"]?.jsonArray
    val scenarios = content["scenarios"]?.jsonArray
    val factors = content["factors"]?.jsonObject

    var completedTasks by remember { mutableStateOf(setOf<String>()) }

    // 1. Instrucción/Escenario
    if (instruction.isNotEmpty()) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Science, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(instruction, style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    // 2. Técnicas MITRE (si existen)
    if (techniques != null) {
        Text("Técnicas Identificadas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        techniques.forEach { tech ->
            val t = tech.jsonObject
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(t["technique_id"]?.jsonPrimitive?.content ?: "", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(t["technique_name"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                    Text(t["tactic"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    // 3. Tareas Interactivas (Checkboxes)
    if (tasks != null) {
        Text("Tareas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        tasks.forEachIndexed { index, task ->
            val t = task.jsonObject
            val taskId = index.toString()
            val isCompleted = completedTasks.contains(taskId)

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clickable {
                        completedTasks = if (isCompleted) completedTasks - taskId else completedTasks + taskId
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCompleted, onCheckedChange = null)
                    Text(t["task"]?.jsonPrimitive?.content ?: "", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }

    // 4. Factores de Riesgo (Risk Calculator)
    if (factors != null) {
        factors.entries.forEach { (key, value) ->
            val factor = value.jsonObject
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(key.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Text(factor["question"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    val status = factor["status"]?.jsonPrimitive?.content
                        ?: factor["likelihood"]?.jsonPrimitive?.content
                        ?: factor["magnitude"]?.jsonPrimitive?.content
                    Text(status ?: "", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // 5. Escenarios/Preguntas con Reveal
    val itemsToReveal = questions ?: scenarios
    itemsToReveal?.forEach { q ->
        var revealed by remember { mutableStateOf(false) }
        val item = q.jsonObject
        val qText = item["q"]?.jsonPrimitive?.content ?: item["description"]?.jsonPrimitive?.content ?: ""
        val aText = item["a"]?.jsonPrimitive?.content ?: item["correct_answer"]?.jsonPrimitive?.content ?: ""

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { revealed = !revealed },
            colors = CardDefaults.cardColors(containerColor = if(revealed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("❓ $qText", fontWeight = FontWeight.Bold)
                if (revealed) {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("💡 $aText", color = MaterialTheme.colorScheme.primary)
                    item["explanation"]?.jsonPrimitive?.content?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                } else {
                    Text("Toca para ver la respuesta", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

// 4. ARCHITECTURE DESIGN (Selección de componentes)
@Composable
fun RenderArchitectureDesign(content: JsonObject) {
    val scenario = content["scenario"]?.jsonPrimitive?.content ?: ""
    val components = content["components"]?.jsonArray
    val requirements = content["requirements"]?.jsonArray
    var selectedComponents by remember { mutableStateOf(setOf<String>()) }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Architecture, null)
            Text("Escenario de Diseño", fontWeight = FontWeight.Bold)
            Text(scenario)
        }
    }
    Spacer(Modifier.height(16.dp))

    if (requirements != null) {
        Text("Requisitos", fontWeight = FontWeight.Bold)
        requirements.forEach { req ->
            Row(Modifier.padding(vertical = 2.dp)) {
                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(req.jsonPrimitive.content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    if (components != null) {
        Text("Selecciona Componentes:", fontWeight = FontWeight.Bold)
        components.forEach { comp ->
            val c = comp.jsonObject
            val id = c["id"]?.jsonPrimitive?.content ?: ""
            val name = c["name"]?.jsonPrimitive?.content ?: ""
            val isSelected = selectedComponents.contains(id)

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clickable { selectedComponents = if (isSelected) selectedComponents - id else selectedComponents + id },
                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE3F2FD) else MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isSelected, onCheckedChange = null)
                    Text(name, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 5. FINAL EXAM INTRO
@Composable
fun RenderFinalExamIntro(content: JsonObject) {
    val description = content["description"]?.jsonPrimitive?.content
        ?: content["scenario"]?.jsonPrimitive?.content ?: ""
    val timeLimit = content["time_limit"]?.jsonPrimitive?.content ?: "Variable"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Assignment, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(Modifier.height(16.dp))
            Text("Examen Final", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(description, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 8.dp))

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Timer, null)
                    Text(timeLimit, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Grade, null)
                    Text("Aprobación 70%", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    Spacer(Modifier.height(24.dp))
    Button(onClick = { /* Lógica de inicio manejada por navegación principal */ }, modifier = Modifier.fillMaxWidth()) {
        Text("Preparado para iniciar")
    }
}

// 6. PRACTICAL LAB (PCAP / Terminal)
@Composable
fun RenderPracticalLab(content: JsonObject) {
    val instruction = content["instruction"]?.jsonPrimitive?.content ?: ""
    val pcap = content["pcap_simulation"]?.jsonArray
    val simulation = content["simulation"]?.jsonObject

    Text(instruction, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

    // Modo PCAP
    if (pcap != null) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B))) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("NETWORK TRAFFIC CAPTURE", color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = MaterialTheme.typography.labelSmall.fontSize)
                HorizontalDivider(color = Color.Gray, thickness = 0.5.dp)
                pcap.forEach { pkt ->
                    val p = pkt.jsonObject
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("[${p["packet"]}] ", color = Color.Yellow, fontFamily = FontFamily.Monospace)
                        Text("${p["src"]} -> ${p["dst"]} ", color = Color.White, fontFamily = FontFamily.Monospace)
                        Text("[${p["flags"]}]", color = Color.Cyan, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }

    // Modo Terminal
    if (simulation != null) {
        val tasks = simulation["tasks"]?.jsonArray
        tasks?.forEach { t ->
            val task = t.jsonObject
            Text(task["task"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top=8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = Color.Black)) {
                Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                    Text("$ " + (task["command"]?.jsonPrimitive?.content ?: ""), color = Color.Green, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(4.dp))
                    Text(task["output_excerpt"]?.jsonPrimitive?.content ?: "", color = Color.LightGray, fontFamily = FontFamily.Monospace)
                }
            }
            Text("Análisis: " + (task["analysis"]?.jsonPrimitive?.content ?: ""), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom=8.dp))
        }
    }

    // Reusar la lógica de preguntas
    if (content.containsKey("questions")) {
        RenderPracticalExerciseWrapper(content)
    }
}

// 7. INTERACTIVE DIAGRAM
@Composable
fun RenderInteractiveDiagram(content: JsonObject) {
    val description = content["description"]?.jsonPrimitive?.content
    val layers = content["layers"]?.jsonArray ?: return

    if(description != null) Text(description, Modifier.padding(bottom = 16.dp))

    layers.forEachIndexed { index, layerJson ->
        val layer = layerJson.jsonObject
        val name = layer["name"]?.jsonPrimitive?.content ?: ""
        val controls = layer["controls"]?.jsonArray?.joinToString(", ") { it.jsonPrimitive.content } ?: ""
        val icon = layer["icon"]?.jsonPrimitive?.content ?: "layers"
        val paddingHorizontal = (index * 8).dp

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = paddingHorizontal, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3447)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(getIconByName(icon), null, tint = Color.White)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(controls, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// 8. KILL CHAIN
@Composable
fun RenderKillChain(content: JsonObject) {
    val phases = content["phases"]?.jsonArray ?: return

    Column {
        phases.forEachIndexed { index, p ->
            val phase = p.jsonObject
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(MaterialTheme.colorScheme.error),
                        contentAlignment = Alignment.Center
                    ) {
                        Text((index + 1).toString(), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                    if (index < phases.size - 1) {
                        Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
                    }
                }
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(phase["phase"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        phase["wannacry_action"]?.jsonPrimitive?.content?.let { Text("Acción: $it", fontWeight = FontWeight.Medium) }
                        phase["detection"]?.jsonPrimitive?.content?.let { Text("Detección: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                    }
                }
            }
        }
    }
}

// 9. INCIDENT TIMELINE
@Composable
fun RenderIncidentTimeline(content: JsonObject) {
    val timeline = content["timeline"]?.jsonArray ?: return
    timeline.forEach { t ->
        val event = t.jsonObject
        val severity = event["severity"]?.jsonPrimitive?.content ?: "low"
        val color = when(severity) { "critical" -> Color(0xFFD32F2F); "high" -> Color(0xFFF57C00); "medium" -> Color(0xFFFBC02D); else -> Color(0xFF388E3C) }

        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(event["time"]?.jsonPrimitive?.content ?: "", modifier = Modifier.width(80.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, color)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(getIconByName(event["icon"]?.jsonPrimitive?.content ?: ""), null, tint = color)
                    Spacer(Modifier.width(12.dp))
                    Text(event["event"]?.jsonPrimitive?.content ?: "")
                }
            }
        }
    }
}

// 10. QUIZ
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
        val containerColor = when {
            isSubmitted && isCorrect -> Color(0xFFC8E6C9)
            isSubmitted && isSelected && !isCorrect -> Color(0xFFFFCDD2)
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                .clickable(enabled = !isSubmitted) {
                    selectedId = id
                    isSubmitted = true
                    onAnswer(isCorrect)
                },
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, if(isSelected) MaterialTheme.colorScheme.primary else Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(text, modifier = Modifier.weight(1f))
                }
                if (isSubmitted && (isSelected || isCorrect)) {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    if (isCorrect) Text("✅ $feedback", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32))
                    else if (isSelected) Text("❌ $feedback", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFC62828))
                }
            }
        }
    }
}

// --- OTROS RENDERERS SIMPLES ---

@Composable
fun RenderTheorySection(content: JsonObject) {
    val items = content["common_ports"]?.jsonArray
        ?: content["components"]?.jsonArray
        ?: content["types"]?.jsonArray

    items?.forEach { item ->
        val obj = item.jsonObject
        val title = obj["service"]?.jsonPrimitive?.content ?: obj["component"]?.jsonPrimitive?.content ?: obj["type"]?.jsonPrimitive?.content ?: ""
        val sub = obj["port"]?.jsonPrimitive?.toString() ?: obj["description"]?.jsonPrimitive?.content ?: ""
        Card(Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(sub, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun RenderInteractiveConcepts(content: JsonObject) {
    val concepts = content["concepts"]?.jsonArray
    concepts?.forEach { c ->
        val item = c.jsonObject
        ElevatedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(getIconByName(item["icon"]?.jsonPrimitive?.content ?: ""), null)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(item["name"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                    Text(item["definition"]?.jsonPrimitive?.content ?: "")
                }
            }
        }
    }
}

@Composable
fun RenderInteractiveSystem(content: JsonObject) {
    val structure = content["cve_structure"]?.jsonObject
    if (structure != null) {
        Card {
            Column(Modifier.padding(16.dp)) {
                Text("Estructura CVE", fontWeight = FontWeight.Bold)
                Text(structure["example"]?.jsonPrimitive?.content ?: "", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun RenderInteractiveExercise(content: JsonObject) {
    RenderPracticalExerciseWrapper(content)
}

@Composable
fun RenderAnalysisQuestions(content: JsonObject) {
    RenderPracticalExerciseWrapper(content)
}

@Composable
fun RenderControlAssessment(content: JsonObject) {
    val controls = content["controls"]?.jsonArray
    controls?.forEach { c ->
        val ctrl = c.jsonObject
        var rating by remember { mutableIntStateOf(0) }
        Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(ctrl["control_name"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                Row {
                    repeat(5) { i ->
                        IconButton(onClick = { rating = i + 1 }) {
                            Icon(if (i < rating) Icons.Default.Star else Icons.Default.StarBorder, null, tint = Color(0xFFFFA000))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderTheoreticalQuestions(content: JsonObject) {
    val questions = content["questions"]?.jsonArray
    questions?.forEach { q ->
        var answer by remember { mutableStateOf("") }
        Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(q.jsonObject["question"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                OutlinedTextField(value = answer, onValueChange = { answer = it }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun RenderFinalDeliverable(content: JsonObject) {
    val items = content["deliverables"]?.jsonArray ?: content["required_documents"]?.jsonArray
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Entregables Finales", style = MaterialTheme.typography.titleLarge)
            items?.forEach { item ->
                val text = if (item is JsonObject) item["title"]?.jsonPrimitive?.content else item.jsonPrimitive.content
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                    Text(text ?: "", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
fun RenderPcapAnalysis(content: JsonObject) {
    RenderPracticalLab(content)
}