package com.kire.audio.domain.model

data class TrackDomain(
    val title: String,
    val album: String?,
    val artist: String,
    val duration: Long = 0,
    val lyrics: String = "",
    val path: String,
    val albumId: Long?,
    val imageUri: String?,
    val dateAdded: String?,
    val isFavourite: Boolean = false,
    val defaultImageUri: String?,
    val id: String
)
