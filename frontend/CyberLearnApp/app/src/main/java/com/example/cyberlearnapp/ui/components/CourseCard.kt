package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ ICONO CON GRADIENTE/COLOR DINÁMICO
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = getColorForCourse(course.title),
                modifier = Modifier.size(64.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = getIconForCourse(course.title),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ✅ INFORMACIÓN DEL CURSO
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Descripción
                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ BADGES DE NIVEL Y XP
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip de Nivel
                    AssistChip(
                        onClick = { /* No action */ },
                        label = {
                            Text(
                                text = course.level,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (course.level.lowercase()) {
                                    "principiante" -> Icons.Default.Stars
                                    "intermedio" -> Icons.Default.TrendingUp
                                    "avanzado" -> Icons.Default.Whatshot
                                    else -> Icons.Default.School
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = null
                    )

                    // XP Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "+${course.xpReward} XP",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ✅ FLECHA INDICADORA
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ✅ COLORES DINÁMICOS SEGÚN EL CURSO
fun getColorForCourse(title: String): Color {
    val t = title.lowercase()
    return when {
        "fundamento" in t || "introduc" in t -> Color(0xFF6366F1) // Índigo
        "red" in t && "seguridad" in t -> Color(0xFFEF4444) // Rojo (Red Team)
        "identidad" in t || "acceso" in t -> Color(0xFF8B5CF6) // Púrpura (IAM)
        "respuesta" in t || "incidente" in t -> Color(0xFFF59E0B) // Ámbar (IR)
        "riesgo" in t || "gestión" in t -> Color(0xFF10B981) // Verde (Risk)
        "web" in t -> Color(0xFF06B6D4) // Cyan (Web)
        "red" in t || "protocolo" in t -> Color(0xFF3B82F6) // Azul (Network)
        "forense" in t -> Color(0xFFA855F7) // Púrpura oscuro
        "crypto" in t -> Color(0xFF14B8A6) // Teal
        "cloud" in t -> Color(0xFF0EA5E9) // Sky
        else -> Color(0xFF6366F1) // Índigo por defecto
    }
}

// ✅ ICONOS INTELIGENTES SEGÚN EL TEMA
fun getIconForCourse(title: String): ImageVector {
    val t = title.lowercase()
    return when {
        "fundamento" in t || "introduc" in t -> Icons.Default.School
        "red" in t && "seguridad" in t -> Icons.Default.Security
        "identidad" in t || "acceso" in t -> Icons.Default.VpnKey
        "respuesta" in t || "incidente" in t -> Icons.Default.LocalFireDepartment
        "riesgo" in t || "gestión" in t -> Icons.Default.Assessment
        "web" in t -> Icons.Default.Language
        "red" in t || "protocolo" in t -> Icons.Default.Router
        "forense" in t -> Icons.Default.Search
        "crypto" in t -> Icons.Default.Lock
        "cloud" in t -> Icons.Default.Cloud
        "phishing" in t -> Icons.Default.Mail
        "malware" in t -> Icons.Default.BugReport
        else -> Icons.Default.MenuBook
    }
}
