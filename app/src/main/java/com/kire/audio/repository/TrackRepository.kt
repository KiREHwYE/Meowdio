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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@SuppressLint("Range")
class TrackRepository @Inject constructor(
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

//    suspend fun getTrack(id: String): Track = database.dao.getTrack(id)

//    fun getTracks(): Flow<List<Track>> = database.dao.getTracks()

    fun deleteTrack(track: Track) = database.dao.deleteTrack(track)
    fun getTracksOrderedByDateAddedASC(): Flow<List<Track>> = database.dao.getTracksOrderedByDateAddedASC()
    fun getTracksOrderedByDateAddedDESC(): Flow<List<Track>> = database.dao.getTracksOrderedByDateAddedDESC()
    fun getTracksOrderedByTitleASC(): Flow<List<Track>> = database.dao.getTracksOrderedByTitleASC()
    fun getTracksOrderedByTitleDESC(): Flow<List<Track>> = database.dao.getTracksOrderedByTitleDESC()
    fun getTracksOrderedByArtistASC(): Flow<List<Track>> = database.dao.getTracksOrderedByArtistASC()
    fun getTracksOrderedByArtistDESC(): Flow<List<Track>> = database.dao.getTracksOrderedByArtistDESC()



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
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))

                val imageUriC: Uri? = getAlbumart(album_idC, context)

                val track = Track(
                    id = idC,
                    title = when {
                        titleC == null -> "No title"
                        titleC.length > 27 -> titleC.take(27) + "..."
                        else -> titleC
                    },
                    album = albumC,
                    artist = when {
                        artistC == null -> "Unknown artist"
                        artistC == "<unknown>" -> "Unknown artist"
                        artistC.length > 27 -> artistC.take(27) + "..."
                        else -> artistC
                    },
                    path = pathC,
                    duration = durationC,
                    album_id = album_idC,
                    imageUri = imageUriC,
                    date_added = date_addedC
                )

                if (File(pathC).exists())
                    upsertTrack(track)


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