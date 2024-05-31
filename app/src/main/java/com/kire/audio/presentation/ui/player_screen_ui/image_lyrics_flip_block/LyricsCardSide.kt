package com.kire.audio.presentation.ui.player_screen_ui.image_lyrics_flip_block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kire.audio.R

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.LyricsUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.LyricsRequestMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun LyricsCardSide(
    trackUiState: StateFlow<TrackUiState>,
    lyricsUiState: LyricsUiState,
    updateTrackUiState: (TrackUiState) -> Unit,
    updateLyricsUiState: (LyricsUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    getTrackLyricsFromGenius: suspend (LyricsRequestMode, String?, String?, String?) -> ILyricsRequestState,
    modifier: Modifier
) {

    val _trackUiState by trackUiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    lyricsUiState.apply {

        var isClearNeeded by remember {
            mutableStateOf(false)
        }

        val lyricsRequest: (lyricsRequestMode: LyricsRequestMode) -> Unit = {

            coroutineScope.launch(Dispatchers.IO) {
                    _trackUiState.currentTrackPlaying?.copy(lyrics = ILyricsRequestState.OnRequest)
                        .also { track ->
                            updateTrackUiState(
                                _trackUiState.copy(
                                    currentTrackPlaying = track
                                )
                            )
                        }?.let { track -> upsertTrack(track) }


                    _trackUiState.currentTrackPlaying?.copy(
                        lyrics =
                        getTrackLyricsFromGenius(
                            lyricsRequestMode,
                            _trackUiState.currentTrackPlaying!!.title,
                            _trackUiState.currentTrackPlaying!!.artist,
                            userInput
                        )
                    ).also { track ->
                        updateTrackUiState(
                            _trackUiState.copy(
                                currentTrackPlaying = track
                            )
                        )
                    }?.let { track ->
                        upsertTrack(track)
                    }
            }
        }

        LaunchedEffect(key1 = isEditModeEnabled) {
            if (!isEditModeEnabled)
                updateLyricsUiState(this@apply.copy(userInput = ""))
        }

        LaunchedEffect(key1 = lyricsRequestMode) {
            if (lyricsRequestMode == LyricsRequestMode.AUTOMATIC)
                updateLyricsUiState(this@apply.copy(isEditModeEnabled = false))
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = AudioExtendedTheme.extendedColors.controlElementsBackground,
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.app_rounded_corner))
                )
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom =
                    if (!isEditModeEnabled && _trackUiState.currentTrackPlaying?.lyrics is ILyricsRequestState.OnRequest
                        || lyricsRequestMode == LyricsRequestMode.SELECTOR_IS_VISIBLE)
                        0.dp
                    else 28.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    if (!isEditModeEnabled && _trackUiState.currentTrackPlaying?.lyrics is ILyricsRequestState.OnRequest)
                        0.dp
                    else 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LyricsHeader(
                upsertTrack = upsertTrack,
                trackUiState = trackUiState,
                clearUserInput = {
                    isClearNeeded = true
                },
                updateTrackUiState = updateTrackUiState,
                lyricsRequest = lyricsRequest,
                lyricsUiState = lyricsUiState,
                updateLyricsUiState = updateLyricsUiState
            )

            if (isEditModeEnabled && lyricsRequestMode == LyricsRequestMode.SELECTOR_IS_VISIBLE)
                LyricsEditOptions(
                    lyricsRequest = {
                        lyricsRequest(LyricsRequestMode.AUTOMATIC)
                    },
                    updateLyricsRequestMode = {
                        updateLyricsUiState(lyricsUiState.copy(lyricsRequestMode = it))
                    },
                    modifier = Modifier.weight(1f)
                )

            else if (isEditModeEnabled && lyricsRequestMode != LyricsRequestMode.AUTOMATIC)

                LyricsPickedEditOption(
                    isClearNeeded = isClearNeeded,
                    lyricsRequestMode = lyricsRequestMode,
                    lyrics = _trackUiState.currentTrackPlaying?.lyrics ?: ILyricsRequestState.OnRequest,
                    updateUserInput = {
                        updateLyricsUiState(lyricsUiState.copy(userInput = it))
                    },
                    changeIsClearNeeded = {
                        isClearNeeded  = true
                    },
                    modifier = Modifier.weight(1f, fill = false)
                )

            if (!isEditModeEnabled && lyricsRequestMode != LyricsRequestMode.SELECTOR_IS_VISIBLE)
                LyricsResult(
                    lyrics = _trackUiState.currentTrackPlaying?.lyrics ?: ILyricsRequestState.OnRequest,
                    modifier = Modifier
                        .weight(1f)
                )
        }
    }
}