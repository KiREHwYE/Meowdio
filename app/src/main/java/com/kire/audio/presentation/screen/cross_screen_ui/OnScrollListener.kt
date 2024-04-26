package com.kire.audio.presentation.screen.cross_screen_ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import com.kire.audio.presentation.model.TrackUiState

@Composable
fun OnScrollListener(
    listState: LazyListState,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit
){
    var previousVisibleItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    listState.apply {
        LaunchedEffect(firstVisibleItemIndex){
            if (firstVisibleItemIndex - previousVisibleItemIndex > 1){
                changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = false))
                previousVisibleItemIndex = firstVisibleItemIndex
            }
            trackUiState.currentTrackPlaying?.let {
                if (firstVisibleItemIndex - previousVisibleItemIndex < -1){
                    changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = true))
                    previousVisibleItemIndex = firstVisibleItemIndex
                }
            }
        }
    }
}