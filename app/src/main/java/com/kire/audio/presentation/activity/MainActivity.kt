package com.kire.audio.presentation.activity

import android.annotation.SuppressLint
import android.app.Activity

import android.content.ComponentName
import android.content.Context

import android.os.Build
import android.os.Bundle

import android.view.Window
import android.view.WindowManager

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLifecycleOwner

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken

import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

import com.kire.audio.device.audio.AudioPlayerService
import com.kire.audio.device.audio.MediaControllerManager
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.device.audio.performPlayMedia
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.rememberManagedMediaController
import com.kire.audio.presentation.navigation.NavigationUI
import com.kire.audio.presentation.ui.cross_screen_ui.PlayerBottomBar
import com.kire.audio.presentation.ui.cross_screen_ui.AutoSkipOnRepeatMode
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.viewmodel.TrackViewModel
import com.kire.audio.screen.functional.GetPermissions

import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var trackViewModel: TrackViewModel

    private var factory: ListenableFuture<MediaController>? = null

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.hideSystemUi(extraAction = {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        })
        setDisplayCutoutMode()

        setContent {

            val navHostEngine = rememberAnimatedNavHostEngine(navHostContentAlignment = Alignment.TopCenter)
            val navHostController = navHostEngine.rememberNavController()

            val mediaController by rememberManagedMediaController()

            AudioExtendedTheme {

                Scaffold(
                    bottomBar = {
                        PlayerBottomBar(
                            trackUiState = trackViewModel.trackUiState,
                            mediaController = mediaController,
                            selectListOfTracks = trackViewModel::selectListOfTracks,
                            changeTrackUiState = trackViewModel::updateTrackUiState,
                            navHostController = navHostController
                        )
                    }
                ) { _ ->

                    GetPermissions(
                        lifecycleOwner = LocalLifecycleOwner.current,
                        updateTrackDataBase = trackViewModel::updateTrackDataBase
                    )

                    AutoSkipOnRepeatMode(
                        trackUiState = trackViewModel.trackUiState,
                        mediaController = mediaController
                    )

                    NavigationUI(
                        trackViewModel = trackViewModel,
                        mediaController = mediaController,
                        navHostController = navHostController,
                        navHostEngine = navHostEngine
                    )
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch(Dispatchers.IO) {
                    MediaCommands.isPlayRequired.collect {
                        trackViewModel.updateTrackUiState(trackUiState = trackViewModel.trackUiState.value.copy(isPlaying = it))
                    }
                }
                launch {
                    MediaCommands.isPreviousTrackRequired.collect {
                        if (it)
                            skipTrack(this@MainActivity, SkipTrackAction.PREVIOUS, trackViewModel)
                    }
                }
                launch {
                    MediaCommands.isNextTrackRequired.collect {
                        if (it)
                            skipTrack(this@MainActivity, SkipTrackAction.NEXT, trackViewModel)
                    }
                }
                launch {
                    MediaCommands.isRepeatRequired.collect {
                        if (it)
                            skipTrack(this@MainActivity, SkipTrackAction.REPEAT, trackViewModel)
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

    val mediaController = MediaControllerManager.getInstance(context)

    viewModel.apply {

        val newINDEX =
            skipTrackAction.action(
                trackUiState.value.currentTrackPlayingIndex!!,
                selectListOfTracks(trackUiState.value.currentListSelector).value.size
            )

        if (skipTrackAction == SkipTrackAction.NEXT || skipTrackAction == SkipTrackAction.PREVIOUS) {
            updateTrackUiState(
                trackUiState.value.copy(
                    currentTrackPlaying = selectListOfTracks(trackUiState.value.currentListSelector).value[newINDEX],
                    currentTrackPlayingURI = selectListOfTracks(trackUiState.value.currentListSelector).value[newINDEX].path,
                    currentTrackPlayingIndex = newINDEX
                )
            )

            MediaCommands.isTrackRepeated.value = false
        }

        mediaController.controller.value?.performPlayMedia(selectListOfTracks(trackUiState.value.currentListSelector).value[newINDEX])

        MediaCommands.isRepeatRequired.value = false
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


