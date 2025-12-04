package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.viewmodel.RecoveryState
import com.example.cyberlearnapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.recoveryState.collectAsState()

    // ✅ CRITICAL: Variables de estado locales
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ✅ CRITICAL: Sincronizar email cuando cambia el estado
    LaunchedEffect(state) {
        if (state is RecoveryState.EmailSent) {
            // Guardar el email en lowercase y trimmed
            email = (state as RecoveryState.EmailSent).email.trim().lowercase()
            println("🔍 Email guardado desde estado: '$email'")
        }
    }

    // ✅ CRITICAL: Navegar automáticamente al login cuando se cambia la contraseña
    LaunchedEffect(state) {
        if (state is RecoveryState.PasswordResetSuccess) {
            kotlinx.coroutines.delay(2000) // Esperar 2 segundos
            viewModel.resetRecoveryFlow()
            navController.navigate("auth") {
                popUpTo("forgot_password") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar Cuenta", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetRecoveryFlow()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = PrimaryCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundMain)
            )
        },
        containerColor = BackgroundMain
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
            Text("🔐", fontSize = 64.sp, modifier = Modifier.padding(bottom = 16.dp))

            Text(
                text = "Recuperación de Contraseña",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when (state) {
                is RecoveryState.Idle, is RecoveryState.Error -> {
                    // PASO 1: INGRESAR EMAIL
                    Text(
                        text = "Ingresa tu correo electrónico para recibir un código de recuperación.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trim().lowercase() }, // ✅ Siempre lowercase
                        label = { Text("Email", color = TextTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = SurfaceActive,
                            cursorColor = PrimaryCyan
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    if (state is RecoveryState.Error) {
                        Text(
                            text = (state as RecoveryState.Error).message,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            println("🚀 Enviando código a: '$email'")
                            viewModel.sendRecoveryEmail(email.trim().lowercase())
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = email.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Enviar Código", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BackgroundMain)
                    }
                }

                is RecoveryState.EmailSent -> {
                    // PASO 2: INGRESAR CÓDIGO
                    Text(
                        text = "Ingresa el código de 6 dígitos que enviamos a:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = email, // ✅ Mostrar el email guardado
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) code = it },
                        label = { Text("Código (6 dígitos)", color = TextTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = SurfaceActive,
                            cursorColor = PrimaryCyan
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            println("🔍 Validando código: '$code' para email: '$email'")
                            viewModel.validateRecoveryToken(email, code) // ✅ Usar email guardado
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = code.length == 6,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Verificar Código", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BackgroundMain)
                    }
                }

                is RecoveryState.TokenValid -> {
                    // PASO 3: INGRESAR NUEVA CONTRASEÑA
                    Text(
                        text = "Código verificado ✅",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Ingresa tu nueva contraseña para $email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it.trim() },
                        label = { Text("Nueva Contraseña", color = TextTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = SurfaceActive,
                            cursorColor = PrimaryCyan
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            println("🔐 Cambiando contraseña para: '$email' con código: '$code'")
                            viewModel.resetPassword(email, code, newPassword)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = newPassword.length >= 8,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cambiar Contraseña", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BackgroundMain)
                    }

                    if (newPassword.isNotEmpty() && newPassword.length < 8) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "La contraseña debe tener al menos 8 caracteres",
                            color = WarningOrange,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                is RecoveryState.PasswordResetSuccess -> {
                    // PASO 4: ÉXITO
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
                    )

                    Text(
                        text = "¡Contraseña actualizada!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Tu contraseña ha sido cambiada exitosamente. Serás redirigido al login...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    CircularProgressIndicator(
                        color = PrimaryCyan,
                        modifier = Modifier.size(48.dp)
                    )
                }

                is RecoveryState.Loading -> {
                    CircularProgressIndicator(
                        color = PrimaryCyan,
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 6.dp
                    )
                }
            }
        }
    }
}