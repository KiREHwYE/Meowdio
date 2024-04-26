package com.kire.audio.presentation.screen

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.presentation.navigation.AlbumScreenTransitions
import com.kire.audio.presentation.screen.album_screen_ui.AlbumItem
import com.kire.audio.presentation.screen.cross_screen_ui.OnScrollListener
import com.kire.audio.presentation.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination(style = AlbumScreenTransitions::class)
@Composable
fun AlbumScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigator: DestinationsNavigator
){

    val listState = rememberLazyListState()

    val albumsWithTracks = trackViewModel.artistWithTracks
    val albums = albumsWithTracks.keys.toList()

    val trackUiState by trackViewModel.trackUiState.collectAsStateWithLifecycle()

    OnScrollListener(
        listState = listState,
        trackUiState = trackUiState,
        changeTrackUiState = trackViewModel::changeTrackUiState
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()

                    val x = dragAmount

                    if (x > 60)
                        navigator.popBackStack(AlbumScreenDestination.route, inclusive = true)
                }
            },
        contentAlignment = Alignment.Center
    ) {

        BackHandler {
            navigator.navigateUp()
            return@BackHandler
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            itemsIndexed(albums){ index, album ->
                AlbumItem(
                    tracks = albumsWithTracks[album] ?: emptyList(),
                    trackUiState = trackUiState,
                    changeTrackUiState = trackViewModel::changeTrackUiState,
                    upsertTrack = trackViewModel::upsertTrack,
                    mediaController = mediaController
                )
            }
        }
    }
}


@Preview
@Composable
fun Preview() {
//    AlbumScreen()
}
