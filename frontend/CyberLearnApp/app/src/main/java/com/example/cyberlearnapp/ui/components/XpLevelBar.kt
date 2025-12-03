package com.example.cyberlearnapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun XpLevelBar(
    currentXp: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    // ✅ CORRECCIÓN: Backend usa 250 XP por nivel
    val xpPerLevel = 250
    val xpInCurrentLevel = currentXp % xpPerLevel
    val xpNeededForNextLevel = xpPerLevel - xpInCurrentLevel
    val progress = xpInCurrentLevel.toFloat() / xpPerLevel.toFloat()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        ),
        border = BorderStroke(2.dp, Color(0xFF00D9FF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF00D9FF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "🎓",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Nivel $level",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "$xpInCurrentLevel/$xpPerLevel XP",
                            color = Color(0xFF00D9FF),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = Color(0xFF00D9FF),
                trackColor = Color(0xFF2D3748),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "$xpNeededForNextLevel XP para siguiente nivel",
                color = Color.White.copy(0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}