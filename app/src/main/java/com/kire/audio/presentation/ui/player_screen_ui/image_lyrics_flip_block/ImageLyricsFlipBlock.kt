package com.kire.audio.presentation.ui.player_screen_ui.image_lyrics_flip_block

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kire.audio.R

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.LyricsUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.util.CardFace
import com.kire.audio.presentation.util.LyricsRequestMode

import kotlinx.coroutines.flow.StateFlow

@Composable
fun ImageLyricsFlipBlock(
    trackUiState: TrackUiState,
    lyricsUiState: StateFlow<LyricsUiState>,
    updateTrackUiState: (TrackUiState) -> Unit,
    updateLyricsUiState: (LyricsUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    getTrackLyricsFromGenius: suspend (LyricsRequestMode, String?, String?, String?) -> ILyricsRequestState
){

    val lyricsUiState by lyricsUiState.collectAsStateWithLifecycle()

    var cardFace by rememberSaveable {
        mutableStateOf(CardFace.Front)
    }

    if (cardFace == CardFace.Back)
        LaunchedEffect(trackUiState.currentTrackPlaying?.path) {
            if (trackUiState.currentTrackPlaying?.lyrics !is ILyricsRequestState.Success
                || trackUiState.currentTrackPlaying?.lyrics.lyrics.isEmpty())

                trackUiState.currentTrackPlaying?.let { track ->
                    updateTrackUiState(trackUiState.copy(currentTrackPlaying = track.copy(lyrics = ILyricsRequestState.OnRequest)))
                    updateTrackUiState(trackUiState.copy(
                        currentTrackPlaying =
                        track.copy(
                            lyrics = getTrackLyricsFromGenius(
                                LyricsRequestMode.AUTOMATIC,
                                trackUiState.currentTrackPlaying.title,
                                trackUiState.currentTrackPlaying.artist,
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
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner))),
        front = {
            ImageCardSide(trackUiState.currentTrackPlaying?.imageUri)
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