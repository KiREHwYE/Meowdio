package com.kire.audio.presentation.screen.player_screen_ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
            durationMillis = 600,
            easing = FastOutSlowInEasing,
        ),
        label = "FlipCardRotation"
    )

    Card(
        onClick = { onClick(cardFace) },
        modifier = modifier
            .graphicsLayer {
                rotationY = -rotation.value
                cameraDistance = 12f * density
            },
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