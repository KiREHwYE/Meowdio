package com.kire.audio.notification

import android.Manifest
import android.annotation.SuppressLint

import android.app.PendingIntent

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.kire.audio.R
import com.kire.audio.models.Track
import com.kire.audio.viewmodels.TrackListViewModel

class AudioNotification(
    private val context: Context,
    private var notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat,
) {

    private val pauseIntent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("STOP", "pause")
    }
    private val pausePendingIntent = PendingIntent.getBroadcast(
        context,
        Channels.PAUSE.channel,
        pauseIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    private val skipPreviousIntent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("PREVIOUS", "previous")
    }
    private val skipPreviousPendingIntent = PendingIntent.getBroadcast(
        context,
        Channels.SKIP_PREVIOUS.channel,
        skipPreviousIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    private val skipNextIntent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("NEXT", "next")
    }
    private val skipNextPendingIntent = PendingIntent.getBroadcast(
        context,
        Channels.SKIP_NEXT.channel,
        skipNextIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    private val skipPreviousAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
        R.drawable.baseline_skip_previous_24, "Skip Previous", skipPreviousPendingIntent
    ).build()

    private val skipNextAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
        R.drawable.baseline_skip_next_24, "Skip Next", skipNextPendingIntent
    ).build()

    private val playPauseAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
        R.drawable.baseline_play_arrow_24, "PlayPause", pausePendingIntent
    ).build()


    init {
        notificationBuilder = notificationBuilder
            .addAction(skipPreviousAction)
            .addAction(playPauseAction)
            .addAction(skipNextAction)
    }

    @SuppressLint("RestrictedApi")
    fun updateNotification(currentTrackPlaying: Track?) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        currentTrackPlaying?.let {track ->
            notificationBuilder.mActions[1] =
                NotificationCompat.Action(if(TrackListViewModel.reason.value) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24, "PlayPause", pausePendingIntent)

            notificationManager.notify(1,
                notificationBuilder
                    .setContentTitle(if (track.title.length > 25) track.title.take(25) + "..." else track.title)
                    .setContentText(if (track.artist.length > 25) track.artist.take(25) + "..." else track.artist)
                    .build()
            )
        }
    }
}