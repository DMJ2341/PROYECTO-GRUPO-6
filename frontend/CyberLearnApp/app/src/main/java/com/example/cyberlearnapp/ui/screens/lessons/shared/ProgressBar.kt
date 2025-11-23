package com.example.cyberlearnapp.ui.screens.lessons.shared

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Barra de progreso personalizada con estilo cyber
 */
@Composable
fun CyberProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    showPercentage: Boolean = true,
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = if (animated) 500 else 0),
        label = "progress_animation"
    )

    Column(modifier = modifier) {
        if (showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progreso",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.NeonGreen
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(CyberColors.DarkBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                CyberColors.NeonGreen,
                                CyberColors.NeonBlue
                            )
                        ),
                        shape = RoundedCornerShape(height / 2)
                    )
            )
        }
    }
}

/**
 * Barra de progreso con etapas/milestones
 */
@Composable
fun MilestoneProgressBar(
    currentMilestone: Int,
    totalMilestones: Int,
    milestoneLabels: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    val progress = currentMilestone.toFloat() / totalMilestones.coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        // Barra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CyberColors.CardBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(CyberColors.NeonGreen, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Puntos de milestone
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(totalMilestones) { index ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (index < currentMilestone)
                                    CyberColors.NeonGreen
                                else
                                    CyberColors.CardBg,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < currentMilestone) {
                            Text("✓", fontSize = 10.sp, color = Color.Black)
                        }
                    }

                    if (index < milestoneLabels.size) {
                        Text(
                            text = milestoneLabels[index],
                            fontSize = 8.sp,
                            color = if (index < currentMilestone)
                                CyberColors.NeonGreen
                            else
                                Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Barra circular de progreso
 */
@Composable
fun CircularProgressIndicator(
    progress: Float,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    showPercentage: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.size(size),
            strokeWidth = strokeWidth,
            color = CyberColors.NeonGreen,
            trackColor = CyberColors.CardBg
        )

        if (showPercentage) {
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )
        }
    }
}