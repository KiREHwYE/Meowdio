package com.kire.audio.presentation.ui.cross_screen_ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import coil.compose.AsyncImage

import com.kire.audio.R
import com.kire.audio.device.audio.util.MediaCommands
import com.kire.audio.device.audio.util.SkipTrackAction
import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.nonScaledSp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerBottomFloatingCard(
    trackUiState: StateFlow<TrackUiState>,
    skipTrack: (SkipTrackAction) -> Unit,
    playOrPause: () -> Unit,
    onTap: () -> Unit = { },
    onDragDown: () -> Unit = { },
    playerBottomFloatingCardPaddingValues: PaddingValues =
        PaddingValues(
            start = dimensionResource(id = R.dimen.app_universal_pad),
            end = dimensionResource(id = R.dimen.app_universal_pad),
            bottom = dimensionResource(id = R.dimen.app_universal_pad)
        )
) {
    val coroutineScope = rememberCoroutineScope()

    val trackUiState by trackUiState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .padding(playerBottomFloatingCardPaddingValues)
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
                elevation = dimensionResource(id = R.dimen.app_universal_shadow_elevation),
                spotColor = AudioExtendedTheme.extendedColors.shadow,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner))
            )
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner)))
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
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.column_and_row_universal_spacedby)),
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

        Box(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ){
            
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(-2.dp)
            ) {

                Text(
                    text = trackUiState.currentTrackPlaying?.title ?: "",
                    color = AudioExtendedTheme.extendedColors.primaryText,
                    fontSize = 16.sp.nonScaledSp,
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
                    fontSize = 12.sp.nonScaledSp,
                    fontWeight = FontWeight.W300,
                    modifier = Modifier
                        .basicMarquee(
                            animationMode = MarqueeAnimationMode.Immediately,
                            delayMillis = 0
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.column_and_row_universal_spacedby))
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