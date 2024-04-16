package com.kire.audio.data.trackDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kire.audio.data.models.TrackEntity
import com.kire.audio.presentation.models.Track

@Database(
    entities = [TrackEntity::class],
    version = 6
)
@TypeConverters(Converters::class)
abstract class TrackDatabase : RoomDatabase(){
    abstract val dao: TrackDao

    companion object {
        private var instance: TrackDatabase? = null

        @Synchronized
        fun getInstance(context: Context): TrackDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackDatabase::class.java,
                    "tracks.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!

        }
    }
}