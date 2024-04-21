package com.kire.audio.domain.use_case

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
}