package com.kire.audio.presentation.ui.player_screen_ui.image_lyrics_flip_block

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

import com.kire.audio.presentation.util.CardFace

@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit = {},
    back: @Composable (Modifier) -> Unit = {}
) {

    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 450,
            easing = LinearOutSlowInEasing,
        ),
        label = "FlipCard's Rotation"
    )

    Card(
        onClick = {
            onClick(cardFace)
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent
        ),
        modifier = modifier
            .graphicsLayer {
                rotationY = -rotation.value
                cameraDistance = 32f * density
            }
            .background(Color.Transparent)
    ) {

        if (rotation.value <= 90f)
            front()

        back(
            Modifier
                .graphicsLayer { rotationY = 180f }
                .alpha(1f)
        )
    }
}