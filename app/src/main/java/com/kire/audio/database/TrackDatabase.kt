package com.kire.audio.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kire.audio.models.Track

@Database(
    entities = [Track::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class TrackDatabase : RoomDatabase(){
    abstract val dao: TrackDao
}