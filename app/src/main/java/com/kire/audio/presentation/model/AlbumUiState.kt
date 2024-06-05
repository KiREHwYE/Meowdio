package com.kire.audio.presentation.model

data class AlbumUiState(
    val tracks: List<Track> = emptyList(),
    val albumTitle: String = "No title"
)
