package com.kire.audio.functional

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("dd|MM|yyyy", Locale.getDefault())
    return format.format(date)
}