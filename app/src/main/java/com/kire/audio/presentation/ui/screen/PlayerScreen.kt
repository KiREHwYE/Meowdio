package com.kire.audio.presentation.ui.screen

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.skipTrack
import com.kire.audio.presentation.navigation.PlayerScreenTransitions
import com.kire.audio.presentation.ui.player_screen_ui.Background
import com.kire.audio.presentation.ui.player_screen_ui.FunctionalBlock
import com.kire.audio.presentation.ui.player_screen_ui.Header
import com.kire.audio.presentation.ui.player_screen_ui.ImageLyricsFlipBlock
import com.kire.audio.presentation.ui.player_screen_ui.TextAndHeart
import com.kire.audio.presentation.viewmodel.TrackViewModel
import com.kire.audio.presentation.util.ListSelector

import com.ramcosta.composedestinations.annotation.Destination

@Destination(style = PlayerScreenTransitions::class)
@Composable
fun PlayerScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigateBack: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(start = 28.dp, end = 28.dp)
){

    val trackUiState by trackViewModel.trackUiState.collectAsStateWithLifecycle()

    var currentTrackList = trackViewModel.selectListOfTracks(trackUiState.currentListSelector).collectAsStateWithLifecycle().value

    if (currentTrackList.isEmpty() && (trackUiState.currentListSelector != ListSelector.MAIN_LIST)) {
        trackViewModel.updateTrackUiState(trackUiState.copy(currentListSelector = ListSelector.MAIN_LIST))
        currentTrackList = trackViewModel.selectListOfTracks(ListSelector.MAIN_LIST).collectAsStateWithLifecycle().value
    }

    var duration: Float by remember { mutableFloatStateOf(0f) }

    trackUiState.currentTrackPlaying?.let {
        duration = it.duration.toFloat()
    } ?: 0f

    BackHandler {
        navigateBack()
        return@BackHandler
    }

    Background(imageUri = trackUiState.currentTrackPlaying?.imageUri)

    Column(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
        .pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
                val y = dragAmount

                if (y > 50)
                    navigateBack()
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {

        Header(
            trackUiState = trackUiState,
            changeTrackUiState = trackViewModel::updateTrackUiState,
            upsertTrack = trackViewModel::upsertTrack,
            navigateBack = navigateBack
        )

        ImageLyricsFlipBlock(
            trackUiState = trackUiState,
            lyricsUiState = trackViewModel.lyricsUiState,
            updateLyricsUiState = trackViewModel::updateLyricsUiState,
            updateTrackUiState = trackViewModel::updateTrackUiState,
            upsertTrack = trackViewModel::upsertTrack,
            getTrackLyricsFromGenius = trackViewModel::getTrackLyricsFromGenius
        )

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TextAndHeart(
                trackUiState = trackUiState,
                changeTrackUiState = trackViewModel::updateTrackUiState,
                upsertTrack = trackViewModel::upsertTrack
            )

            FunctionalBlock(
                trackUiState = trackUiState,
                changeTrackUiState = trackViewModel::updateTrackUiState,
                upsertTrack = trackViewModel::upsertTrack,
                saveRepeatMode = trackViewModel::saveRepeatMode,
                skipTrack = { skipTrackAction ->
                    mediaController?.skipTrack(
                        skipTrackAction = skipTrackAction,
                        currentTrackList = currentTrackList,
                        trackUiState = trackUiState,
                        changeTrackUiState = trackViewModel::updateTrackUiState
                    )
                },
                mediaController = mediaController,
                durationGet = { duration },
                play = {
                    mediaController?.apply {
                        if (!trackUiState.isPlaying) {
                            play()
                            trackViewModel.updateTrackUiState(trackUiState.copy(isPlaying = true))
                        }
                        else {
                            pause()
                            trackViewModel.updateTrackUiState(trackUiState.copy(isPlaying = false))
                        }
                    }
                },
                selectListOfTracks = trackViewModel::selectListOfTracks
            )
        }
    }
}










