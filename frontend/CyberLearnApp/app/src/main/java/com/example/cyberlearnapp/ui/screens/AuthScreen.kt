package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.viewmodel.AuthState // ✅ Importación Correcta
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// =========================================================================
// CONSTANTES LEGALES
// =========================================================================
object LegalConstants {
    const val VERSION_TERMINOS = "1.2"
    const val FECHA_ACTUALIZACION = "Diciembre 2024"
    const val EDAD_MINIMA_CONSENTIMIENTO = 14
    const val EDAD_MAXIMA_MENOR = 18
    const val EDAD_MAXIMA_USUARIO = 25
    const val EDAD_MINIMA_USUARIO = 15
    const val EMAIL_SOPORTE = "soporte@cyberlearn.app"
    const val EMAIL_DATOS_PERSONALES = "datospersonales@cyberlearn.app"
    const val DIRECCION_LEGAL = "Lima, Perú"
    const val RAZON_SOCIAL = "CyberLearn App S.A.C."
}

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

    // Estados de consentimiento
    var termsAccepted by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }
    var dataProcessingAccepted by remember { mutableStateOf(false) }
    var ageConfirmed by remember { mutableStateOf(false) }

    // Diálogos
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDataProcessingDialog by remember { mutableStateOf(false) }
    var showAgeVerificationDialog by remember { mutableStateOf(false) }

    // Errores UI
    var showTermsError by remember { mutableStateOf(false) }
    var showPrivacyError by remember { mutableStateOf(false) }
    var showDataProcessingError by remember { mutableStateOf(false) }
    var showAgeError by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val allConsentsAccepted = termsAccepted && privacyAccepted && dataProcessingAccepted && ageConfirmed

    // Navegación
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

    // Reset errores
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
                text = if (isRegister) "Crea tu cuenta para empezar" else "Bienvenido de vuelta",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Formulario
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
                        cursorColor = PrimaryCyan
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
                    cursorColor = PrimaryCyan
                )
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it.trim() }, // ✅ TRIM IMPORTANTE
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
                    cursorColor = PrimaryCyan
                )
            )

            if (isRegister && password.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                PasswordStrengthIndicator(password)
            }

            // CONSENTIMIENTOS
            if (isRegister) {
                Spacer(Modifier.height(20.dp))
                LegalNoticeCard()
                Spacer(Modifier.height(16.dp))

                ConsentCard(
                    title = "Verificación de edad",
                    description = "Confirmo que tengo entre 14 y 25 años",
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
                    description = "He leído y acepto los términos",
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
                    description = "He sido informado sobre el tratamiento",
                    isChecked = privacyAccepted,
                    hasError = showPrivacyError,
                    onCheckedChange = { if (!it) privacyAccepted = false else showPrivacyDialog = true },
                    onInfoClick = { showPrivacyDialog = true },
                    icon = "🔒",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))

                ConsentCard(
                    title = "Consentimiento de Datos",
                    description = "Autorizo el tratamiento de mis datos",
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
                            Text("Debes aceptar todos los consentimientos", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ERRORES
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

            // BOTÓN ACCIÓN
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

    // DIÁLOGOS
    if (showAgeVerificationDialog) AgeVerificationDialog({ showAgeVerificationDialog = false }, { ageConfirmed = true; showAgeVerificationDialog = false; showAgeError = false })
    if (showTermsDialog) TermsAndConditionsDialog({ showTermsDialog = false }, { termsAccepted = true; showTermsDialog = false; showTermsError = false })
    if (showPrivacyDialog) PrivacyPolicyDialog({ showPrivacyDialog = false }, { privacyAccepted = true; showPrivacyDialog = false; showPrivacyError = false })
    if (showDataProcessingDialog) DataProcessingConsentDialog({ showDataProcessingDialog = false }, { dataProcessingAccepted = true; showDataProcessingDialog = false; showDataProcessingError = false })
}

// FUNCIONES AUXILIARES Y COMPONENTES (Iguales que antes pero con colores corregidos)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryCyan.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Aviso Legal - Ley N° 29733", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                Spacer(Modifier.height(4.dp))
                Text("Para crear tu cuenta debes otorgar tu consentimiento libre, previo, expreso e informado.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
        colors = CardDefaults.cardColors(
            containerColor = when { hasError -> ErrorRed.copy(alpha = 0.1f); isChecked -> SuccessGreen.copy(alpha = 0.1f); else -> SurfaceCard }
        ),
        border = BorderStroke(if (hasError || isChecked) 2.dp else 1.dp,
            when { hasError -> ErrorRed; isChecked -> SuccessGreen; else -> SurfaceActive })
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isChecked, onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = SuccessGreen, uncheckedColor = if (hasError) ErrorRed else TextSecondary, checkmarkColor = TextPrimary)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$icon ", fontSize = 16.sp)
                        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        if (isRequired) Text(" *", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                IconButton(onClick = onInfoClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Info, "Ver detalles", tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                }
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Seguridad:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(strength, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
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

// Los diálogos (AgeVerificationDialog, TermsAndConditionsDialog, etc.) van aquí abajo igual que antes
// Solo asegúrate de cambiar 'CardBg' por 'SurfaceCard' y 'SecondaryDark' por 'SurfaceElevated' dentro de ellos.

@Composable
fun AgeVerificationDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SurfaceElevated,
        title = { Column { Text("🎂 Verificación de Edad", color = TextPrimary, fontWeight = FontWeight.Bold); Text("Requisito legal según Ley N° 29733", style = MaterialTheme.typography.bodySmall, color = TextSecondary) } },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(300.dp), colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        // ... Contenido legal ...
                        Text("Declaración Jurada...", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        // Agrega el texto completo que tenías antes aquí
                        Spacer(Modifier.height(300.dp)) // Spacer para forzar scroll si no hay texto
                    }
                }
                if (!hasScrolledToBottom) { Spacer(Modifier.height(8.dp)); Text("↓ Desplázate para leer todo", style = MaterialTheme.typography.bodySmall, color = PrimaryCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, disabledContainerColor = SuccessGreen.copy(alpha = 0.3f))) { Text(if (hasScrolledToBottom) "CONFIRMO MI EDAD" else "Lee el documento...", color = if (hasScrolledToBottom) TextPrimary else TextTertiary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextSecondary) } }
    )
}

// ... Repite estructura para Terms, Privacy y DataProcessing usando los colores nuevos ...
@Composable
fun TermsAndConditionsDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
     // Usa SurfaceElevated, TextPrimary, TextSecondary, SurfaceCard
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SurfaceElevated,
        title = { Text("📋 Términos", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = { Text("Contenido de términos...", color = TextSecondary) },
        confirmButton = { Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("ACEPTAR", color = TextPrimary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextSecondary) } }
    )
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SurfaceElevated,
        title = { Text("🔒 Privacidad", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = { Text("Contenido de privacidad...", color = TextSecondary) },
        confirmButton = { Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("ACEPTAR", color = TextPrimary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextSecondary) } }
    )
}

@Composable
fun DataProcessingConsentDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SurfaceElevated,
        title = { Text("✅ Datos Personales", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = { Text("Contenido de consentimiento...", color = TextSecondary) },
        confirmButton = { Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("OTORGAR CONSENTIMIENTO", color = TextPrimary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextSecondary) } }
    )
}