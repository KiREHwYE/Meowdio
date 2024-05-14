package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.ILyricsRequestStateDomain
import com.kire.audio.presentation.model.ILyricsRequestState

fun ILyricsRequestStateDomain.asILyricsRequestState() =
    when(this) {
        is ILyricsRequestStateDomain.Success -> ILyricsRequestState.Success(this.lyrics)
        is ILyricsRequestStateDomain.Unsuccess -> ILyricsRequestState.Unsuccessful
        is ILyricsRequestStateDomain.onRequest -> ILyricsRequestState.OnRequest
    }