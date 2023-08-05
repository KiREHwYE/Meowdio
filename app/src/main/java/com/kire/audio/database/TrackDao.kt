package com.kire.audio.database

import androidx.room.*
import com.kire.audio.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Upsert
    fun upsertTrack(track: Track)

//    @Query("SELECT * FROM track WHERE id = (:id)")
//    suspend fun getTrack(id: String): Track

    @Query("SELECT * FROM track")
    fun getTracks(): Flow<List<Track>>

    @Delete
    fun deleteTrack(track: Track)

//    @Query("SELECT * FROM track ORDER BY date_added ASC")
//    fun getTracksOrderedByDateAdded(): Flow<List<Track>>
//
//    @Query("SELECT * FROM track ORDER BY duration ASC")
//    fun getTracksOrderedByDurationAdded(): Flow<List<Track>>
//
//    @Query("SELECT * FROM track ORDER BY title ASC")
//    fun getTracksOrderedByTitleAdded(): Flow<List<Track>>
//
//    @Query("SELECT * FROM track ORDER BY artist ASC")
//    fun getTracksOrderedByArtistAdded(): Flow<List<Track>>

}