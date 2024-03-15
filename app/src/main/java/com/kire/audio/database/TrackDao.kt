package com.kire.audio.database

import androidx.room.*
import com.kire.audio.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Upsert
    fun upsertTrack(track: Track)
    @Update
    suspend fun updateIsLoved(track: Track)

    @Delete
    fun deleteTrack(track: Track)

    @Query("SELECT * FROM track WHERE isFavourite LIKE :value")
    fun getFavouriteTracks(value: Boolean = true): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY dateAdded ASC")
    fun getTracksOrderedByDateAddedASC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY dateAdded DESC")
    fun getTracksOrderedByDateAddedDESC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY title ASC")
    fun getTracksOrderedByTitleASC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY title DESC")
    fun getTracksOrderedByTitleDESC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY artist ASC")
    fun getTracksOrderedByArtistASC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY artist DESC")
    fun getTracksOrderedByArtistDESC(): Flow<List<Track>>
    @Query("SELECT * FROM track ORDER BY duration ASC")
    fun getTracksOrderedByDurationASC(): Flow<List<Track>>
    @Query("SELECT * FROM track ORDER BY duration DESC")
    fun getTracksOrderedByDurationDESC(): Flow<List<Track>>
    @Query("SELECT * FROM track WHERE id = (:id)")
    fun getTrack(id: String): Track


}