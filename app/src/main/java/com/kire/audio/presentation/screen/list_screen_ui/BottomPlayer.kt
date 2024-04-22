package com.kire.audio.presentation.screen.list_screen_ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.skipTrack
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.screen.destinations.PlayerScreenDestination
import com.kire.audio.screen.functional.ListSelector

import com.ramcosta.composedestinations.navigation.DestinationsNavigator

import kotlinx.coroutines.flow.StateFlow

@Composable
fun BottomPlayer(
    trackUiState: StateFlow<TrackUiState>,
    changeTrackUiState: (TrackUiState) -> Unit,
    navigator: DestinationsNavigator,
    selectListOfTracks: (ListSelector) -> StateFlow<List<Track>>,
    mediaController: MediaController,
) {
    val trackUiState by trackUiState.collectAsStateWithLifecycle()

    var currentTrackList = selectListOfTracks(trackUiState.currentListSelector).collectAsStateWithLifecycle().value

    if (currentTrackList.isEmpty() && (trackUiState.currentListSelector != ListSelector.MAIN_LIST)) {
        changeTrackUiState(trackUiState.copy(currentListSelector = ListSelector.MAIN_LIST))
        currentTrackList = selectListOfTracks(ListSelector.MAIN_LIST).collectAsStateWithLifecycle().value
    }

    var duration: Float by remember { mutableFloatStateOf(0f) }

    trackUiState.currentTrackPlaying?.let {
        duration = it.duration.toFloat()
    }

    AutoSkipOnRepeatMode(
        trackUiState = trackUiState,
        mediaController = mediaController,
        skipTrack = { skipTrackAction ->
            mediaController.skipTrack(
                skipTrackAction = skipTrackAction,
                currentTrackList = currentTrackList,
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState
            )
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        AnimatedVisibility(
            visible = trackUiState.isPlayerBottomCardShown,
            enter = slideInVertically(
                initialOffsetY = { 120 },
                animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(durationMillis = 100)),
            exit = slideOutVertically(
                targetOffsetY = { 120 },
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(durationMillis = 90))
        ) {

            PlayerBottomFloatingCard(
                trackUiState = trackUiState,
                skipTrack = { skipTrackAction ->
                    mediaController.skipTrack(
                        skipTrackAction = skipTrackAction,
                        currentTrackList = currentTrackList,
                        trackUiState = trackUiState,
                        changeTrackUiState = changeTrackUiState
                    )
                },
                playOrPause = {
                    if (!trackUiState.isPlaying) {
                        mediaController.play()
                        changeTrackUiState(trackUiState.copy(isPlaying = true))
                    } else {
                        mediaController.pause()
                        changeTrackUiState(trackUiState.copy(isPlaying = false))
                    }
                },
                onTap = {
                        navigator.navigate(PlayerScreenDestination)
                },
                onDragDown = {
                    changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = false))
                }
            )
        }
    }
}