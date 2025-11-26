package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ MEJORA: Contenedor con Icono Inteligente
            // Usamos un Surface coloreado para darle estilo al icono
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getIconForCourse(course.title),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del Curso
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badges de Nivel y XP
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = { /* No action */ },
                        label = { Text(course.level) },
                        modifier = Modifier.height(24.dp),
                        border = null,
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "+${course.xpReward} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Flecha indicadora pequeña
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ✅ Lógica simple para asignar iconos según el tema del curso
fun getIconForCourse(title: String): ImageVector {
    val t = title.lowercase()
    return when {
        "introduc" in t -> Icons.Default.School
        "red" in t -> Icons.Default.Security      // Red Team -> Seguridad
        "blue" in t -> Icons.Default.Shield       // Blue Team -> Escudo
        "purple" in t -> Icons.Default.CompareArrows // Purple -> Mezcla
        "phishing" in t -> Icons.Default.Mail     // Phishing -> Correo
        "malware" in t -> Icons.Default.BugReport // Malware -> Bicho
        "web" in t -> Icons.Default.Language      // Web -> Mundo/Red
        "forense" in t -> Icons.Default.Search    // Forense -> Lupa
        "crypto" in t -> Icons.Default.VpnKey     // Cripto -> Llave
        else -> Icons.Default.MenuBook            // Por defecto -> Libro
    }
}