package com.kire.audio

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class AudioApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        TrackRepository.initialize(this@AudioApplication)
    }
}