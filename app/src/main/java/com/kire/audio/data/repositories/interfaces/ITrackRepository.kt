package com.kire.audio.data.repositories.interfaces

import com.kire.audio.data.models.TrackEntity
import com.kire.audio.presentation.models.Track
import kotlinx.coroutines.flow.Flow

interface ITrackRepository {

    suspend fun getTrack(id: String): TrackEntity

    suspend fun upsertTrack(track: TrackEntity)
    suspend fun deleteTrack(track: TrackEntity)

    suspend fun updateIsLoved(track: TrackEntity)

    fun getFavouriteTracks(): Flow<List<Track>>
    fun getTracksOrderedByDateAddedASC(): Flow<List<Track>>
    fun getTracksOrderedByDateAddedDESC(): Flow<List<Track>>
    fun getTracksOrderedByTitleASC(): Flow<List<Track>>
    fun getTracksOrderedByTitleDESC(): Flow<List<Track>>
    fun getTracksOrderedByArtistASC(): Flow<List<Track>>
    fun getTracksOrderedByArtistDESC(): Flow<List<Track>>
    fun getTracksOrderedByDurationASC(): Flow<List<Track>>
    fun getTracksOrderedByDurationDESC(): Flow<List<Track>>

//    suspend fun getArtistsWithTracks(): Map<String, List<Track>>

    suspend fun loadTracksToDatabase()
    suspend fun deleteTracksFromDatabase(tracks :List<Track>)
}