package com.kire.audio.presentation.ui.album_screen_ui.dialog_album_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kire.audio.R
import com.kire.audio.presentation.model.AlbumUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.player_screen_ui.dialog.DialogGalleryOrPhoto
import com.kire.audio.presentation.ui.player_screen_ui.dialog.dialog_info.GridElement
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.util.nonScaledSp
import com.kire.audio.screen.functional.convertLongToTime
import com.kire.audio.screen.functional.getContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAlbumInfo(
    albumUiState: AlbumUiState,
    trackUiState: TrackUiState,
    updateTrackUiState: (TrackUiState) -> Unit,
    updateOpenDialog: () -> Unit,
    updateArtistWithTracks: () -> Unit,
    upsertTrack: suspend (Track) -> Unit,
) {

    val context = getContext()

    var openDialog by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    albumUiState.apply {
        val summaryDuration = tracks.sumOf { it.duration }

        val hoursAll = TimeUnit.MILLISECONDS.toHours(summaryDuration)
        val minutesAll = TimeUnit.MILLISECONDS.toMinutes(summaryDuration)
        val secondsAll = TimeUnit.MILLISECONDS.toSeconds(summaryDuration) % 60

        val map = mapOf(
            stringResource(id = R.string.info_dialog_album) to tracks[0].album,
            stringResource(id = R.string.info_dialog_artist) to tracks[0].artist,
            stringResource(id = R.string.info_dialog_duration) to "$hoursAll:$minutesAll:$secondsAll",
            stringResource(id = R.string.info_dialog_date_added) to convertLongToTime(tracks[0].dateAdded?.toLong() ?: 0),
            stringResource(id = R.string.info_dialog_album_id) to tracks[0].albumId.toString(),
            stringResource(id = R.string.info_dialog_image_uri) to tracks[0].imageUri.toString(),
            stringResource(id = R.string.info_dialog_path) to tracks[0].path
        )

        var isEnabled by rememberSaveable { mutableStateOf(false) }

        var newArtist by rememberSaveable { mutableStateOf(tracks[0].artist) }
        var newAlbum by rememberSaveable { mutableStateOf(tracks[0].album) }

        BasicAlertDialog(
            onDismissRequest = {
                updateOpenDialog()
            }
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(
                        color = AudioExtendedTheme.extendedColors.controlElementsBackground,
                        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.app_universal_rounded_corner))
                    ),
                columns = GridCells.Fixed(count = 2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 32.dp,
                    end = 32.dp,
                    top = 28.dp,
                    bottom = 28.dp
                )
            ) {

                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){

                            Box(modifier = Modifier.size(20.dp)) {  }

                            Text(
                                text = stringResource(id = R.string.info_dialog_header),
                                fontWeight = FontWeight.W700,
                                fontSize = 28.sp.nonScaledSp,
                                fontFamily = FontFamily.SansSerif,
                                color = AudioExtendedTheme.extendedColors.primaryText
                            )

                            Icon(
                                imageVector = if (!isEnabled) Icons.Rounded.Edit else Icons.Rounded.Save,
                                contentDescription = "",
                                tint = AudioExtendedTheme.extendedColors.orangeAccents,
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.app_universal_icon_size))
                                    .bounceClick {
                                        isEnabled = !isEnabled.also {

                                            if (tracks[0].artist != newArtist || !tracks[0].album.equals(newAlbum)) {

                                                tracks.forEach { track ->
                                                    coroutineScope.launch(Dispatchers.IO) {
                                                        upsertTrack(track
                                                            .copy(
                                                                artist = newArtist,
                                                                album = newAlbum ?: "Null"
                                                            )
                                                            .also {
                                                                if (trackUiState.currentTrackPlaying?.id == track.id)
                                                                    updateTrackUiState(
                                                                        trackUiState.copy(
                                                                            currentTrackPlaying = it
                                                                        )
                                                                    )
                                                            }
                                                        )
                                                    }
                                                }

                                                updateArtistWithTracks()
                                            }
                                        }
                                    }
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(0.25f)
                                .clip(
                                    RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner))
                                ),
                            thickness = 4.dp,
                            color = AudioExtendedTheme.extendedColors.divider
                        )
                    }
                }


                for(element in map){
                    item {
                        GridElement(
                            text = element.key,
                            switcher = true,
                            isFirst = element.key == stringResource(id = R.string.info_dialog_album)
                        )
                    }
                    item {
                        GridElement(
                            text = element.value ?: "Null",
                            switcher = false,
                            isEnabled = isEnabled,
                            isImageURI = element.key == stringResource(id = R.string.info_dialog_image_uri),
                            isEditable = element.key in arrayOf(
                                stringResource(id = R.string.info_dialog_artist),
                                stringResource(id = R.string.info_dialog_album),
                                stringResource(id = R.string.info_dialog_image_uri)
                            ),
                            updateText = { newText ->
                                when(element.key){
                                    context.getString(R.string.info_dialog_artist) -> newArtist = newText
                                    context.getString(R.string.info_dialog_album) -> newAlbum = newText
                                }
                            },
                            isFirst = element.key == stringResource(id = R.string.info_dialog_album),
                            changeOpenDialog = {isIt ->
                                openDialog = isIt
                            }
                        )
                    }
                }
            }
        }


        if (openDialog) {
            DialogGalleryOrPhoto(
                imageUri = tracks[0].imageUri,
                defaultImageUri = tracks[0].defaultImageUri,
                changeOpenDialog = {isIt ->
                    openDialog = isIt
                },
                updateUri = { imageUri ->

                    if (tracks[0].imageUri != imageUri)

                        tracks.forEach { track ->
                            coroutineScope.launch(Dispatchers.Default) {
                                upsertTrack(track.copy(imageUri = imageUri)
                                    .also {
                                        if (trackUiState.currentTrackPlaying?.id == track.id)
                                            updateTrackUiState(trackUiState.copy(currentTrackPlaying = it))
                                    }
                                )
                            }
                        }
                },
            )
        }
    }

}