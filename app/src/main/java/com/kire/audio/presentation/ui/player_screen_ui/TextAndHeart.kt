package com.kire.audio.presentation.ui.player_screen_ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder

import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.nonScaledSp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextAndHeart(
    trackUiState: StateFlow<TrackUiState>,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
){
    val coroutineScope = rememberCoroutineScope()

    val trackUiState by trackUiState.collectAsStateWithLifecycle()

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
                .padding(end = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            Text(
                text = trackUiState.currentTrackPlaying?.title ?: "",
                color = Color.White,
                fontSize = 23.sp.nonScaledSp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 0
                    )
            )
            Text(
                text = trackUiState.currentTrackPlaying?.artist ?: "",
                color = Color.LightGray,
                fontSize = 15.sp.nonScaledSp,
                fontWeight = FontWeight.W300,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 0
                    )
            )
        }

        Icon(
            if (trackUiState.currentTrackPlaying?.isFavourite == true) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = "Favourite Button",
            tint = if (trackUiState.currentTrackPlaying?.isFavourite == true) Color.Red else AudioExtendedTheme.extendedColors.playerScreenButton,
            modifier = Modifier
                .size(34.dp)
                .alpha(0.8f)
                .bounceClick {
                    coroutineScope.launch(Dispatchers.IO) {
                        trackUiState.currentTrackPlaying?.let { track ->
                            upsertTrack(track.copy(isFavourite = !track.isFavourite)
                                .also {
                                    changeTrackUiState(trackUiState.copy(currentTrackPlaying = it))
                                }
                            )
                        }
                    }
                }
        )
    }
}