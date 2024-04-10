package com.kire.audio.ui.state_holders.models

import com.kire.audio.mediaHandling.functional.RepeatMode

data class BottomSheetUiState(
    val isExpanded: Boolean = false,
    val isShown: Boolean = false,
    val repeatMode: RepeatMode? = null
)
