package com.kire.audio.presentation.ui.cross_screen_ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.navigation.NavHostController
import com.kire.audio.device.audio.media_controller.skipTrack
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.screen.destinations.PlayerScreenDestination
import com.kire.audio.presentation.util.ListSelector
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PlayerBottomBar(
    trackUiState: StateFlow<TrackUiState>,
    mediaController: MediaController?,
    selectListOfTracks: (ListSelector) -> StateFlow<List<Track>>,
    changeTrackUiState: (TrackUiState) -> Unit,
    navHostController: NavHostController,
) {

    val _trackUiState by trackUiState.collectAsStateWithLifecycle()

    val currentTrackList by selectListOfTracks(_trackUiState.currentListSelector).collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = _trackUiState.isPlayerBottomCardShown &&
                navHostController.currentDestination?.route != PlayerScreenDestination.route,
        enter = slideInVertically(
            initialOffsetY = { 120 },
            animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(durationMillis = 100)),
        exit = slideOutVertically(
            targetOffsetY = { 120 },
            animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(durationMillis = 100))
    ) {

        PlayerBottomFloatingCard(
            trackUiState = trackUiState,
            skipTrack = { skipTrackAction ->
                mediaController?.skipTrack(
                    skipTrackAction = skipTrackAction,
                    currentTrackList = currentTrackList,
                    trackUiState = _trackUiState,
                    updateTrackUiState = changeTrackUiState
                )
            },
            playOrPause = {
                if (!_trackUiState.isPlaying) {
                    mediaController?.play()
                    changeTrackUiState(_trackUiState.copy(isPlaying = true))
                } else {
                    mediaController?.pause()
                    changeTrackUiState(_trackUiState.copy(isPlaying = false))
                }
            },
            onTap = {
                navHostController.navigate(PlayerScreenDestination)
                changeTrackUiState(_trackUiState.copy(isPlayerBottomCardShown = false))
            },
            onDragDown = {
                changeTrackUiState(_trackUiState.copy(isPlayerBottomCardShown = false))
            }
        )
    }
}