package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ✅ SOLUCIÓN: Definimos un modelo UI simple aquí mismo
data class ProgressData(
    val name: String,
    val level: Int,
    val xpTotal: Int,
    val lessonsCompleted: Int,
    val badgesCount: Int,
    val streak: Int
)

@Composable
fun ProgressCard(
    data: ProgressData, // Recibimos el nuevo modelo de datos
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        // Usamos colores seguros de Material 3
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "¡Hola, ${data.name}! 👋",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Nivel ${data.level} - Hacker novato",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = data.xpTotal.toString(),
                    label = "XP Total"
                )
                StatItem(
                    value = data.lessonsCompleted.toString(),
                    label = "Lecciones"
                )
                StatItem(
                    value = data.badgesCount.toString(),
                    label = "Insignias"
                )
            }

            // Racha
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFFFFC107).copy(alpha = 0.1f)) // Color Ámbar (Warning)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔥", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = "Racha de ${data.streak} días",
                        color = Color(0xFFFFC107), // Warning Color
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¡Sigue así!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary, // Color primario (Cyan/Purple)
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}