package com.example.cyberlearnapp.ui.screens.lessons.shared

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/**
 * Estado de un elemento arrastrable
 */
data class DragState(
    val isDragging: Boolean = false,
    val offset: Offset = Offset.Zero,
    val draggedItemId: String? = null
)

/**
 * Elemento que puede ser arrastrado
 */
@Composable
fun DraggableItem(
    id: String,
    content: String,
    icon: String? = null,
    isDragging: Boolean,
    onDragStart: (String) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var itemPosition by remember { mutableStateOf(Offset.Zero) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                itemPosition = coordinates.positionInRoot()
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDragStart(id)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd()
                    }
                )
            }
            .graphicsLayer {
                if (isDragging) {
                    scaleX = 1.05f
                    scaleY = 1.05f
                    shadowElevation = 16f
                }
            }
            .zIndex(if (isDragging) 1f else 0f),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging)
                CyberColors.NeonGreen.copy(alpha = 0.2f)
            else
                CyberColors.CardBg
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 12.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = if (isDragging) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/**
 * Zona donde se puede soltar un elemento
 */
@Composable
fun DropZone(
    id: String,
    label: String,
    icon: String,
    color: Color,
    isHighlighted: Boolean,
    acceptedItemId: String?,
    onDrop: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var zonePosition by remember { mutableStateOf(Offset.Zero) }
    var zoneSize by remember { mutableStateOf(IntSize.Zero) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .onGloballyPositioned { coordinates ->
                zonePosition = coordinates.positionInRoot()
                zoneSize = coordinates.size
            }
            .border(
                width = if (isHighlighted) 3.dp else 2.dp,
                color = if (isHighlighted) color else color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(
                elevation = if (isHighlighted) 12.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted)
                color.copy(alpha = 0.2f)
            else
                CyberColors.CardBg.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mostrar icono o elemento aceptado
            if (acceptedItemId != null) {
                SuccessCheckmark(size = 48.dp, color = color)
            } else {
                Text(
                    text = icon,
                    fontSize = 40.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) color else Color.White.copy(alpha = 0.7f)
            )

            if (isHighlighted) {
                Text(
                    text = "¡Suelta aquí!",
                    fontSize = 12.sp,
                    color = color,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Composable de drag and drop completo
 */
@Composable
fun DragAndDropClassifier(
    items: List<DraggableItemData>,
    categories: List<CategoryData>,
    onItemCategorized: (itemId: String, categoryId: String, isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragState by remember { mutableStateOf(DragState()) }
    var draggedItemPosition by remember { mutableStateOf(Offset.Zero) }
    var categorizedItems by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Elementos arrastrables
        Text(
            text = "Arrastra cada elemento a su categoría:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonGreen
        )

        items.filter { it.id !in categorizedItems.keys }.forEach { item ->
            DraggableItem(
                id = item.id,
                content = item.content,
                icon = item.icon,
                isDragging = dragState.isDragging && dragState.draggedItemId == item.id,
                onDragStart = { id ->
                    dragState = DragState(
                        isDragging = true,
                        draggedItemId = id,
                        offset = Offset.Zero
                    )
                },
                onDrag = { offset ->
                    draggedItemPosition += offset
                },
                onDragEnd = {
                    // Detectar en qué categoría se soltó
                    val droppedCategoryId = categories.find { category ->
                        // Lógica simple de detección de colisión
                        // En producción, usar coordenadas reales
                        true // Simplificado por ahora
                    }?.id

                    if (droppedCategoryId != null && dragState.draggedItemId != null) {
                        val item = items.find { it.id == dragState.draggedItemId }
                        if (item != null) {
                            val isCorrect = item.correctCategoryId == droppedCategoryId
                            categorizedItems = categorizedItems + (item.id to droppedCategoryId)
                            onItemCategorized(item.id, droppedCategoryId, isCorrect)
                        }
                    }

                    dragState = DragState()
                    draggedItemPosition = Offset.Zero
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Zonas de destino
        Text(
            text = "Categorías:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberColors.NeonBlue
        )

        categories.forEach { category ->
            DropZone(
                id = category.id,
                label = category.label,
                icon = category.icon,
                color = category.color,
                isHighlighted = dragState.isDragging,
                acceptedItemId = categorizedItems.entries
                    .find { it.value == category.id }?.key,
                onDrop = { itemId, categoryId ->
                    val item = items.find { it.id == itemId }
                    if (item != null) {
                        val isCorrect = item.correctCategoryId == categoryId
                        onItemCategorized(itemId, categoryId, isCorrect)
                    }
                }
            )
        }
    }
}

/**
 * Data classes para drag and drop
 */
data class DraggableItemData(
    val id: String,
    val content: String,
    val icon: String? = null,
    val correctCategoryId: String
)

data class CategoryData(
    val id: String,
    val label: String,
    val icon: String,
    val color: Color
)