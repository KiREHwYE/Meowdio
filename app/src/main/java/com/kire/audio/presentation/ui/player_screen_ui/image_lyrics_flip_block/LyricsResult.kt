package com.kire.audio.presentation.ui.player_screen_ui.image_lyrics_flip_block

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

import com.kire.audio.R
import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.nonScaledSp

@Composable
fun LyricsResult(
    lyrics: ILyricsRequestState,
    modifier: Modifier = Modifier
) {

    val waitingMessage = stringResource(R.string.lyrics_dialog_waiting_message)
    val unsuccessfulMessage = stringResource(R.string.lyrics_dialog_unsuccessful_message)

    Box(
        modifier =
            if (lyrics is ILyricsRequestState.OnRequest)
                modifier
                    .fillMaxWidth()
            else Modifier
                .wrapContentHeight(),
        contentAlignment =
            if (lyrics !is ILyricsRequestState.Success)
                Alignment.Center
            else
                Alignment.TopStart
    ){

        Text(
            text = when(lyrics){
                is ILyricsRequestState.Success -> lyrics.lyrics
                is ILyricsRequestState.Unsuccessful -> unsuccessfulMessage
                is ILyricsRequestState.OnRequest -> waitingMessage
            },
            color = AudioExtendedTheme.extendedColors.lyricsText,
            fontSize = 18.sp.nonScaledSp,
            lineHeight = 24.sp.nonScaledSp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.W600,
            textAlign = if (lyrics is ILyricsRequestState.OnRequest) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}