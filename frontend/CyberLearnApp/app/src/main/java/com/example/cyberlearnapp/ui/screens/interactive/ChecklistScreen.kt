package com.example.cyberlearnapp.ui.screens.interactive

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.LessonScreen
import kotlinx.coroutines.delay

@Composable
fun ChecklistScreen(
    screen: LessonScreen,
    signalsFound: List<Int>,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()
    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animateItems = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Título
        Text(
            text = screen.title,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Contador de señales encontradas
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Encontraste:",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${signalsFound.size}/${screen.items?.size ?: 8}",
                    color = Color(0xFF60A5FA),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (signalsFound.size >= 6) "✨" else "🔍",
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de señales
        screen.items?.forEachIndexed { index, item ->
            val isFound = signalsFound.contains(item.id)

            AnimatedChecklistItem(
                item = item,
                isFound = isFound,
                shouldAnimate = animateItems,
                delay = index * 100L
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tip
        screen.tip?.let { tip ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFBBF24).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tip,
                        color = Color(0xFFFBBF24),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón continuar
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF60A5FA)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Aprender a reaccionar →",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AnimatedChecklistItem(
    item: com.example.cyberlearnapp.network.models.ChecklistItem,
    isFound: Boolean,
    shouldAnimate: Boolean,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            kotlinx.coroutines.delay(delay)
            visible = true
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = if (isFound)
                Color(0xFF10B981).copy(alpha = 0.2f)
            else
                Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono check/circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (isFound)
                            Color(0xFF10B981)
                        else
                            Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isFound) "✓" else "",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = if (isFound) Color(0xFF10B981) else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}