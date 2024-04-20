package com.kire.audio.presentation.screen.list_screen_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.session.MediaController
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.presentation.model.TrackUiState
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

@Composable
fun AutoSkipOnRepeatMode(
    trackUiState: TrackUiState,
    mediaController: MediaController,
    skipTrack: (SkipTrackAction) -> Unit
){
    var sliderPosition by remember {
        mutableFloatStateOf(mediaController.currentPosition.toFloat())
    }

    var minutesCur by remember { mutableLongStateOf(TimeUnit.MILLISECONDS.toMinutes(mediaController.currentPosition)) }
    var secondsCur by  remember { mutableLongStateOf((TimeUnit.MILLISECONDS.toSeconds(mediaController.currentPosition) % 60)) }
    var minutesAll by remember { mutableLongStateOf(TimeUnit.MILLISECONDS.toMinutes(mediaController.duration)) }
    var secondsAll by remember { mutableLongStateOf((TimeUnit.MILLISECONDS.toSeconds(mediaController.duration) % 60)) }

    LaunchedEffect(Unit) {
        while(true) {
            sliderPosition = mediaController.currentPosition.toFloat()
            delay(1.seconds / 70)
        }
    }

    LaunchedEffect(key1 = sliderPosition){
        minutesCur = TimeUnit.MILLISECONDS.toMinutes(mediaController.currentPosition)
        secondsCur = TimeUnit.MILLISECONDS.toSeconds(mediaController.currentPosition) % 60
        minutesAll = TimeUnit.MILLISECONDS.toMinutes(mediaController.duration)
        secondsAll = TimeUnit.MILLISECONDS.toSeconds(mediaController.duration) % 60
    }

    LaunchedEffect(minutesCur.toInt() == minutesAll.toInt()
            && secondsCur.toInt() == secondsAll.toInt() &&
            !(minutesAll.toInt() == 0 && secondsAll.toInt() == 0)
    ) {

        if (minutesCur.toInt() == minutesAll.toInt()
            && secondsCur.toInt() == secondsAll.toInt() &&
            !(minutesAll.toInt() == 0 && secondsAll.toInt() == 0)
        )
            when (trackUiState.trackRepeatMode) {
                RepeatMode.REPEAT_ONCE -> skipTrack(SkipTrackAction.NEXT)
                    .also { MediaCommands.isTrackRepeated.value = false }

                RepeatMode.REPEAT_TWICE -> {
                    if (!MediaCommands.isTrackRepeated.value)
                        skipTrack(SkipTrackAction.REPEAT)
                            .also { MediaCommands.isTrackRepeated.value = true }
                    else
                        skipTrack(SkipTrackAction.NEXT)
                            .also { MediaCommands.isTrackRepeated.value = false }
                }
                RepeatMode.REPEAT_CYCLED -> skipTrack(SkipTrackAction.REPEAT)
                    .also { MediaCommands.isTrackRepeated.value = false }
            }
    }
}