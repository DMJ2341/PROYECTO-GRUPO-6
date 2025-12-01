package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
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
import com.example.cyberlearnapp.viewmodel.AuthState
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// =========================================================================
// CONSTANTES PARA CUMPLIMIENTO LEGAL - LEY 29733 PERÚ
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
    const val RUC = "20XXXXXXXXX" // Reemplazar con RUC real
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
    
    // Estados de consentimiento - Ley 29733
    var termsAccepted by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }
    var dataProcessingAccepted by remember { mutableStateOf(false) }
    var ageConfirmed by remember { mutableStateOf(false) }
    
    // Estados de diálogos
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDataProcessingDialog by remember { mutableStateOf(false) }
    var showAgeVerificationDialog by remember { mutableStateOf(false) }
    
    // Estados de error
    var showTermsError by remember { mutableStateOf(false) }
    var showPrivacyError by remember { mutableStateOf(false) }
    var showDataProcessingError by remember { mutableStateOf(false) }
    var showAgeError by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Validación de todos los consentimientos requeridos
    val allConsentsAccepted = termsAccepted && privacyAccepted && dataProcessingAccepted && ageConfirmed

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

    // Reset errores cuando se aceptan los consentimientos
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
                        color = TextWhite
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryDark
                )
            )
        },
        containerColor = PrimaryDark
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

            // Logo/Icono
            Text(
                text = "🛡️",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "CyberLearn",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )

            Text(
                text = if (isRegister) "Crea tu cuenta para empezar" else "Bienvenido de vuelta",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campos del formulario
            if (isRegister) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo", color = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = SecondaryDark,
                        cursorColor = AccentCyan,
                        focusedLabelColor = AccentCyan,
                        unfocusedLabelColor = TextGray
                    )
                )
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Correo electrónico", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = SecondaryDark,
                    cursorColor = AccentCyan,
                    focusedLabelColor = AccentCyan,
                    unfocusedLabelColor = TextGray
                )
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = TextGray
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
                            if (allConsentsAccepted) {
                                viewModel.register(name, email, password, true)
                            } else {
                                validateConsents(termsAccepted, privacyAccepted, dataProcessingAccepted, ageConfirmed,
                                    { showTermsError = true }, { showPrivacyError = true },
                                    { showDataProcessingError = true }, { showAgeError = true })
                            }
                        } else {
                            viewModel.login(email, password)
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = SecondaryDark,
                    cursorColor = AccentCyan,
                    focusedLabelColor = AccentCyan,
                    unfocusedLabelColor = TextGray
                )
            )

            // Indicador de seguridad de contraseña
            if (isRegister && password.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                PasswordStrengthIndicator(password)
            }

            // SECCIÓN DE CONSENTIMIENTOS - LEY 29733 PERÚ
            if (isRegister) {
                Spacer(Modifier.height(20.dp))
                LegalNoticeCard()
                Spacer(Modifier.height(16.dp))

                // 1. VERIFICACIÓN DE EDAD
                ConsentCard(
                    title = "Verificación de edad",
                    description = "Confirmo que tengo entre 14 y 25 años de edad",
                    isChecked = ageConfirmed,
                    hasError = showAgeError,
                    onCheckedChange = { if (!it) ageConfirmed = false else showAgeVerificationDialog = true },
                    onInfoClick = { showAgeVerificationDialog = true },
                    icon = "🎂",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))

                // 2. TÉRMINOS Y CONDICIONES
                ConsentCard(
                    title = "Términos y Condiciones",
                    description = "He leído y acepto los términos de uso del servicio",
                    isChecked = termsAccepted,
                    hasError = showTermsError,
                    onCheckedChange = { if (!it) termsAccepted = false else showTermsDialog = true },
                    onInfoClick = { showTermsDialog = true },
                    icon = "📋",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))

                // 3. POLÍTICA DE PRIVACIDAD
                ConsentCard(
                    title = "Política de Privacidad",
                    description = "He sido informado sobre el tratamiento de mis datos personales",
                    isChecked = privacyAccepted,
                    hasError = showPrivacyError,
                    onCheckedChange = { if (!it) privacyAccepted = false else showPrivacyDialog = true },
                    onInfoClick = { showPrivacyDialog = true },
                    icon = "🔒",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))

                // 4. CONSENTIMIENTO DE DATOS
                ConsentCard(
                    title = "Consentimiento de Datos",
                    description = "Otorgo mi consentimiento libre, expreso e informado para el tratamiento de mis datos",
                    isChecked = dataProcessingAccepted,
                    hasError = showDataProcessingError,
                    onCheckedChange = { if (!it) dataProcessingAccepted = false else showDataProcessingDialog = true },
                    onInfoClick = { showDataProcessingDialog = true },
                    icon = "✅",
                    isRequired = true
                )

                // Mensaje de error general
                if (showTermsError || showPrivacyError || showDataProcessingError || showAgeError) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Danger.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, Danger)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Debes leer y aceptar todos los consentimientos obligatorios para crear tu cuenta",
                                style = MaterialTheme.typography.bodySmall, color = TextWhite)
                        }
                    }
                }

                // Texto informativo Ley 29733
                Spacer(Modifier.height(12.dp))
                Text(
                    text = buildAnnotatedString {
                        append("De conformidad con la ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = AccentCyan)) {
                            append("Ley N° 29733")
                        }
                        append(", Ley de Protección de Datos Personales del Perú, y su Reglamento, tu información será tratada de manera confidencial y segura.")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Mensajes de error del servidor
            if (authState is AuthState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Danger.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, Danger)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Text((authState as AuthState.Error).message, color = TextWhite, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Mensaje de código reenviado
            if (authState is AuthState.CodeResent) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, Success)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Success, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Código reenviado a tu email", color = TextWhite, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Botón principal
            Button(
                onClick = {
                    keyboardController?.hide()
                    if (isRegister) {
                        if (allConsentsAccepted) {
                            viewModel.register(name, email, password, true)
                        } else {
                            validateConsents(termsAccepted, privacyAccepted, dataProcessingAccepted, ageConfirmed,
                                { showTermsError = true }, { showPrivacyError = true },
                                { showDataProcessingError = true }, { showAgeError = true })
                        }
                    } else {
                        viewModel.login(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = authState !is AuthState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    disabledContainerColor = AccentCyan.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = PrimaryDark, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isRegister) "REGISTRARSE" else "ENTRAR", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botón cambiar login/registro
            TextButton(
                onClick = {
                    isRegister = !isRegister
                    termsAccepted = false; privacyAccepted = false; dataProcessingAccepted = false; ageConfirmed = false
                    showTermsError = false; showPrivacyError = false; showDataProcessingError = false; showAgeError = false
                    viewModel.resetNavigation()
                }
            ) {
                Text(
                    if (isRegister) "¿Ya tienes cuenta? Inicia sesión aquí" else "¿Nuevo usuario? Regístrate aquí",
                    color = AccentCyan
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // DIÁLOGOS
    if (showAgeVerificationDialog) {
        AgeVerificationDialog(onDismiss = { showAgeVerificationDialog = false },
            onAccept = { ageConfirmed = true; showAgeVerificationDialog = false; showAgeError = false })
    }
    if (showTermsDialog) {
        TermsAndConditionsDialog(onDismiss = { showTermsDialog = false },
            onAccept = { termsAccepted = true; showTermsDialog = false; showTermsError = false })
    }
    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false },
            onAccept = { privacyAccepted = true; showPrivacyDialog = false; showPrivacyError = false })
    }
    if (showDataProcessingDialog) {
        DataProcessingConsentDialog(onDismiss = { showDataProcessingDialog = false },
            onAccept = { dataProcessingAccepted = true; showDataProcessingDialog = false; showDataProcessingError = false })
    }
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

// =========================================================================
// COMPONENTES REUTILIZABLES
// =========================================================================

@Composable
fun LegalNoticeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AccentCyan.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, null, tint = AccentCyan, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Aviso Legal - Ley N° 29733", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AccentCyan)
                Spacer(Modifier.height(4.dp))
                Text("Para crear tu cuenta debes otorgar tu consentimiento libre, previo, expreso e informado. Lee cada documento antes de aceptar.",
                    style = MaterialTheme.typography.bodySmall, color = TextGray)
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
            containerColor = when { hasError -> Danger.copy(alpha = 0.1f); isChecked -> Success.copy(alpha = 0.1f); else -> CardBg }
        ),
        border = BorderStroke(if (hasError || isChecked) 2.dp else 1.dp,
            when { hasError -> Danger; isChecked -> Success; else -> SecondaryDark })
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isChecked, onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = Success, uncheckedColor = if (hasError) Danger else TextGray, checkmarkColor = TextWhite)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$icon ", fontSize = 16.sp)
                        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                        if (isRequired) Text(" *", color = Danger, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(description, style = MaterialTheme.typography.bodySmall, color = TextGray)
                }
                IconButton(onClick = onInfoClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Info, "Ver detalles", tint = AccentCyan, modifier = Modifier.size(20.dp))
                }
            }
            if (isChecked) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 44.dp, top = 4.dp)) {
                    Icon(Icons.Default.CheckCircle, null, tint = Success, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Aceptado", style = MaterialTheme.typography.bodySmall, color = Success)
                }
            }
            if (hasError) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 44.dp, top = 4.dp)) {
                    Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Debes leer y aceptar para continuar", style = MaterialTheme.typography.bodySmall, color = Danger)
                }
            }
        }
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)
    val (color, progress) = when (strength) {
        "Muy débil" -> Pair(Danger, 0.2f); "Débil" -> Pair(Danger.copy(alpha = 0.7f), 0.4f)
        "Media" -> Pair(Warning, 0.6f); "Fuerte" -> Pair(Success.copy(alpha = 0.7f), 0.8f)
        "Muy fuerte" -> Pair(Success, 1f); else -> Pair(TextGray, 0f)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Seguridad:", style = MaterialTheme.typography.bodySmall, color = TextGray)
            Text(strength, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = color, trackColor = SecondaryDark)
        if (strength == "Muy débil" || strength == "Débil") {
            Spacer(Modifier.height(8.dp))
            Text("💡 Tip: Usa mayúsculas, números y símbolos (!@#\$%)", style = MaterialTheme.typography.bodySmall, color = TextGray)
        }
    }
}

fun calculatePasswordStrength(password: String): String {
    var score = 0
    if (password.length >= 6) score++; if (password.length >= 8) score++; if (password.length >= 12) score++
    if (password.any { it.isUpperCase() }) score++; if (password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++; if (password.any { !it.isLetterOrDigit() }) score++
    return when { score <= 2 -> "Muy débil"; score <= 3 -> "Débil"; score <= 4 -> "Media"; score <= 5 -> "Fuerte"; else -> "Muy fuerte" }
}

// =========================================================================
// DIÁLOGOS DE CONSENTIMIENTO - LEY 29733 PERÚ
// =========================================================================

@Composable
fun AgeVerificationDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SecondaryDark,
        title = { Column { Text("🎂 Verificación de Edad", color = TextWhite, fontWeight = FontWeight.Bold); Text("Requisito legal según Ley N° 29733", style = MaterialTheme.typography.bodySmall, color = TextGray) } },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(300.dp), colors = CardDefaults.cardColors(containerColor = CardBg)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        LegalSection("BASE LEGAL", "Conforme al artículo 27 y 28 del Reglamento de la Ley N° 29733 (Decreto Supremo N° 003-2013-JUS), para el tratamiento de datos personales de menores de edad se requiere:\n\n• Menores de 14 años: Consentimiento de padres o tutores\n• Mayores de 14 y menores de 18 años: Pueden otorgar su propio consentimiento para actividades permitidas a adolescentes")
                        LegalSection("REQUISITOS DE EDAD PARA CYBERLEARN", "CyberLearn está diseñado para estudiantes universitarios de entre 15 y 25 años.\n\nAl aceptar, declaras bajo juramento que:\n\n✓ Tienes al menos 14 años de edad (edad mínima legal para otorgar consentimiento según Ley 29733)\n\n✓ Si tienes entre 14 y 17 años, confirmas que esta es una actividad educativa permitida para adolescentes\n\n✓ Comprendes el contenido educativo sobre ciberseguridad\n\n✓ La información proporcionada es en lenguaje comprensible para ti")
                        LegalSection("DECLARACIÓN JURADA", "Al marcar la casilla de verificación de edad, declaro bajo juramento que:\n\n1. Tengo la edad mínima requerida (14 años o más)\n2. Comprendo la finalidad educativa de la plataforma\n3. La información que proporcionaré es veraz\n4. Entiendo mis derechos según la Ley 29733\n\n⚠️ ADVERTENCIA: Proporcionar información falsa sobre tu edad puede resultar en la cancelación de tu cuenta y constituye una falta según la legislación peruana.")
                    }
                }
                if (!hasScrolledToBottom) { Spacer(Modifier.height(8.dp)); Text("↓ Desplázate para leer todo", style = MaterialTheme.typography.bodySmall, color = AccentCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = Success, disabledContainerColor = Success.copy(alpha = 0.3f))) { Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(if (hasScrolledToBottom) "CONFIRMO MI EDAD" else "Lee el documento...", color = if (hasScrolledToBottom) TextWhite else TextGray) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextGray) } }
    )
}

@Composable
fun TermsAndConditionsDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SecondaryDark,
        title = { Column { Text("📋 Términos y Condiciones", color = TextWhite, fontWeight = FontWeight.Bold); Text("Versión ${LegalConstants.VERSION_TERMINOS} - ${LegalConstants.FECHA_ACTUALIZACION}", style = MaterialTheme.typography.bodySmall, color = TextGray) } },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(350.dp), colors = CardDefaults.cardColors(containerColor = CardBg)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        LegalSection("1. IDENTIFICACIÓN DEL RESPONSABLE", "Razón Social: ${LegalConstants.RAZON_SOCIAL}\nDirección: ${LegalConstants.DIRECCION_LEGAL}\nEmail de contacto: ${LegalConstants.EMAIL_SOPORTE}\nEmail para datos personales: ${LegalConstants.EMAIL_DATOS_PERSONALES}")
                        LegalSection("2. ACEPTACIÓN DE LOS TÉRMINOS", "Al crear una cuenta en CyberLearn, aceptas estos términos y condiciones en su totalidad. Si no estás de acuerdo con alguno de estos términos, no debes usar la plataforma.\n\nEste acuerdo tiene efectos legales vinculantes conforme a la legislación peruana, incluyendo el Código Civil y las normas de protección al consumidor.")
                        LegalSection("3. DESCRIPCIÓN DEL SERVICIO", "CyberLearn es una plataforma educativa GRATUITA enfocada en ciberseguridad, diseñada para estudiantes universitarios de ${LegalConstants.EDAD_MINIMA_USUARIO}-${LegalConstants.EDAD_MAXIMA_USUARIO} años.\n\nServicios ofrecidos:\n• Cursos interactivos sobre ciberseguridad\n• Glosarios técnicos interactivos\n• Evaluaciones y certificados de progreso\n• Material basado en estándares NIST e INCIBE")
                        LegalSection("4. REQUISITOS DE USO", "Para usar CyberLearn debes cumplir con:\n\n• Tener al menos ${LegalConstants.EDAD_MINIMA_CONSENTIMIENTO} años de edad\n• Proporcionar información veraz y actualizada\n• Mantener la confidencialidad de tu cuenta\n• Usar la plataforma solo para fines educativos legítimos\n• No compartir tu cuenta con terceros")
                        LegalSection("5. CONDUCTA DEL USUARIO", "Al usar CyberLearn, te comprometes a:\n\n✓ Usar el conocimiento adquirido de forma ÉTICA y LEGAL\n✓ NO realizar actividades de hacking sin autorización\n✓ NO usar la plataforma para planificar actividades ilícitas\n✓ Reportar vulnerabilidades de forma responsable\n✓ Respetar la propiedad intelectual\n\n⚠️ IMPORTANTE: El uso del conocimiento adquirido para actividades ilegales resultará en:\n• Suspensión inmediata de tu cuenta\n• Reporte a las autoridades competentes\n• Posibles acciones legales según el Código Penal Peruano")
                        LegalSection("6. PROPIEDAD INTELECTUAL", "• Todo el contenido educativo es propiedad de CyberLearn\n• Puedes usar el contenido SOLO para aprendizaje personal\n• NO está permitida la redistribución comercial\n• Las referencias a NIST e INCIBE son con fines educativos\n• El código fuente y diseño son propiedad exclusiva de CyberLearn")
                        LegalSection("7. LIMITACIÓN DE RESPONSABILIDAD", "• La plataforma se ofrece \"tal cual\"\n• No garantizamos certificaciones oficiales\n• No somos responsables del uso indebido del conocimiento\n• El contenido es educativo, NO constituye asesoría profesional\n• No garantizamos disponibilidad continua del servicio")
                        LegalSection("8. MODIFICACIONES", "Nos reservamos el derecho de modificar estos términos. Te notificaremos de cambios significativos por email con al menos 15 días de anticipación.\n\nEl uso continuado de la plataforma después de las modificaciones implica tu aceptación de los nuevos términos.")
                        LegalSection("9. LEY APLICABLE Y JURISDICCIÓN", "Estos términos se rigen por las leyes de la República del Perú.\n\nPara cualquier controversia, las partes se someten a la jurisdicción de los tribunales de Lima, Perú.\n\nNormativa aplicable:\n• Código Civil Peruano\n• Ley N° 29733 - Protección de Datos Personales\n• Ley N° 29571 - Código de Protección al Consumidor")
                        LegalSection("10. CONTACTO", "Para consultas sobre estos términos:\n📧 ${LegalConstants.EMAIL_SOPORTE}\n\nPara ejercer tus derechos de datos personales:\n📧 ${LegalConstants.EMAIL_DATOS_PERSONALES}\n\nÚltima actualización: ${LegalConstants.FECHA_ACTUALIZACION}")
                        Spacer(Modifier.height(16.dp))
                    }
                }
                if (!hasScrolledToBottom) { Spacer(Modifier.height(8.dp)); Text("↓ Desplázate para leer todo", style = MaterialTheme.typography.bodySmall, color = AccentCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = Success, disabledContainerColor = Success.copy(alpha = 0.3f))) { Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(if (hasScrolledToBottom) "ACEPTO LOS TÉRMINOS" else "Lee los términos...", color = if (hasScrolledToBottom) TextWhite else TextGray) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextGray) } }
    )
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SecondaryDark,
        title = { Column { Text("🔒 Política de Privacidad", color = TextWhite, fontWeight = FontWeight.Bold); Text("Conforme a la Ley N° 29733", style = MaterialTheme.typography.bodySmall, color = TextGray) } },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(350.dp), colors = CardDefaults.cardColors(containerColor = CardBg)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        LegalSection("1. RESPONSABLE DEL TRATAMIENTO", "Conforme al artículo 18 de la Ley N° 29733:\n\nTitular del Banco de Datos: ${LegalConstants.RAZON_SOCIAL}\nDirección: ${LegalConstants.DIRECCION_LEGAL}\nEmail: ${LegalConstants.EMAIL_DATOS_PERSONALES}\n\nEl banco de datos \"USUARIOS_CYBERLEARN\" se encuentra registrado ante la Autoridad Nacional de Protección de Datos Personales del Ministerio de Justicia y Derechos Humanos.")
                        LegalSection("2. DATOS PERSONALES RECOPILADOS", "Recopilamos los siguientes datos personales:\n\nDATOS DE IDENTIFICACIÓN:\n• Nombre completo\n• Correo electrónico\n• Contraseña (encriptada)\n\nDATOS DE USO:\n• Progreso en cursos y lecciones\n• Puntuación en evaluaciones\n• Fecha de registro y última actividad\n• Logros y certificados obtenidos\n\nNO recopilamos datos sensibles (origen racial, opiniones políticas, religión, salud, orientación sexual, datos biométricos).")
                        LegalSection("3. FINALIDAD DEL TRATAMIENTO", "Conforme al artículo 6 de la Ley 29733 (Principio de Finalidad), tus datos serán utilizados EXCLUSIVAMENTE para:\n\n✓ Crear y gestionar tu cuenta de usuario\n✓ Personalizar tu experiencia educativa\n✓ Registrar tu progreso académico\n✓ Emitir certificados de completación\n✓ Enviarte notificaciones sobre tu curso\n✓ Mejorar nuestros servicios educativos\n\n✗ NO usamos tus datos para:\n• Publicidad de terceros\n• Venta o comercialización a terceros\n• Perfilamiento comercial")
                        LegalSection("4. PLAZO DE CONSERVACIÓN", "Tus datos personales serán conservados durante:\n\n• Mientras tu cuenta esté activa\n• Hasta 2 años después de la última actividad (cuentas inactivas)\n• Hasta que solicites la eliminación de tu cuenta\n\nDespués de estos plazos, los datos serán eliminados de forma segura o anonimizados para fines estadísticos.")
                        LegalSection("5. TRANSFERENCIA DE DATOS", "Conforme al artículo 15 de la Ley 29733:\n\n• NO transferimos tus datos a terceros sin tu consentimiento\n• Nuestros servidores pueden estar ubicados fuera del Perú (servicios cloud)\n• En caso de transferencia internacional, garantizamos un nivel de protección equivalente al de la ley peruana\n• Usamos proveedores que cumplen con estándares de seguridad internacionales")
                        LegalSection("6. MEDIDAS DE SEGURIDAD", "Conforme al artículo 16 de la Ley 29733, implementamos:\n\nMEDIDAS TÉCNICAS:\n• Encriptación de contraseñas (hash + salt)\n• Conexiones seguras (HTTPS/TLS)\n• Firewall y protección contra intrusiones\n• Copias de seguridad periódicas\n\nMEDIDAS ORGANIZATIVAS:\n• Acceso restringido a datos personales\n• Políticas de confidencialidad del personal\n• Procedimientos de respuesta a incidentes")
                        LegalSection("7. TUS DERECHOS ARCO", "Conforme a los artículos 19-24 de la Ley 29733, tienes derecho a:\n\n📖 ACCESO: Conocer qué datos tenemos sobre ti\n✏️ RECTIFICACIÓN: Corregir datos inexactos o incompletos\n🗑️ CANCELACIÓN: Solicitar la eliminación de tus datos\n⛔ OPOSICIÓN: Oponerte al tratamiento de tus datos\n\nPara ejercer estos derechos:\n📧 ${LegalConstants.EMAIL_DATOS_PERSONALES}\n\nPlazo de respuesta: 20 días hábiles máximo\n\nSi no recibes respuesta o no estás conforme, puedes presentar una reclamación ante la Autoridad Nacional de Protección de Datos Personales.")
                        LegalSection("8. COOKIES Y TECNOLOGÍAS SIMILARES", "Utilizamos cookies esenciales para:\n• Mantener tu sesión activa\n• Recordar tus preferencias\n• Mejorar el rendimiento de la app\n\nNO utilizamos cookies de seguimiento publicitario.")
                        LegalSection("9. MENORES DE EDAD", "Conforme al artículo 27-28 del Reglamento de la Ley 29733:\n\n• Menores de 14 años: No pueden registrarse\n• De 14 a 17 años: Pueden registrarse con su propio consentimiento para esta actividad educativa\n• Mayores de 18: Plena capacidad\n\nEl contenido de CyberLearn es apto para adolescentes y jóvenes universitarios.")
                        LegalSection("10. MODIFICACIONES", "Cualquier modificación a esta política será notificada por email con 15 días de anticipación.\n\nÚltima actualización: ${LegalConstants.FECHA_ACTUALIZACION}")
                        Spacer(Modifier.height(16.dp))
                    }
                }
                if (!hasScrolledToBottom) { Spacer(Modifier.height(8.dp)); Text("↓ Desplázate para leer todo", style = MaterialTheme.typography.bodySmall, color = AccentCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = Success, disabledContainerColor = Success.copy(alpha = 0.3f))) { Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(if (hasScrolledToBottom) "HE SIDO INFORMADO" else "Lee la política...", color = if (hasScrolledToBottom) TextWhite else TextGray) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextGray) } }
    )
}

@Composable
fun DataProcessingConsentDialog(onDismiss: () -> Unit, onAccept: () -> Unit) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val currentDateTime = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("es", "PE")).format(Date()) }
    LaunchedEffect(scrollState.value, scrollState.maxValue) { if (scrollState.maxValue > 0) hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50 }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = SecondaryDark,
        title = { Column { Text("✅ Consentimiento para Tratamiento de Datos", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("Artículo 5 y 7 - Ley N° 29733", style = MaterialTheme.typography.bodySmall, color = TextGray) } },
        text = {
            Column {
                Card(modifier = Modifier.fillMaxWidth().height(350.dp), colors = CardDefaults.cardColors(containerColor = CardBg)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = AccentCyan.copy(alpha = 0.15f))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("DECLARACIÓN DE CONSENTIMIENTO INFORMADO", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AccentCyan, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Text("Este documento constituye tu consentimiento expreso para el tratamiento de datos personales conforme a la Ley N° 29733 del Perú.", style = MaterialTheme.typography.bodySmall, color = TextGray, textAlign = TextAlign.Center)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        LegalSection("CARACTERÍSTICAS DEL CONSENTIMIENTO", "De conformidad con el artículo 7 del Reglamento de la Ley 29733, mi consentimiento es:\n\n✓ LIBRE: Lo otorgo sin coacción, error ni dolo\n✓ PREVIO: Antes de la recopilación de mis datos\n✓ EXPRESO: Mediante esta aceptación explícita\n✓ INFORMADO: He leído la política de privacidad\n✓ INEQUÍVOCO: No admite dudas sobre mi aceptación")
                        LegalSection("AUTORIZO EL TRATAMIENTO DE:", "Datos de identificación:\n• Mi nombre completo\n• Mi correo electrónico\n• Mi contraseña (almacenada de forma encriptada)\n\nDatos de actividad educativa:\n• Mi progreso en los cursos\n• Mis puntuaciones y evaluaciones\n• Mis logros y certificados\n• Mi actividad en la plataforma")
                        LegalSection("FINALIDADES AUTORIZADAS", "Autorizo que mis datos sean tratados para:\n\n1. Gestión de mi cuenta de usuario\n2. Personalización de mi experiencia educativa\n3. Registro de mi progreso académico\n4. Emisión de certificados de completación\n5. Comunicaciones sobre mi curso (notificaciones)\n6. Mejora de los servicios educativos\n7. Estadísticas anónimas de uso")
                        LegalSection("DERECHOS RESERVADOS", "Entiendo que conservo los siguientes derechos:\n\n📖 ACCESO: Solicitar copia de mis datos\n✏️ RECTIFICACIÓN: Corregir información incorrecta\n🗑️ CANCELACIÓN: Eliminar mis datos\n⛔ OPOSICIÓN: Oponerme al tratamiento\n🔄 REVOCACIÓN: Retirar este consentimiento\n\nPara ejercerlos: ${LegalConstants.EMAIL_DATOS_PERSONALES}")
                        LegalSection("REVOCACIÓN DEL CONSENTIMIENTO", "Entiendo que puedo revocar este consentimiento en cualquier momento, sin efectos retroactivos, enviando un correo a:\n\n📧 ${LegalConstants.EMAIL_DATOS_PERSONALES}\n\nLa revocación puede implicar la imposibilidad de seguir usando la plataforma.")
                        Spacer(Modifier.height(16.dp))
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f)), border = BorderStroke(1.dp, Success.copy(alpha = 0.3f))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("📝 REGISTRO DEL CONSENTIMIENTO", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Success)
                                Spacer(Modifier.height(8.dp))
                                Text("Fecha y hora: $currentDateTime\nVersión de términos: ${LegalConstants.VERSION_TERMINOS}\nMétodo: Aceptación electrónica en app\n\nAl hacer clic en \"OTORGO MI CONSENTIMIENTO\", confirmo que:\n• He leído todos los documentos legales\n• Comprendo el tratamiento de mis datos\n• Otorgo mi consentimiento voluntariamente", style = MaterialTheme.typography.bodySmall, color = TextGray)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
                if (!hasScrolledToBottom) { Spacer(Modifier.height(8.dp)); Text("↓ Desplázate para leer todo", style = MaterialTheme.typography.bodySmall, color = AccentCyan, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
            }
        },
        confirmButton = { Button(onClick = onAccept, enabled = hasScrolledToBottom, colors = ButtonDefaults.buttonColors(containerColor = Success, disabledContainerColor = Success.copy(alpha = 0.3f))) { Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(if (hasScrolledToBottom) "OTORGO MI CONSENTIMIENTO" else "Lee el documento...", color = if (hasScrolledToBottom) TextWhite else TextGray) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = TextGray) } }
    )
}

@Composable
fun LegalSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AccentCyan, modifier = Modifier.padding(bottom = 4.dp))
        Text(content, style = MaterialTheme.typography.bodySmall, color = TextGray, lineHeight = 18.sp)
    }
}