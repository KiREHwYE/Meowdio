package com.kire.audio.presentation.ui.player_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.util.ListSelector
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FunctionalBlock(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    skipTrack: (SkipTrackAction) -> Unit,
    selectListOfTracks: (ListSelector) -> StateFlow<List<Track>>,
    saveRepeatMode: (Int) -> Unit,
    durationGet: () -> Float,
    mediaController: MediaController?,
    play: () -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        SliderBlock(
            durationGet = durationGet,
            mediaController = mediaController
        )

        ControlBlock(
            trackUiState = trackUiState,
            changeTrackUiState = changeTrackUiState,
            upsertTrack = upsertTrack,
            skipTrack = skipTrack,
            saveRepeatMode = saveRepeatMode,
            play = play,
            mediaController = mediaController,
            selectListOfTracks = selectListOfTracks
        )
    }
}