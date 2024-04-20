package com.kire.audio.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.material3.MaterialTheme

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
import com.kire.audio.R

import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

import com.kire.audio.device.audio.functional.PlayerState
import com.kire.audio.device.audio.rememberManagedMediaController
import com.kire.audio.device.audio.functional.state
import com.kire.audio.presentation.screen.list_screen_ui.BottomPlayer
import com.kire.audio.screen.functional.GetPermissions
import com.kire.audio.screen.functional.ListSelector
import com.kire.audio.presentation.screen.list_screen_ui.DropDownMenu
import com.kire.audio.presentation.screen.list_screen_ui.Header
import com.kire.audio.presentation.screen.list_screen_ui.OnScrollListener
import com.kire.audio.presentation.screen.list_screen_ui.ScrollToTopButton
import com.kire.audio.presentation.screen.list_screen_ui.SearchBar
import com.kire.audio.presentation.screen.list_screen_ui.TrackItem
import com.kire.audio.presentation.screen.list_screen_ui.UserActionBar

@Composable
fun ListScreen(
    viewModel: TrackViewModel
) {
    val mediaController by rememberManagedMediaController()

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

    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val trackUiState by viewModel.trackUiState.collectAsStateWithLifecycle()
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()

    val tracks by viewModel.selectListOfTracks(ListSelector.MAIN_LIST).collectAsStateWithLifecycle()

    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    OnScrollListener(
        listState = listState,
        currentTrackPlaying = trackUiState.currentTrackPlaying,
        trackUiState = trackUiState,
        changeTrackUiState = viewModel::changeTrackUiState
    )

    GetPermissions(
        lifecycleOwner = LocalLifecycleOwner.current,
        updateTrackDataBase = viewModel::updateTrackDataBase
    )

    mediaController?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = {

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {

                            Header(
                                text = stringResource(
                                    id = R.string.listscreen_header
                                )
                            )

                            UserActionBar(
                                updateTrackDataBase = viewModel::updateTrackDataBase,
                                dropDownMenu = {
                                    DropDownMenu(
                                        sortType = viewModel.sortType,
                                        saveSortOption = viewModel::saveSortOption,
                                        onEvent = viewModel::onEvent,
                                    )
                                },
                                searchBar = {
                                    SearchBar(
                                        trackUiState = trackUiState,
                                        changeTrackUiState = viewModel::changeTrackUiState,
                                        searchUiState = searchUiState,
                                        changeSearchUiState = viewModel::changeSearchUiState,
                                        mediaController = mediaController!!,
                                        upsertTrack = viewModel::upsertTrack,
                                        selectListTracks = viewModel::selectListOfTracks
                                    )
                                }
                            )
                        }
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
                            changeTrackUiState = viewModel::changeTrackUiState,
                            upsertTrack = viewModel::upsertTrack,
                            selector = ListSelector.MAIN_LIST,
                            mediaController = mediaController!!,
                            listINDEX = listIndex,
                            modifier = Modifier
                        )
                    }
                }
            )

            val itemSize = 70.dp
            val density = LocalDensity.current
            val itemSizePx = with(density) { itemSize.toPx() }
            val itemsScrollCount = tracks.size


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
                        durationMillis = 300
                    )
                ),
            ) {

                ScrollToTopButton {
                    coroutineScope.launch {
                        listState.animateScrollBy(
                            value = -1 * itemSizePx * itemsScrollCount,
                            animationSpec = tween(durationMillis = 4000)
                        )
                    }
                }
            }

            BottomPlayer(
                _trackUiState = viewModel.trackUiState,
                changeTrackUiState = viewModel::changeTrackUiState,
                upsertTrack = viewModel::upsertTrack,
                selectListOfTracks = viewModel::selectListOfTracks,
                saveRepeatMode = viewModel::saveRepeatMode,
                mediaController = mediaController!!
            )
        }
    }
}








