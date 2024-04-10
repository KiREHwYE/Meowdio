package com.kire.audio.data.repositories.functional

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.kire.audio.ui.state_holders.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class TracksLoading(private val context: Context) {

    private var _tracksFromLocal: MutableStateFlow<Track?> = MutableStateFlow(null)
    val tracksFromLocal: StateFlow<Track?> = _tracksFromLocal.asStateFlow()

    @SuppressLint("Range")
    fun getTracksFromLocal() {

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
                    title = when (titleC) {
                        null -> "No title"
                        else -> titleC
                    },
                    album = albumC,
                    artist = when (artistC) {
                        null -> "Unknown artist"
                        "<unknown>" -> "Unknown artist"
                        else -> artistC
                    },
                    path = pathC,
                    duration = durationC,
                    albumId = album_idC,
                    imageUri = imageUriC,
                    dateAdded = date_addedC,
                    isFavourite = false,
                    defaultImageUri = imageUriC
                )

                if (File(pathC).exists())
                    _tracksFromLocal.value = track

            } while (cursor.moveToNext())
        }
        cursor?.close()
    }

    operator fun invoke() {
        getTracksFromLocal()
    }
}