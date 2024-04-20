package com.kire.audio.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.screen.functional.ListSelector
import com.kire.audio.presentation.screen.player_screen_ui.Background
import com.kire.audio.presentation.screen.player_screen_ui.FunctionalBlock
import com.kire.audio.presentation.screen.player_screen_ui.ImageAndTextBlock

import kotlinx.coroutines.flow.StateFlow

@Composable
fun PlayerScreen(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    skipTrack: (SkipTrackAction)->Unit,
    saveRepeatMode: (Int) -> Unit,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    play: () -> Unit,
    mediaController: MediaController,
){

    var duration: Float by remember { mutableFloatStateOf(0f) }

    trackUiState.currentTrackPlaying?.let {
        duration = it.duration.toFloat()
    } ?: 0f
    
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ){

        Background(imageUri = trackUiState.currentTrackPlaying?.imageUri)

        Column(modifier = Modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.86f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {


            ImageAndTextBlock(
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                upsertTrack = upsertTrack
            )

            FunctionalBlock(
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                upsertTrack = upsertTrack,
                saveRepeatMode = saveRepeatMode,
                skipTrack = skipTrack,
                mediaController = mediaController,
                durationGet = { duration },
                play = play,
                selectListTracks = selectListTracks
            )
        }
    }
}










