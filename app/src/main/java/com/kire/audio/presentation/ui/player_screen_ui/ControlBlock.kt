package com.kire.audio.presentation.ui.player_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.kire.audio.R
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.ListSelector
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ControlBlock(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    skipTrack: (SkipTrackAction) -> Unit,
    mediaController: MediaController?,
    saveRepeatMode: (Int) -> Unit,
    selectListOfTracks: (ListSelector) -> StateFlow<List<Track>>,
    play: () -> Unit
) {

    var openDialog by remember {
        mutableStateOf(false)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        Icon(
            when (trackUiState.trackRepeatMode) {
                RepeatMode.REPEAT_ONCE -> Icons.Rounded.Repeat
                RepeatMode.REPEAT_TWICE -> Icons.Rounded.RepeatOne
                RepeatMode.REPEAT_CYCLED -> Icons.Rounded.RepeatOn
            },
            contentDescription = "RepeatMode",
            tint = AudioExtendedTheme.extendedColors.playerScreenButton,
            modifier = Modifier
                .size(30.dp)
                .alpha(0.7f)
                .bounceClick {
                    changeTrackUiState(trackUiState
                        .copy(
                            trackRepeatMode = RepeatMode
                                .entries[
                                ((trackUiState.trackRepeatMode.ordinal + 1) % 3)
                                    .also { rep ->
                                        saveRepeatMode(rep)
                                    }
                            ]
                        )
                    )
                }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            Icon(
                painter = painterResource(id = R.drawable.skip_previous_button),
                contentDescription = "Previous",
                tint = AudioExtendedTheme.extendedColors.playerScreenButton,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(0.78f)
                    .bounceClick {
                        skipTrack(SkipTrackAction.PREVIOUS)
                            .also { MediaCommands.isTrackRepeated.value = false }
                    }
            )

            Icon(
                painter = painterResource(id =
                if(trackUiState.isPlaying)
                    R.drawable.pause_button
                else
                    R.drawable.play_button
                ),
                contentDescription = "Play",
                tint = AudioExtendedTheme.extendedColors.playerScreenButton,
                modifier = Modifier
                    .size(63.dp)
                    .alpha(0.8f)
                    .bounceClick {
                        play()
                    }
            )

            Icon(
                painter = painterResource(id = R.drawable.skip_next_button),
                contentDescription = "Next",
                tint = AudioExtendedTheme.extendedColors.playerScreenButton,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(0.78f)
                    .bounceClick {
                        skipTrack(SkipTrackAction.NEXT)
                            .also { MediaCommands.isTrackRepeated.value = false }
                    }
            )
        }

        Icon(
            Icons.AutoMirrored.Rounded.PlaylistPlay,
            contentDescription = "Playlist",
            modifier = Modifier
                .size(30.dp)
                .alpha(0.7f)
                .bounceClick {
                    openDialog = !openDialog
                },
            tint = AudioExtendedTheme.extendedColors.playerScreenButton,
        )


        if (openDialog)
            DialogFavourite(
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                mediaController = mediaController,
                favouriteTracks = selectListOfTracks(ListSelector.FAVOURITE_LIST),
                upsertTrack = upsertTrack,
                changeOpenDialog = {isIt ->
                    openDialog = isIt
                }
            )
    }
}