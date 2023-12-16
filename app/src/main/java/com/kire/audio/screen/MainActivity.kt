package com.kire.audio.screen

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager

import android.net.Uri

import android.os.Build
import android.os.Bundle

import android.view.Window
import android.view.WindowManager

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

import com.kire.audio.R
import com.kire.audio.datastore.DataStore
import com.kire.audio.mediaHandling.AudioPlayer
import com.kire.audio.mediaHandling.SkipTrackAction
import com.kire.audio.notification.AudioNotification
import com.kire.audio.repository.TrackRepository
import com.kire.audio.ui.theme.AudioTheme
import com.kire.audio.viewmodels.TrackListViewModel

import kotlinx.coroutines.launch

@UnstableApi
class MainActivity() : ComponentActivity() {


    lateinit var viewModel: TrackListViewModel

    val listener = object : Player.Listener {
        override fun onIsPlayingChanged(_isPlaying: Boolean) {
            TrackListViewModel.reason.value = _isPlaying
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val trackRepository = TrackRepository(this) // Data source
        val dataStore = DataStore(this)
        val audioPlayer = AudioPlayer(this)

        val notificationBuilder = NotificationCompat.Builder(this, "Main Channel ID")
            .setContentTitle("Audio Notification")
            .setContentText("Track is playing")
            .setSmallIcon(R.drawable.music_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val channel = NotificationChannel(
            "Main Channel ID",
            "Main Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager = NotificationManagerCompat.from(this).also {
            it.createNotificationChannel(channel)
        }

        val audioNotification = AudioNotification(
            context = this,
            notificationBuilder = notificationBuilder,
            notificationManager = notificationManager
        )

        val factory = TrackListViewModel.Factory(
            dataStore = dataStore,
            trackRepository = trackRepository,
            audioPlayer = audioPlayer,
            audioNotification = audioNotification
        )

        viewModel = ViewModelProvider(this, factory).get(TrackListViewModel::class.java)
        viewModel.exoPlayer.addListener(listener)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.hideSystemUi(extraAction = {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        })
        setDisplayCutoutMode()

        setContent {
            AudioTheme {
                ListScreen(viewModel)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.currentTrackPlaying.collect{track ->
                    track?.let {
                        viewModel.updateNotification()
                    }
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackListViewModel.reason.collect {
                    viewModel.apply {
                        if (!it)
                            exoPlayer.pause()
                        else
                            exoPlayer.play()

                        updateNotificationPlayPauseButton()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackListViewModel.previousTrack.collect {
                    if (it)
                        skipTrack(SkipTrackAction.PREVIOUS, viewModel)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackListViewModel.nextTrack.collect {
                    if (it)
                        skipTrack(SkipTrackAction.NEXT, viewModel)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        viewModel.exoPlayer.removeListener(listener)
    }
}


private fun skipTrack(skipTrackAction: SkipTrackAction, viewModel: TrackListViewModel){
    TrackListViewModel.isRepeated.value = false

    viewModel.apply {
        val newINDEX =
            skipTrackAction.action(
                bottomSheetTrackINDEX.value,
                selectListTracks(selectList.value).value.size
            )

        sentInfoToBottomSheet(
            selectListTracks(selectList.value).value[newINDEX],
            selectList.value,
            newINDEX,
            selectListTracks(selectList.value).value[newINDEX].path
        )

        val newMediaItem =
            MediaItem.fromUri(Uri.parse(selectListTracks(selectList.value).value[newINDEX].path))

        exoPlayer.apply {
            setMediaItem(newMediaItem)
            prepare()
            play()
        }
    }

    if (skipTrackAction == SkipTrackAction.PREVIOUS)
        TrackListViewModel.previousTrack.value = false
    else
        TrackListViewModel.nextTrack.value = false

    TrackListViewModel.reason.value = true
}




private fun Window.hideSystemUi(extraAction:(WindowInsetsControllerCompat.() -> Unit)? = null) {
    WindowInsetsControllerCompat(this, this.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        extraAction?.invoke(controller)
    }
}

internal fun Activity.setDisplayCutoutMode() {
    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
    }
}


