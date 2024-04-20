package com.kire.audio.presentation.screen.list_screen_ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kire.audio.R
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.TrackUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerBottomFloatingCard(
    trackUiState: TrackUiState,
    skipTrack: (SkipTrackAction) -> Unit,
    playOrPause: () -> Unit,
    onTap: () -> Unit = { },
    onDragDown: () -> Unit = { }
) {

    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val (x, y) = dragAmount

                    if (y > 10 && x < 60 && x > -60) {
                        coroutineScope.launch(Dispatchers.IO) {
                            onDragDown()
                        }
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
            .padding(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {

            this@Card.AnimatedVisibility(
                visible = !trackUiState.isPlayerScreenExpanded,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 90,
                        easing = LinearOutSlowInEasing
                    )
                ),
                exit = fadeOut()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Crossfade(
                        targetState = trackUiState.currentTrackPlaying?.imageUri,
                        label = "BottomSheet Partial Expanded"
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = it,
                                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                                contentScale = ContentScale.Crop,
                                contentDescription = "Track Image",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .aspectRatio(ratio = 1f)
                                    .fillMaxHeight()
                            )

                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(trackUiState.currentTrackPlaying?.title)
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.W300
                                        )
                                    ) {
                                        append("\n" + trackUiState.currentTrackPlaying?.artist)
                                    }
                                },
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .fillMaxWidth(0.55f)
                                    .basicMarquee(
                                        animationMode = MarqueeAnimationMode.Immediately,
                                        delayMillis = 0
                                    )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 28.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.skip_previous_button_bottom_sheet),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
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
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(if (trackUiState.isPlaying) 22.dp else 20.dp)
                                .bounceClick {
                                    playOrPause()
                                }
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.skip_next_button_bottom_sheet),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
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
        }
    }
}