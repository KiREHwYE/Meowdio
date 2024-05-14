package com.kire.audio.presentation.ui.album_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.media3.session.MediaController

import coil.compose.AsyncImage

import com.kire.audio.R
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.list_screen_ui.TrackItem


@Composable
fun AlbumItem(
    tracks: List<Track>,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: (Track) -> Unit,
    mediaController: MediaController?,
    blockWidth: Int
) {
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onBackground)

    ) {

        Row(
            modifier = Modifier
                .padding(20.dp)
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
                    .size(blockWidth.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .size(blockWidth.dp)
                    .clip(RoundedCornerShape(12.dp)),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
                        changeTrackUiState = changeTrackUiState,
                        mediaController = mediaController,
                        upsertTrack = upsertTrack,
                        showImage = false,
                        textTitleSize = 13.sp,
                        textArtistSize = 11.sp,
                        startPadding = 0.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.DarkGray)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}