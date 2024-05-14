package com.kire.audio.presentation.ui.cross_screen_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.presentation.model.TrackUiState

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive

import java.util.concurrent.TimeUnit

import kotlin.time.Duration.Companion.seconds

@Composable
fun AutoSkipOnRepeatMode(
    trackUiState: StateFlow<TrackUiState>,
    mediaController: MediaController?
){
    val trackUiState by trackUiState.collectAsStateWithLifecycle()

    var minutesCurrent by remember { mutableLongStateOf(0L)}
    var secondsCurrent by  remember { mutableLongStateOf(0L) }
    var minutesAll by remember { mutableLongStateOf(0L) }
    var secondsAll by remember { mutableLongStateOf(0L) }

    LaunchedEffect(key1 = trackUiState.currentTrackPlaying?.path) {

        minutesAll = TimeUnit.MILLISECONDS.toMinutes(trackUiState.currentTrackPlaying?.duration ?: 0L)
        secondsAll = TimeUnit.MILLISECONDS.toSeconds(trackUiState.currentTrackPlaying?.duration ?: 0L) % 60

        MediaCommands.isTrackRepeated.value = false

        while (isActive) {
            minutesCurrent = TimeUnit.MILLISECONDS.toMinutes(mediaController?.currentPosition ?: 0L)
            secondsCurrent = TimeUnit.MILLISECONDS.toSeconds(mediaController?.currentPosition ?: 0L) % 60

            if ((minutesCurrent >= minutesAll && secondsCurrent >= secondsAll)
                && !(minutesAll == 0L && secondsAll == 0L)
            ) {
                when (trackUiState.trackRepeatMode) {
                    RepeatMode.REPEAT_ONCE ->
                        MediaCommands.isNextTrackRequired.value = true

                    RepeatMode.REPEAT_TWICE -> {
                        if (!MediaCommands.isTrackRepeated.value) {
                            MediaCommands.isTrackRepeated.value = true
                            MediaCommands.isRepeatRequired.value = true
                        } else
                            MediaCommands.isNextTrackRequired.value = true
                    }
                    RepeatMode.REPEAT_CYCLED ->
                        MediaCommands.isRepeatRequired.value = true
                }
            }

            delay(1.seconds / 70)
        }
    }
}