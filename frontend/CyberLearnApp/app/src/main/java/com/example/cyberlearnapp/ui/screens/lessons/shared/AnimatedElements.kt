package com.example.cyberlearnapp.ui.screens.lessons.shared

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Icono con animación de pulso
 */
@Composable
fun PulsingIcon(
    icon: String,
    size: Dp = 48.dp,
    color: Color = CyberColors.NeonGreen,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_pulse"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_pulse"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = (size.value * 0.6).sp,
            color = color
        )
    }
}

/**
 * Efecto de brillo/glow alrededor de un composable
 */
@Composable
fun GlowEffect(
    color: Color = CyberColors.NeonGreen,
    radius: Dp = 20.dp,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box {
        // Capa de brillo
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = alpha),
                            Color.Transparent
                        ),
                        radius = radius.value
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // Contenido principal
        content()
    }
}

/**
 * Contador animado que incrementa de 0 a un valor
 */
@Composable
fun CounterAnimation(
    targetValue: Int,
    duration: Int = 2000,
    prefix: String = "",
    suffix: String = "",
    fontSize: Int = 32,
    color: Color = CyberColors.NeonGreen,
    modifier: Modifier = Modifier
) {
    var currentValue by remember { mutableStateOf(0) }

    LaunchedEffect(targetValue) {
        val increment = targetValue / (duration / 16) // 60 FPS aprox
        var value = 0
        while (value < targetValue) {
            value = (value + increment).coerceAtMost(targetValue)
            currentValue = value
            kotlinx.coroutines.delay(16)
        }
        currentValue = targetValue
    }

    Text(
        text = "$prefix$currentValue$suffix",
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}

/**
 * Barra de carga animada
 */
@Composable
fun LoadingBar(
    progress: Float,
    height: Dp = 8.dp,
    backgroundColor: Color = CyberColors.CardBg,
    foregroundColor: Color = CyberColors.NeonGreen,
    animated: Boolean = true,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (animated) progress else progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "loading_progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor, RoundedCornerShape(height / 2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                .background(foregroundColor, RoundedCornerShape(height / 2))
        )
    }
}

/**
 * Texto con efecto de typing (como si se estuviera escribiendo)
 */
@Composable
fun TypingText(
    fullText: String,
    typingSpeed: Long = 50L, // milisegundos por carácter
    fontSize: Int = 14,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(fullText) {
        displayedText = ""
        fullText.forEachIndexed { index, char ->
            kotlinx.coroutines.delay(typingSpeed)
            displayedText = fullText.substring(0, index + 1)
        }
    }

    Text(
        text = displayedText,
        fontSize = fontSize.sp,
        color = color,
        modifier = modifier
    )
}

/**
 * Indicador de carga circular con pulso
 */
@Composable
fun PulsingLoader(
    size: Dp = 60.dp,
    color: Color = CyberColors.NeonGreen,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loader_scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loader_rotation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * scale)
                .background(color.copy(alpha = 0.3f), CircleShape)
        )

        Box(
            modifier = Modifier
                .size(size * 0.6f)
                .background(color, CircleShape)
        )
    }
}

/**
 * Shake animation para errores
 */
@Composable
fun ShakeAnimation(
    trigger: Boolean,
    onAnimationEnd: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            val shakePattern = listOf(0f, -10f, 10f, -8f, 8f, -5f, 5f, 0f)
            shakePattern.forEach { offset ->
                offsetX = offset
                kotlinx.coroutines.delay(50)
            }
            offsetX = 0f
            onAnimationEnd()
        }
    }

    Box(modifier = Modifier.offset(x = offsetX.dp)) {
        content()
    }
}

/**
 * Success checkmark animada
 */
@Composable
fun SuccessCheckmark(
    size: Dp = 80.dp,
    color: Color = CyberColors.NeonGreen,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkmark_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.9f)
                .background(color.copy(alpha = 0.2f), CircleShape)
        )

        Text(
            text = "✓",
            fontSize = (size.value * 0.6).sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Error X animada
 */
@Composable
fun ErrorCross(
    size: Dp = 80.dp,
    color: Color = CyberColors.NeonPink,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cross_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.9f)
                .background(color.copy(alpha = 0.2f), CircleShape)
        )

        Text(
            text = "✕",
            fontSize = (size.value * 0.6).sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}