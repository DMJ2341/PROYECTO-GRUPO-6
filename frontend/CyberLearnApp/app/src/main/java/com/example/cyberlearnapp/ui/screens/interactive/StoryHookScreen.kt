package com.example.cyberlearnapp.ui.screens.interactive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.network.models.LessonScreen

@Composable
fun StoryHookScreen(
    screen: LessonScreen,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7F1D1D),
                        Color(0xFF991B1B)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text = screen.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Icono/Emoji grande
            Text(
                text = "📧",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Historia
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val content = screen.content as? Map<*, *>

                    Text(
                        text = content?.get("story") as? String ?: "",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quote
                    content?.get("quote")?.let { quote ->
                        Text(
                            text = "💭 \"${quote}\"",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Hook question
            val content = screen.content as? Map<*, *>
            content?.get("hook_question")?.let { question ->
                Text(
                    text = question as String,
                    color = Color(0xFFFBBF24),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón CTA
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
                    text = screen.ctaButton ?: "Continuar →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}