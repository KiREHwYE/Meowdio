package com.kire.audio.di

import com.kire.audio.domain.use_case.util.ITrackUseCases
import com.kire.audio.domain.use_case.util.TrackUseCases
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackUseCasesModule {

    @Binds
    abstract fun provideTrackUseCases(trackUseCases: TrackUseCases): ITrackUseCases
}