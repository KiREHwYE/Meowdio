package com.kire.audio.data.repository.functional

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.kire.audio.data.mapper.asTrackDomain
import com.kire.audio.data.model.TrackEntity
import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.presentation.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

class TracksLoading @Inject constructor(
    private val context: Context
) {
    @SuppressLint("Range")
    suspend fun getTracksFromLocalStorage(
        getTrack: suspend (String) -> TrackEntity,
        upsertTrack: suspend (TrackDomain) -> Unit
    ) {

        val cursor: Cursor? = context.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val trackTitle =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val trackId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val trackAlbum =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val trackArtist =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val trackPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val trackDuration =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val trackAlbum_id =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                val date_addedC =
                    File(trackPath).lastModified().toString()

                val imageUriC: Uri? = getAlbumart(trackAlbum_id, context)

                val track = TrackEntity(
                    id = trackId,
                    title = when (trackTitle) {
                        null -> "No title"
                        else -> trackTitle
                    },
                    album = trackAlbum,
                    artist = when (trackArtist) {
                        null -> "Unknown artist"
                        "<unknown>" -> "Unknown artist"
                        else -> trackArtist
                    },
                    path = trackPath,
                    duration = trackDuration,
                    albumId = trackAlbum_id,
                    imageUri = imageUriC,
                    dateAdded = date_addedC,
                    isFavourite = false,
                    defaultImageUri = imageUriC
                )

                if (File(trackPath).exists()) {
                    val existingTrack: TrackEntity = getTrack(track.id)

                    if (existingTrack != null && existingTrack.path != track.path)
                        upsertTrack(track.asTrackDomain())
                    else if (existingTrack == null)
                        upsertTrack(track.asTrackDomain())
                }

            } while (cursor.moveToNext())
        }

        cursor?.close()
    }
}