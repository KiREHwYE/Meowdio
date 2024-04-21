package com.kire.audio.presentation.mapper

import com.kire.audio.domain.util.LyricsRequestModeDomain
import com.kire.audio.presentation.util.LyricsRequestMode

fun LyricsRequestMode.asLyricsRequestModeDomain() =
    LyricsRequestModeDomain.valueOf(this.toString())