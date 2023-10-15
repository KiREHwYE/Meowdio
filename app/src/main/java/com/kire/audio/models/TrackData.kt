package com.kire.audio.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track")
data class Track(
    val title: String,
    val album: String?,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val album_id: Long?,
    val imageUri: Uri?,
    val date_added: String?,
    var isFavourite: Boolean,
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
