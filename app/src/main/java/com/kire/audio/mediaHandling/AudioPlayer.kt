package com.kire.audio.mediaHandling

import android.content.Context

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.C
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector

import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi @Singleton
class AudioPlayer @Inject constructor(
    private val context: Context
) {

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun buildRenderersFactory(
        context: Context, preferExtensionRenderer: Boolean
    ): RenderersFactory {
        val extensionRendererMode = if (preferExtensionRenderer)
            DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
        else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON

        return DefaultRenderersFactory(context)
            .setExtensionRendererMode(extensionRendererMode)
            .setEnableDecoderFallback(true)
    }

    val renderersFactory = buildRenderersFactory(context, true)
    val trackSelector = DefaultTrackSelector(context)


    var audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .build()


    val exoPlayer = ExoPlayer.Builder(context, renderersFactory)
        .setTrackSelector(trackSelector)
        .setAudioAttributes(audioAttributes, true)
        .build().apply {
            trackSelectionParameters = DefaultTrackSelector.Parameters.Builder(context).build()
            playWhenReady = false
        }

}