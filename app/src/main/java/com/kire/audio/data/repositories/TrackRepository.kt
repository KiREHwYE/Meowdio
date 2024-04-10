package com.kire.audio.data.repositories

import android.annotation.SuppressLint

import com.kire.audio.data.repositories.functional.TracksLoading

import com.kire.audio.data.trackDatabase.TrackDatabase
import com.kire.audio.ui.state_holders.models.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

import java.io.File

@SuppressLint("Range")
class TrackRepository(
    private val trackDatabase: TrackDatabase,
    private val tracksLoading: TracksLoading,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): ITrackRepository {

    override suspend fun getTrack(id: String): Track = trackDatabase.dao.getTrack(id)
    override suspend fun upsertTrack(track: Track) = trackDatabase.dao.upsertTrack(track)
    override suspend fun deleteTrack(track: Track) = trackDatabase.dao.deleteTrack(track)

    override suspend fun updateIsLoved(track: Track) = trackDatabase.dao.updateIsLoved(track)

    override fun getFavouriteTracks(): Flow<List<Track>> = trackDatabase.dao.getFavouriteTracks()
    override fun getTracksOrderedByDateAddedASC(): Flow<List<Track>> = trackDatabase.dao.getTracksOrderedByDateAddedASC()
    override fun getTracksOrderedByDateAddedDESC(): Flow<List<Track>> = trackDatabase.dao.getTracksOrderedByDateAddedDESC()
    override fun getTracksOrderedByTitleASC(): Flow<List<Track>> = trackDatabase.dao.getTracksOrderedByTitleASC()
    override fun getTracksOrderedByTitleDESC(): Flow<List<Track>> = trackDatabase.dao.getTracksOrderedByTitleDESC()
    override fun getTracksOrderedByArtistASC(): Flow<List<Track>> = trackDatabase.dao.getTracksOrderedByArtistASC()
    override fun getTracksOrderedByArtistDESC(): Flow<List<Track>> = trackDatabase.dao.getTracksOrderedByArtistDESC()
    override fun getTracksOrderedByDurationASC(): Flow<List<Track>> = trackDatabase.dao.getTracksOrderedByDurationASC()
    override fun getTracksOrderedByDurationDESC(): Flow<List<Track>> = trackDatabase.dao.getTracksOrderedByDurationDESC()

    @SuppressLint("Range")
    override suspend fun loadTracksToDatabase() {

        withContext(coroutineDispatcher) {
            tracksLoading().also {
                tracksLoading.tracksFromLocal.collect { track ->

                    track?.let {
                        val existingTrack: Track = getTrack(track.id)

                        if (existingTrack != null && existingTrack.path == track.path)
                            return@collect

                        upsertTrack(track)
                    }
                }
            }

        }
    }

    override suspend fun deleteTracksFromDatabase(tracks :List<Track>){
        tracks.forEach { track ->
            if (!File(track.path).exists())
                deleteTrack(track)
        }
    }
}