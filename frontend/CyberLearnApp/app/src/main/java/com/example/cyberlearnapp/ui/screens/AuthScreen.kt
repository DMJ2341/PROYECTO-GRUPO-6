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
import androidx.compose.ui.text.style.TextDecoration
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
    var birthDate by remember { mutableStateOf("") }
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
                color = PrimaryCyan
            )

            Text(
                text = if (isRegister) "Crea tu cuenta para empezar" else "Bienvenido de vuelta",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campos del formulario
            if (isRegister) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
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
                onValueChange = { password = it },
                label = { Text("Contraseña", color = TextTertiary) },
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
                                "Mostrar contraseña",
                            tint = TextSecondary
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
                                validateConsents(
                                    termsAccepted = termsAccepted,
                                    privacyAccepted = privacyAccepted,
                                    dataProcessingAccepted = dataProcessingAccepted,
                                    ageConfirmed = ageConfirmed,
                                    onTermsError = { showTermsError = true },
                                    onPrivacyError = { showPrivacyError = true },
                                    onDataProcessingError = { showDataProcessingError = true },
                                    onAgeError = { showAgeError = true }
                                )
                            }
                        } else {
                            viewModel.login(email, password)
                        }
                    }
                ),
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

            // Indicador de seguridad de contraseña
            if (isRegister && password.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                PasswordStrengthIndicator(password)
            }

            // =====================================================================
            // SECCIÓN DE CONSENTIMIENTOS - CUMPLIMIENTO LEY 29733 PERÚ
            // =====================================================================
            if (isRegister) {
                Spacer(Modifier.height(20.dp))

                // Aviso legal informativo
                LegalNoticeCard()

                Spacer(Modifier.height(16.dp))

                // 1. VERIFICACIÓN DE EDAD (Art. 27-28 Reglamento)
                ConsentCard(
                    title = "Verificación de edad",
                    description = "Confirmo que tengo entre 14 y 25 años de edad",
                    isChecked = ageConfirmed,
                    hasError = showAgeError,
                    onCheckedChange = {
                        if (!it) {
                            ageConfirmed = false
                        } else {
                            showAgeVerificationDialog = true
                        }
                    },
                    onInfoClick = { showAgeVerificationDialog = true },
                    icon = "🎂",
                    isRequired = true
                )

                Spacer(Modifier.height(12.dp))

                // 2. TÉRMINOS Y CONDICIONES DE USO
                ConsentCard(
                    title = "Términos y Condiciones",
                    description = "He leído y acepto los términos de uso del servicio",
                    isChecked = termsAccepted,
                    hasError = showTermsError,
                    onCheckedChange = {
                        if (!it) {
                            termsAccepted = false
                        } else {
                            showTermsDialog = true
                        }
                    },
                    onInfoClick = { showTermsDialog = true },
                    icon = "📋",
                    isRequired = true
                )

                Spacer(Modifier.height(12.dp))

                // 3. POLÍTICA DE PRIVACIDAD (Art. 18 Ley 29733)
                ConsentCard(
                    title = "Política de Privacidad",
                    description = "He sido informado sobre el tratamiento de mis datos personales",
                    isChecked = privacyAccepted,
                    hasError = showPrivacyError,
                    onCheckedChange = {
                        if (!it) {
                            privacyAccepted = false
                        } else {
                            showPrivacyDialog = true
                        }
                    },
                    onInfoClick = { showPrivacyDialog = true },
                    icon = "🔒",
                    isRequired = true
                )

                Spacer(Modifier.height(12.dp))

                // 4. CONSENTIMIENTO EXPRESO PARA TRATAMIENTO DE DATOS (Art. 5 y 7)
                ConsentCard(
                    title = "Consentimiento de Datos",
                    description = "Otorgo mi consentimiento libre, expreso e informado para el tratamiento de mis datos",
                    isChecked = dataProcessingAccepted,
                    hasError = showDataProcessingError,
                    onCheckedChange = {
                        if (!it) {
                            dataProcessingAccepted = false
                        } else {
                            showDataProcessingDialog = true
                        }
                    },
                    onInfoClick = { showDataProcessingDialog = true },
                    icon = "✅",
                    isRequired = true
                )

                // Mensaje de error general si falta algún consentimiento
                if (showTermsError || showPrivacyError || showDataProcessingError || showAgeError) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorRed.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.dp, ErrorRed)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Debes leer y aceptar todos los consentimientos obligatorios para crear tu cuenta",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Texto informativo sobre Ley 29733
                Spacer(Modifier.height(12.dp))
                Text(
                    text = buildAnnotatedString {
                        append("De conformidad con la ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = PrimaryCyan)) {
                            append("Ley N° 29733")
                        }
                        append(", Ley de Protección de Datos Personales del Perú, y su Reglamento, tu información será tratada de manera confidencial y segura.")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Mensajes de error del servidor
            if (authState is AuthState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.15f)
                    ),
                    border = BorderStroke(1.dp, ErrorRed)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Mensaje de código reenviado
            if (authState is AuthState.CodeResent) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen.copy(alpha = 0.15f)
                    ),
                    border = BorderStroke(1.dp, SuccessGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Código reenviado a tu email",
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                            validateConsents(
                                termsAccepted = termsAccepted,
                                privacyAccepted = privacyAccepted,
                                dataProcessingAccepted = dataProcessingAccepted,
                                ageConfirmed = ageConfirmed,
                                onTermsError = { showTermsError = true },
                                onPrivacyError = { showPrivacyError = true },
                                onDataProcessingError = { showDataProcessingError = true },
                                onAgeError = { showAgeError = true }
                            )
                        }
                    } else {
                        viewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = authState !is AuthState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryCyan,
                    disabledContainerColor = PrimaryCyan.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color = BackgroundMain,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isRegister) "REGISTRARSE" else "ENTRAR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundMain
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botón para cambiar entre login y registro
            TextButton(
                onClick = {
                    isRegister = !isRegister
                    // Reset todos los consentimientos
                    termsAccepted = false
                    privacyAccepted = false
                    dataProcessingAccepted = false
                    ageConfirmed = false
                    // Reset errores
                    showTermsError = false
                    showPrivacyError = false
                    showDataProcessingError = false
                    showAgeError = false
                    viewModel.resetNavigation()
                }
            ) {
                Text(
                    text = if (isRegister)
                        "¿Ya tienes cuenta? Inicia sesión aquí"
                    else
                        "¿Nuevo usuario? Regístrate aquí",
                    color = PrimaryCyan
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // =====================================================================
    // DIÁLOGOS DE CONSENTIMIENTO
    // =====================================================================

    // Diálogo de verificación de edad
    if (showAgeVerificationDialog) {
        AgeVerificationDialog(
            onDismiss = { showAgeVerificationDialog = false },
            onAccept = {
                ageConfirmed = true
                showAgeVerificationDialog = false
                showAgeError = false
            }
        )
    }

    // Diálogo de términos y condiciones
    if (showTermsDialog) {
        TermsAndConditionsDialog(
            onDismiss = { showTermsDialog = false },
            onAccept = {
                termsAccepted = true
                showTermsDialog = false
                showTermsError = false
            }
        )
    }

    // Diálogo de política de privacidad
    if (showPrivacyDialog) {
        PrivacyPolicyDialog(
            onDismiss = { showPrivacyDialog = false },
            onAccept = {
                privacyAccepted = true
                showPrivacyDialog = false
                showPrivacyError = false
            }
        )
    }

    // Diálogo de consentimiento de tratamiento de datos
    if (showDataProcessingDialog) {
        DataProcessingConsentDialog(
            onDismiss = { showDataProcessingDialog = false },
            onAccept = {
                dataProcessingAccepted = true
                showDataProcessingDialog = false
                showDataProcessingError = false
            }
        )
    }
}

// =========================================================================
// FUNCIONES AUXILIARES
// =========================================================================

private fun validateConsents(
    termsAccepted: Boolean,
    privacyAccepted: Boolean,
    dataProcessingAccepted: Boolean,
    ageConfirmed: Boolean,
    onTermsError: () -> Unit,
    onPrivacyError: () -> Unit,
    onDataProcessingError: () -> Unit,
    onAgeError: () -> Unit
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
        colors = CardDefaults.cardColors(
            containerColor = PrimaryCyan.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = "Aviso Legal - Ley N° 29733",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCyan
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Para crear tu cuenta debes otorgar tu consentimiento libre, previo, expreso e informado. Lee cada documento antes de aceptar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ConsentCard(
    title: String,
    description: String,
    isChecked: Boolean,
    hasError: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onInfoClick: () -> Unit,
    icon: String,
    isRequired: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                hasError -> ErrorRed.copy(alpha = 0.1f)
                isChecked -> SuccessGreen.copy(alpha = 0.1f)
                else -> SurfaceCard
            }
        ),
        border = BorderStroke(
            width = if (hasError || isChecked) 2.dp else 1.dp,
            color = when {
                hasError -> ErrorRed
                isChecked -> SuccessGreen
                else -> SurfaceActive
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = SuccessGreen,
                        uncheckedColor = if (hasError) ErrorRed else TextSecondary,
                        checkmarkColor = TextPrimary
                    )
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$icon ",
                            fontSize = 16.sp
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (isRequired) {
                            Text(
                                text = " *",
                                color = ErrorRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Ver detalles",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (isChecked) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 44.dp, top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Aceptado",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessGreen
                    )
                }
            }

            if (hasError) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 44.dp, top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Debes leer y aceptar para continuar",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)
    val (color, progress) = when (strength) {
        "Muy débil" -> Pair(ErrorRed, 0.2f)
        "Débil" -> Pair(ErrorRed.copy(alpha = 0.7f), 0.4f)
        "Media" -> Pair(WarningOrange, 0.6f)
        "Fuerte" -> Pair(SuccessGreen.copy(alpha = 0.7f), 0.8f)
        "Muy fuerte" -> Pair(SuccessGreen, 1f)
        else -> Pair(TextTertiary, 0f)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Seguridad:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                text = strength,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = SurfaceElevated
        )

        if (strength == "Muy débil" || strength == "Débil") {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "💡 Tip: Usa mayúsculas, números y símbolos (!@#\$%)",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
    }
}

fun calculatePasswordStrength(password: String): String {
    var score = 0

    if (password.length >= 6) score++
    if (password.length >= 8) score++
    if (password.length >= 12) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return when {
        score <= 2 -> "Muy débil"
        score <= 3 -> "Débil"
        score <= 4 -> "Media"
        score <= 5 -> "Fuerte"
        else -> "Muy fuerte"
    }
}

// =========================================================================
// DIÁLOGOS DE CONSENTIMIENTO - LEY 29733 PERÚ
// =========================================================================

@Composable
fun AgeVerificationDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceElevated,
        title = {
            Column {
                Text(
                    "🎂 Verificación de Edad",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Requisito legal según Ley N° 29733",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        LegalSection(
                            title = "BASE LEGAL",
                            content = """
                                Conforme al artículo 27 y 28 del Reglamento de la Ley N° 29733 (Decreto Supremo N° 003-2013-JUS), para el tratamiento de datos personales de menores de edad se requiere:

                                • Menores de 14 años: Consentimiento de padres o tutores
                                • Mayores de 14 y menores de 18 años: Pueden otorgar su propio consentimiento para actividades permitidas a adolescentes
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "REQUISITOS DE EDAD PARA CYBERLEARN",
                            content = """
                                CyberLearn está diseñado para estudiantes universitarios de entre 15 y 25 años.
                                
                                Al aceptar, declaras bajo juramento que:
                                
                                ✓ Tienes al menos 14 años de edad (edad mínima legal para otorgar consentimiento según Ley 29733)
                                
                                ✓ Si tienes entre 14 y 17 años, confirmas que esta es una actividad educativa permitida para adolescentes
                                
                                ✓ Comprendes el contenido educativo sobre ciberseguridad
                                
                                ✓ La información proporcionada es en lenguaje comprensible para ti
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "DECLARACIÓN JURADA",
                            content = """
                                Al marcar la casilla de verificación de edad, declaro bajo juramento que:

                                1. Tengo la edad mínima requerida (14 años o más)
                                2. Comprendo la finalidad educativa de la plataforma
                                3. La información que proporcionaré es veraz
                                4. Entiendo mis derechos según la Ley 29733

                                ⚠️ ADVERTENCIA: Proporcionar información falsa sobre tu edad puede resultar en la cancelación de tu cuenta y constituye una falta según la legislación peruana.
                            """.trimIndent()
                        )
                    }
                }

                if (!hasScrolledToBottom) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "↓ Desplázate para leer todo",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryCyan,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                enabled = hasScrolledToBottom,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen,
                    disabledContainerColor = SuccessGreen.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (hasScrolledToBottom) "CONFIRMO MI EDAD" else "Lee el documento...",
                    color = if (hasScrolledToBottom) TextPrimary else TextTertiary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", color = TextSecondary)
            }
        }
    )
}

@Composable
fun TermsAndConditionsDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceElevated,
        title = {
            Column {
                Text(
                    "📋 Términos y Condiciones",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Versión ${LegalConstants.VERSION_TERMINOS} - ${LegalConstants.FECHA_ACTUALIZACION}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        LegalSection(
                            title = "1. IDENTIFICACIÓN DEL RESPONSABLE",
                            content = """
                                Razón Social: ${LegalConstants.RAZON_SOCIAL}
                                Dirección: ${LegalConstants.DIRECCION_LEGAL}
                                Email de contacto: ${LegalConstants.EMAIL_SOPORTE}
                                Email para datos personales: ${LegalConstants.EMAIL_DATOS_PERSONALES}
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "2. ACEPTACIÓN DE LOS TÉRMINOS",
                            content = """
                                Al crear una cuenta en CyberLearn, aceptas estos términos y condiciones en su totalidad. Si no estás de acuerdo con alguno de estos términos, no debes usar la plataforma.
                                
                                Este acuerdo tiene efectos legales vinculantes conforme a la legislación peruana, incluyendo el Código Civil y las normas de protección al consumidor.
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "3. DESCRIPCIÓN DEL SERVICIO",
                            content = """
                                CyberLearn es una plataforma educativa GRATUITA enfocada en ciberseguridad, diseñada para estudiantes universitarios de ${LegalConstants.EDAD_MINIMA_USUARIO}-${LegalConstants.EDAD_MAXIMA_USUARIO} años.
                                
                                Servicios ofrecidos:
                                • Cursos interactivos sobre ciberseguridad
                                • Glosarios técnicos interactivos
                                • Evaluaciones y certificados de progreso
                                • Material basado en estándares NIST e INCIBE
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "4. REQUISITOS DE USO",
                            content = """
                                Para usar CyberLearn debes cumplir con:
                                
                                • Tener al menos ${LegalConstants.EDAD_MINIMA_CONSENTIMIENTO} años de edad
                                • Proporcionar información veraz y actualizada
                                • Mantener la confidencialidad de tu cuenta
                                • Usar la plataforma solo para fines educativos legítimos
                                • No compartir tu cuenta con terceros
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "5. CONDUCTA DEL USUARIO",
                            content = """
                                Al usar CyberLearn, te comprometes a:
                                
                                ✓ Usar el conocimiento adquirido de forma ÉTICA y LEGAL
                                ✓ NO realizar actividades de hacking sin autorización
                                ✓ NO usar la plataforma para planificar actividades ilícitas
                                ✓ Reportar vulnerabilidades de forma responsable
                                ✓ Respetar la propiedad intelectual
                                
                                ⚠️ IMPORTANTE: El uso del conocimiento adquirido para actividades ilegales resultará en:
                                • Suspensión inmediata de tu cuenta
                                • Reporte a las autoridades competentes
                                • Posibles acciones legales según el Código Penal Peruano
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "6. PROPIEDAD INTELECTUAL",
                            content = """
                                • Todo el contenido educativo es propiedad de CyberLearn
                                • Puedes usar el contenido SOLO para aprendizaje personal
                                • NO está permitida la redistribución comercial
                                • Las referencias a NIST e INCIBE son con fines educativos
                                • El código fuente y diseño son propiedad exclusiva de CyberLearn
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "7. LIMITACIÓN DE RESPONSABILIDAD",
                            content = """
                                • La plataforma se ofrece "tal cual"
                                • No garantizamos certificaciones oficiales
                                • No somos responsables del uso indebido del conocimiento
                                • El contenido es educativo, NO constituye asesoría profesional
                                • No garantizamos disponibilidad continua del servicio
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "8. MODIFICACIONES",
                            content = """
                                Nos reservamos el derecho de modificar estos términos. Te notificaremos de cambios significativos por email con al menos 15 días de anticipación.
                                
                                El uso continuado de la plataforma después de las modificaciones implica tu aceptación de los nuevos términos.
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "9. LEY APLICABLE Y JURISDICCIÓN",
                            content = """
                                Estos términos se rigen por las leyes de la República del Perú.
                                
                                Para cualquier controversia, las partes se someten a la jurisdicción de los tribunales de Lima, Perú.
                                
                                Normativa aplicable:
                                • Código Civil Peruano
                                • Ley N° 29733 - Protección de Datos Personales
                                • Ley N° 29571 - Código de Protección al Consumidor
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "10. CONTACTO",
                            content = """
                                Para consultas sobre estos términos:
                                📧 ${LegalConstants.EMAIL_SOPORTE}
                                
                                Para ejercer tus derechos de datos personales:
                                📧 ${LegalConstants.EMAIL_DATOS_PERSONALES}
                                
                                Última actualización: ${LegalConstants.FECHA_ACTUALIZACION}
                            """.trimIndent()
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }

                if (!hasScrolledToBottom) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "↓ Desplázate para leer todo",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryCyan,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                enabled = hasScrolledToBottom,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen,
                    disabledContainerColor = SuccessGreen.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (hasScrolledToBottom) "ACEPTO LOS TÉRMINOS" else "Lee los términos...",
                    color = if (hasScrolledToBottom) TextPrimary else TextTertiary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", color = TextSecondary)
            }
        }
    )
}

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceElevated,
        title = {
            Column {
                Text(
                    "🔒 Política de Privacidad",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Conforme a la Ley N° 29733",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        LegalSection(
                            title = "1. RESPONSABLE DEL TRATAMIENTO",
                            content = """
                                Conforme al artículo 18 de la Ley N° 29733:
                                
                                Titular del Banco de Datos: ${LegalConstants.RAZON_SOCIAL}
                                Dirección: ${LegalConstants.DIRECCION_LEGAL}
                                Email: ${LegalConstants.EMAIL_DATOS_PERSONALES}
                                
                                El banco de datos "USUARIOS_CYBERLEARN" se encuentra registrado ante la Autoridad Nacional de Protección de Datos Personales del Ministerio de Justicia y Derechos Humanos.
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "2. DATOS PERSONALES RECOPILADOS",
                            content = """
                                Recopilamos los siguientes datos personales:
                                
                                DATOS DE IDENTIFICACIÓN:
                                • Nombre completo
                                • Correo electrónico
                                • Contraseña (encriptada)
                                
                                DATOS DE USO:
                                • Progreso en cursos y lecciones
                                • Puntuación en evaluaciones
                                • Fecha de registro y última actividad
                                • Logros y certificados obtenidos
                                
                                NO recopilamos datos sensibles (origen racial, opiniones políticas, religión, salud, orientación sexual, datos biométricos).
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "3. FINALIDAD DEL TRATAMIENTO",
                            content = """
                                Conforme al artículo 6 de la Ley 29733 (Principio de Finalidad), tus datos serán utilizados EXCLUSIVAMENTE para:
                                
                                ✓ Crear y gestionar tu cuenta de usuario
                                ✓ Personalizar tu experiencia educativa
                                ✓ Registrar tu progreso académico
                                ✓ Emitir certificados de completación
                                ✓ Enviarte notificaciones sobre tu curso
                                ✓ Mejorar nuestros servicios educativos
                                
                                ✗ NO usamos tus datos para:
                                • Publicidad de terceros
                                • Venta o comercialización a terceros
                                • Perfilamiento comercial
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "4. PLAZO DE CONSERVACIÓN",
                            content = """
                                Tus datos personales serán conservados durante:
                                
                                • Mientras tu cuenta esté activa
                                • Hasta 2 años después de la última actividad (cuentas inactivas)
                                • Hasta que solicites la eliminación de tu cuenta
                                
                                Después de estos plazos, los datos serán eliminados de forma segura o anonimizados para fines estadísticos.
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "5. TRANSFERENCIA DE DATOS",
                            content = """
                                Conforme al artículo 15 de la Ley 29733:
                                
                                • NO transferimos tus datos a terceros sin tu consentimiento
                                • Nuestros servidores pueden estar ubicados fuera del Perú (servicios cloud)
                                • En caso de transferencia internacional, garantizamos un nivel de protección equivalente al de la ley peruana
                                • Usamos proveedores que cumplen con estándares de seguridad internacionales
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "6. MEDIDAS DE SEGURIDAD",
                            content = """
                                Conforme al artículo 16 de la Ley 29733, implementamos:
                                
                                MEDIDAS TÉCNICAS:
                                • Encriptación de contraseñas (hash + salt)
                                • Conexiones seguras (HTTPS/TLS)
                                • Firewall y protección contra intrusiones
                                • Copias de seguridad periódicas
                                
                                MEDIDAS ORGANIZATIVAS:
                                • Acceso restringido a datos personales
                                • Políticas de confidencialidad del personal
                                • Procedimientos de respuesta a incidentes
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "7. TUS DERECHOS ARCO",
                            content = """
                                Conforme a los artículos 19-24 de la Ley 29733, tienes derecho a:
                                
                                📖 ACCESO: Conocer qué datos tenemos sobre ti
                                ✏️ RECTIFICACIÓN: Corregir datos inexactos o incompletos
                                🗑️ CANCELACIÓN: Solicitar la eliminación de tus datos
                                ⛔ OPOSICIÓN: Oponerte al tratamiento de tus datos
                                
                                Para ejercer estos derechos:
                                📧 ${LegalConstants.EMAIL_DATOS_PERSONALES}
                                
                                Plazo de respuesta: 20 días hábiles máximo
                                
                                Si no recibes respuesta o no estás conforme, puedes presentar una reclamación ante la Autoridad Nacional de Protección de Datos Personales.
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "8. COOKIES Y TECNOLOGÍAS SIMILARES",
                            content = """
                                Utilizamos cookies esenciales para:
                                • Mantener tu sesión activa
                                • Recordar tus preferencias
                                • Mejorar el rendimiento de la app
                                
                                NO utilizamos cookies de seguimiento publicitario.
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "9. MENORES DE EDAD",
                            content = """
                                Conforme al artículo 27-28 del Reglamento de la Ley 29733:
                                
                                • Menores de 14 años: No pueden registrarse
                                • De 14 a 17 años: Pueden registrarse con su propio consentimiento para esta actividad educativa
                                • Mayores de 18: Plena capacidad
                                
                                El contenido de CyberLearn es apto para adolescentes y jóvenes universitarios.
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "10. MODIFICACIONES",
                            content = """
                                Cualquier modificación a esta política será notificada por email con 15 días de anticipación.
                                
                                Última actualización: ${LegalConstants.FECHA_ACTUALIZACION}
                            """.trimIndent()
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }

                if (!hasScrolledToBottom) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "↓ Desplázate para leer todo",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryCyan,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                enabled = hasScrolledToBottom,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen,
                    disabledContainerColor = SuccessGreen.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (hasScrolledToBottom) "HE SIDO INFORMADO" else "Lee la política...",
                    color = if (hasScrolledToBottom) TextPrimary else TextTertiary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", color = TextSecondary)
            }
        }
    )
}

@Composable
fun DataProcessingConsentDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Obtener fecha y hora actual para el registro del consentimiento
    val currentDateTime = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("es", "PE")).format(Date())
    }

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            hasScrolledToBottom = scrollState.value >= scrollState.maxValue - 50
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceElevated,
        title = {
            Column {
                Text(
                    "✅ Consentimiento para Tratamiento de Datos",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Artículo 5 y 7 - Ley N° 29733",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        // Encabezado destacado
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryCyan.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "DECLARACIÓN DE CONSENTIMIENTO INFORMADO",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Este documento constituye tu consentimiento expreso para el tratamiento de datos personales conforme a la Ley N° 29733 del Perú.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        LegalSection(
                            title = "CARACTERÍSTICAS DEL CONSENTIMIENTO",
                            content = """
                                De conformidad con el artículo 7 del Reglamento de la Ley 29733, mi consentimiento es:
                                
                                ✓ LIBRE: Lo otorgo sin coacción, error ni dolo
                                ✓ PREVIO: Antes de la recopilación de mis datos
                                ✓ EXPRESO: Mediante esta aceptación explícita
                                ✓ INFORMADO: He leído la política de privacidad
                                ✓ INEQUÍVOCO: No admite dudas sobre mi aceptación
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "AUTORIZO EL TRATAMIENTO DE:",
                            content = """
                                Datos de identificación:
                                • Mi nombre completo
                                • Mi correo electrónico
                                • Mi contraseña (almacenada de forma encriptada)
                                
                                Datos de actividad educativa:
                                • Mi progreso en los cursos
                                • Mis puntuaciones y evaluaciones
                                • Mis logros y certificados
                                • Mi actividad en la plataforma
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "FINALIDADES AUTORIZADAS",
                            content = """
                                Autorizo que mis datos sean tratados para:
                                
                                1. Gestión de mi cuenta de usuario
                                2. Personalización de mi experiencia educativa
                                3. Registro de mi progreso académico
                                4. Emisión de certificados de completación
                                5. Comunicaciones sobre mi curso (notificaciones)
                                6. Mejora de los servicios educativos
                                7. Estadísticas anónimas de uso
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "DERECHOS RESERVADOS",
                            content = """
                                Entiendo que conservo los siguientes derechos:
                                
                                📖 ACCESO: Solicitar copia de mis datos
                                ✏️ RECTIFICACIÓN: Corregir información incorrecta
                                🗑️ CANCELACIÓN: Eliminar mis datos
                                ⛔ OPOSICIÓN: Oponerme al tratamiento
                                🔄 REVOCACIÓN: Retirar este consentimiento
                                
                                Para ejercerlos: ${LegalConstants.EMAIL_DATOS_PERSONALES}
                            """.trimIndent()
                        )

                        LegalSection(
                            title = "REVOCACIÓN DEL CONSENTIMIENTO",
                            content = """
                                Entiendo que puedo revocar este consentimiento en cualquier momento, sin efectos retroactivos, enviando un correo a:
                                
                                📧 ${LegalConstants.EMAIL_DATOS_PERSONALES}
                                
                                La revocación puede implicar la imposibilidad de seguir usando la plataforma.
                            """.trimIndent()
                        )

                        Spacer(Modifier.height(16.dp))

                        // Registro del consentimiento
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = SuccessGreen.copy(alpha = 0.1f)
                            ),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📝 REGISTRO DEL CONSENTIMIENTO",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = """
                                        Fecha y hora: $currentDateTime
                                        Versión de términos: ${LegalConstants.VERSION_TERMINOS}
                                        Método: Aceptación electrónica en app
                                        
                                        Al hacer clic en "OTORGO MI CONSENTIMIENTO", confirmo que:
                                        • He leído todos los documentos legales
                                        • Comprendo el tratamiento de mis datos
                                        • Otorgo mi consentimiento voluntariamente
                                    """.trimIndent(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }

                if (!hasScrolledToBottom) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "↓ Desplázate para leer todo",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryCyan,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                enabled = hasScrolledToBottom,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen,
                    disabledContainerColor = SuccessGreen.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (hasScrolledToBottom) "OTORGO MI CONSENTIMIENTO" else "Lee el documento...",
                    color = if (hasScrolledToBottom) TextPrimary else TextTertiary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", color = TextSecondary)
            }
        }
    )
}

@Composable
fun LegalSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryCyan,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            lineHeight = 18.sp
        )
    }
}