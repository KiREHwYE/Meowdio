package com.kire.audio.presentation.ui.screen

import androidx.activity.compose.BackHandler

import androidx.compose.animation.core.tween

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.R

import com.kire.audio.presentation.navigation.transitions.ListAlbumScreenTransitions
import com.kire.audio.presentation.ui.album_screen_ui.AlbumItem
import com.kire.audio.presentation.ui.cross_screen_ui.OnScrollListener
import com.kire.audio.presentation.ui.cross_screen_ui.ScrollToTopButton
import com.kire.audio.presentation.ui.list_screen_ui.top_block.Header
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.ListScreenDestination
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

import kotlinx.coroutines.launch

@Destination(style = ListAlbumScreenTransitions::class)
@Composable
fun ListAlbumScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigator: DestinationsNavigator
){
    val coroutineScope = rememberCoroutineScope()

    val trackUiState by trackViewModel.trackUiState.collectAsStateWithLifecycle()
    val albumUiState by trackViewModel.albumUiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2 && trackUiState.isPlayerBottomCardShown
        }
    }

    val configuration = LocalConfiguration.current
    val widthDP = configuration.screenWidthDp
    val blockWidth = widthDP / 2

    val albumsWithTracks = trackViewModel.artistWithTracks
    val albums = albumsWithTracks.keys.toList()

    OnScrollListener(
        listState = listState,
        trackUiState = trackUiState,
        changeTrackUiState = trackViewModel::updateTrackUiState
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AudioExtendedTheme.extendedColors.background)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()

                    val x = dragAmount

                    if (x > 60)
                        navigator.popBackStack(AlbumScreenDestination.route, inclusive = true)
                }
            }
            .padding(horizontal = dimensionResource(id = R.dimen.app_horizontal_pad)),
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
            contentPadding = PaddingValues(bottom = dimensionResource(id = R.dimen.list_bottom_pad)),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Header(
                    text = stringResource(id = R.string.albumscreen_header),
                    onTitleClick = {
                        navigator.popBackStack(ListScreenDestination, inclusive = false)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(albums){ _, album ->
                AlbumItem(
                    tracks = albumsWithTracks[album] ?: emptyList(),
                    trackUiState = trackUiState,
                    changeTrackUiState = trackViewModel::updateTrackUiState,
                    upsertTrack = trackViewModel::upsertTrack,
                    mediaController = mediaController,
                    onImageClick = {
                        navigator.navigate(AlbumScreenDestination())
                        trackViewModel.updateAlbumUiState(albumUiState.copy(tracks = albumsWithTracks[album] ?: emptyList()))
                    }
                )
            }
        }
    }

    val itemSize = blockWidth.dp
    val density = LocalDensity.current
    val itemSizePx = with(density) { itemSize.toPx() }
    val itemsScrollCount = albums.size

    ScrollToTopButton(
        showButton = showButton,
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
