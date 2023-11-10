package com.kire.audio.repository

import android.annotation.SuppressLint

import android.content.Context

import android.database.Cursor

import android.net.Uri

import android.provider.MediaStore

import androidx.room.Room

import com.kire.audio.database.TrackDatabase
import com.kire.audio.functional.getAlbumart
import com.kire.audio.models.Track

import kotlinx.coroutines.flow.Flow

import java.io.File

@SuppressLint("Range")
class TrackRepository(
    private val context: Context
) {



    private val database by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            TrackDatabase::class.java,
            "tracks.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }



    private fun upsertTrack(track: Track) = database.dao.upsertTrack(track)
    fun getTrack(id: String): Track = database.dao.getTrack(id)

    suspend fun updateIsLoved(track: Track) = database.dao.updateIsLoved(track)

    fun getFavouriteTracks(): Flow<List<Track>> = database.dao.getFavouriteTracks()

    fun deleteTrack(track: Track) = database.dao.deleteTrack(track)
    fun getTracksOrderedByDateAddedASC(): Flow<List<Track>> = database.dao.getTracksOrderedByDateAddedASC()
    fun getTracksOrderedByDateAddedDESC(): Flow<List<Track>> = database.dao.getTracksOrderedByDateAddedDESC()
    fun getTracksOrderedByTitleASC(): Flow<List<Track>> = database.dao.getTracksOrderedByTitleASC()
    fun getTracksOrderedByTitleDESC(): Flow<List<Track>> = database.dao.getTracksOrderedByTitleDESC()
    fun getTracksOrderedByArtistASC(): Flow<List<Track>> = database.dao.getTracksOrderedByArtistASC()
    fun getTracksOrderedByArtistDESC(): Flow<List<Track>> = database.dao.getTracksOrderedByArtistDESC()
    fun getTracksOrderedByDurationASC(): Flow<List<Track>> = database.dao.getTracksOrderedByDurationASC()
    fun getTracksOrderedByDurationDESC(): Flow<List<Track>> = database.dao.getTracksOrderedByDurationDESC()








    @SuppressLint("Range")
    fun loadTracksToDatabase(context: Context){

        val cursor: Cursor? = context.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val titleC =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val albumC =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artistC =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val durationC =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val album_idC =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                val date_addedC =
                    File(pathC).lastModified().toString()

                val imageUriC: Uri? = getAlbumart(album_idC, context)

                val track = Track(
                    id = idC,
                    title = when {
                        titleC == null -> "No title"
                        else -> titleC
                    },
                    album = albumC,
                    artist = when {
                        artistC == null -> "Unknown artist"
                        artistC == "<unknown>" -> "Unknown artist"
                        else -> artistC
                    },
                    path = pathC,
                    duration = durationC,
                    album_id = album_idC,
                    imageUri = imageUriC,
                    date_added = date_addedC,
                    isFavourite = false
                )

                if (File(pathC).exists()) {

                    val existingTrack: Track = getTrack(idC)

                    if (
                        existingTrack != null &&
                        existingTrack.title == titleC &&
                        existingTrack.artist == artistC &&
                        existingTrack.album == albumC &&
                        existingTrack.album_id == album_idC &&
                        existingTrack.imageUri == imageUriC &&
                        existingTrack.duration == durationC &&
                        existingTrack.date_added == date_addedC &&
                        existingTrack.path == pathC
                    )
                        continue

                    upsertTrack(track)
                }

            } while (cursor.moveToNext())
        }

        cursor?.close()
    }



    fun deleteTracksFromDatabase(tracks :List<Track>){
        tracks.forEach { track ->
            if (!File(track.path).exists())
                deleteTrack(track)
        }
    }
}