package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.viewmodel.AuthState
import com.example.cyberlearnapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToVerification: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Navegación automática
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onLoginSuccess()
                viewModel.resetNavigation()
            }
            is AuthState.RequiresVerification -> {
                val verificationEmail = (authState as AuthState.RequiresVerification).email
                onNavigateToVerification(verificationEmail)
                viewModel.resetNavigation()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isRegister) "Crear Cuenta" else "Iniciar Sesión",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo/Icono
            Text(
                text = "🎓",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "CyberLearn",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (isRegister) "Crea tu cuenta para empezar" else "Bienvenido de vuelta",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campos del formulario
            if (isRegister) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = if (isRegister) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!isRegister) {
                            keyboardController?.hide()
                            viewModel.login(email, password)
                        }
                    }
                )
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña"
                            else
                                "Mostrar contraseña"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (isRegister) {
                            if (termsAccepted) {
                                viewModel.register(name, email, password, termsAccepted)
                            }
                        } else {
                            viewModel.login(email, password)
                        }
                    }
                )
            )

            // Indicador de seguridad de contraseña
            if (isRegister && password.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                PasswordStrengthIndicator(password)
            }

            // Términos y condiciones (solo en registro)
            if (isRegister) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it }
                    )
                    Text(
                        text = "Acepto los ",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "términos y condiciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showTermsDialog = true }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Mensajes de error
            if (authState is AuthState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Mensaje de código reenviado
            if (authState is AuthState.CodeResent) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "✅ Código reenviado a tu email",
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Botón principal
            Button(
                onClick = {
                    keyboardController?.hide()
                    if (isRegister) {
                        viewModel.register(name, email, password, termsAccepted)
                    } else {
                        viewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = authState !is AuthState.Loading &&
                        (!isRegister || termsAccepted)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isRegister) "REGISTRARSE" else "ENTRAR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botón para cambiar entre login y registro
            TextButton(
                onClick = {
                    isRegister = !isRegister
                    termsAccepted = false
                    viewModel.resetNavigation()
                }
            ) {
                Text(
                    text = if (isRegister)
                        "¿Ya tienes cuenta? Inicia sesión aquí"
                    else
                        "¿Nuevo usuario? Regístrate aquí",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Diálogo de términos y condiciones
    if (showTermsDialog) {
        TermsDialog(onDismiss = { showTermsDialog = false })
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)
    val color = when (strength) {
        "Débil" -> Color.Red
        "Media" -> Color(0xFFFFA726)
        "Fuerte" -> Color.Green
        else -> Color.Gray
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Seguridad:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = strength,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

fun calculatePasswordStrength(password: String): String {
    var score = 0

    if (password.length >= 8) score++
    if (password.length >= 12) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return when {
        score <= 2 -> "Débil"
        score <= 4 -> "Media"
        else -> "Fuerte"
    }
}

@Composable
fun TermsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Términos y Condiciones") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = """
                        TÉRMINOS Y CONDICIONES DE USO - CYBERLEARN
                        
                        Última actualización: Noviembre 2024
                        
                        1. ACEPTACIÓN DE LOS TÉRMINOS
                        Al crear una cuenta en CyberLearn, aceptas estos términos y condiciones.
                        
                        2. USO DE LA PLATAFORMA
                        - CyberLearn es una plataforma educativa gratuita de ciberseguridad
                        - Debes tener al menos 15 años para usar la plataforma
                        - Tu cuenta es personal e intransferible
                        
                        3. PRIVACIDAD Y DATOS
                        - Recopilamos: email, nombre, progreso académico
                        - No compartimos tus datos con terceros
                        - Puedes eliminar tu cuenta en cualquier momento
                        
                        4. CONTENIDO EDUCATIVO
                        - El contenido es propiedad de CyberLearn
                        - Puedes usarlo para aprendizaje personal
                        - No está permitida la redistribución comercial
                        
                        5. CONDUCTA DEL USUARIO
                        - Prohibido usar técnicas aprendidas para actividades ilegales
                        - El conocimiento debe usarse éticamente
                        - Nos reservamos el derecho de suspender cuentas
                        
                        6. LIMITACIÓN DE RESPONSABILIDAD
                        - La plataforma se ofrece "tal cual"
                        - No garantizamos certificaciones oficiales
                        - No somos responsables del uso indebido del conocimiento
                        
                        7. CONTACTO
                        Para consultas: soporte@cyberlearn.app
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CERRAR")
            }
        }
    )
}