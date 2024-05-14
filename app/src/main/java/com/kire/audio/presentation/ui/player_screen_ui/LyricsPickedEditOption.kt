package com.kire.audio.presentation.ui.player_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.util.LyricsRequestMode

@Composable
fun LyricsPickedEditOption(
    isClearNeeded: Boolean,
    changeIsClearNeeded: () -> Unit,
    lyricsRequestMode: LyricsRequestMode,
    lyrics: ILyricsRequestState,
    updateUserInput: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var input by rememberSaveable {
        mutableStateOf(
            if (lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT && lyrics is ILyricsRequestState.Success)
                lyrics.lyrics.also { updateUserInput(it) }
            else "".also { updateUserInput(it) }
        )
    }

    LaunchedEffect(key1 = isClearNeeded) {
        if (isClearNeeded)
            input = "".also {
                updateUserInput(it)
                changeIsClearNeeded()
            }
    }

    BasicTextField(
        modifier = Modifier
            .background(
                Color.Transparent,
                MaterialTheme.shapes.small,
            )
            .fillMaxWidth(),
        value = input,
        onValueChange = { newText ->
            input = newText.also { updateUserInput(it) }
        },
        enabled = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = Color.LightGray,
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
        ),
        decorationBox = { innerTextField ->

            Box(
                modifier = modifier,
                contentAlignment = Alignment.TopStart
            ) {

                if (input.isEmpty())
                    if (lyricsRequestMode == LyricsRequestMode.BY_LINK)
                        Text(
                            text = "Link example: https://genius.com/While-she-sleeps-feel-lyrics",
                            color = Color.LightGray,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.W300,
                        )
                    else if (lyricsRequestMode == LyricsRequestMode.BY_TITLE_AND_ARTIST)
                        Text(
                            text = "Artist & title example: while she sleeps feels",
                            color = Color.LightGray,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.W300,
                        )

                innerTextField()
            }
        }
    )
}