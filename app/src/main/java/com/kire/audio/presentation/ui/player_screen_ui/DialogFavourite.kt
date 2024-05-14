package com.kire.audio.presentation.ui.player_screen_ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.kire.audio.R
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.list_screen_ui.TrackItem
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.ListSelector
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DialogFavourite(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    mediaController: MediaController?,
    favouriteTracks: StateFlow<List<Track>>,
    upsertTrack: suspend (Track) -> Unit,
    changeOpenDialog: (Boolean) -> Unit
) {
    val _favouriteTracks by favouriteTracks.collectAsStateWithLifecycle()

    BasicAlertDialog(
        onDismissRequest = {
            changeOpenDialog(false)
        }
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(
                    color = AudioExtendedTheme.extendedColors.controlElementsBackground,
                    shape = RoundedCornerShape(size = 24.dp)
                ),
            contentPadding = PaddingValues(
                top = 28.dp,
                start = 32.dp,
                end = 32.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.favourite_dialog_header),
                        fontWeight = FontWeight.W700,
                        fontSize = 28.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = AudioExtendedTheme.extendedColors.primaryText
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                            .clip(
                                RoundedCornerShape(28.dp)
                            ),
                        thickness = 4.dp,
                        color = AudioExtendedTheme.extendedColors.divider
                    )
                }
            }

            itemsIndexed(
                _favouriteTracks,
                key = { _, track ->
                    track.id
                }
            ) { listIndex, track ->
                TrackItem(
                    trackToShow = track,
                    trackUiState = trackUiState,
                    changeTrackUiState = changeTrackUiState,
                    upsertTrack = upsertTrack,
                    selector = ListSelector.FAVOURITE_LIST,
                    mediaController = mediaController,
                    listINDEX = listIndex,
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ),
                    imageSize = 54.dp,
                    textTitleSize = 15.sp,
                    textArtistSize = 11.sp,
                    startPadding = 13.dp,
                    heartIconSize = 22.dp
                )
            }
        }
    }
}