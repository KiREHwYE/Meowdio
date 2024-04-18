package com.kire.audio.di

import android.content.Context
import androidx.room.Room
import com.kire.audio.data.trackDatabase.TrackDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackDataBaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): TrackDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TrackDatabase::class.java,
            "tracks.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
