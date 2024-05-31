package com.kire.audio.presentation.ui.album_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.media3.session.MediaController

import coil.compose.AsyncImage

import com.kire.audio.R

import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.list_screen_ui.TrackItem
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme


@Composable
fun AlbumItem(
    tracks: List<Track>,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: (Track) -> Unit,
    mediaController: MediaController?,
    onImageClick: () -> Unit
) {

    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.app_rounded_corner)))
            .background(AudioExtendedTheme.extendedColors.background)
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ){

            AsyncImage(
                model = tracks[0].imageUri,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                contentDescription = "Album Image",
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        spotColor = AudioExtendedTheme.extendedColors.shadow,
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.app_rounded_corner))
                    )
                    .weight(1f)
                    .aspectRatio(1f / 1f)
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.app_rounded_corner)))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onImageClick()
                        }
                    }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f / 1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                itemsIndexed(
                    tracks,
                    key = { _, track ->
                        track.id
                    }
                )  {index, track ->
                    TrackItem(
                        trackToShow = track,
                        listINDEX = index,
                        trackUiState = trackUiState,
                        updateTrackUiState = changeTrackUiState,
                        mediaController = mediaController,
                        upsertTrack = upsertTrack,
                        showImage = false,
                        textTitleSize = 13.sp,
                        textArtistSize = 11.sp,
                        startPadding = 0.dp,
                        modifier = Modifier
                            .shadow(
                                elevation = 4.dp,
                                spotColor = AudioExtendedTheme.extendedColors.shadow,
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.app_rounded_corner))
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .background(AudioExtendedTheme.extendedColors.controlElementsBackground)
                            .padding(
                                start = 18.dp,
                                end = 18.dp,
                                top = 10.dp,
                                bottom = 10.dp
                            )
                    )
                }
            }
        }
    }
}