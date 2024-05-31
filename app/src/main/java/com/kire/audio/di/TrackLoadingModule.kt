package com.kire.audio.di

import android.content.Context
import com.kire.audio.data.repository.util.TracksLoading
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TrackLoadingModule {

    @Provides
    fun provideTrackLoading(
        @ApplicationContext context: Context
    ): TracksLoading = TracksLoading(context)
}