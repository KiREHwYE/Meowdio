package com.kire.audio.presentation.models

import android.net.Uri

data class Track(
    val title: String,
    val album: String?,
    val artist: String,
    val duration: Long = 0,
    val lyrics: String = "",
    val path: String,
    val albumId: Long?,
    val imageUri: Uri?,
    val dateAdded: String?,
    val isFavourite: Boolean = false,
    val defaultImageUri: Uri?,
    val id: String
    ) {

    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            title,
            artist,
            "$album"
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}
