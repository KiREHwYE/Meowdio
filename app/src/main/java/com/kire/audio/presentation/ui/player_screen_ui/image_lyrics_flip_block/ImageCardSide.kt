package com.kire.audio.presentation.ui.player_screen_ui.image_lyrics_flip_block

import android.net.Uri

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource

import coil.compose.AsyncImage
import com.kire.audio.R

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
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner)))
        )
    }
}