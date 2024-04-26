package com.kire.audio.presentation.screen.cross_screen_ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.kire.audio.device.audio.skipTrack
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.screen.destinations.Destination
import com.kire.audio.presentation.screen.destinations.PlayerScreenDestination
import com.kire.audio.presentation.screen.list_screen_ui.PlayerBottomFloatingCard
import com.kire.audio.screen.functional.ListSelector
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BottomBar(
    trackUiState: StateFlow<TrackUiState>,
    mediaController: MediaController?,
    selectListOfTracks: (ListSelector) -> StateFlow<List<Track>>,
    changeTrackUiState: (TrackUiState) -> Unit,
    navigateTo: (Destination) -> Unit

) {
    val trackUiState by trackUiState.collectAsStateWithLifecycle()

    val currentTrackList by selectListOfTracks(trackUiState.currentListSelector).collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = trackUiState.isPlayerBottomCardShown,
        enter = slideInVertically(
            initialOffsetY = { 120 },
            animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(durationMillis = 100)),
        exit = slideOutVertically(
            targetOffsetY = { 120 },
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(durationMillis = 100))
    ) {

        PlayerBottomFloatingCard(
            trackUiState = trackUiState,
            skipTrack = { skipTrackAction ->
                mediaController?.skipTrack(
                    skipTrackAction = skipTrackAction,
                    currentTrackList = currentTrackList,
                    trackUiState = trackUiState,
                    changeTrackUiState = changeTrackUiState
                )
            },
            playOrPause = {
                if (!trackUiState.isPlaying) {
                    mediaController?.play()
                    changeTrackUiState(trackUiState.copy(isPlaying = true))
                } else {
                    mediaController?.pause()
                    changeTrackUiState(trackUiState.copy(isPlaying = false))
                }
            },
            onTap = {
                changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = false))
                navigateTo(PlayerScreenDestination)
            },
            onDragDown = {
                changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = false))
            }
        )
    }
}