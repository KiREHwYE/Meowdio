package com.kire.audio.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.kire.audio.viewmodels.TrackListViewModel

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val stopMessage = intent?.getStringExtra("STOP")
        val previousMessage = intent?.getStringExtra("PREVIOUS")
        val nextMessage = intent?.getStringExtra("NEXT")

        stopMessage?.let {
            TrackListViewModel.reason.value = !TrackListViewModel.reason.value
        }

        previousMessage?.let {
            TrackListViewModel.previousTrack.value = true
        }

        nextMessage?.let {
            TrackListViewModel.nextTrack.value = true
        }
    }
}