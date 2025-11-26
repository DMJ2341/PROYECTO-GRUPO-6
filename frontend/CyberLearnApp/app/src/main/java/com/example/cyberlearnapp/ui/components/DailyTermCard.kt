package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cyberlearnapp.network.models.DailyTermWrapper

@Composable
fun DailyTermCard(wrapper: DailyTermWrapper) {
    val term = wrapper.dailyTerm
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44)) // Color oscuro "Cyber"
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Término del Día", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                Badge { Text(term.category) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = term.term,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = term.definition,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB0BEC5)
            )
            if (wrapper.xpEarned > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("+${wrapper.xpEarned} XP ganados", color = Color.Yellow, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}