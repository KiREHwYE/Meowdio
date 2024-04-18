package com.kire.audio.di

import com.kire.audio.data.repository.PreferencesRepository
import com.kire.audio.domain.repository.IPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesRepositoryModule {

    @Binds
    abstract fun providePreferencesRepository(preferencesRepository: PreferencesRepository): IPreferencesRepository
}