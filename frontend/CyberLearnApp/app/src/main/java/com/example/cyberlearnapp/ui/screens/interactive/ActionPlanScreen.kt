package com.example.cyberlearnapp.ui.screens.interactive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.ActionStep
import com.example.cyberlearnapp.network.models.LessonScreen

@Composable
fun ActionPlanScreen(
    screen: LessonScreen,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()

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

        Text(
            text = "SI RECIBES UN CORREO SOSPECHOSO:",
            color = Color(0xFFFBBF24),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Pasos
        screen.steps?.forEach { step ->
            ActionStepCard(step = step)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Reminder
        screen.reminder?.let { reminder ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B5CF6).copy(alpha = 0.2f)
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
                        text = reminder,
                        color = Color(0xFFA78BFA),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
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
                containerColor = Color(0xFF10B981)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Entendido, ir al quiz →",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ActionStepCard(step: ActionStep) {
    val cardColor = when (step.type) {
        "dont" -> Color(0xFFDC2626).copy(alpha = 0.2f)
        "do" -> Color(0xFF10B981).copy(alpha = 0.2f)
        else -> Color(0xFF1E293B)
    }

    val accentColor = when (step.type) {
        "dont" -> Color(0xFFEF4444)
        "do" -> Color(0xFF10B981)
        else -> Color(0xFF60A5FA)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Número y emoji
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = accentColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.icon,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "PASO ${step.number}",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = step.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Acciones
            step.actions.forEach { action ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = if (step.type == "dont") "❌" else "✓",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = action,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}