package com.kire.audio.di

import com.kire.audio.data.repository.TrackRepository
import com.kire.audio.domain.repository.ITrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackRepositoryModule {

    @Binds
    abstract fun provideTrackRepository(trackRepository: TrackRepository): ITrackRepository
}