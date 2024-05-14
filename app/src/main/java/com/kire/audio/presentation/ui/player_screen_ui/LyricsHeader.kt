package com.kire.audio.presentation.ui.player_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kire.audio.R
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.LyricsUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.LyricsRequestMode

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LyricsHeader(
    upsertTrack: suspend (Track) -> Unit,
    trackUiState: TrackUiState,
    clearUserInput: () -> Unit,
    lyricsRequest: (LyricsRequestMode) -> Unit,
    lyricsUiState: LyricsUiState,
    updateTrackUiState: (TrackUiState) -> Unit,
    updateLyricsUiState: (LyricsUiState) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    lyricsUiState.apply {

        Column(
            modifier = Modifier
                .padding(top = 28.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){

                Box(
                    modifier = Modifier
                        .size(20.dp)
                ) {

                    if (isEditModeEnabled && lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT)
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "",
                            tint = AudioExtendedTheme.extendedColors.orangeAccents,
                            modifier = Modifier
                                .fillMaxSize()
                                .bounceClick {
                                    clearUserInput()
                                }
                        )
                    else if (lyricsRequestMode != LyricsRequestMode.SELECTOR_IS_VISIBLE && trackUiState.currentTrackPlaying?.lyrics == ILyricsRequestState.Unsuccessful) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "",
                            tint = AudioExtendedTheme.extendedColors.orangeAccents,
                            modifier = Modifier
                                .fillMaxSize()
                                .bounceClick {
                                    lyricsRequest(LyricsRequestMode.AUTOMATIC)
                                }
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.lyrics_dialog_header),
                    fontWeight = FontWeight.W700,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = AudioExtendedTheme.extendedColors.primaryText
                )

                Icon(
                    imageVector = if (!isEditModeEnabled) Icons.Rounded.Edit else Icons.Rounded.Save,
                    contentDescription = "",
                    tint = AudioExtendedTheme.extendedColors.orangeAccents,
                    modifier = Modifier
                        .size(20.dp)
                        .bounceClick {
                            updateLyricsUiState(
                                this@apply.copy(
                                    lyricsRequestMode =
                                    if (!isEditModeEnabled)
                                        LyricsRequestMode.SELECTOR_IS_VISIBLE
                                    else
                                        LyricsRequestMode.AUTOMATIC,
                                    isEditModeEnabled = !isEditModeEnabled
                                        .also {

                                            if (userInput.isNotEmpty()) {

                                                if (((lyricsRequestMode == LyricsRequestMode.BY_LINK || lyricsRequestMode == LyricsRequestMode.BY_TITLE_AND_ARTIST) && it))
                                                    lyricsRequest(lyricsRequestMode)
                                                else if (lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT && it)

                                                    trackUiState.currentTrackPlaying
                                                        ?.copy(
                                                            lyrics = ILyricsRequestState.Success(
                                                                userInput
                                                            )
                                                        )
                                                        .also { track ->
                                                            updateTrackUiState(
                                                                trackUiState.copy(
                                                                    currentTrackPlaying = track
                                                                )
                                                            )
                                                        }
                                                        ?.let { track ->
                                                            coroutineScope.launch(Dispatchers.IO) {
                                                                upsertTrack(track)
                                                            }
                                                        }
                                            }
                                        }
                                )
                            )
                        }
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .clip(
                        RoundedCornerShape(28.dp)
                    ),
                thickness = 4.dp,
                color = AudioExtendedTheme.extendedColors.divider
            )
        }
    }
}