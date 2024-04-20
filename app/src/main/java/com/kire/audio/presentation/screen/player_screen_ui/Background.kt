package com.kire.audio.presentation.screen.player_screen_ui

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kire.audio.screen.functional.getContext

@Composable
fun Background(
    imageUri: Uri?
){
    val context = getContext()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){}

    Crossfade(
        targetState = imageUri,
        label = "Background Image"
    ) {

        AsyncImage(
            model = it,
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply{
                if (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                    setToScale(0.35f,0.35f,0.35f,1f)
                else setToScale(0.7f,0.7f,0.7f,1f)
            }),
            modifier = Modifier
                .fillMaxWidth()
                .blur(10.dp)
                .alpha(1f)
        )

    }
}