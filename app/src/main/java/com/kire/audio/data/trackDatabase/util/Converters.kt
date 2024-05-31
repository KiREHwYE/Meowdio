package com.kire.audio.data.trackDatabase.util

import android.net.Uri
import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun toStringFromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUriFromString(string: String?): Uri? {
        return if (string == null) null else Uri.parse(string)
    }
}