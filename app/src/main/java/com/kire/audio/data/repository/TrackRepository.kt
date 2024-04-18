package com.kire.audio.data.repository

import android.annotation.SuppressLint
import com.kire.audio.data.mapper.asFlowListOfTracks
import com.kire.audio.data.mapper.asTrackDomain
import com.kire.audio.data.mapper.asTrackEntity
import com.kire.audio.data.model.TrackEntity

import com.kire.audio.data.repository.functional.TracksLoading
import com.kire.audio.data.trackDatabase.TrackDao
import com.kire.audio.domain.repository.ITrackRepository

import com.kire.audio.di.IoDispatcher
import com.kire.audio.domain.model.TrackDomain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

import java.io.File
import javax.inject.Inject

@SuppressLint("Range")
class TrackRepository @Inject constructor(
    private val trackDatabaseDao: TrackDao,
    private val tracksLoading: TracksLoading,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): ITrackRepository {

    override suspend fun getTrack(id: String): TrackEntity  =
        trackDatabaseDao.getTrack(id)
    override suspend fun upsertTrack(track: TrackDomain) =
        trackDatabaseDao.upsertTrack(track.asTrackEntity())
    override suspend fun deleteTrack(track: TrackEntity) =
        trackDatabaseDao.deleteTrack(track)

    override suspend fun updateIsLoved(track: TrackDomain) =
        trackDatabaseDao.updateIsLoved(track.asTrackEntity())

    override fun getFavouriteTracks(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getFavouriteTracks().asFlowListOfTracks()
    override fun getTracksOrderedByDateAddedASC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByDateAddedASC().asFlowListOfTracks()
    override fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByDateAddedDESC().asFlowListOfTracks()
    override fun getTracksOrderedByTitleASC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByTitleASC().asFlowListOfTracks()
    override fun getTracksOrderedByTitleDESC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByTitleDESC().asFlowListOfTracks()
    override fun getTracksOrderedByArtistASC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByArtistASC().asFlowListOfTracks()
    override fun getTracksOrderedByArtistDESC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByArtistDESC().asFlowListOfTracks()
    override fun getTracksOrderedByDurationASC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByDurationASC().asFlowListOfTracks()
    override fun getTracksOrderedByDurationDESC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByDurationDESC().asFlowListOfTracks()

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

                            upsertTrack(track.asTrackDomain())
                        }
                    }
                }
        }
    }

    override suspend fun deleteNoLongerExistingTracksFromDatabaseUseCase(tracks: List<TrackDomain>){
        tracks.forEach { track ->
            if (!File(track.path).exists())
                deleteTrack(track.asTrackEntity())
        }
    }
}