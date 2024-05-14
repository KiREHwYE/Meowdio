package com.kire.audio.domain.repository

import com.kire.audio.data.model.TrackEntity

import com.kire.audio.domain.model.TrackDomain

import kotlinx.coroutines.flow.Flow

interface ITrackRepository {

    suspend fun getTrack(id: String): TrackEntity

    suspend fun upsertTrack(track: TrackDomain)
    suspend fun deleteTrack(track: TrackEntity)

    suspend fun updateIsLoved(track: TrackDomain)

    fun getFavouriteTracks(): Flow<List<TrackDomain>>
    fun getTracksOrderedByDateAddedASC(): Flow<List<TrackDomain>>
    fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackDomain>>
    fun getTracksOrderedByTitleASC(): Flow<List<TrackDomain>>
    fun getTracksOrderedByTitleDESC(): Flow<List<TrackDomain>>
    fun getTracksOrderedByArtistASC(): Flow<List<TrackDomain>>
    fun getTracksOrderedByArtistDESC(): Flow<List<TrackDomain>>
    fun getTracksOrderedByDurationASC(): Flow<List<TrackDomain>>
    fun getTracksOrderedByDurationDESC(): Flow<List<TrackDomain>>

    suspend fun getAlbumsWithTracks(): Map<String, List<TrackDomain>>

    suspend fun loadTracksToDatabase()
    suspend fun deleteNoLongerExistingTracksFromDatabase()
}