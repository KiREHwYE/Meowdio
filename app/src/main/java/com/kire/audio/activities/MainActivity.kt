package com.kire.audio.activities

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
import com.kire.audio.notification.AudioNotificationService
import com.kire.audio.repository.TrackRepository
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

        val audioNotificationService = AudioNotificationService(
            context = this,
            notificationBuilder = notificationBuilder,
            notificationManager = notificationManager
        )

        val factory = TrackListViewModel.Factory(
            dataStore = dataStore,
            trackRepository = trackRepository,
            audioPlayer = audioPlayer,
            audioNotificationService = audioNotificationService
        )

        viewModel = ViewModelProvider(this, factory).get(TrackListViewModel::class.java)
        viewModel.exoPlayer.addListener(listener)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.hideSystemUi(extraAction = {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        })
        setDisplayCutoutMode()

        setContent {
            ListScreen(viewModel)
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
                    if (!it && viewModel.exoPlayer.isPlaying) {
                        viewModel.exoPlayer.apply {
                            pause()
                            TrackListViewModel.reason.value = false
                        }
                    } else {
                        viewModel.exoPlayer.apply {
                            play()
                            TrackListViewModel.reason.value = true
                        }
                    }
                    viewModel.updateNotification()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackListViewModel.previousTrack.collect {
                    if (it) {
                        skip(SkipTrackAction.PREVIOUS, viewModel)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackListViewModel.nextTrack.collect {
                    if (it) {
                        skip(SkipTrackAction.NEXT, viewModel)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.exoPlayer.removeListener(listener)
    }
}

fun skip(skipTrackAction: SkipTrackAction, viewModel: TrackListViewModel){
    viewModel.changeRepeatCount(0)

    viewModel.apply {
        val newINDEX =

            if (selectListTracks(selectList.value).value[bottomSheetTrackINDEX.value].imageUri == currentTrackPlaying.value!!.imageUri
                && selectListTracks(selectList.value).value[bottomSheetTrackINDEX.value].title == currentTrackPlaying.value!!.title
                && selectListTracks(selectList.value).value[bottomSheetTrackINDEX.value].artist == currentTrackPlaying.value!!.artist
            )
                skipTrackAction.action(
                    bottomSheetTrackINDEX.value,
                    selectListTracks(selectList.value).value.size
                )
            else 0

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

fun Window.hideSystemUi(extraAction:(WindowInsetsControllerCompat.() -> Unit)? = null) {
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


