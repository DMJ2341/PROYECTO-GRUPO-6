package com.example.cyberlearnapp.ui.screens.lessons.shared

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 🎨 PALETA DE COLORES CYBER
 */
object CyberColors {
    val NeonGreen = Color(0xFF00FF41)
    val NeonBlue = Color(0xFF00D9FF)
    val NeonPink = Color(0xFFFF006E)
    val DarkBg = Color(0xFF0A0E27)
    val CardBg = Color(0xFF1A1F3A)
    val BorderGlow = Color(0xFF00FF41).copy(alpha = 0.5f)
}

/**
 * 🃏 TARJETA DE IMPACTO (Story Hook)
 * Usada en pantallas tipo "CASO REAL"
 */
@Composable
fun ImpactCard(
    icon: String,
    value: String,
    label: String,
    detail: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { if (detail != null) expanded = !expanded }
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = CyberColors.CardBg
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono grande
            Text(
                text = icon,
                fontSize = 40.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Valor principal (número grande)
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )

            // Label
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            // Detalle expandible
            if (expanded && detail != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "→ $detail",
                    fontSize = 12.sp,
                    color = CyberColors.NeonBlue,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * 🎯 BOTÓN CYBER (con borde neón animado)
 */
@Composable
fun CyberButton(
    text: String,
    icon: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .border(
                width = 2.dp,
                color = CyberColors.NeonGreen.copy(alpha = alpha),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = CyberColors.CardBg,
            disabledContainerColor = CyberColors.CardBg.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) CyberColors.NeonGreen else Color.Gray
            )
        }
    }
}

/**
 * 📦 CATEGORY CHIP (para clasificadores)
 */
@Composable
fun CategoryChip(
    name: String,
    icon: String,
    color: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .shadow(if (isSelected) 12.dp else 4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.3f) else CyberColors.CardBg
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else Color.White
            )
        }
    }
}

/**
 * ✅ FEEDBACK MESSAGE (correcto/incorrecto)
 */
@Composable
fun FeedbackMessage(
    isCorrect: Boolean,
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect)
                CyberColors.NeonGreen.copy(alpha = 0.2f)
            else
                CyberColors.NeonPink.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isCorrect) "✅" else "❌",
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * 📊 STAT DISPLAY (para resúmenes)
 */
@Composable
fun StatDisplay(
    icon: String,
    percentage: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = percentage,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonGreen
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 🎮 SCREEN CONTAINER (wrapper para todas las pantallas)
 */
@Composable
fun ScreenContainer(
    title: String,
    screenNumber: Int,
    totalScreens: Int,
    onNext: () -> Unit,
    buttonText: String = "Siguiente",
    buttonEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBg)
            .padding(16.dp)
    ) {
        // Header con título
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Indicador de progreso
        LinearProgressIndicator(
            progress = screenNumber.toFloat() / totalScreens,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = CyberColors.NeonGreen,
            trackColor = CyberColors.CardBg
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Contenido de la pantalla (scrolleable)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de continuar
        CyberButton(
            text = buttonText,
            icon = "→",
            onClick = onNext,
            enabled = buttonEnabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}