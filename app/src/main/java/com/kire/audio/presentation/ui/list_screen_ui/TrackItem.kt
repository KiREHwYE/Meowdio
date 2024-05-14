package com.kire.audio.presentation.ui.list_screen_ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import com.kire.audio.R
import com.kire.audio.device.audio.performPlayMedia
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.ListSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackItem(
    trackToShow: Track,
    listINDEX: Int,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    mediaController: MediaController?,
    upsertTrack: suspend (Track) -> Unit,
    selector: ListSelector = ListSelector.MAIN_LIST,
    showImage: Boolean = true,
    imageSize: Dp = 60.dp,
    textTitleSize: TextUnit = 17.sp,
    textArtistSize: TextUnit = 13.sp,
    startPadding: Dp = 16.dp,
    heartIconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
){

    val coroutineScope = rememberCoroutineScope()

    var track by remember {
        mutableStateOf(trackToShow)
    }

    val currentTrackPlaying = trackUiState.currentTrackPlaying.also {
        it?.let {
            if (it.id == track.id)
                track = it
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (selector != ListSelector.FAVOURITE_LIST) Arrangement.Start else Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .bounceClick {
                changeTrackUiState(
                    trackUiState.copy(
                        isPlaying = if (track.path == currentTrackPlaying?.path) !trackUiState.isPlaying else true,
                        currentTrackPlaying = track,
                        currentListSelector = selector,
                        currentTrackPlayingIndex = listINDEX,
                        currentTrackPlayingURI = track.path,
                        isPlayerBottomCardShown = true
                    )
                )

                mediaController?.apply {
                    if (trackUiState.isPlaying && trackUiState.currentTrackPlayingURI == track.path)
                        pause()
                    else if (!trackUiState.isPlaying && trackUiState.currentTrackPlayingURI == track.path) {
                        prepare()
                        play()

                    } else
                        performPlayMedia(track)
                }
            }
    ) {

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(startPadding)
        ){

            if (showImage)
                AsyncImage(
                    model = track.imageUri,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Track Image",
                    modifier = Modifier
                        .height(imageSize)
                        .width(imageSize)
                        .clip(CircleShape)

                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = track.title,
                        color = AudioExtendedTheme.extendedColors.primaryText,
                        fontSize = textTitleSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier
                            .basicMarquee(
                                animationMode = MarqueeAnimationMode.Immediately,
                                delayMillis = 0
                            )
                    )
                    Text(
                        text = track.artist,
                        color = AudioExtendedTheme.extendedColors.secondaryText,
                        fontSize = textArtistSize,
                        fontWeight = FontWeight.W300,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier
                            .basicMarquee(
                                animationMode = MarqueeAnimationMode.Immediately,
                                delayMillis = 0
                            )
                    )
                }
            }

        if (selector == ListSelector.FAVOURITE_LIST) {
            Icon(
                Icons.Rounded.Favorite,
                contentDescription = "Favourite",
                tint = Color.Red,
                modifier = Modifier
                    .size(heartIconSize)
                    .bounceClick {
                        coroutineScope.launch(Dispatchers.IO) {
                            upsertTrack(
                                track
                                    .copy(isFavourite = !track.isFavourite)
                                    .also { thisTrack ->
                                        currentTrackPlaying?.let {
                                            if (it.title == track.title && it.artist == track.artist && it.path == track.path)
                                                changeTrackUiState(
                                                    trackUiState.copy(
                                                        currentTrackPlaying = thisTrack
                                                    )
                                                )
                                        }
                                    }
                            )
                        }
                    }
            )
        }
    }
}