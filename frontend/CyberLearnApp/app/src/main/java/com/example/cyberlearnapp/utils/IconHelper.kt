package com.example.cyberlearnapp.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Mapea los strings del JSON ("shield", "router") a Iconos de Material Design
fun getIconByName(name: String): ImageVector {
    return when (name.lowercase().trim()) {
        "security", "shield" -> Icons.Default.Security
        "attach_money" -> Icons.Default.AttachMoney
        "schedule", "access_time" -> Icons.Default.Schedule
        "public", "language" -> Icons.Default.Public
        "group", "people" -> Icons.Default.Group
        "credit_card" -> Icons.Default.CreditCard
        "speed" -> Icons.Default.Speed
        "router" -> Icons.Default.Router
        "layers" -> Icons.Default.Layers
        "sync_alt" -> Icons.Default.SyncAlt
        "flash_on" -> Icons.Default.FlashOn
        "dns" -> Icons.Default.Dns
        "lock", "vpn_key" -> Icons.Default.Lock
        "verified", "verified_user" -> Icons.Default.VerifiedUser
        "fingerprint" -> Icons.Default.Fingerprint
        "history" -> Icons.Default.History
        "bug_report" -> Icons.Default.BugReport
        "warning" -> Icons.Default.Warning
        "science" -> Icons.Default.Science
        "gavel" -> Icons.Default.Gavel
        "computer" -> Icons.Default.Computer
        "search" -> Icons.Default.Search
        "healing" -> Icons.Default.Healing
        "assignment" -> Icons.Default.Assignment
        "calculate" -> Icons.Default.Calculate
        "balance" -> Icons.Default.Balance
        "trending_down" -> Icons.Default.TrendingDown
        "help_outline", "help" -> Icons.AutoMirrored.Filled.HelpOutline
        "manage_accounts" -> Icons.Default.ManageAccounts
        "campaign" -> Icons.Default.Campaign
        "settings" -> Icons.Default.Settings
        "corporate_fare" -> Icons.Default.CorporateFare
        "business_center" -> Icons.Default.BusinessCenter
        "crisis_alert" -> Icons.Default.Warning // Fallback si no existe CrisisAlert en versiones viejas
        "percent" -> Icons.Default.Percent
        "description" -> Icons.Default.Description
        "visibility_off" -> Icons.Default.VisibilityOff
        "emergency" -> Icons.Default.MedicalServices // Aproximación
        else -> Icons.Default.Info // Icono por defecto si no encuentra coincidencia
    }
}