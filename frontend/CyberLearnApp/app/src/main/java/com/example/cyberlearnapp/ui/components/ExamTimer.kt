package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExamTimer(secondsLeft: Int) {
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    Text(
        text = "Tiempo restante: ${minutes}:${seconds.toString().padStart(2, '0')}",
        style = MaterialTheme.typography.titleMedium,
        color = if (secondsLeft < 300) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(16.dp)
    )
}