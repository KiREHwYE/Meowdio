package com.kire.audio.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.presentation.navigation.AlbumScreenTransitions
import com.kire.audio.presentation.ui.album_screen_ui.AlbumItem
import com.kire.audio.presentation.ui.cross_screen_ui.OnScrollListener
import com.kire.audio.presentation.ui.cross_screen_ui.ScrollToTopButton
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch


@Destination(style = AlbumScreenTransitions::class)
@Composable
fun AlbumScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigator: DestinationsNavigator,
    horizontalPaddingValues: PaddingValues = PaddingValues(start = 28.dp, end = 28.dp)
){

    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    val configuration = LocalConfiguration.current

    val widthDP = configuration.screenWidthDp
    val blockWidth = widthDP / 2 - 20

    val albumsWithTracks = trackViewModel.artistWithTracks
    val albums = albumsWithTracks.keys.toList()

    val trackUiState by trackViewModel.trackUiState.collectAsStateWithLifecycle()

    OnScrollListener(
        listState = listState,
        trackUiState = trackUiState,
        changeTrackUiState = trackViewModel::updateTrackUiState
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
            }
            .padding(horizontalPaddingValues),
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
            contentPadding = PaddingValues(28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            itemsIndexed(albums){ index, album ->
                AlbumItem(
                    tracks = albumsWithTracks[album] ?: emptyList(),
                    trackUiState = trackUiState,
                    changeTrackUiState = trackViewModel::updateTrackUiState,
                    upsertTrack = trackViewModel::upsertTrack,
                    mediaController = mediaController,
                    blockWidth = blockWidth
                )
            }
        }

        val itemSize = blockWidth.dp + 40.dp
        val density = LocalDensity.current
        val itemSizePx = with(density) { itemSize.toPx() }
        val itemsScrollCount = albums.size


        AnimatedVisibility(
            visible = showButton,
            enter = slideInHorizontally(initialOffsetX = { 82 }) + fadeIn(
                animationSpec = tween(
                    durationMillis = 250,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutHorizontally(targetOffsetX = { 82 }) + fadeOut(
                animationSpec = tween(
                    durationMillis = 250
                )
            ),
        ) {

            ScrollToTopButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollBy(
                            value = -1 * itemSizePx * itemsScrollCount,
                            animationSpec = tween(durationMillis = 4000)
                        )
                    }
                }
            )
        }
    }
}


@Preview
@Composable
fun Preview() {
//    AlbumScreen()
}
