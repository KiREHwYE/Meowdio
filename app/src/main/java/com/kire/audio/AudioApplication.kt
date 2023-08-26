package com.kire.audio

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AudioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}