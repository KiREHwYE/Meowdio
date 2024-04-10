package com.kire.audio.ui

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
import androidx.datastore.preferences.preferencesDataStore

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

import com.kire.audio.data.preferencesDataStore.PreferencesDataStore
import com.kire.audio.mediaHandling.AudioPlayerService
import com.kire.audio.mediaHandling.MediaControllerManager
import com.kire.audio.mediaHandling.functional.SkipTrackAction
import com.kire.audio.mediaHandling.performPlayMedia
import com.kire.audio.data.repositories.TrackRepository
import com.kire.audio.data.repositories.functional.TracksLoading
import com.kire.audio.data.trackDatabase.TrackDatabase
import com.kire.audio.data.preferencesDataStore.PreferencesDataStoreConstants
import com.kire.audio.data.repositories.PreferencesDataStoreRepository
import com.kire.audio.ui.theme.AudioTheme
import com.kire.audio.ui.state_holders.viewmodels.TrackViewModel

import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = PreferencesDataStoreConstants.DATASTORE_NAME)

@UnstableApi
class MainActivity : ComponentActivity() {

    private var factory: ListenableFuture<MediaController>? = null

    private lateinit var viewModel: TrackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = TrackDatabase.getInstance(this)
        val tracksLoading = TracksLoading(this)

        val trackRepository = TrackRepository(
            trackDatabase = database,
            tracksLoading = tracksLoading
        )

        val dataStore = this.dataStore

        val preferencesDataStore = PreferencesDataStore(dataStore)

        val preferencesDataStoreRepository = PreferencesDataStoreRepository(preferencesDataStore)

        val vmFactory = TrackViewModel.Factory(
            preferencesDataStoreRepository = preferencesDataStoreRepository,
            trackRepository = trackRepository
        )

        viewModel = ViewModelProvider(this, vmFactory)[TrackViewModel::class.java]


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
                TrackViewModel.previousTrack.collect {
                    if (it)
                        skipTrack(this@MainActivity, SkipTrackAction.PREVIOUS, viewModel)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackViewModel.nextTrack.collect {
                    if (it)
                        skipTrack(this@MainActivity, SkipTrackAction.NEXT, viewModel)
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

    TrackViewModel.isRepeated.value = false

    val mediaController = MediaControllerManager.getInstance(context)

    viewModel.apply {
        val newINDEX =
            skipTrackAction.action(
                uiState.value.currentTrackPlayingIndex!!,
                selectListTracks(selectList.value).value.size
            )

        sentInfoToBottomSheet(
            selectListTracks(selectList.value).value[newINDEX],
            selectList.value,
            newINDEX,
            selectListTracks(selectList.value).value[newINDEX].path
        )

        mediaController.controller.value?.performPlayMedia(selectListTracks(selectList.value).value[newINDEX])
    }

    if (skipTrackAction == SkipTrackAction.PREVIOUS)
        TrackViewModel.previousTrack.value = false
    else
        TrackViewModel.nextTrack.value = false
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


