package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cyberlearnapp.network.models.LessonResponse
import com.example.cyberlearnapp.utils.getIconByName
import kotlinx.serialization.json.*

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
            Text("Cargando contenido...")
        }
        return
    }

    val type = currentScreen.type
    val contentData = currentScreen.content
    val title = currentScreen.title

    // ✅ CAMBIO CLAVE: Usamos Scaffold interno para asegurar que la barra inferior se vea
    Scaffold(
        containerColor = Color.Transparent, // Usamos el fondo del tema general
        bottomBar = {
            // Barra de Navegación Inferior (Botones)
            Surface(
                tonalElevation = 8.dp, // Sombra para separarlo del contenido
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        // ✅ Añade insets seguros por si la barra del sistema es transparente
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onPrev,
                        enabled = screenIndex > 0,
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Anterior")
                    }

                    // Lógica para ocultar "Siguiente" solo en la intro del examen (que tiene su propio botón)
                    if (type != "final_exam_intro") {
                        Button(onClick = onNext) {
                            Text(if (isLastScreen) "Finalizar" else "Siguiente")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    ) { paddingValues ->
        // Contenido Principal
        Column(
            modifier = Modifier
                .padding(paddingValues) // Respeta el espacio de los botones
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Contenedor del contenido específico
            Box(modifier = Modifier.weight(1f)) {
                when (type) {
                    // Tipos básicos
                    "story_hook" -> RenderStoryHook(contentData)
                    "theory_tabs" -> RenderTheoryTabs(contentData)
                    "theory_section" -> RenderTheorySection(contentData)
                    "interactive_concept" -> RenderInteractiveConcept(contentData)
                    "quiz" -> RenderQuiz(contentData)

                    // Tipos Avanzados e Interactivos
                    "practical_exercise" -> RenderPracticalExercise(contentData)
                    "final_exam_intro" -> RenderFinalExamIntro(contentData, onStartExam = onNext)
                    "architecture_design" -> RenderArchitectureDesign(contentData)
                    "interactive_exercise" -> RenderInteractiveExercise(contentData)
                    "analysis_questions" -> RenderAnalysisQuestions(contentData)
                    "control_assessment" -> RenderControlAssessment(contentData)
                    "theoretical_questions" -> RenderTheoreticalQuestions(contentData)
                    "final_deliverable" -> RenderFinalDeliverable(contentData)
                    "pcap_analysis" -> RenderPcapAnalysis(contentData)

                    else -> {
                        Text("Tipo de contenido no soportado: $type", color = Color.Red)
                    }
                }
            }
        }
    }
}

// ==========================================
// RENDERIZADORES (SIN CAMBIOS, SOLO COPIAR EL RESTO)
// ==========================================

@Composable
fun RenderStoryHook(content: JsonObject) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            content["image_url"]?.jsonPrimitive?.content?.let { url ->
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    }
                }
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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Text(content["description"]?.jsonPrimitive?.content ?: "", modifier = Modifier.padding(bottom = 16.dp)) }
        items(concepts?.size ?: 0) { index ->
            val c = concepts!![index].jsonObject
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(c["name"]?.jsonPrimitive?.content ?: "", fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
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
}

@Composable
fun RenderPracticalExercise(content: JsonObject) {
    val instruction = content["instruction"]?.jsonPrimitive?.content ?: ""
    val techniques = content["techniques"]?.jsonArray
    val tasks = content["tasks"]?.jsonArray

    var completedTasks by remember { mutableStateOf(setOf<String>()) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.width(12.dp))
                    Text(instruction, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (techniques != null) {
            item {
                Text("Técnicas MITRE ATT&CK®", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
            }
            items(techniques.size) { index ->
                val tech = techniques[index].jsonObject
                val name = tech["technique_name"]?.jsonPrimitive?.content ?: ""
                val id = tech["technique_id"]?.jsonPrimitive?.content ?: ""

                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(name, fontWeight = FontWeight.Bold)
                        Text(id, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (tasks != null) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Tareas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(tasks.size) { index ->
                val t = tasks[index].jsonObject
                val taskText = t["task"]?.jsonPrimitive?.content ?: ""
                val taskId = index.toString()
                val isCompleted = completedTasks.contains(taskId)

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                        completedTasks = if (isCompleted) completedTasks - taskId else completedTasks + taskId
                    },
                    colors = CardDefaults.cardColors(containerColor = if (isCompleted) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCompleted, onCheckedChange = null)
                        Text(taskText, modifier = Modifier.padding(start = 8.dp), color = if (isCompleted) Color.Black else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun RenderFinalExamIntro(content: JsonObject, onStartExam: () -> Unit) {
    val description = content["description"]?.jsonPrimitive?.content ?: ""
    val timeLimit = content["time_limit"]?.jsonPrimitive?.content ?: "45 min"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Icon(Icons.Default.Assignment, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Examen Final", style = MaterialTheme.typography.headlineMedium)
            Text(description, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))

            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row {
                        Icon(Icons.Default.Timer, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tiempo límite: $timeLimit")
                    }
                }
            }

            Button(
                onClick = onStartExam,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("COMENZAR EXAMEN")
            }
        }
    }
}

@Composable
fun RenderArchitectureDesign(content: JsonObject) {
    val scenario = content["scenario"]?.jsonPrimitive?.content ?: ""
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("Diseño de Arquitectura", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(scenario)
        }
    }
}

@Composable
fun RenderInteractiveExercise(content: JsonObject) {
    val description = content["description"]?.jsonPrimitive?.content ?: ""
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Text(description) }
    }
}

@Composable
fun RenderAnalysisQuestions(content: JsonObject) {
    val context = content["context"]?.jsonPrimitive?.content ?: ""
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Text("Preguntas de Análisis", fontWeight = FontWeight.Bold) }
        item { Text(context) }
    }
}

@Composable
fun RenderControlAssessment(content: JsonObject) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Text("Evaluación de Controles") }
    }
}

@Composable
fun RenderTheoreticalQuestions(content: JsonObject) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Text("Preguntas Teóricas") }
    }
}

@Composable
fun RenderFinalDeliverable(content: JsonObject) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Text("Entregable Final") }
    }
}

@Composable
fun RenderPcapAnalysis(content: JsonObject) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Text("Análisis PCAP") }
    }
}