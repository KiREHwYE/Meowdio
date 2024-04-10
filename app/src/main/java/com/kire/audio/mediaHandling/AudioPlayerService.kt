package com.kire.audio.mediaHandling

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.kire.audio.R
import com.kire.audio.notification.CustomMediaNotificationProvider
import com.kire.audio.notification.NotificationPlayerCustomCommandButton
import com.kire.audio.ui.MainActivity
import com.kire.audio.ui.state_holders.viewmodels.TrackViewModel

class AudioPlayerService : MediaSessionService() {

    private var _mediaSession: MediaSession? = null
    private val mediaSession get() = _mediaSession!!

    private val notificationPlayerCustomCommandButtons =
        NotificationPlayerCustomCommandButton.entries.map { command -> command.commandButton }

    @UnstableApi
    private lateinit var customMediaNotificationProvider: CustomMediaNotificationProvider

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "session_notification_channel_id"
    }

    private inner class CustomMediaSessionCallback: MediaSession.Callback {

        @UnstableApi
        override fun onMediaButtonEvent(
            session: MediaSession,
            controllerInfo: MediaSession.ControllerInfo,
            intent: Intent
        ): Boolean {
            val ke: KeyEvent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
            } else {
                @Suppress("DEPRECATION") intent.extras?.getParcelable(Intent.EXTRA_KEY_EVENT) as? KeyEvent
            }

            when (ke?.keyCode) {

                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    TrackViewModel.reason.value = !TrackViewModel.reason.value
                        .also {
                            if (!it)
                                session.player.pause()
                            else
                                session.player.play()
                        }
                }

                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    session.player.play()
                    TrackViewModel.reason.value = true
                }

                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    session.player.pause()
                    TrackViewModel.reason.value = false
                }

                KeyEvent.KEYCODE_MEDIA_NEXT -> {
                    TrackViewModel.nextTrack.value = !TrackViewModel.nextTrack.value
                }

                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                    TrackViewModel.previousTrack.value = !TrackViewModel.previousTrack.value
                }
            }

            return super.onMediaButtonEvent(session, controllerInfo, intent)
        }

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()

            /* Registering custom player command buttons for player notification. */
            notificationPlayerCustomCommandButtons.forEach { commandButton ->
                commandButton.sessionCommand?.let(availableSessionCommands::add)
            }

            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            super.onPostConnect(session, controller)
            if (notificationPlayerCustomCommandButtons.isNotEmpty()) {
                /* Setting custom player command buttons to mediaLibrarySession for player notification. */
                mediaSession.setCustomLayout(notificationPlayerCustomCommandButtons)
            }
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            /* Handling custom command buttons from player notification. */
            when(customCommand.customAction){
                NotificationPlayerCustomCommandButton.PREVIOUS.customAction -> TrackViewModel.previousTrack.value = !TrackViewModel.nextTrack.value
                NotificationPlayerCustomCommandButton.PLAY.customAction -> {
                    session.player.play()
                    TrackViewModel.reason.value = true
                }
                NotificationPlayerCustomCommandButton.PAUSE.customAction -> {
                    session.player.pause()
                    TrackViewModel.reason.value = false
                }
                NotificationPlayerCustomCommandButton.NEXT.customAction -> TrackViewModel.nextTrack.value = !TrackViewModel.nextTrack.value
            }

            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }

    /**
     * This method is called when the service is being created.
     * It initializes the ExoPlayer and MediaSession instances and sets the MediaSessionServiceListener.
     */
    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate() // Call the superclass method

        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .build()

        // Create an ExoPlayer instance
        val player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(audioAttributes, true)
            .build()

        val forwardingPlayer = object : ForwardingPlayer(player) {

            override fun play() {
                super.play()
                TrackViewModel.reason.value = true
            }

            override fun pause() {
                super.pause()
                TrackViewModel.reason.value = false
            }

            override fun stop() {
                super.stop()
                TrackViewModel.reason.value = false
            }

            override fun setPlayWhenReady(playWhenReady: Boolean) {
                super.setPlayWhenReady(playWhenReady)
                TrackViewModel.reason.value = true
            }
        }

        // Create a MediaSession instance
        _mediaSession = MediaSession.Builder(this, forwardingPlayer)
            .also { builder ->
                // Set the session activity to the PendingIntent returned by getSingleTopActivity() if it's not null
                getSingleTopActivity()?.let { builder.setSessionActivity(it) }
            }
            .setCallback(CustomMediaSessionCallback())
            .setCustomLayout(notificationPlayerCustomCommandButtons)
            .build() // Build the MediaSession instance


        customMediaNotificationProvider = CustomMediaNotificationProvider(this)

        // Set the listener for the MediaSessionService
        setListener(MediaSessionServiceListener())
        setMediaNotificationProvider(customMediaNotificationProvider)

    }


    /**
     * This method is called when the system determines that the service is no longer used and is being removed.
     * It checks the player's state and if the player is not ready to play or there are no items in the media queue, it stops the service.
     *
     * @param rootIntent The original root Intent that was used to launch the task that is being removed.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        // Get the player from the media session
        val player = mediaSession.player

        // Check if the player is not ready to play or there are no items in the media queue
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service
            stopSelf()
        }
    }

    /**
     * This method is called when a MediaSession.ControllerInfo requests the MediaSession.
     * It returns the current MediaSession instance.
     *
     * @param controllerInfo The MediaSession.ControllerInfo that is requesting the MediaSession.
     * @return The current MediaSession instance.
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return _mediaSession
    }


    /**
     * This method is called when the service is being destroyed.
     * It releases the player and the MediaSession instances, sets the _mediaSession to null, clears the listener, and calls the superclass method.
     */
    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        // If _mediaSession is not null, run the following block
        _mediaSession?.run {
            // If getBackStackedActivity() returns a non-null value, set it as the session activity
            getBackStackedActivity()?.let {
                setSessionActivity(it)
            }
            // Release the player
            player.release()
            // Release the MediaSession instance
            release()
            // Set _mediaSession to null
            _mediaSession = null
        }
        // Clear the listener
        clearListener()
        // Call the superclass method
        super.onDestroy()
    }

    /**
     * This method creates a PendingIntent that starts the MainActivity.
     * The PendingIntent is created with the "FLAG_UPDATE_CURRENT" flag, which means that if the described PendingIntent already exists,
     * then keep it but replace its extra data with what is in this new Intent.
     * The PendingIntent also has the "FLAG_IMMUTABLE" flag if the Android version is 23 or above, which means that the created PendingIntent
     * is immutable and cannot be changed after it's created.
     *
     * @return A PendingIntent that starts the MainActivity. If the PendingIntent cannot be created for any reason, it returns null.
     */
    @OptIn(UnstableApi::class)
    private fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * This method creates a PendingIntent that starts the MainActivity with a back stack.
     * The PendingIntent is created with the "FLAG_UPDATE_CURRENT" flag, which means that if the described PendingIntent already exists,
     * then keep it but replace its extra data with what is in this new Intent.
     * The PendingIntent also has the "FLAG_IMMUTABLE" flag if the Android version is 23 or above, which means that the created PendingIntent
     * is immutable and cannot be changed after it's created.
     * The back stack is created using TaskStackBuilder, which allows the user to navigate back to the MainActivity from the PendingIntent.
     *
     * @return A PendingIntent that starts the MainActivity with a back stack. If the PendingIntent cannot be created for any reason, it returns null.
     */
    @OptIn(UnstableApi::class)
    private fun getBackStackedActivity(): PendingIntent? {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@AudioPlayerService, MainActivity::class.java))
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @OptIn(UnstableApi::class) // MediaSessionService.Listener
    private inner class MediaSessionServiceListener : Listener {

        /**
         * This method is only required to be implemented on Android 12 or above when an attempt is made
         * by a media controller to resume playback when the {@link MediaSessionService} is in the
         * background.
         */
        override fun onForegroundServiceStartNotAllowedException() {
            if (
                Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // Notification permission is required but not granted
                return
            }
            val notificationManagerCompat = NotificationManagerCompat.from(this@AudioPlayerService)
            ensureNotificationChannel(notificationManagerCompat)
            val builder =
                NotificationCompat.Builder(this@AudioPlayerService, CHANNEL_ID)
                    .setSmallIcon(R.drawable.music_icon)
                    .setContentTitle(getString(R.string.notification_content_title))
//                    .setStyle(
//                        NotificationCompat.BigTextStyle()
//                    )
                    .setStyle(
                        MediaStyleNotificationHelper.MediaStyle(mediaSession)
                        .setShowActionsInCompactView(0, 1, 2)
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .also { builder -> getBackStackedActivity()?.let { builder.setContentIntent(it) } }
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
        if (notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null) {
            return
        }

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManagerCompat.createNotificationChannel(channel)
    }
}