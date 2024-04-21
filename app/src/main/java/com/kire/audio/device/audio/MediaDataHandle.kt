package com.kire.audio.device.audio

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState

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

        if (isPlaying)
            stop()

        setMediaItem(mediaItem)
        prepare()
        play()

        MediaCommands.isPlayRequired.value = true
    }
}

fun MediaController.skipTrack(skipTrackAction: SkipTrackAction, currentTrackList: List<Track>, trackUiState: TrackUiState, changeTrackUiState: (TrackUiState) -> Unit){

    val newINDEX = trackUiState.currentTrackPlayingIndex?.let { index ->
        skipTrackAction.action(index, currentTrackList.size)
    } ?: 0

    changeTrackUiState(
        trackUiState.copy(
            currentTrackPlaying = currentTrackList[newINDEX],
            currentTrackPlayingIndex = newINDEX,
            currentTrackPlayingURI = currentTrackList[newINDEX].path
        )
    )

    performPlayMedia(currentTrackList[newINDEX])
}