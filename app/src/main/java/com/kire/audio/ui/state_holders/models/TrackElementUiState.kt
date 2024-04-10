package com.kire.audio.ui.state_holders.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track")
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
    val isFavourite: Boolean,
    val defaultImageUri: Uri?,
    @PrimaryKey
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
