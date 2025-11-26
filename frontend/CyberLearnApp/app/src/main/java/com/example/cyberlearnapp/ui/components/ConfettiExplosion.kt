package com.example.cyberlearnapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.*
import com.example.cyberlearnapp.R

@Composable
fun ConfettiExplosion() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    val progress by animateLottieCompositionAsState(composition, iterations = 1)

    LottieAnimation(
        composition = composition,
        progress = { progress }
    )
}