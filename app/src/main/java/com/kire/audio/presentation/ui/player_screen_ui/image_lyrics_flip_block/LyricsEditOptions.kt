package com.kire.audio.presentation.ui.player_screen_ui.image_lyrics_flip_block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lyrics

import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kire.audio.R

import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.LyricsRequestMode
import com.kire.audio.presentation.util.nonScaledSp

@Composable
fun LyricsEditOptions(
    updateLyricsRequestMode: (LyricsRequestMode) -> Unit,
    lyricsRequest: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ){

            EditOption(
                leadingIcon = Icons.Rounded.Link,
                text = stringResource(id = R.string.by_genius_link_mode_text),
                onClick = {
                    updateLyricsRequestMode(LyricsRequestMode.BY_LINK)
                }
            )

            EditOption(
                leadingIcon = Icons.Rounded.Lyrics,
                text = stringResource(id = R.string.by_artist_and_title_mode_text),
                onClick = {
                    updateLyricsRequestMode(LyricsRequestMode.BY_TITLE_AND_ARTIST)
                }
            )

            EditOption(
                leadingIcon = Icons.Rounded.EditNote,
                text = stringResource(id = R.string.edit_mode_text),
                onClick = {
                    updateLyricsRequestMode(LyricsRequestMode.EDIT_CURRENT_TEXT)
                }
            )

            EditOption(
                leadingIcon = Icons.Rounded.AutoAwesome,
                text = stringResource(id = R.string.automatic_mode_text),
                onClick = {
                    updateLyricsRequestMode(LyricsRequestMode.AUTOMATIC)
                    lyricsRequest()
                }
            )
            
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun EditOption(
    leadingIcon: ImageVector,
    text: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .bounceClick {
                onClick()
            },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = AudioExtendedTheme.extendedColors.orangeAccents,
            modifier = Modifier
                .size(32.dp)
        )
        Text(
            text = text,
            color = AudioExtendedTheme.extendedColors.lyricsText,
            fontSize = 16.sp.nonScaledSp,
            lineHeight = 20.sp.nonScaledSp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.W400,
        )
    }
}