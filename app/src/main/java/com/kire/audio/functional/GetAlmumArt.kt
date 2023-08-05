package com.kire.audio.functional

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor

fun getAlbumart(
    album_id: Long?,
    context: Context
): Uri {

    var bitmap: Bitmap? = null
    lateinit var uri: Uri

    try {
        val sArtworkUri = Uri
            .parse("content://media/external/audio/albumart")
        uri = ContentUris.withAppendedId(sArtworkUri, album_id!!)
        val parcelFileDescriptor: ParcelFileDescriptor? = context.getContentResolver().openFileDescriptor(uri, "r")
        if (parcelFileDescriptor != null) {
            val fileDescriptor = parcelFileDescriptor.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        }
        parcelFileDescriptor?.close()
    } catch (exception: Exception) { }

    return if (bitmap == null) Uri.parse("android.resource://com.kire.audio/drawable/music_icon") else uri
}

fun getAlbumartURI(
    album_id: Long?
): Uri? {

    var uri: Uri?

    try {
        val sArtworkUri = Uri
            .parse("content://media/external/audio/albumart")
        uri = ContentUris.withAppendedId(sArtworkUri, album_id!!)

    } catch (exception: Exception) {
        uri = null
    }

    return uri ?: Uri.parse("android.resource://com.kire.audio/drawable/music_icon")
}
