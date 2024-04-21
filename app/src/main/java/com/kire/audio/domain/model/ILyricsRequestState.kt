package com.kire.audio.domain.model

sealed interface ILyricsRequestStateDomain {
    data class Success(val lyrics: String): ILyricsRequestStateDomain
    data object Unsuccess: ILyricsRequestStateDomain
    data object onRequest: ILyricsRequestStateDomain
}