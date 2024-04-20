package com.kire.audio.presentation.screen.list_screen_ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.device.audio.performPlayMedia
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.screen.PlayerScreen
import com.kire.audio.screen.functional.ListSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun BottomPlayer(
    _trackUiState: StateFlow<TrackUiState>,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    saveRepeatMode: (Int) -> Unit,
    selectListOfTracks: (ListSelector) -> StateFlow<List<Track>>,
    mediaController: MediaController,
) {
    val coroutineScope = rememberCoroutineScope()

    val trackUiState by _trackUiState.collectAsStateWithLifecycle()

    var currentTrackList = selectListOfTracks(trackUiState.currentListSelector).collectAsStateWithLifecycle().value

    if (currentTrackList.isEmpty() && (trackUiState.currentListSelector != ListSelector.MAIN_LIST)) {
        changeTrackUiState(trackUiState.copy(currentListSelector = ListSelector.MAIN_LIST))
        currentTrackList = selectListOfTracks(ListSelector.MAIN_LIST).collectAsStateWithLifecycle().value
    }

    var duration: Float by remember { mutableFloatStateOf(0f) }

    trackUiState.currentTrackPlaying?.let {
        duration = it.duration.toFloat()
    }

    val skipTrack: (SkipTrackAction) -> Unit = { skipTrackAction ->

        val newINDEX = trackUiState.currentTrackPlayingIndex?.let { index ->
            skipTrackAction.action(index, currentTrackList.size)
        } ?: 0

        duration = currentTrackList[newINDEX].duration.toFloat()

        changeTrackUiState(
            trackUiState.copy(
                currentTrackPlaying = currentTrackList[newINDEX],
                currentTrackPlayingIndex = newINDEX,
                currentTrackPlayingURI = currentTrackList[newINDEX].path
            )
        )

        mediaController.apply {
            if (mediaController.isPlaying)
                stop()

            performPlayMedia(currentTrackList[newINDEX])
        }
    }

    AutoSkipOnRepeatMode(
        trackUiState = trackUiState,
        mediaController = mediaController,
        skipTrack = skipTrack
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
                skipTrack = skipTrack,
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
                    changeTrackUiState(trackUiState.copy(isPlayerScreenExpanded = true))
                },
                onDragDown = {
                    changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = false))
                }
            )
        }


        AnimatedVisibility(
            visible = trackUiState.isPlayerScreenExpanded,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(250, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(250, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(280, easing = FastOutSlowInEasing))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount

                            if (y > 50 && x < 40 && x > -40) {
                                coroutineScope.launch(Dispatchers.IO) {
                                    changeTrackUiState(trackUiState.copy(isPlayerScreenExpanded = false))
                                }
                            }
                        }
                    }
            ) {

                BackHandler {
                    coroutineScope.launch(Dispatchers.IO) {
                        changeTrackUiState(trackUiState.copy(isPlayerScreenExpanded = false))
                    }
                    return@BackHandler
                }


                PlayerScreen(
                    trackUiState = trackUiState,
                    changeTrackUiState = changeTrackUiState,
                    upsertTrack = upsertTrack,
                    skipTrack = skipTrack,
                    saveRepeatMode = saveRepeatMode,
                    selectListTracks = selectListOfTracks,
                    mediaController = mediaController,
                    play = {
                        mediaController.apply {
                            if (!trackUiState.isPlaying) {
                                play()
                                changeTrackUiState(trackUiState.copy(isPlaying = true))
                            }
                            else {
                                pause()
                                changeTrackUiState(trackUiState.copy(isPlaying = false))
                            }
                        }
                    }
                )
            }
        }
    }
}