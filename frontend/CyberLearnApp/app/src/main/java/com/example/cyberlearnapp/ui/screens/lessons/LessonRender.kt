package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// ✅ CAMBIO: Usamos LessonResponse que tiene los detalles completos (screens)
import com.example.cyberlearnapp.network.models.LessonResponse
// ✅ IMPORT CORRECTO: Solo la función, quitamos 'IconHelper' que no existe como clase
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