package com.kire.audio.presentation.model

sealed interface ILyricsRequestState {
    data class Success(val lyrics: String): ILyricsRequestState
    data class Unsuccessful(val message: String) : ILyricsRequestState
    data object OnRequest: ILyricsRequestState
}