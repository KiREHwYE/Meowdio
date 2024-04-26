package com.kire.audio.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGestures

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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController
import com.kire.audio.device.audio.skipTrack

import com.kire.audio.presentation.navigation.PlayerScreenTransitions
import com.kire.audio.presentation.screen.player_screen_ui.Background
import com.kire.audio.presentation.screen.player_screen_ui.FunctionalBlock
import com.kire.audio.presentation.screen.player_screen_ui.ImageAndTextBlock
import com.kire.audio.presentation.viewmodel.TrackViewModel
import com.kire.audio.screen.functional.ListSelector

import com.ramcosta.composedestinations.annotation.Destination

@Destination(style = PlayerScreenTransitions::class)
@Composable
fun PlayerScreen(
    viewModel: TrackViewModel,
    mediaController: MediaController?,
    navigateBack: () -> Unit
){

    val trackUiState by viewModel.trackUiState.collectAsStateWithLifecycle()

    var currentTrackList = viewModel.selectListOfTracks(trackUiState.currentListSelector).collectAsStateWithLifecycle().value

    if (currentTrackList.isEmpty() && (trackUiState.currentListSelector != ListSelector.MAIN_LIST)) {
        viewModel.changeTrackUiState(trackUiState.copy(currentListSelector = ListSelector.MAIN_LIST))
        currentTrackList = viewModel.selectListOfTracks(ListSelector.MAIN_LIST).collectAsStateWithLifecycle().value
    }

    var duration: Float by remember { mutableFloatStateOf(0f) }

    trackUiState.currentTrackPlaying?.let {
        duration = it.duration.toFloat()
    } ?: 0f

    BackHandler {
        navigateBack()
        return@BackHandler
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    val (x, y) = dragAmount

                    if (y > 50 && x < 40 && x > -40) {
                        navigateBack()
                    }
                }
            },
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
                navigateBack = navigateBack,
                changeTrackUiState = viewModel::changeTrackUiState,
                upsertTrack = viewModel::upsertTrack,
                getTrackLyricsFromGenius = viewModel::getTrackLyricsFromGenius
            )

            FunctionalBlock(
                trackUiState = trackUiState,
                changeTrackUiState = viewModel::changeTrackUiState,
                upsertTrack = viewModel::upsertTrack,
                saveRepeatMode = viewModel::saveRepeatMode,
                skipTrack = { skipTrackAction ->
                    mediaController?.skipTrack(
                        skipTrackAction = skipTrackAction,
                        currentTrackList = currentTrackList,
                        trackUiState = trackUiState,
                        changeTrackUiState = viewModel::changeTrackUiState
                    )
                },
                mediaController = mediaController!!,
                durationGet = { duration },
                play = {
                    mediaController.apply {
                        if (!trackUiState.isPlaying) {
                            play()
                            viewModel.changeTrackUiState(trackUiState.copy(isPlaying = true))
                        }
                        else {
                            pause()
                            viewModel.changeTrackUiState(trackUiState.copy(isPlaying = false))
                        }
                    }
                },
                selectListOfTracks = viewModel::selectListOfTracks
            )
        }
    }
}










