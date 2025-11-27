package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue // Importar getValue
import androidx.compose.runtime.collectAsState // Importar collectAsState
import androidx.compose.runtime.* import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.viewmodel.UserViewModel
// Importar User para la correcta inferencia de tipo en el StateFlow
import com.example.cyberlearnapp.network.models.User

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    // ✅ CORRECCIÓN 1: Se usa el delegate 'by' con el tipo explícito y valor inicial.
    // Resuelve los errores de Property delegate, Cannot infer type y Unresolved reference 'user'.
    val user: User? by viewModel.user.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ AVATAR Y NOMBRE
        Surface(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        user?.let { userData ->
            Text(
                // ✅ Resuelve Unresolved reference 'name'
                text = userData.name ?: "Usuario",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(4.dp))

            Text(
                // ✅ Resuelve Unresolved reference 'email'
                text = userData.email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(32.dp))

        // ✅ INFORMACIÓN DEL USUARIO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Información de la Cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.dp))

                // Email
                ProfileInfoItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    // ✅ Resuelve Unresolved reference 'email'
                    value = user?.email ?: "---"
                )

                // ✅ CORRECCIÓN 2: Usar HorizontalDivider (Componente de Material 3)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Nombre
                ProfileInfoItem(
                    icon = Icons.Default.Person,
                    label = "Nombre",
                    // ✅ Resuelve Unresolved reference 'name'
                    value = user?.name ?: "Usuario"
                )

                // ✅ CORRECCIÓN 2: Usar HorizontalDivider (Componente de Material 3)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Rol (si existe en tu modelo)
                ProfileInfoItem(
                    // ✅ CORRECCIÓN 3: Se usa CardMembership (Badge no existe en Icons.Default)
                    icon = Icons.Default.CardMembership,
                    label = "Rol",
                    value = "Estudiante" // O user?.role si lo tienes
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ✅ ESTADÍSTICAS (Opcional - si tienes estos datos)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Estadísticas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Default.School,
                        value = "0",
                        label = "Cursos"
                    )
                    StatItem(
                        icon = Icons.Default.EmojiEvents,
                        value = "0",
                        label = "Insignias"
                    )
                    StatItem(
                        icon = Icons.Default.TrendingUp,
                        value = "0",
                        label = "Nivel"
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ✅ ACCIONES
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Acciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.dp))

                // Editar Perfil (placeholder)
                OutlinedButton(
                    onClick = { /* TODO: Navigate to edit profile */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Editar Perfil",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Cerrar Sesión
                Button(
                    onClick = {
                        viewModel.logout() // ✅ Resuelve Unresolved reference 'logout'
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Cerrar Sesión",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// COMPONENTE REUTILIZABLE - Info Item
@Composable
private fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// COMPONENTE REUTILIZABLE - Stat Item
@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )

        Spacer(Modifier.height(8.dp))


        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}