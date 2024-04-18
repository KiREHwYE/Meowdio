package com.kire.audio.data.trackDatabase

import androidx.room.*
import com.kire.audio.data.model.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Upsert
    fun upsertTrack(track: TrackEntity)
    @Update
    suspend fun updateIsLoved(track: TrackEntity)
    @Delete
    fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM track WHERE isFavourite LIKE :value")
    fun getFavouriteTracks(value: Boolean = true): Flow<List<TrackEntity>>

    @Query("SELECT * FROM track ORDER BY dateAdded ASC")
    fun getTracksOrderedByDateAddedASC(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM track ORDER BY dateAdded DESC")
    fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM track ORDER BY title ASC")
    fun getTracksOrderedByTitleASC(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM track ORDER BY title DESC")
    fun getTracksOrderedByTitleDESC(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM track ORDER BY artist ASC")
    fun getTracksOrderedByArtistASC(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM track ORDER BY artist DESC")
    fun getTracksOrderedByArtistDESC(): Flow<List<TrackEntity>>
    @Query("SELECT * FROM track ORDER BY duration ASC")
    fun getTracksOrderedByDurationASC(): Flow<List<TrackEntity>>
    @Query("SELECT * FROM track ORDER BY duration DESC")
    fun getTracksOrderedByDurationDESC(): Flow<List<TrackEntity>>
    @Query("SELECT * FROM track WHERE id = (:id)")
    fun getTrack(id: String): TrackEntity

//    @Query("SELECT * FROM track")
//    fun getArtistsWithTracks():
//            Map<@MapColumn(columnName = "artist") String,
//                    List<TrackEntity>>
}