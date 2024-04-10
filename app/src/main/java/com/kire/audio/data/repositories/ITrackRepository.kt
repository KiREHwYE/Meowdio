package com.kire.audio.data.repositories

import com.kire.audio.ui.state_holders.models.Track
import kotlinx.coroutines.flow.Flow

interface ITrackRepository {

    suspend fun getTrack(id: String): Track

    suspend fun upsertTrack(track: Track)
    suspend fun deleteTrack(track: Track)

    suspend fun updateIsLoved(track: Track)

    fun getFavouriteTracks(): Flow<List<Track>>
    fun getTracksOrderedByDateAddedASC(): Flow<List<Track>>
    fun getTracksOrderedByDateAddedDESC(): Flow<List<Track>>
    fun getTracksOrderedByTitleASC(): Flow<List<Track>>
    fun getTracksOrderedByTitleDESC(): Flow<List<Track>>
    fun getTracksOrderedByArtistASC(): Flow<List<Track>>
    fun getTracksOrderedByArtistDESC(): Flow<List<Track>>
    fun getTracksOrderedByDurationASC(): Flow<List<Track>>
    fun getTracksOrderedByDurationDESC(): Flow<List<Track>>

    suspend fun loadTracksToDatabase()
    suspend fun deleteTracksFromDatabase(tracks :List<Track>)
}