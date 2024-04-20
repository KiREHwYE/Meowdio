package com.kire.audio.presentation.screen.list_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import com.kire.audio.screen.functional.ListSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TrackItem(
    trackToShow: Track,
    listINDEX: Int,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    mediaController: MediaController,
    upsertTrack: suspend (Track) -> Unit,
    selector: ListSelector = ListSelector.MAIN_LIST,
    imageSize: Dp = 56.dp,
    textTitleSize: TextUnit = 17.sp,
    textArtistSize: TextUnit = 13.sp,
    startPadding: Dp = 16.dp,
    heartIconSize: Dp = 24.dp,
    modifier: Modifier
){

    val coroutineScope = rememberCoroutineScope()

    var _track by remember {
        mutableStateOf(trackToShow)
    }

    val currentTrackPlaying = trackUiState.currentTrackPlaying.also {
        it?.let {
            if (it.id == _track.id)
                _track = it
        }
    }

    _track.apply {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .bounceClick {

                    changeTrackUiState(
                        trackUiState
                            .copy(
                                isPlaying = if (_track.path == currentTrackPlaying?.path) !trackUiState.isPlaying else true,
                                currentTrackPlaying = _track,
                                currentListSelector = selector,
                                currentTrackPlayingIndex = listINDEX,
                                currentTrackPlayingURI = path,
                                isPlayerBottomCardShown = true
                            )
                    )

                    mediaController.apply {

                        if (trackUiState.isPlaying && trackUiState.currentTrackPlayingURI == path)
                            pause()
                        else if (!trackUiState.isPlaying && trackUiState.currentTrackPlayingURI == path) {
                            prepare()
                            play()

                        } else
                            performPlayMedia(_track)
                    }
                }
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if(selector != ListSelector.FAVOURITE_LIST) Arrangement.Start else Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    AsyncImage(
                        model = imageUri,
                        placeholder = painterResource(R.drawable.ic_launcher_foreground),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Track Image",
                        modifier = Modifier
                            .height(imageSize)
                            .width(imageSize)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = textTitleSize,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                            ) {
                                append(
                                    with(title) {
                                        if (selector == ListSelector.SEARCH_LIST)
                                            if (length > 11) take(11) + "..." else this
                                        else if (selector == ListSelector.FAVOURITE_LIST)
                                            if (length > 13) take(13) + "..." else this
                                        else
                                            if (length > 23) take(23) + "..." else this
                                    }
                                )
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = textArtistSize,
                                    fontWeight = FontWeight.W300,
                                    fontFamily = FontFamily.SansSerif
                                )
                            ) {
                                append("\n" +
                                        with(artist) {
                                            if (selector == ListSelector.SEARCH_LIST)
                                                if (length > 11) take(11) + "..." else this
                                            else if (selector == ListSelector.FAVOURITE_LIST)
                                                if (length > 13) take(13) + "..." else this
                                            else
                                                if (length > 23) take(23) + "..." else this
                                        }
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(start = startPadding)
                    )
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
                                        _track
                                            .copy(isFavourite = !_track.isFavourite)
                                            .also { thisTrack ->
                                                currentTrackPlaying?.let {
                                                    if (it.title == title && it.artist == artist && it.path == path)
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
    }
}