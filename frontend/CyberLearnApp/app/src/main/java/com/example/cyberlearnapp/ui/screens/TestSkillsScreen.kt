// app/src/main/java/com/example/cyberlearnapp/ui/screens/TestSkillsScreen.kt
package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.models.CyberRole
import com.example.cyberlearnapp.ui.theme.*
import com.example.cyberlearnapp.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestSkillsScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel()
) {
    val recommendations by viewModel.recommendations.collectAsState()
    val result by viewModel.result.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("💡 Skills Necesarias")
                        result?.let {
                            val role = CyberRole.fromString(it.recommendedRole)
                            Text(
                                role?.displayName ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundMain
                )
            )
        }
    ) { padding ->

        if (recommendations == null || result == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val role = CyberRole.fromString(result!!.recommendedRole)!!

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header con descripción
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(role.color).copy(alpha = 0.1f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    role.emoji,
                                    style = MaterialTheme.typography.displaySmall
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        role.displayName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(role.color)
                                    )
                                    Text(
                                        "Habilidades técnicas que necesitas dominar",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Contador de skills
                item {
                    Text(
                        "📊 Total: ${recommendations!!.skills.size} habilidades",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Lista de skills
                items(recommendations!!.skills) { skill ->
                    SkillCardExpanded(
                        skill = skill,
                        index = recommendations!!.skills.indexOf(skill) + 1,
                        roleColor = Color(role.color)
                    )
                }

                // Footer con botones
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { navController.navigate("test_recommendations") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(role.color)
                            )
                        ) {
                            Icon(Icons.Default.School, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver Certificaciones y Labs")
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("dashboard") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Home, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Volver al Inicio")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillCardExpanded(
    skill: com.example.cyberlearnapp.models.RoleSkill,
    index: Int,
    roleColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número de skill
            Box(
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = roleColor.copy(alpha = 0.15f)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            index.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = roleColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Texto de la skill
            Text(
                skill.skill,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Checkmark icon
            Icon(
                Icons.Default.CheckCircle,
                null,
                tint = roleColor.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}