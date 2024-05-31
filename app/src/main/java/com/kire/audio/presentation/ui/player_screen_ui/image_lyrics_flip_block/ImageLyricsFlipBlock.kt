package com.kire.audio.presentation.ui.player_screen_ui.image_lyrics_flip_block

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kire.audio.R

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.LyricsUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.util.CardFace
import com.kire.audio.presentation.util.LyricsRequestMode

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Composable
fun ImageLyricsFlipBlock(
    trackUiState: StateFlow<TrackUiState>,
    lyricsUiState: StateFlow<LyricsUiState>,
    updateTrackUiState: (TrackUiState) -> Unit,
    updateLyricsUiState: (LyricsUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    getTrackLyricsFromGenius: suspend (LyricsRequestMode, String?, String?, String?) -> ILyricsRequestState
){

    val _trackUiState by trackUiState.collectAsStateWithLifecycle()
    val lyricsUiState by lyricsUiState.collectAsStateWithLifecycle()

    var cardFace by rememberSaveable {
        mutableStateOf(CardFace.Front)
    }

    if (cardFace == CardFace.Back)
        LaunchedEffect(_trackUiState.currentTrackPlaying?.path) {
            if (_trackUiState.currentTrackPlaying?.lyrics !is ILyricsRequestState.Success
                || (_trackUiState.currentTrackPlaying?.lyrics as ILyricsRequestState.Success).lyrics.isEmpty())

                _trackUiState.currentTrackPlaying?.let { track ->
                    updateTrackUiState(_trackUiState.copy(currentTrackPlaying = track.copy(lyrics = ILyricsRequestState.OnRequest)))
                    updateTrackUiState(_trackUiState.copy(
                        currentTrackPlaying =
                        track.copy(
                            lyrics = getTrackLyricsFromGenius(
                                LyricsRequestMode.AUTOMATIC,
                                _trackUiState.currentTrackPlaying!!.title,
                                _trackUiState.currentTrackPlaying!!.artist,
                                lyricsUiState.userInput
                            )
                        ).also {
                            if (it.lyrics is ILyricsRequestState.Success)
                                upsertTrack(it)
                        }
                    ))
                }
        }


    FlipCard(
        cardFace = cardFace,
        onClick = {
            if (!lyricsUiState.isEditModeEnabled)
                cardFace = cardFace.next
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1f)
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.app_rounded_corner))),
        front = {
            ImageCardSide(_trackUiState.currentTrackPlaying?.imageUri)
        },
        back = { graphicModifier ->

            LyricsCardSide(
                trackUiState = trackUiState,
                lyricsUiState = lyricsUiState,
                updateTrackUiState = updateTrackUiState,
                updateLyricsUiState = updateLyricsUiState,
                upsertTrack = upsertTrack,
                getTrackLyricsFromGenius = getTrackLyricsFromGenius,
                modifier = graphicModifier
            )
        }
    )
}