
package com.example.cyberlearnapp.network.models

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.DailyTermWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTermCard(
    wrapper: DailyTermWrapper?,
    onComplete: (Int) -> Unit
) {
    if (wrapper == null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        return
    }

    val term = wrapper.term
    val isCompleted = wrapper.alreadyViewedToday
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFF1B5E20) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = term.term,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                if (!isCompleted) {
                    AssistChip(
                        onClick = { },
                        label = { Text("+$wrapper.xpReward XP") },
                        leadingIcon = {
                            Icon(Icons.Default.Star, null, tint = Color.Yellow)
                        }
                    )
                } else {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                    Spacer(Modifier.width(8.dp))
                    Text("Completado", color = Color(0xFF4CAF50))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = term.definition,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) 10 else 3,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(visible = expanded && term.example != null) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text("Ejemplo: ${term.example}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (!isCompleted) {
                Button(
                    onClick = { onComplete(term.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("¡Aprendido! Ganar +${wrapper.xpReward} XP")
                }
            }
        }
    }
}