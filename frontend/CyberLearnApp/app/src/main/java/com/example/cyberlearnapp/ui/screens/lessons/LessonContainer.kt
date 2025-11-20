package com.example.cyberlearnapp.ui.screens.lessons

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberlearnapp.ui.screens.lessons.shared.CyberColors

/**
 * Contenedor principal para todas las lecciones
 * Incluye: Header, Barra de progreso, Contenido scrolleable, Navegación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonContainer(
    lessonTitle: String,
    currentScreen: Int,
    totalScreens: Int,
    canGoBack: Boolean = true,
    onBack: () -> Unit = {},
    onExit: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val progress = if (totalScreens > 0) currentScreen.toFloat() / totalScreens else 0f

    Scaffold(
        topBar = {
            LessonTopBar(
                lessonTitle = lessonTitle,
                canGoBack = canGoBack,
                onBack = if (canGoBack) onBack else null,
                onExit = { showExitDialog = true }
            )
        },
        containerColor = CyberColors.DarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de progreso
            LessonProgressBar(
                currentScreen = currentScreen,
                totalScreens = totalScreens,
                progress = progress
            )

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }

    // Diálogo de confirmación de salida
    if (showExitDialog) {
        ExitLessonDialog(
            onConfirm = {
                showExitDialog = false
                onExit()
            },
            onDismiss = { showExitDialog = false }
        )
    }
}

/**
 * Top Bar de la lección
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonTopBar(
    lessonTitle: String,
    canGoBack: Boolean,
    onBack: (() -> Unit)?,
    onExit: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = lessonTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen,
                maxLines = 1
            )
        },
        navigationIcon = {
            if (canGoBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onExit) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Salir",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CyberColors.CardBg
        )
    )
}

/**
 * Barra de progreso de la lección
 */
@Composable
private fun LessonProgressBar(
    currentScreen: Int,
    totalScreens: Int,
    progress: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberColors.CardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pantalla $currentScreen de $totalScreens",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Barra de progreso animada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CyberColors.DarkBg)
        ) {
            AnimatedContent(
                targetState = progress,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "progress_animation"
            ) { targetProgress ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(targetProgress)
                        .background(CyberColors.NeonGreen)
                )
            }
        }
    }
}

/**
 * Diálogo de confirmación para salir de la lección
 */
@Composable
private fun ExitLessonDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("⚠️", fontSize = 40.sp)
        },
        title = {
            Text(
                text = "¿Salir de la lección?",
                fontWeight = FontWeight.Bold,
                color = CyberColors.NeonGreen
            )
        },
        text = {
            Text(
                text = "Tu progreso actual se guardará, pero perderás la racha de aprendizaje.",
                color = Color.White.copy(alpha = 0.9f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberColors.NeonPink
                )
            ) {
                Text("Salir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continuar", color = CyberColors.NeonGreen)
            }
        },
        containerColor = CyberColors.CardBg,
        textContentColor = Color.White
    )
}

/**
 * Variante simplificada sin TopBar (para pantallas individuales)
 */
@Composable
fun SimpleScreenContainer(
    currentScreen: Int,
    totalScreens: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBg)
    ) {
        // Solo barra de progreso
        LessonProgressBar(
            currentScreen = currentScreen,
            totalScreens = totalScreens,
            progress = if (totalScreens > 0) currentScreen.toFloat() / totalScreens else 0f
        )

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            content()
        }
    }
}