package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.models.CyberRole
import com.example.cyberlearnapp.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestSkillsScreen(
    navController: NavController,
    viewModel: TestViewModel = hiltViewModel()
) {
    val recommendations by viewModel.recommendations.collectAsState()
    val result by viewModel.result.collectAsState()

    // ✅ FONDO CON GRADIENTE
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1419),
                        Color(0xFF1A2332)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 24.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Skills Necesarias",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            result?.let {
                                val role = CyberRole.fromString(it.recommendedRole)
                                Text(
                                    role?.displayName ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFF00D9FF))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A2332)
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
                    CircularProgressIndicator(color = Color(0xFF00D9FF))
                }
            } else {
                val role = CyberRole.fromString(result!!.recommendedRole)!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // HEADER CON EMOJI Y DESCRIPCIÓN
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(16.dp, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(role.color).copy(alpha = 0.2f)
                            ),
                            border = BorderStroke(3.dp, Color(role.color)),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(role.color).copy(0.3f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(24.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // EMOJI EN CÍRCULO
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .shadow(12.dp, CircleShape)
                                            .background(Color(role.color), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            role.emoji,
                                            fontSize = 48.sp
                                        )
                                    }

                                    Spacer(Modifier.width(20.dp))

                                    Column {
                                        Text(
                                            role.displayName,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            fontSize = 24.sp
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            "Habilidades técnicas que necesitas dominar",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(0.8f),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // CONTADOR DE SKILLS
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A2332)
                            ),
                            border = BorderStroke(2.dp, Color(0xFF00D9FF).copy(0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📊", fontSize = 28.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Total de habilidades",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(role.color)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "${recommendations!!.skills.size}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }

                    // LISTA DE SKILLS
                    items(recommendations!!.skills) { skill ->
                        SkillCardExpanded(
                            skill = skill,
                            index = recommendations!!.skills.indexOf(skill) + 1,
                            roleColor = Color(role.color)
                        )
                    }

                    // FOOTER CON BOTONES
                    item {
                        Spacer(Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { navController.navigate("test_recommendations") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(role.color)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    null,
                                    modifier = Modifier.size(28.dp),
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Ver Certificaciones y Labs",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                            }

                            OutlinedButton(
                                onClick = { navController.navigate("dashboard") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF00D9FF)
                                ),
                                border = BorderStroke(2.dp, Color(0xFF00D9FF)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Home,
                                    null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Volver al Inicio",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))
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
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        ),
        border = BorderStroke(2.dp, roleColor.copy(0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // NÚMERO DE SKILL EN CÍRCULO
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(6.dp, CircleShape)
                    .background(roleColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    index.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.width(20.dp))

            // TEXTO DE LA SKILL
            Text(
                skill.skill,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )

            Spacer(Modifier.width(16.dp))

            // CHECKMARK ICON
            Icon(
                Icons.Default.CheckCircle,
                null,
                tint = roleColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}