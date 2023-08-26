package com.kire.audio.database

import androidx.room.*
import com.kire.audio.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Upsert
    fun upsertTrack(track: Track)

//    @Query("SELECT * FROM track")
//    fun getTracks(): Flow<List<Track>>

    @Delete
    fun deleteTrack(track: Track)

    @Query("SELECT * FROM track ORDER BY date_added ASC")
    fun getTracksOrderedByDateAddedASC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY date_added DESC")
    fun getTracksOrderedByDateAddedDESC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY title ASC")
    fun getTracksOrderedByTitleASC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY title DESC")
    fun getTracksOrderedByTitleDESC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY artist ASC")
    fun getTracksOrderedByArtistASC(): Flow<List<Track>>

    @Query("SELECT * FROM track ORDER BY artist DESC")
    fun getTracksOrderedByArtistDESC(): Flow<List<Track>>

    //    @Query("SELECT * FROM track WHERE id = (:id)")
    //    suspend fun getTrack(id: String): Track

    //    @Query("SELECT * FROM track ORDER BY duration ASC")
    //    fun getTracksOrderedByDuration(): Flow<List<Track>>
}