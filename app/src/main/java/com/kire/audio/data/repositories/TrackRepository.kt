package com.kire.audio.data.repositories

import android.annotation.SuppressLint
import android.util.Log
import com.kire.audio.data.mappers.asExternalModel
import com.kire.audio.data.mappers.asFlowListOfTracks
import com.kire.audio.data.models.TrackEntity

import com.kire.audio.data.repositories.functional.TracksLoading
import com.kire.audio.data.repositories.interfaces.ITrackRepository

import com.kire.audio.data.trackDatabase.TrackDatabase
import com.kire.audio.presentation.mappers.asEntity
import com.kire.audio.presentation.models.Track
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

    override suspend fun getTrack(id: String): TrackEntity  =
        trackDatabase.dao.getTrack(id)
    override suspend fun upsertTrack(track: TrackEntity) =
        trackDatabase.dao.upsertTrack(track)
    override suspend fun deleteTrack(track: TrackEntity) =
        trackDatabase.dao.deleteTrack(track)

    override suspend fun updateIsLoved(track: TrackEntity) =
        trackDatabase.dao.updateIsLoved(track)

    override fun getFavouriteTracks(): Flow<List<Track>> =
        trackDatabase.dao.getFavouriteTracks().asFlowListOfTracks()
    override fun getTracksOrderedByDateAddedASC(): Flow<List<Track>> =
        trackDatabase.dao.getTracksOrderedByDateAddedASC().asFlowListOfTracks()
    override fun getTracksOrderedByDateAddedDESC(): Flow<List<Track>> =
        trackDatabase.dao.getTracksOrderedByDateAddedDESC().asFlowListOfTracks()
    override fun getTracksOrderedByTitleASC(): Flow<List<Track>> =
        trackDatabase.dao.getTracksOrderedByTitleASC().asFlowListOfTracks()
    override fun getTracksOrderedByTitleDESC(): Flow<List<Track>> =
        trackDatabase.dao.getTracksOrderedByTitleDESC().asFlowListOfTracks()
    override fun getTracksOrderedByArtistASC(): Flow<List<Track>> =
        trackDatabase.dao.getTracksOrderedByArtistASC().asFlowListOfTracks()
    override fun getTracksOrderedByArtistDESC(): Flow<List<Track>> =
        trackDatabase.dao.getTracksOrderedByArtistDESC().asFlowListOfTracks()
    override fun getTracksOrderedByDurationASC(): Flow<List<Track>> =
        trackDatabase.dao.getTracksOrderedByDurationASC().asFlowListOfTracks()
    override fun getTracksOrderedByDurationDESC(): Flow<List<Track>> =
        trackDatabase.dao.getTracksOrderedByDurationDESC().asFlowListOfTracks()

/*    override suspend fun getArtistsWithTracks(): Map<String, List<Track>> = trackDatabase.dao.getArtistsWithTracks()*/

    @SuppressLint("Range")
    override suspend fun loadTracksToDatabase() {

        withContext(coroutineDispatcher) {

            tracksLoading.getTracksFromLocal()
                .also {
                    tracksLoading.tracksFromLocal.collect { track ->

                        track?.let {
                            val existingTrack: TrackEntity = getTrack(track.id)

                            if (existingTrack != null && existingTrack.path == track.path)
                                return@collect

                            upsertTrack(track)
                        }
                    }
                }
        }
    }

    override suspend fun deleteTracksFromDatabase(tracks: List<Track>){
        tracks.forEach { track ->
            if (!File(track.path).exists())
                deleteTrack(track.asEntity())
        }
    }
}