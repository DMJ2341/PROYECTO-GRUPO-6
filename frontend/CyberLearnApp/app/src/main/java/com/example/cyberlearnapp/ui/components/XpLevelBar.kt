package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun XpLevelBar(currentXp: Int, level: Int) {
    val xpForNextLevel = level * 100 // Ejemplo simple
    val progress = (currentXp % 100).toFloat() / 100f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Nivel $level", fontWeight = FontWeight.Bold)
            Text("$currentXp XP Total")
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            "${(currentXp % 100)} / 100 XP para nivel ${level + 1}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}