package com.kire.audio

import android.app.Application
import com.kire.audio.repository.TrackRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AudioApplication : Application() {

    override fun onCreate() {
        super.onCreate()

//        TrackRepository.initialize(this@AudioApplication)
    }
}