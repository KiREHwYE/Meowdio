package com.kire.audio.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAudioExtendedColors = staticCompositionLocalOf { AudioExtendedColors() }

val extendedLightColors = AudioExtendedColors(
    background = Color(0xFFFAFAFA),
    controlElementsBackground  = Color.White,
    divider = Color(0xFFD0D0D0),
    primaryText = Color.Black,
    secondaryText = Color.Gray,
    button = Color.Black,
    playerScreenButton = Color.White,
    scrollToTopButton = Color.White,
    lyricsText = Color.DarkGray,
    shadow = Color.DarkGray,
    orangeAccents = Color(0xFFFEBAA1)
)

val extendedDarkColors = AudioExtendedColors(
    background = Color.Black,
    controlElementsBackground  = Color(0xFF1A1A1A),
    divider = Color.DarkGray,
    primaryText = Color(0xFFD0D0D0),
    secondaryText = Color(0xFF8C8C8C),
    button = Color(0xFFD0D0D0),
    playerScreenButton = Color.White,
    scrollToTopButton = Color.Black,
    lyricsText = Color.LightGray,
    orangeAccents = Color(0xFFFEBAA1)

)

@Composable
fun AudioExtendedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val extendedColors = if (darkTheme) extendedDarkColors else extendedLightColors

    CompositionLocalProvider(LocalAudioExtendedColors provides extendedColors) {
        AudioTheme(
            /* colors = ..., typography = ..., shapes = ... */
            content = content
        )
    }
}

// Use with eg. ExtendedTheme.colors.tertiary
object AudioExtendedTheme {
    val extendedColors: AudioExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAudioExtendedColors.current
}