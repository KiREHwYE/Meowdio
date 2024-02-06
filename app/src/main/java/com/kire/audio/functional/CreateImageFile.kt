package com.kire.audio.functional

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

fun Context.createImageFile(): File {

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"

    val image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg", /* suffix */
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)    /* directory */
    )

    return image
}