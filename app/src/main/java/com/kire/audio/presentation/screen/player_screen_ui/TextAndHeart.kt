package com.kire.audio.presentation.screen.player_screen_ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextAndHeart(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
){
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    ) {
                        append(trackUiState.currentTrackPlaying?.title)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.LightGray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W300,
                            fontFamily = FontFamily.SansSerif
                        )
                    ) {
                        append("\n" + trackUiState.currentTrackPlaying?.artist)
                    }
                },
                modifier = Modifier
                    .padding(start = 6.dp)
                    .fillMaxWidth(0.8f)
                    .alpha(0.8f)
                    .basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 0
                    )
            )

            Icon(
                if (trackUiState.currentTrackPlaying?.isFavourite == true) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Favourite",
                tint = if (trackUiState.currentTrackPlaying?.isFavourite == true) Color.Red else Color.White,
                modifier = Modifier
                    .size(34.dp)
                    .alpha(0.8f)
                    .bounceClick {
                        MediaCommands.isTrackRepeated.value = false

                        coroutineScope.launch(Dispatchers.IO) {
                            trackUiState.currentTrackPlaying?.let { track ->
                                upsertTrack(track.copy(isFavourite = !track.isFavourite)
                                    .also {
                                        changeTrackUiState(trackUiState.copy(currentTrackPlaying = it))
                                    }
                                )
                            }
                        }
                    }
            )
        }
    }
}