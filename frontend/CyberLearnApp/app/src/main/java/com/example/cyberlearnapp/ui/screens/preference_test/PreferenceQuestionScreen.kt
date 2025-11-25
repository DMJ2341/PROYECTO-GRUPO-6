package com.example.cyberlearnapp.ui.screens.preference

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.cyberlearnapp.network.models.assessments.PreferenceQuestion
import com.example.cyberlearnapp.network.models.assessments.PreferenceOption

@Composable
fun PreferenceQuestionScreen(
    question: PreferenceQuestion,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = question.section.replace("_", " ").uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = question.question_text,
            style = MaterialTheme.typography.headlineSmall
        )

        if (question.question_subtext != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.question_subtext,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Opciones
        Column(Modifier.selectableGroup()) {
            question.options.forEach { option ->
                PreferenceOptionItem(
                    option = option,
                    isSelected = selectedOption == option.id,
                    onClick = { onOptionSelected(option.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PreferenceOptionItem(
    option: PreferenceOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null // Manejado por selectable
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}