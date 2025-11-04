package com.example.cyberlearnapp.ui.screens.interactive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cyberlearnapp.network.models.LessonScreen
import com.example.cyberlearnapp.network.models.Signal

@Composable
fun InteractiveEmailScreen(
    screen: LessonScreen,
    signalsFound: List<Int>,
    onSignalFound: (Int, Int) -> Unit,
    onNext: () -> Unit
) {
    var showFeedback by remember { mutableStateOf<Signal?>(null) }
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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = screen.subtitle ?: "",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Email card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val emailData = screen.emailData

                // From (clicable)
                EmailField(
                    label = "De:",
                    value = emailData?.from ?: "",
                    signal = screen.signals?.find { it.element == "from" },
                    signalsFound = signalsFound,
                    onSignalFound = onSignalFound,
                    onShowFeedback = { showFeedback = it }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // To
                Text(
                    text = "Para: ${emailData?.to ?: ""}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Subject (clicable)
                EmailField(
                    label = "Asunto:",
                    value = emailData?.subject ?: "",
                    signal = screen.signals?.find { it.element == "subject" },
                    signalsFound = signalsFound,
                    onSignalFound = onSignalFound,
                    onShowFeedback = { showFeedback = it }
                )

                Divider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 2.dp
                )

                // Body (con partes clicables)
                EmailBody(
                    body = emailData?.body ?: "",
                    signals = screen.signals ?: emptyList(),
                    signalsFound = signalsFound,
                    onSignalFound = onSignalFound,
                    onShowFeedback = { showFeedback = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📊 Señales encontradas:",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${signalsFound.size}/${screen.totalSignals}",
                        color = Color(0xFF60A5FA),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = signalsFound.size.toFloat() / (screen.totalSignals ?: 8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF60A5FA),
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                if (signalsFound.size < (screen.totalSignals ?: 8)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = screen.hint ?: "",
                        color = Color(0xFFFBBF24),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón continuar (solo si encontró suficientes)
        if (signalsFound.size >= 4) {
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
                    text = "Continuar →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Feedback Dialog
    showFeedback?.let { signal ->
        FeedbackDialog(
            signal = signal,
            onDismiss = { showFeedback = null }
        )
    }
}

@Composable
fun EmailField(
    label: String,
    value: String,
    signal: Signal?,
    signalsFound: List<Int>,
    onSignalFound: (Int, Int) -> Unit,
    onShowFeedback: (Signal) -> Unit
) {
    val isFound = signal?.let { signalsFound.contains(it.id) } ?: false

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (signal != null && !isFound) {
                    Modifier
                        .border(
                            width = 2.dp,
                            color = Color(0xFF60A5FA).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onSignalFound(signal.id, signal.xp)
                            onShowFeedback(signal)
                        }
                        .padding(8.dp)
                } else if (isFound) {
                    Modifier
                        .background(
                            Color(0xFF10B981).copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                } else {
                    Modifier.padding(8.dp)
                }
            )
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color.Black
        )
        if (isFound) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "✅", fontSize = 12.sp)
        } else if (signal != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "👆", fontSize = 12.sp)
        }
    }
}

@Composable
fun EmailBody(
    body: String,
    signals: List<Signal>,
    signalsFound: List<Int>,
    onSignalFound: (Int, Int) -> Unit,
    onShowFeedback: (Signal) -> Unit
) {
    Column {
        val lines = body.split("\n")

        lines.forEach { line ->
            // Buscar si esta línea contiene una señal
            val signal = signals.find { sig ->
                when (sig.element) {
                    "greeting" -> line.contains("Estimado")
                    "typo" -> line.contains("sospechoza")
                    "threat" -> line.contains("bloqueada")
                    "url" -> line.contains("http")
                    else -> false
                }
            }

            val isFound = signal?.let { signalsFound.contains(it.id) } ?: false

            if (signal != null && !isFound) {
                Text(
                    text = "$line 👆",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFF60A5FA).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            onSignalFound(signal.id, signal.xp)
                            onShowFeedback(signal)
                        }
                        .padding(8.dp)
                )
            } else if (isFound) {
                Text(
                    text = "$line ✅",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF10B981).copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp)
                )
            } else {
                Text(
                    text = line,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun FeedbackDialog(
    signal: Signal,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✅ ¡EXCELENTE!",
                    color = Color(0xFF10B981),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🚩 ${signal.name}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                signal.correctValue?.let { correct ->
                    Text(
                        text = "✅ Correcto: $correct",
                        color = Color(0xFF10B981),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "💡 ${signal.explanation}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🎁 +${signal.xp} XP",
                    color = Color(0xFFFBBF24),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF60A5FA)
                    )
                ) {
                    Text("Continuar buscando →")
                }
            }
        }
    }
}