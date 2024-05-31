package com.kire.audio.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track")
data class TrackEntity(
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
    @PrimaryKey
    val id: String
)
