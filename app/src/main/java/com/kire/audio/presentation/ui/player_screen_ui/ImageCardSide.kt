package com.kire.audio.presentation.ui.player_screen_ui

import android.net.Uri

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import coil.compose.AsyncImage

@Composable
fun ImageCardSide(
    imageUri: Uri?
) {
    Crossfade(
        targetState = imageUri,
        label = "Track Image in foreground"
    ) {
        AsyncImage(
            model = it,
            contentDescription = "Track Image in foreground",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
        )
    }
}