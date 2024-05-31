package com.kire.audio.device.audio.util

import kotlinx.coroutines.flow.MutableStateFlow

object MediaCommands {
    val isPlayRequired = MutableStateFlow(true)
    val isNextTrackRequired = MutableStateFlow(false)
    val isPreviousTrackRequired = MutableStateFlow(false)
    val isRepeatRequired = MutableStateFlow(false)
    val isTrackRepeated = MutableStateFlow(false)
}