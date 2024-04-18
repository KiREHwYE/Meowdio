package com.kire.audio.presentation.activity

import android.app.Activity

import android.content.ComponentName
import android.content.Context

import android.os.Build
import android.os.Bundle

import android.view.Window
import android.view.WindowManager

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

import com.kire.audio.device.audio.AudioPlayerService
import com.kire.audio.device.audio.MediaControllerManager
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.device.audio.performPlayMedia
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.presentation.screen.ListScreen
import com.kire.audio.presentation.theme.AudioTheme
import com.kire.audio.presentation.viewmodel.TrackViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var viewModel: TrackViewModel

    private var factory: ListenableFuture<MediaController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        lifecycleScope.launch(Dispatchers.Default) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    MediaCommands.isPlayRequired.collect {
                        viewModel.changeTrackUiState(trackUiState = viewModel.trackUiState.value.copy(isPlaying = it))
                    }
                }
                launch {
                    MediaCommands.isPreviousTrackRequired.collect {
                        if (it)
                            skipTrack(this@MainActivity, SkipTrackAction.PREVIOUS, viewModel)
                    }
                }
                launch {
                    MediaCommands.isNextTrackRequired.collect {
                        if (it)
                            skipTrack(this@MainActivity, SkipTrackAction.NEXT, viewModel)
                    }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()

        factory = MediaController.Builder(
            this,
            SessionToken(this, ComponentName(this, AudioPlayerService::class.java))
        ).buildAsync()

        factory?.addListener(
            {
                // MediaController is available here with controllerFuture.get()
                factory?.let {
                    if (it.isDone)
                        it.get()
                    else
                        null
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        factory?.let {
            MediaController.releaseFuture(it)
            mediaController = null
        }
        factory = null
    }
}


private fun skipTrack(context: Context, skipTrackAction: SkipTrackAction, viewModel: TrackViewModel){

    MediaCommands.isTrackRepeated.value = false

    val mediaController = MediaControllerManager.getInstance(context)

    viewModel.apply {
        val newINDEX =
            skipTrackAction.action(
                trackUiState.value.currentTrackPlayingIndex!!,
                selectListOfTracks(trackUiState.value.currentListSelector).value.size
            )

        changeTrackUiState(
            trackUiState.value.copy(
                currentTrackPlaying = selectListOfTracks(trackUiState.value.currentListSelector).value[newINDEX],
                currentTrackPlayingURI = selectListOfTracks(trackUiState.value.currentListSelector).value[newINDEX].path,
                currentTrackPlayingIndex = newINDEX
            )
        )

        mediaController.controller.value?.performPlayMedia(selectListOfTracks(trackUiState.value.currentListSelector).value[newINDEX])
    }

    if (skipTrackAction == SkipTrackAction.PREVIOUS)
        MediaCommands.isPreviousTrackRequired.value = false
    else
        MediaCommands.isNextTrackRequired.value = false
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


