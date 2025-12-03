package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.Course

@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit
) {
    val courseColor = getColorForCourse(course.title)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332) // Azul oscuro en lugar de gris
        ),
        border = BorderStroke(2.dp, courseColor.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            courseColor.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp), // ✅ Padding uniforme
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ ICONO CON GRADIENTE Y SOMBRA
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = courseColor
                    ),
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = getIconForCourse(course.title),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // ✅ INFORMACIÓN DEL CURSO
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Título en BLANCO con peso BLACK
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Descripción en blanco más suave
                    Text(
                        text = course.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White.copy(0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // ✅ MÁS ESPACIO para los chips

                    // ✅ BADGES CON COLORES VIBRANTES - ARREGLADO
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth() // ✅ Ocupar todo el ancho disponible
                    ) {
                        // Chip de Nivel con emoji
                        val (levelEmoji, levelColor) = when (course.level.lowercase()) {
                            "principiante" -> "🌱" to Color(0xFF10B981) // Verde
                            "intermedio" -> "⚡" to Color(0xFFF59E0B) // Ámbar
                            "avanzado" -> "🔥" to Color(0xFFEF4444) // Rojo
                            else -> "📚" to Color(0xFF6366F1) // Índigo
                        }

                        Surface(
                            color = levelColor.copy(0.25f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, levelColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), // ✅ Padding reducido
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = levelEmoji,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = course.level,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // XP Badge con color dorado
                        Surface(
                            color = Color(0xFFFBBF24).copy(0.25f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFFBBF24))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), // ✅ Padding reducido
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = "⭐",
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "+${course.xpReward} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // ✅ FLECHA CON COLOR
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = courseColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ✅ COLORES VIBRANTES SEGÚN EL CURSO
fun getColorForCourse(title: String): Color {
    val t = title.lowercase()
    return when {
        "fundamento" in t || "introduc" in t -> Color(0xFF6366F1) // Índigo vibrante
        "red" in t && "seguridad" in t || "defensa" in t -> Color(0xFFEF4444) // Rojo intenso
        "identidad" in t || "acceso" in t || "iam" in t -> Color(0xFF8B5CF6) // Púrpura
        "respuesta" in t || "incidente" in t -> Color(0xFFF59E0B) // Ámbar
        "riesgo" in t || "gestión" in t -> Color(0xFF10B981) // Verde esmeralda
        "web" in t -> Color(0xFF06B6D4) // Cyan brillante
        "red" in t || "protocolo" in t -> Color(0xFF3B82F6) // Azul eléctrico
        "forense" in t -> Color(0xFFA855F7) // Púrpura brillante
        "crypto" in t || "criptograf" in t -> Color(0xFF14B8A6) // Teal
        "cloud" in t || "nube" in t -> Color(0xFF0EA5E9) // Azul cielo
        "phishing" in t -> Color(0xFFEC4899) // Rosa
        "malware" in t -> Color(0xFFDC2626) // Rojo oscuro
        else -> Color(0xFF00D9FF) // Cyan por defecto
    }
}

// ✅ ICONOS INTELIGENTES
fun getIconForCourse(title: String): ImageVector {
    val t = title.lowercase()
    return when {
        "fundamento" in t || "introduc" in t -> Icons.Default.School
        "red" in t && "seguridad" in t || "defensa" in t -> Icons.Default.Security
        "identidad" in t || "acceso" in t || "iam" in t -> Icons.Default.VpnKey
        "respuesta" in t || "incidente" in t -> Icons.Default.LocalFireDepartment
        "riesgo" in t || "gestión" in t -> Icons.Default.Assessment
        "web" in t -> Icons.Default.Language
        "red" in t || "protocolo" in t -> Icons.Default.Router
        "forense" in t -> Icons.Default.Search
        "crypto" in t || "criptograf" in t -> Icons.Default.Lock
        "cloud" in t || "nube" in t -> Icons.Default.Cloud
        "phishing" in t -> Icons.Default.Mail
        "malware" in t -> Icons.Default.BugReport
        else -> Icons.Default.MenuBook
    }
}