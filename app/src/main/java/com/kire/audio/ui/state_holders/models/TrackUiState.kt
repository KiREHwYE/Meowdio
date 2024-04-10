package com.kire.audio.ui.state_holders.models

data class TrackUiState(
    val isPlaying: Boolean = false,
    val currentTrackPlaying: Track? = null,
    val currentTrackPlayingURI: String? = null,
    val currentTrackPlayingIndex: Int? = null
)