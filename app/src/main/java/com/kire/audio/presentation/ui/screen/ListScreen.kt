package com.kire.audio.presentation.ui.screen

import androidx.compose.animation.core.tween

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.viewmodel.TrackViewModel

import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.dimensionResource

import androidx.media3.session.MediaController
import com.kire.audio.R

import com.kire.audio.device.audio.util.PlayerState
import com.kire.audio.device.audio.util.state
import com.kire.audio.presentation.navigation.transitions.ListScreenTransitions
import com.kire.audio.presentation.util.ListSelector
import com.kire.audio.presentation.ui.cross_screen_ui.OnScrollListener
import com.kire.audio.presentation.ui.cross_screen_ui.ScrollToTopButton
import com.kire.audio.presentation.ui.list_screen_ui.top_block.TopBlock
import com.kire.audio.presentation.ui.list_screen_ui.TrackItem
import com.kire.audio.presentation.ui.screen.destinations.ListAlbumScreenDestination
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination(style = ListScreenTransitions::class)
@Composable
fun ListScreen(
    trackViewModel: TrackViewModel,
    navigator: DestinationsNavigator,
    mediaController: MediaController?
) {

    var playerState: PlayerState? by remember {
        mutableStateOf(mediaController?.state())
    }

    DisposableEffect(key1 = mediaController) {
        mediaController?.run {
            playerState = state()
        }
        onDispose {
            playerState?.dispose()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val trackUiState by trackViewModel.trackUiState.collectAsStateWithLifecycle()

    val tracks by trackViewModel.selectListOfTracks(ListSelector.MAIN_LIST).collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2 && trackUiState.isPlayerBottomCardShown
        }
    }

    OnScrollListener(
        listState = listState,
        trackUiState = trackUiState,
        changeTrackUiState = trackViewModel::updateTrackUiState
    )

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = AudioExtendedTheme.extendedColors.background)
            .padding(horizontal = dimensionResource(id = R.dimen.app_universal_pad)),
        contentPadding = PaddingValues(bottom = dimensionResource(id = R.dimen.column_universal_vertical_content_pad)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.column_and_row_universal_spacedby)),
    ) {

        item {

            TopBlock(
                trackViewModel = trackViewModel,
                onTitleClick = {
                    navigator.navigate(ListAlbumScreenDestination)
                    trackViewModel.updateArtistWithTracks()
                },
                mediaController = mediaController
            )
        }

        itemsIndexed(
            tracks,
            key = { _, track ->
                track.id
            }
        ) { listIndex, track ->

            TrackItem(
                trackToShow = track,
                trackUiState = trackUiState,
                updateTrackUiState = trackViewModel::updateTrackUiState,
                upsertTrack = trackViewModel::upsertTrack,
                selector = ListSelector.MAIN_LIST,
                mediaController = mediaController,
                listINDEX = listIndex
            )
        }
    }

    val itemSize = 70.dp
    val density = LocalDensity.current
    val itemSizePx = with(density) { itemSize.toPx() }
    val itemsScrollCount = tracks.size

    ScrollToTopButton(
        showButton = showButton,
        onClick = {
            coroutineScope.launch {
                listState.animateScrollBy(
                    value = -1 * itemSizePx * itemsScrollCount,
                    animationSpec = tween(durationMillis = 3000)
                )
            }
        }
    )
}








