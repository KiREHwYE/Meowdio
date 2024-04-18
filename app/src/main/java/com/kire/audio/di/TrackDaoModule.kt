package com.kire.audio.di

import com.kire.audio.data.trackDatabase.TrackDao
import com.kire.audio.data.trackDatabase.TrackDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TrackDaoModule {

    @Provides
    fun provideTrackDao(trackDatabase: TrackDatabase): TrackDao =
        trackDatabase.dao
}