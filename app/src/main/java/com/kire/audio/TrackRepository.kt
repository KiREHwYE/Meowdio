package com.kire.audio

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.room.Room
import com.kire.audio.database.TrackDatabase
import com.kire.audio.functional.getAlbumart
import com.kire.audio.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

@SuppressLint("Range")
class TrackRepository private constructor(context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            TrackDatabase::class.java,
            "tracks.db"
        ).build()
    }

    private fun upsertTrack(track: Track) = database.dao.upsertTrack(track)

//    suspend fun getTrack(id: String): Track = database.dao.getTrack(id)

    fun getTracks(): Flow<List<Track>> = database.dao.getTracks()

    fun deleteTrack(track: Track) = database.dao.deleteTrack(track)

//    fun getTracksOrderedByDateAdded(): Flow<List<Track>> = database.dao.getTracksOrderedByDateAdded()
//
//    fun getTracksOrderedByDurationAdded(): Flow<List<Track>> = database.dao.getTracksOrderedByDurationAdded()
//
//    fun getTracksOrderedByTitleAdded(): Flow<List<Track>> = database.dao.getTracksOrderedByTitleAdded()
//
//    fun getTracksOrderedByArtistAdded(): Flow<List<Track>> = database.dao.getTracksOrderedByArtistAdded()




    @SuppressLint("Range")
    fun loadTracksToDatabase(context: Context){

        val bitmapIfNull =
            BitmapFactory.decodeResource(context.getResources(), R.drawable.music_icon)

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
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))

                val imageUriC: Uri? = getAlbumart(album_idC, context)

                val track = Track(
                    id = idC,
                    title = when {
                        titleC == null -> "No title"
                        titleC.length > 40 -> titleC.take(40) + "..."
                        else -> titleC
                    },
                    album = albumC,
                    artist = when {
                        artistC == null -> "Unknown artist"
                        artistC == "<unknown>" -> "Unknown artist"
                        artistC.length > 40 -> artistC.take(40) + "..."
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


    companion object {
        @Volatile private var INSTANCE: TrackRepository? = null

        fun initialize(context: Context) : TrackRepository{
            return INSTANCE ?: synchronized(this){
                TrackRepository(context).also {
                    INSTANCE = it
                }
            }
        }

        fun get(): TrackRepository {
            return INSTANCE
                ?: throw java.lang.IllegalStateException("NoteRepository must be initialized")
        }
    }
}