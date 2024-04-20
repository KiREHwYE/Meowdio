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
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.util.CardFace
import com.kire.audio.presentation.util.LyricsRequestMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.IOException

@Composable
fun ImageAndTextBlock(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit
){

    val waitingMessage = stringResource(R.string.lyrics_dialog_waiting_message)
    val unsuccessfulMessage = stringResource(R.string.lyrics_dialog_unsuccessful_message)

    var defaultMessage by remember { mutableStateOf(waitingMessage) }

    val coroutineScope = rememberCoroutineScope()

    val lyrics = remember { mutableStateOf(trackUiState.currentTrackPlaying?.lyrics) }

    var isEnabled by rememberSaveable { mutableStateOf(false) }

    var switcher by remember { mutableStateOf(LyricsRequestMode.DEFAULT) }


    fun String.toAllowedForm(): String {
        val notAllowedCharacters = "[^\\sa-zA-Z0-9_-]".toRegex()
        val hyphen = "[\\s_]+".toRegex()

        return this.trim().lowercase().replace("&", "and").replace(notAllowedCharacters, "")
            .replace(hyphen, "-").run {
                if (this.contains("feat")) this.removeRange(
                    this.indexOf("feat") - 1,
                    this.length
                ) else this
            }
    }

    val lyricsRequest: (mode: LyricsRequestMode) -> Unit = {

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val title =
                    trackUiState.currentTrackPlaying?.title?.toAllowedForm()

                val artist =
                    trackUiState.currentTrackPlaying?.artist?.toAllowedForm()?.replaceFirstChar(Char::titlecase)

                val url =
                    when(it) {
                        LyricsRequestMode.BY_LINK -> lyrics.value.also {
                            lyrics.value = ""
                            defaultMessage = waitingMessage
                        }
                        LyricsRequestMode.BY_TITLE_AND_ARTIST -> {

                            val urlPart = lyrics.value?.toAllowedForm()?.replaceFirstChar(Char::titlecase)
                            lyrics.value = ""
                            defaultMessage = waitingMessage
                            ("https://genius.com/$urlPart-lyrics").replace("--+".toRegex(), "-")
                        }
                        else -> {
                            ("https://genius.com/$artist-$title-lyrics").replace("--+".toRegex(), "-")
                        }
                    }

                var doc: org.jsoup.nodes.Document =
                    Jsoup.connect(url).userAgent(
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0"
                    ).get()
                val temp = doc.html().replace("<br>", "$$$")
                doc = Jsoup.parse(temp)

                val elements = doc.select("div.Lyrics__Container-sc-1ynbvzw-1.kUgSbL")

                var text = ""

                for (i in 0 until elements.size)
                    text += elements.eq(i).text().replace("$$$", "\n")

                lyrics.value = text

                trackUiState.currentTrackPlaying?.let { track ->
                    upsertTrack(track.copy(lyrics = lyrics.value ?: "No lyrics"))
                }

            } catch (e: IOException) {

                if (it != LyricsRequestMode.BY_LINK)
                    lyrics.value = ""

                defaultMessage = unsuccessfulMessage
            }
        }

        switcher = LyricsRequestMode.DEFAULT
    }

    LaunchedEffect(trackUiState.currentTrackPlaying?.path) {
        if (trackUiState.currentTrackPlaying?.lyrics?.isEmpty() == true) {
            defaultMessage = waitingMessage
            lyrics.value = ""
            switcher = LyricsRequestMode.DEFAULT

            lyricsRequest(LyricsRequestMode.DEFAULT)
        }
        else
            lyrics.value = trackUiState.currentTrackPlaying?.lyrics
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
            upsertTrack = upsertTrack
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
                        verticalArrangement =  Arrangement.spacedBy(if (!isEnabled && lyrics.value?.isEmpty() == true && defaultMessage == waitingMessage) 0.dp else 28.dp),
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
                                                    lyrics.value = ""
                                                }
                                        )
                                    else if (lyrics.value?.isEmpty() == true && defaultMessage == unsuccessfulMessage){
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .bounceClick {
                                                    defaultMessage = waitingMessage
                                                    lyricsRequest(LyricsRequestMode.DEFAULT)
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
                                                if (!lyrics.equals(trackUiState.currentTrackPlaying?.lyrics))
                                                    if (switcher == LyricsRequestMode.BY_LINK || switcher == LyricsRequestMode.BY_TITLE_AND_ARTIST || (lyrics.value?.isEmpty() == true && it))
                                                        lyricsRequest(switcher)
                                                    else
                                                        coroutineScope.launch(Dispatchers.Default) {
                                                            trackUiState.currentTrackPlaying?.let { track ->
                                                                upsertTrack(track.copy(lyrics = lyrics.value ?: "No lyrics"))
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
                                            lyrics.value = ""
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
                                            lyrics.value = ""
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

                        if (isEnabled || lyrics.value?.isNotEmpty() == true)
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
                                    value = if (switcher != LyricsRequestMode.SELECTOR_IS_VISIBLE) lyrics.value ?: "No lyrics" else "",
                                    onValueChange = {
                                        lyrics.value = it
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
                                                if (switcher != LyricsRequestMode.SELECTOR_IS_VISIBLE && lyrics.value?.isEmpty() == true) {
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
                                if (defaultMessage == waitingMessage)
                                    Modifier
                                        .fillMaxSize()
                                        .weight(1f, fill = false)
                                        .padding(bottom = 28.dp)
                                else Modifier
                                    .wrapContentHeight()
                                    .padding(bottom = 28.dp),
                                contentAlignment = Alignment.Center
                            ){

                                if (!isEnabled && lyrics.value?.isEmpty() == true && switcher == LyricsRequestMode.DEFAULT){
                                    Text(
                                        text = defaultMessage,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.W600,
                                        textAlign = if (defaultMessage == waitingMessage) TextAlign.Center else TextAlign.Start,
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