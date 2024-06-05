package com.kire.audio.presentation.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import coil.compose.AsyncImage
import coil.request.ImageRequest

import com.kire.audio.R

import com.kire.audio.presentation.navigation.transitions.AlbumScreenTransitions
import com.kire.audio.presentation.ui.album_screen_ui.AlbumScreenHeader
import com.kire.audio.presentation.ui.album_screen_ui.dialog_album_info.DialogAlbumInfo
import com.kire.audio.presentation.ui.cross_screen_ui.OnScrollListener
import com.kire.audio.presentation.ui.list_screen_ui.TrackItem
import com.kire.audio.presentation.ui.screen.destinations.ListAlbumScreenDestination
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.nonScaledSp
import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalFoundationApi::class)
@Destination(style = AlbumScreenTransitions::class)
@Composable
fun AlbumScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigator: DestinationsNavigator
) {

    val albumUiState by trackViewModel.albumUiState.collectAsStateWithLifecycle()
    val trackUiState by trackViewModel.trackUiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    var openDialog by remember {
        mutableStateOf(false)
    }

    OnScrollListener(
        listState = listState,
        trackUiState = trackUiState,
        changeTrackUiState = trackViewModel::updateTrackUiState
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->

                    if (dragAmount > 60) {
                        navigator.popBackStack(
                            ListAlbumScreenDestination,
                            inclusive = false
                        )
                    }
                }
            }
            .background(AudioExtendedTheme.extendedColors.background),
        verticalArrangement = Arrangement.spacedBy((-24).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(albumUiState.tracks[0].imageUri)
                .build(),
            placeholder = painterResource(id = R.drawable.music_icon),
            contentDescription = "Shopping cart item image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1.2f)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    AudioExtendedTheme.extendedColors.background,
                    RoundedCornerShape(
                        topStart = dimensionResource(id = R.dimen.app_universal_rounded_corner),
                        topEnd = dimensionResource(id = R.dimen.app_universal_rounded_corner)
                    )
                )
                .padding(
                    start = dimensionResource(id = R.dimen.app_universal_pad),
                    end = dimensionResource(id = R.dimen.app_universal_pad)
                ),
            state = listState,
            contentPadding = PaddingValues(
                top = dimensionResource(id = R.dimen.column_universal_vertical_content_pad),
                bottom = dimensionResource(id = R.dimen.column_universal_vertical_content_pad)
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.column_and_row_universal_spacedby))
        ) {

            item {
                Text(
                    text = albumUiState.tracks[0].album ?: "No album",
                    color = AudioExtendedTheme.extendedColors.primaryText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp.nonScaledSp,
                    lineHeight = 24.sp.nonScaledSp,
                    modifier = Modifier
                        .basicMarquee(
                            animationMode = MarqueeAnimationMode.Immediately,
                            delayMillis = 0
                        )
                )
            }
            itemsIndexed(
                albumUiState.tracks,
                key = { _, track ->
                    track.id
                }
            ) { index, track ->
                TrackItem(
                    trackToShow = track,
                    listINDEX = index,
                    trackUiState = trackUiState,
                    updateTrackUiState = trackViewModel::updateTrackUiState,
                    mediaController = mediaController,
                    upsertTrack = trackViewModel::upsertTrack
                )
            }
        }
    }

    AlbumScreenHeader(
        openDialog = {
            openDialog = true
        },
        navigateBack = {
            navigator.popBackStack()
        }
    )

    if (openDialog) {
        DialogAlbumInfo(
            albumUiState = albumUiState,
            trackUiState = trackUiState,
            updateTrackUiState = trackViewModel::updateTrackUiState,
            upsertTrack = trackViewModel::upsertTrack,
            updateArtistWithTracks = trackViewModel::updateArtistWithTracks,
            updateOpenDialog = {
                openDialog = false
            }
        )
    }
}