package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.cyberlearnapp.ui.theme.*

object LegalConstants {
    const val VERSION_TERMINOS = "1.2"
    const val FECHA_ACTUALIZACION = "Diciembre 2025"
    const val EDAD_MINIMA_CONSENTIMIENTO = 14
    const val EMAIL_SOPORTE = "soporte@cyberlearn.app"
    const val EMAIL_DATOS_PERSONALES = "datospersonales@cyberlearn.app"
    const val DIRECCION_LEGAL = "Lima, Perú"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToVerification: (String) -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    var termsAccepted by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }
    var dataProcessingAccepted by remember { mutableStateOf(false) }
    var ageConfirmed by remember { mutableStateOf(false) }

    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDataProcessingDialog by remember { mutableStateOf(false) }
    var showAgeVerificationDialog by remember { mutableStateOf(false) }

    var showTermsError by remember { mutableStateOf(false) }
    var showPrivacyError by remember { mutableStateOf(false) }
    var showDataProcessingError by remember { mutableStateOf(false) }
    var showAgeError by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val allConsentsAccepted = termsAccepted && privacyAccepted && dataProcessingAccepted && ageConfirmed

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onLoginSuccess()
                viewModel.resetNavigation()
            }
            is AuthState.RequiresVerification -> {
                val emailDest = (authState as AuthState.RequiresVerification).email
                onNavigateToVerification(emailDest)
                viewModel.resetNavigation()
            }
            else -> {}
        }
    }

    LaunchedEffect(termsAccepted) { if (termsAccepted) showTermsError = false }
    LaunchedEffect(privacyAccepted) { if (privacyAccepted) showPrivacyError = false }
    LaunchedEffect(dataProcessingAccepted) { if (dataProcessingAccepted) showDataProcessingError = false }
    LaunchedEffect(ageConfirmed) { if (ageConfirmed) showAgeError = false }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isRegister) "Crear Cuenta" else "Iniciar Sesión",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundMain
                )
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
            Spacer(modifier = Modifier.height(32.dp))

            Text("🛡️", fontSize = 64.sp, modifier = Modifier.padding(bottom = 16.dp))

            Text(
                text = "CyberLearn",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan
            )

            Text(
                text = if (isRegister) "Únete y aprende ciberseguridad" else "Bienvenido de vuelta",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (isRegister) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = SurfaceActive,
                        cursorColor = PrimaryCyan,
                        focusedLabelColor = PrimaryCyan,
                        unfocusedLabelColor = TextTertiary
                    )
                )
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Correo electrónico", color = TextTertiary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = SurfaceActive,
                    cursorColor = PrimaryCyan,
                    focusedLabelColor = PrimaryCyan,
                    unfocusedLabelColor = TextTertiary
                )
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it.trim() },
                label = { Text("Contraseña", color = TextTertiary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    if (isRegister) {
                        if (allConsentsAccepted) viewModel.register(name.trim(), email.trim(), password.trim(), true)
                        else validateConsents(termsAccepted, privacyAccepted, dataProcessingAccepted, ageConfirmed, { showTermsError = true }, { showPrivacyError = true }, { showDataProcessingError = true }, { showAgeError = true })
                    } else {
                        viewModel.login(email.trim(), password.trim())
                    }
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = SurfaceActive,
                    cursorColor = PrimaryCyan,
                    focusedLabelColor = PrimaryCyan,
                    unfocusedLabelColor = TextTertiary
                )
            )

            // ✅ BOTÓN FORGOT PASSWORD (SOLO EN LOGIN) O INDICADOR DE FUERZA (SOLO EN REGISTRO)
            if (!isRegister) {
                // LOGIN: Mostrar botón de recuperación
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(onClick = onNavigateToForgotPassword) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = PrimaryCyan,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else if (password.isNotEmpty()) {
                // REGISTRO: Mostrar indicador de fuerza
                Spacer(Modifier.height(8.dp))
                PasswordStrengthIndicator(password)
            }

            if (isRegister) {
                Spacer(Modifier.height(20.dp))
                LegalNoticeCard()
                Spacer(Modifier.height(16.dp))

                ConsentCard(
                    title = "Verificación de edad",
                    description = "Confirmo que tengo al menos ${LegalConstants.EDAD_MINIMA_CONSENTIMIENTO} años",
                    isChecked = ageConfirmed,
                    hasError = showAgeError,
                    onCheckedChange = { if (!it) ageConfirmed = false else showAgeVerificationDialog = true },
                    onInfoClick = { showAgeVerificationDialog = true },
                    icon = "🎂",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))

                ConsentCard(
                    title = "Términos y Condiciones",
                    description = "Acepto las reglas de uso de la plataforma",
                    isChecked = termsAccepted,
                    hasError = showTermsError,
                    onCheckedChange = { if (!it) termsAccepted = false else showTermsDialog = true },
                    onInfoClick = { showTermsDialog = true },
                    icon = "📋",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))

                ConsentCard(
                    title = "Política de Privacidad",
                    description = "Entiendo cómo se protegen mis datos",
                    isChecked = privacyAccepted,
                    hasError = showPrivacyError,
                    onCheckedChange = { if (!it) privacyAccepted = false else showPrivacyDialog = true },
                    onInfoClick = { showPrivacyDialog = true },
                    icon = "🔒",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))

                ConsentCard(
                    title = "Tratamiento de Datos",
                    description = "Autorizo el uso de mis datos académicos",
                    isChecked = dataProcessingAccepted,
                    hasError = showDataProcessingError,
                    onCheckedChange = { if (!it) dataProcessingAccepted = false else showDataProcessingDialog = true },
                    onInfoClick = { showDataProcessingDialog = true },
                    icon = "✅",
                    isRequired = true
                )

                if (showTermsError || showPrivacyError || showDataProcessingError || showAgeError) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, ErrorRed)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Debes leer y aceptar todos los consentimientos", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Tu información está protegida por la Ley N° 29733 de Protección de Datos Personales.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            if (authState is AuthState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, ErrorRed)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = ErrorRed, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Text((authState as AuthState.Error).message, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (authState is AuthState.CodeResent) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, SuccessGreen)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Código reenviado", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Button(
                onClick = {
                    keyboardController?.hide()
                    if (isRegister) {
                        if (allConsentsAccepted) viewModel.register(name.trim(), email.trim(), password.trim(), true)
                        else validateConsents(termsAccepted, privacyAccepted, dataProcessingAccepted, ageConfirmed, { showTermsError = true }, { showPrivacyError = true }, { showDataProcessingError = true }, { showAgeError = true })
                    } else {
                        viewModel.login(email.trim(), password.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = authState !is AuthState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, disabledContainerColor = PrimaryCyan.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = BackgroundMain, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isRegister) "REGISTRARSE" else "ENTRAR", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BackgroundMain)
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = {
                    isRegister = !isRegister
                    termsAccepted = false; privacyAccepted = false; dataProcessingAccepted = false; ageConfirmed = false
                    showTermsError = false; showPrivacyError = false; showDataProcessingError = false; showAgeError = false
                    viewModel.resetNavigation()
                }
            ) {
                Text(if (isRegister) "¿Ya tienes cuenta? Inicia sesión" else "¿Nuevo? Regístrate aquí", color = PrimaryCyan)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAgeVerificationDialog) AgeVerificationDialog({ showAgeVerificationDialog = false }, { ageConfirmed = true; showAgeVerificationDialog = false; showAgeError = false })
    if (showTermsDialog) TermsAndConditionsDialog({ showTermsDialog = false }, { termsAccepted = true; showTermsDialog = false; showTermsError = false })
    if (showPrivacyDialog) PrivacyPolicyDialog({ showPrivacyDialog = false }, { privacyAccepted = true; showPrivacyDialog = false; showPrivacyError = false })
    if (showDataProcessingDialog) DataProcessingConsentDialog({ showDataProcessingDialog = false }, { dataProcessingAccepted = true; showDataProcessingDialog = false; showDataProcessingError = false })
}

private fun validateConsents(
    termsAccepted: Boolean, privacyAccepted: Boolean, dataProcessingAccepted: Boolean, ageConfirmed: Boolean,
    onTermsError: () -> Unit, onPrivacyError: () -> Unit, onDataProcessingError: () -> Unit, onAgeError: () -> Unit
) {
    if (!ageConfirmed) onAgeError()
    if (!termsAccepted) onTermsError()
    if (!privacyAccepted) onPrivacyError()
    if (!dataProcessingAccepted) onDataProcessingError()
}

@Composable
fun LegalNoticeCard() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PrimaryCyan.copy(alpha = 0.1f)), border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Aviso Legal - Ley N° 29733", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                Text("Consentimiento libre, previo, expreso e informado.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun ConsentCard(
    title: String, description: String, isChecked: Boolean, hasError: Boolean,
    onCheckedChange: (Boolean) -> Unit, onInfoClick: () -> Unit, icon: String, isRequired: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = when { hasError -> ErrorRed.copy(alpha = 0.1f); isChecked -> SuccessGreen.copy(alpha = 0.1f); else -> SurfaceCard }),
        border = BorderStroke(if (hasError || isChecked) 2.dp else 1.dp, when { hasError -> ErrorRed; isChecked -> SuccessGreen; else -> SurfaceElevated })
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isChecked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(checkedColor = SuccessGreen, uncheckedColor = if (hasError) ErrorRed else TextSecondary, checkmarkColor = TextPrimary))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$icon ", fontSize = 16.sp)
                        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        if (isRequired) Text(" *", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                    Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                IconButton(onClick = onInfoClick, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Info, null, tint = PrimaryCyan, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)
    val (color, progress) = when (strength) {
        "Muy débil" -> Pair(ErrorRed, 0.2f); "Débil" -> Pair(ErrorRed.copy(alpha = 0.7f), 0.4f)
        "Media" -> Pair(WarningOrange, 0.6f); "Fuerte" -> Pair(SuccessGreen.copy(alpha = 0.7f), 0.8f)
        "Muy fuerte" -> Pair(SuccessGreen, 1f); else -> Pair(TextTertiary, 0f)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Seguridad:", style = MaterialTheme.typography.bodySmall, color = TextSecondary); Text(strength, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color) }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = color, trackColor = SurfaceElevated)
    }
}

fun calculatePasswordStrength(password: String): String {
    var score = 0
    if (password.length >= 6) score++; if (password.length >= 8) score++; if (password.length >= 12) score++
    if (password.any { it.isUpperCase() }) score++; if (password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++; if (password.any { !it.isLetterOrDigit() }) score++
    return when { score <= 2 -> "Muy débil"; score <= 3 -> "Débil"; score <= 4 -> "Media"; score <= 5 -> "Fuerte"; else -> "Muy fuerte" }
}

@Composable
fun AgeVerificationDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SurfaceElevated,
        title = { Text("🎂 Verificación de Edad", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(300.dp), colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        LegalSection("REQUISITOS DE EDAD", "De conformidad con la Ley N° 29733, el tratamiento de datos de menores de 14 años requiere consentimiento parental.\n\nAl marcar la casilla, declaras bajo juramento que:\n\n✓ Tienes ${LegalConstants.EDAD_MINIMA_CONSENTIMIENTO} años cumplidos o más.\n✓ Tienes la capacidad para consentir el uso de tus datos para fines educativos.\n✓ La información proporcionada es veraz.")
                        Spacer(Modifier.height(300.dp))
                    }
                }
                if (!hasScrolledToBottom) Text("↓ Baja para aceptar", color = PrimaryCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("CONFIRMO MI EDAD") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextSecondary) } }
    )
}

@Composable
fun TermsAndConditionsDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SurfaceElevated,
        title = { Text("📋 Términos y Condiciones", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(350.dp), colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        LegalSection("1. OBJETO", "CyberLearn es una plataforma educativa de ciberseguridad.")
                        LegalSection("2. USO ACEPTABLE", "Te comprometes a utilizar el conocimiento solo con fines éticos y legales. El hacking no autorizado está prohibido y resultará en la eliminación de la cuenta.")
                        LegalSection("3. PROPIEDAD", "El contenido es propiedad de CyberLearn S.A.C. No se permite la redistribución.")
                        LegalSection("4. RESPONSABILIDAD", "No nos hacemos responsables por el mal uso de las herramientas enseñadas.")
                        LegalSection("5. MODIFICACIONES", "Podemos actualizar estos términos notificándote previamente.")
                        Spacer(Modifier.height(300.dp))
                    }
                }
                if (!hasScrolledToBottom) Text("↓ Baja para aceptar", color = PrimaryCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("ACEPTO TÉRMINOS") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextSecondary) } }
    )
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SurfaceElevated,
        title = { Text("🔒 Política de Privacidad", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(350.dp), colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        LegalSection("1. RESPONSABLE", "CyberLearn App, con domicilio en Lima, Perú.")
                        LegalSection("2. DATOS RECOPILADOS", "Nombre, email y progreso académico. No recopilamos datos sensibles.")
                        LegalSection("3. FINALIDAD", "Gestión de cuenta.")
                        LegalSection("4. TUS DERECHOS (ARCO)", "Puedes acceder, rectificar, cancelar u oponerte al tratamiento de tus datos escribiendo a ${LegalConstants.EMAIL_DATOS_PERSONALES}.")
                        Spacer(Modifier.height(300.dp))
                    }
                }
                if (!hasScrolledToBottom) Text("↓ Baja para aceptar", color = PrimaryCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("HE LEÍDO LA POLÍTICA") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextSecondary) } }
    )
}

@Composable
fun DataProcessingConsentDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SurfaceElevated,
        title = { Text("✅ Consentimiento de Datos", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(300.dp), colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        LegalSection("CONSENTIMIENTO EXPRESO", "De conformidad con el Art. 5 de la Ley 29733, otorgo mi consentimiento libre, previo, expreso e informado para que CyberLearn trate mis datos personales para fines de gestión educativa.")
                        Spacer(Modifier.height(200.dp))
                    }
                }
                if (!hasScrolledToBottom) Text("↓ Baja para aceptar", color = PrimaryCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("OTORGO CONSENTIMIENTO") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextSecondary) } }
    )
}

@Composable
fun LegalSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryCyan, modifier = Modifier.padding(bottom = 4.dp))
        Text(content, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}