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
import javax.inject.Inject
data class TrackUseCases @Inject constructor(
    override val getSortedTracksUseCase: GetSortedTracksUseCase,
    override val getFavouriteTracksUseCase: GetFavouriteTracksUseCase,
    override val saveSortOptionUseCase: SaveSortOptionUseCase,
    override val readSortOptionUseCase: ReadSortOptionUseCase,
    override val saveRepeatModeUseCase: SaveRepeatModeUseCase,
    override val readRepeatModeUseCase: ReadRepeatModeUseCase,
    override val upsertTrackUseCase: UpsertTrackUseCase,
    override val updateTrackDataBaseUseCase: UpdateTrackDataBaseUseCase,
    override val getTrackLyricsFromGenius: GetTrackLyricsFromGenius,
    override val getAlbumsWithTracksUseCase: GetAlbumsWithTracksUseCase
): ITrackUseCases
