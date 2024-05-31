package com.kire.audio.data.repository

import android.annotation.SuppressLint

import com.kire.audio.data.mapper.asFlowListOfTracksDomain
import com.kire.audio.data.mapper.asMapAlbumListTrackDomain
import com.kire.audio.data.mapper.asTrackEntity
import com.kire.audio.data.model.TrackEntity
import com.kire.audio.data.repository.util.TracksLoading
import com.kire.audio.data.trackDatabase.TrackDao

import com.kire.audio.domain.repository.ITrackRepository
import com.kire.audio.domain.model.TrackDomain

import com.kire.audio.di.IoDispatcher

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

    override suspend fun getTrack(id: String): TrackEntity {
       return trackDatabaseDao.getTrack(id)
    }
    override suspend fun upsertTrack(track: TrackDomain) {
        return trackDatabaseDao.upsertTrack(track.asTrackEntity())
    }
    override suspend fun deleteTrack(track: TrackEntity) =
        trackDatabaseDao.deleteTrack(track)

    override suspend fun updateIsLoved(track: TrackDomain) =
        trackDatabaseDao.updateIsLoved(track.asTrackEntity())

    override fun getFavouriteTracks(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getFavouriteTracks().asFlowListOfTracksDomain()
    override fun getTracksOrderedByDateAddedASC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByDateAddedASC().asFlowListOfTracksDomain()
    override fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByDateAddedDESC().asFlowListOfTracksDomain()
    override fun getTracksOrderedByTitleASC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByTitleASC().asFlowListOfTracksDomain()
    override fun getTracksOrderedByTitleDESC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByTitleDESC().asFlowListOfTracksDomain()
    override fun getTracksOrderedByArtistASC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByArtistASC().asFlowListOfTracksDomain()
    override fun getTracksOrderedByArtistDESC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByArtistDESC().asFlowListOfTracksDomain()
    override fun getTracksOrderedByDurationASC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByDurationASC().asFlowListOfTracksDomain()
    override fun getTracksOrderedByDurationDESC(): Flow<List<TrackDomain>> =
        trackDatabaseDao.getTracksOrderedByDurationDESC().asFlowListOfTracksDomain()

    override suspend fun getAlbumsWithTracks(): Map<String, List<TrackDomain>> =
        trackDatabaseDao.getAlbumsWithTracks().asMapAlbumListTrackDomain()

    @SuppressLint("Range")
    override suspend fun loadTracksToDatabase() {

        withContext(coroutineDispatcher) {
            tracksLoading.getTracksFromLocalStorage(getTrack = ::getTrack, upsertTrack = ::upsertTrack)
        }
    }

    override suspend fun deleteNoLongerExistingTracksFromDatabase() {

        withContext(coroutineDispatcher) {

            getTracksOrderedByDateAddedASC().collect { tracks ->
                tracks.forEach { track ->
                    if (!File(track.path).exists())
                        deleteTrack(track.asTrackEntity())
                }
            }
        }
    }
}