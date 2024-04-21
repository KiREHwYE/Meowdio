package com.kire.audio.presentation.model

sealed interface ILyricsRequestState {
    data class Success(val lyrics: String): ILyricsRequestState
    data object Unsuccess: ILyricsRequestState
    data object onRequest: ILyricsRequestState
}