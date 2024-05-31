package com.kire.audio.domain.use_case.util

import com.kire.audio.domain.use_case.GetAlbumsWithTracksUseCase
import com.kire.audio.domain.use_case.GetFavouriteTracksUseCase
import com.kire.audio.domain.use_case.GetSortedTracksUseCase
import com.kire.audio.domain.use_case.GetTrackLyricsFromGenius
import com.kire.audio.domain.use_case.ReadRepeatModeUseCase
import com.kire.audio.domain.use_case.ReadSortOptionUseCase
import com.kire.audio.domain.use_case.SaveRepeatModeUseCase
import com.kire.audio.domain.use_case.SaveSortOptionUseCase
import com.kire.audio.domain.use_case.UpdateTrackDataBaseUseCase
import com.kire.audio.domain.use_case.UpsertTrackUseCase

sealed interface ITrackUseCases {
    val getSortedTracksUseCase: GetSortedTracksUseCase
    val getFavouriteTracksUseCase: GetFavouriteTracksUseCase
    val saveSortOptionUseCase: SaveSortOptionUseCase
    val readSortOptionUseCase: ReadSortOptionUseCase
    val saveRepeatModeUseCase: SaveRepeatModeUseCase
    val readRepeatModeUseCase: ReadRepeatModeUseCase
    val upsertTrackUseCase: UpsertTrackUseCase
    val updateTrackDataBaseUseCase: UpdateTrackDataBaseUseCase
    val getTrackLyricsFromGenius: GetTrackLyricsFromGenius
    val getAlbumsWithTracksUseCase: GetAlbumsWithTracksUseCase
}