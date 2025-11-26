package com.example.cyberlearnapp.ui.screens.final_exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.network.models.assessments.FinalExamQuestion
import com.example.cyberlearnapp.ui.components.ExamTimer
import com.example.cyberlearnapp.viewmodel.FinalExamViewModel
import kotlinx.coroutines.delay

@Composable
fun FinalExamScreen(
    navController: NavController,
    vm: FinalExamViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    // Timer de 60 minutos
    var secondsLeft by remember { mutableStateOf(3600) }

    LaunchedEffect(Unit) {
        vm.startExam()
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        // Auto-submit si se acaba el tiempo
        if (!state.submitting && state.result == null) {
            vm.submit(navController)
        }
    }

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Timer
        ExamTimer(secondsLeft = secondsLeft)

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de preguntas
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.questions) { question ->
                ExamQuestionItem(
                    question = question,
                    selectedOptionId = state.answers[question.id.toString()],
                    onOptionSelected = { optionId ->
                        vm.selectAnswer(question.id.toString(), optionId)
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { vm.submit(navController) },
                    enabled = !state.submitting,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.submitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("ENVIAR EXAMEN FINAL", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ExamQuestionItem(
    question: FinalExamQuestion,
    selectedOptionId: String?,
    onOptionSelected: (String) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // O Surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question.question_text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))

            question.options?.forEach { option ->
                val isSelected = selectedOptionId == option.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFFE3F2FD) else Color.Transparent)
                        .border(1.dp, if (isSelected) Color(0xFF2196F3) else Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { onOptionSelected(option.id) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null // El click lo maneja el Row
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}