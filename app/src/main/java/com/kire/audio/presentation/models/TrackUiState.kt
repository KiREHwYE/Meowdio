package com.kire.audio.presentation.models

import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.screen.functional.ListSelector

data class TrackUiState(
    val isPlaying: Boolean = false,
    val currentTrackPlaying: Track? = null,
    val currentTrackPlayingURI: String? = null,
    val currentTrackPlayingIndex: Int? = null,
    val trackRepeatMode: RepeatMode = RepeatMode.REPEAT_ONCE,
    val currentListSelector: ListSelector = ListSelector.MAIN_LIST,
    val isPlayerScreenExpanded: Boolean = false,
    val isPlayerBottomCardShown: Boolean = false
)