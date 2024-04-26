package com.kire.audio.presentation.screen.player_screen_ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage

import com.kire.audio.R
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.util.CardFace
import com.kire.audio.presentation.util.LyricsRequestMode

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImageAndTextBlock(
    trackUiState: TrackUiState,
    navigateBack: () -> Unit,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    getTrackLyricsFromGenius: suspend (LyricsRequestMode, String?, String?, String?) -> ILyricsRequestState
){
    val coroutineScope = rememberCoroutineScope()

    val waitingMessage = stringResource(R.string.lyrics_dialog_waiting_message)
    val unsuccessfulMessage = stringResource(R.string.lyrics_dialog_unsuccessful_message)

    val userInput = remember { mutableStateOf(trackUiState.currentTrackPlaying?.lyrics) }

    var isEnabled by rememberSaveable { mutableStateOf(false) }

    var switcher by remember { mutableStateOf(LyricsRequestMode.DEFAULT) }

    val track = trackUiState.currentTrackPlaying

    val lyricsRequestState: MutableState<ILyricsRequestState> = remember {
        mutableStateOf(ILyricsRequestState.onRequest)
    }

    LaunchedEffect(key1 = track?.path) {
        if (track?.lyrics?.isEmpty() == true) {
            lyricsRequestState.value = ILyricsRequestState.onRequest

            userInput.value = ""
            switcher = LyricsRequestMode.DEFAULT

            this.launch(Dispatchers.IO) {
                lyricsRequestState.value = getTrackLyricsFromGenius(LyricsRequestMode.DEFAULT, track.title, track.artist, userInput.value).also {
                    if (it is ILyricsRequestState.Success){
                        userInput.value = it.lyrics
                    }
                }
            }
        }
        else
            userInput.value = track?.lyrics
    }

    LaunchedEffect(key1 = lyricsRequestState) {
        if (lyricsRequestState.value is ILyricsRequestState.Success){
            track?.let { tr ->
                upsertTrack(tr.copy(lyrics = (lyricsRequestState.value as ILyricsRequestState.Success).lyrics.also { userInput.value = it }))
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(bottom = 14.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .padding(bottom = 22.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Header(
            trackUiState = trackUiState,
            changeTrackUiState = changeTrackUiState,
            upsertTrack = upsertTrack,
            navigateBack = navigateBack
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
                .wrapContentHeight(),
        ) {

            var cardFace by rememberSaveable {
                mutableStateOf(CardFace.Front)
            }

            FlipCard(
                cardFace = cardFace,
                onClick = {
                    if (!isEnabled)
                        cardFace = cardFace.next
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1f)
                    .clip(RoundedCornerShape(25.dp)),
                front = {
                    Crossfade(
                        targetState = trackUiState.currentTrackPlaying?.imageUri,
                        label = "Track Image in foreground"
                    ) {
                        AsyncImage(
                            model = it,
                            contentDescription = "Track Image in foreground",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(25.dp))
                        )
                    }
                },
                back = { graphicModifier ->
                    Column(
                        modifier = graphicModifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(size = 25.dp)
                            )
                            .verticalScroll(rememberScrollState())
                            .padding(
                                start = 32.dp,
                                end = 32.dp,
                            ),
                        verticalArrangement =  Arrangement.spacedBy(if (!isEnabled && userInput.value?.isEmpty() == true && lyricsRequestState.value == ILyricsRequestState.onRequest) 0.dp else 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(top = 28.dp)
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

                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                ) {
                                    if (isEnabled && switcher == LyricsRequestMode.EDIT_CURRENT_TEXT)
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .bounceClick {
                                                    userInput.value = ""
                                                }
                                        )
                                    else if (userInput.value?.isEmpty() == true && lyricsRequestState.value == ILyricsRequestState.Unsuccess){
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .bounceClick {
                                                    lyricsRequestState.value =
                                                        ILyricsRequestState.onRequest

                                                    coroutineScope.launch(Dispatchers.IO) {
                                                        lyricsRequestState.value =
                                                            getTrackLyricsFromGenius(LyricsRequestMode.DEFAULT, track?.title, track?.artist, userInput.value)
                                                    }

                                                }
                                        )
                                    }
                                }

                                Text(
                                    text = stringResource(R.string.lyrics_dialog_header),
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
                                                if (!userInput.equals(track?.lyrics))
                                                    if (switcher == LyricsRequestMode.BY_LINK || switcher == LyricsRequestMode.BY_TITLE_AND_ARTIST || (userInput.value?.isEmpty() == true && it))
                                                        coroutineScope.launch(Dispatchers.IO) {
                                                            lyricsRequestState.value =
                                                                getTrackLyricsFromGenius(switcher, track?.title, track?.artist, userInput.value)
                                                        }
                                                    else
                                                        coroutineScope.launch(Dispatchers.IO) {
                                                            track?.let { track ->
                                                                upsertTrack(
                                                                    track.copy(
                                                                        lyrics = userInput.value
                                                                            ?: "No lyrics"
                                                                    )
                                                                )
                                                            }

                                                        }

                                                switcher = if (!it)
                                                    LyricsRequestMode.SELECTOR_IS_VISIBLE
                                                else
                                                    LyricsRequestMode.DEFAULT
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

                        if (isEnabled && switcher == LyricsRequestMode.SELECTOR_IS_VISIBLE)

                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalAlignment = Alignment.Start
                            ){

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .bounceClick {
                                            switcher = LyricsRequestMode.BY_LINK
                                            userInput.value = ""
                                        },
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector = Icons.Rounded.Link,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.secondaryContainer,
                                        modifier = Modifier
                                            .size(32.dp)
                                    )
                                    Text(
                                        text = "By Genius link",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.W300,
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .bounceClick {
                                            switcher = LyricsRequestMode.BY_TITLE_AND_ARTIST
                                            userInput.value = ""
                                        },
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector = Icons.Rounded.Lyrics,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.secondaryContainer,
                                        modifier = Modifier
                                            .size(32.dp)
                                    )
                                    Text(
                                        text = "By artist name & song title",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.W300,
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .bounceClick {
                                            switcher = LyricsRequestMode.EDIT_CURRENT_TEXT
                                        },
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector = Icons.Rounded.EditNote,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.secondaryContainer,
                                        modifier = Modifier
                                            .size(32.dp)
                                    )
                                    Text(
                                        text = "Edit current text",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.W300,
                                    )
                                }
                            }

                        if (isEnabled || userInput.value?.isNotEmpty() == true)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp, bottom = 28.dp),
                                contentAlignment = Alignment.Center
                            ){
                                BasicTextField(
                                    modifier = Modifier
                                        .background(
                                            Color.Transparent,
                                            MaterialTheme.shapes.small,
                                        )
                                        .fillMaxWidth(),
                                    value = if (switcher != LyricsRequestMode.SELECTOR_IS_VISIBLE) userInput.value ?: "No lyrics" else "",
                                    onValueChange = {
                                        userInput.value = it
                                    },
                                    enabled = isEnabled,
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    textStyle = LocalTextStyle.current.copy(
                                        color = MaterialTheme.colorScheme.onTertiary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W600,
                                    ),
                                    decorationBox = { innerTextField ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(Modifier.weight(1f)) {
                                                if (switcher != LyricsRequestMode.SELECTOR_IS_VISIBLE && userInput.value?.isEmpty() == true) {
                                                    if (switcher == LyricsRequestMode.BY_LINK)
                                                        Text(
                                                            text = "Link example: https://genius.com/While-she-sleeps-feel-lyrics",
                                                            color = MaterialTheme.colorScheme.onPrimary,
                                                            fontSize = 15.sp,
                                                            fontFamily = FontFamily.SansSerif,
                                                            fontWeight = FontWeight.W300,
                                                        )
                                                    if (switcher == LyricsRequestMode.BY_TITLE_AND_ARTIST)
                                                        Text(
                                                            text = "Artist & title example: while she sleeps feels",
                                                            color = MaterialTheme.colorScheme.onPrimary,
                                                            fontSize = 15.sp,
                                                            fontFamily = FontFamily.SansSerif,
                                                            fontWeight = FontWeight.W300,
                                                        )
                                                }
                                                innerTextField()
                                            }
                                        }
                                    }
                                )
                            }

                        else
                            Box(
                                modifier =
                                if (lyricsRequestState.value == ILyricsRequestState.onRequest)
                                    Modifier
                                        .fillMaxSize()
                                        .weight(1f, fill = false)
                                        .padding(bottom = 28.dp)
                                else Modifier
                                    .wrapContentHeight()
                                    .padding(bottom = 28.dp),
                                contentAlignment = Alignment.Center
                            ){

                                if (!isEnabled && userInput.value?.isEmpty() == true && switcher == LyricsRequestMode.DEFAULT){
                                    Text(
                                        text = when(lyricsRequestState.value){
                                            is ILyricsRequestState.Success -> (lyricsRequestState.value as ILyricsRequestState.Success).lyrics
                                            is ILyricsRequestState.Unsuccess -> unsuccessfulMessage
                                            is ILyricsRequestState.onRequest -> waitingMessage
                                        },
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.W600,
                                        textAlign = if (lyricsRequestState.value == ILyricsRequestState.onRequest) TextAlign.Center else TextAlign.Start,
                                    )
                                }
                            }
                    }
                },
            )
        }

        TextAndHeart(
            trackUiState = trackUiState,
            changeTrackUiState = changeTrackUiState,
            upsertTrack = upsertTrack
        )
    }
}