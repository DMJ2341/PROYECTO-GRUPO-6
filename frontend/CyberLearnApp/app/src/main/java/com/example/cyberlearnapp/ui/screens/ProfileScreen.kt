package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.cyberlearnapp.viewmodel.UserViewModel
import com.example.cyberlearnapp.network.models.User

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToBadges: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val user: User? by viewModel.user.collectAsState(initial = null)
    val lifecycleOwner = LocalLifecycleOwner.current

    // ✅ REFRESH AUTOMÁTICO
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshUserState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ AVATAR CON ESTILO
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(12.dp, CircleShape),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00D9FF).copy(0.2f)
                ),
                border = BorderStroke(3.dp, Color(0xFF00D9FF))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF00D9FF)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ✅ NOMBRE Y EMAIL CON ESTILO
            user?.let { userData ->
                Text(
                    text = userData.name ?: "Usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 28.sp
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00D9FF).copy(0.15f)
                    ),
                    border = BorderStroke(1.5.dp, Color(0xFF00D9FF)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = userData.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            // ✅ INFORMACIÓN DE LA CUENTA CON ESTILO MEJORADO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A2332)
                ),
                border = BorderStroke(2.dp, Color(0xFF00D9FF).copy(0.5f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📋", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Información de la Cuenta",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    ProfileInfoItem(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = user?.email ?: "---",
                        iconColor = Color(0xFF00D9FF)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color(0xFF2D3748),
                        thickness = 1.dp
                    )

                    ProfileInfoItem(
                        icon = Icons.Default.Person,
                        label = "Nombre",
                        value = user?.name ?: "Usuario",
                        iconColor = Color(0xFF8B5CF6)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color(0xFF2D3748),
                        thickness = 1.dp
                    )

                    ProfileInfoItem(
                        icon = Icons.Default.CardMembership,
                        label = "Rol",
                        value = "Estudiante",
                        iconColor = Color(0xFFFBBF24)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ✅ ACCIONES CON ESTILO MEJORADO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A2332)
                ),
                border = BorderStroke(2.dp, Color(0xFF00D9FF).copy(0.5f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚙️", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Acciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // ✅ BOTÓN EDITAR PERFIL
                    OutlinedButton(
                        onClick = { /* TODO */ },
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
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF00D9FF)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Editar Perfil",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Default.ChevronRight,
                            null,
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF00D9FF)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // ✅ BOTÓN CERRAR SESIÓN
                    Button(
                        onClick = { viewModel.logout(); onLogout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Cerrar Sesión",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ✅ COMPONENTE AUXILIAR MEJORADO
@Composable
private fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = iconColor.copy(0.2f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(0.6f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}