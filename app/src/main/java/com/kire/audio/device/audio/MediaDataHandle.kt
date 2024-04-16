package com.kire.audio.device.audio

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.presentation.models.Track
import com.kire.audio.presentation.viewmodels.TrackViewModel

private fun getMetaDataFromMediaClass(media: Track): MediaMetadata {
    return MediaMetadata.Builder()
        .setTitle(media.title)
        .setAlbumTitle(media.title)
        .setDisplayTitle(media.title)
        .setArtist(media.artist)
        .setAlbumArtist(media.artist)
        .setArtworkUri(media.imageUri)
        .build()
}

fun MediaController.performPlayMedia(media: Track) {
    val metadata = getMetaDataFromMediaClass(media)
    val mediaItem = MediaItem.Builder()
        .setUri(media.path)
        .setMediaId(media.id)
        .setMediaMetadata(metadata)
        .build()

    this.apply {
        setMediaItem(mediaItem)
        prepare()
        play()

        MediaCommands.isPlayRequired.value = true
    }
}