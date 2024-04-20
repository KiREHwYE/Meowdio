package com.kire.audio.presentation.screen.player_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kire.audio.R
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.screen.functional.convertLongToTime
import com.kire.audio.screen.functional.getContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogInfo(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    changeOpenDialog: (Boolean) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
) {

    val context = getContext()

    var openDialog by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()



    val minutesAll = TimeUnit.MILLISECONDS.toMinutes(trackUiState.currentTrackPlaying?.duration ?: 0L)
    val secondsAll = TimeUnit.MILLISECONDS.toSeconds(trackUiState.currentTrackPlaying?.duration ?: 0L) % 60

    val map = mapOf(
        stringResource(id = R.string.info_dialog_title) to trackUiState.currentTrackPlaying?.title,
        stringResource(id = R.string.info_dialog_artist) to trackUiState.currentTrackPlaying?.artist,
        stringResource(id = R.string.info_dialog_album) to (trackUiState.currentTrackPlaying?.album ?: "0"),
        stringResource(id = R.string.info_dialog_duration) to "$minutesAll:$secondsAll",
        stringResource(id = R.string.info_dialog_favourite) to if (trackUiState.currentTrackPlaying?.isFavourite == true) "Yes" else "No",
        stringResource(id = R.string.info_dialog_date_added) to convertLongToTime(trackUiState.currentTrackPlaying?.dateAdded?.toLong() ?: 0),
        stringResource(id = R.string.info_dialog_album_id) to trackUiState.currentTrackPlaying?.albumId.toString(),
        stringResource(id = R.string.info_dialog_image_uri) to trackUiState.currentTrackPlaying?.imageUri.toString(),
        stringResource(id = R.string.info_dialog_path) to trackUiState.currentTrackPlaying?.path
    )

    var isEnabled by rememberSaveable { mutableStateOf(false) }

    var newTitle by rememberSaveable { mutableStateOf(trackUiState.currentTrackPlaying?.title) }
    var newArtist by rememberSaveable { mutableStateOf(trackUiState.currentTrackPlaying?.artist) }
    var newAlbum by rememberSaveable { mutableStateOf(trackUiState.currentTrackPlaying?.album) }


    BasicAlertDialog(
        onDismissRequest = {
            changeOpenDialog(false)
        }
    ) {

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 24.dp)
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

            header {
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

                        Box(modifier = Modifier.size(18.dp)) {  }

                        Text(
                            text = stringResource(id = R.string.info_dialog_header),
                            fontWeight = FontWeight.W700,
                            fontSize = 28.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Icon(
                            imageVector = if (!isEnabled) Icons.Rounded.Edit else Icons.Rounded.Save,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .size(18.dp)
                                .bounceClick {
                                    isEnabled = !isEnabled.also {
                                        trackUiState.currentTrackPlaying?.apply {
                                            if (
                                                title != newTitle ||
                                                artist != newArtist ||
                                                !album.equals(newAlbum)
                                            )
                                                coroutineScope.launch(Dispatchers.Default) {
                                                    upsertTrack(this@apply
                                                        .copy(
                                                            title = newTitle ?: "Null",
                                                            artist = newArtist ?: "Null",
                                                            album = newAlbum ?: "Null"
                                                        )
                                                        .also {
                                                            changeTrackUiState(trackUiState.copy(currentTrackPlaying = it))
                                                        }
                                                    )
                                                }
                                        }
                                    }
                                }
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                            .clip(
                                RoundedCornerShape(28.dp)
                            ),
                        thickness = 4.dp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }


            for(element in map){
                item {
                    GridElement(
                        text = element.key,
                        switcher = true,
                        isFirst = element.key == stringResource(id = R.string.info_dialog_title)
                    )
                }
                item {
                    GridElement(
                        text = element.value ?: "Null",
                        switcher = false,
                        isEnabled = isEnabled,
                        isImageURI = element.key == stringResource(id = R.string.info_dialog_image_uri),
                        isEditable = element.key in arrayOf(
                            stringResource(id = R.string.info_dialog_title),
                            stringResource(id = R.string.info_dialog_artist),
                            stringResource(id = R.string.info_dialog_album),
                            stringResource(id = R.string.info_dialog_image_uri)
                        ),
                        updateText = { newText ->
                            when(element.key){
                                context.getString(R.string.info_dialog_title) -> newTitle = newText
                                context.getString(R.string.info_dialog_artist) -> newArtist = newText
                                context.getString(R.string.info_dialog_album) -> newAlbum = newText
                            }
                        },
                        isFirst = element.key == stringResource(id = R.string.info_dialog_title),
                        changeOpenDialog = {isIt ->
                            openDialog = isIt
                        }
                    )
                }
            }
        }
    }

    trackUiState.currentTrackPlaying?.apply {
        if (openDialog) {
            DialogGalleryOrPhoto(
                imageUri = imageUri,
                defaultImageUri = defaultImageUri,
                changeOpenDialog = {isIt ->
                    openDialog = isIt
                },
                updateUri = { imageUri ->
                    coroutineScope.launch(Dispatchers.Default) {
                        upsertTrack(copy(imageUri = imageUri)
                            .also {
                                changeTrackUiState(trackUiState.copy(currentTrackPlaying = it))
                            }
                        )
                    }
                },
            )
        }
    }
}

private fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

@Composable
private fun GridElement(
    text: String,
    switcher: Boolean,
    isFirst:Boolean,
    isEnabled: Boolean = false,
    isEditable: Boolean = false,
    isImageURI: Boolean = false,
    updateText: ((String) -> Unit)? = null,
    changeOpenDialog: ((Boolean) -> Unit)? = null
){

    var newText by rememberSaveable { mutableStateOf(text) }

    if (switcher)
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .padding(top = if (isFirst) 18.dp else 0.dp)
        )
    else
        BasicTextField(
            modifier = Modifier
                .background(
                    Color.Transparent,
                    MaterialTheme.shapes.small,
                )
                .fillMaxWidth(0.5f)
                .padding(top = if (isFirst) 18.dp else 0.dp)
                .pointerInput(isEnabled && isEditable && isImageURI) {
                    detectTapGestures {
                        if (isEnabled && isEditable && isImageURI && changeOpenDialog != null)
                            changeOpenDialog(true)
                    }
                },
            value = newText,
            onValueChange = {
                newText = it.also {
                    if (updateText != null && newText != text)
                        updateText(it)
                }
            },
            enabled = isEnabled && isEditable && !isImageURI,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(1f)) {
                        innerTextField()
                    }
                    if (isEnabled && isEditable)
                        Icon(
                            imageVector = Icons.Rounded.Circle,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .size(8.dp)
                        )
                }
            }
        )
}