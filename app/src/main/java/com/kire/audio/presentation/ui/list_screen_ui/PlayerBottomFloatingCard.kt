package com.kire.audio.presentation.ui.list_screen_ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage

import com.kire.audio.R
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerBottomFloatingCard(
    trackUiState: TrackUiState,
    skipTrack: (SkipTrackAction) -> Unit,
    playOrPause: () -> Unit,
    onTap: () -> Unit = { },
    onDragDown: () -> Unit = { },
    playerBottomFloatingCardPaddingValues: PaddingValues = PaddingValues(start = 28.dp, end = 28.dp, bottom = 28.dp)
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .padding(playerBottomFloatingCardPaddingValues)
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
                elevation = 5.dp,
                spotColor = Color.Black,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    val y = dragAmount

                    if (y > 10)
                        coroutineScope.launch(Dispatchers.IO) {
                            onDragDown()
                        }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onTap()
                    }
                )
            }
            .background(AudioExtendedTheme.extendedColors.controlElementsBackground)
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 15.dp,
                bottom = 15.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Crossfade(
            targetState = trackUiState.currentTrackPlaying?.imageUri,
            label = "Track changed"
        ) {

            AsyncImage(
                model = it,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                contentDescription = "Track Image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)

            )
        }

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = trackUiState.currentTrackPlaying?.title ?: "",
                color = AudioExtendedTheme.extendedColors.primaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 0
                    )
            )
            Text(
                text = trackUiState.currentTrackPlaying?.artist ?: "",
                color = AudioExtendedTheme.extendedColors.secondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.W300,
                modifier = Modifier
                    .basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 0
                    )
            )
        }

        Row(
            modifier = Modifier
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Icon(
                painter = painterResource(id = R.drawable.skip_previous_button_bottom_sheet),
                contentDescription = null,
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(17.dp)
                    .bounceClick {
                        skipTrack(SkipTrackAction.PREVIOUS)
                            .also { MediaCommands.isTrackRepeated.value = false }
                    }
            )

            Icon(
                painter =
                if (trackUiState.isPlaying)
                    painterResource(id = R.drawable.pause_button_bottom_sheet)
                else
                    painterResource(id = R.drawable.play_button_bottom_sheet),
                contentDescription = null,
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(20.dp)
                    .bounceClick {
                        playOrPause()
                    }
            )

            Icon(
                painter = painterResource(id = R.drawable.skip_next_button_bottom_sheet),
                contentDescription = null,
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(17.dp)
                    .bounceClick {
                        skipTrack(SkipTrackAction.NEXT)
                            .also { MediaCommands.isTrackRepeated.value = false }
                    }
            )
        }
    }
}