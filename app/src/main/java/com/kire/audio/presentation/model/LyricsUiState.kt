package com.kire.audio.presentation.model

import com.kire.audio.presentation.util.LyricsRequestMode

data class LyricsUiState(
    val userInput: String = "",
    val isEditModeEnabled: Boolean = false,
    val lyricsRequestMode: LyricsRequestMode = LyricsRequestMode.AUTOMATIC
)
