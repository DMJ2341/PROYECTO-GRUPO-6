package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cyberlearnapp.network.models.Course

@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen o Icono Placeholder
            if (course.imageUrl != null) {
                AsyncImage(
                    model = course.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            } else {
                Text("📚", style = MaterialTheme.typography.displayMedium)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(course.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    course.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Nivel: ${course.level} • +${course.xpReward} XP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}