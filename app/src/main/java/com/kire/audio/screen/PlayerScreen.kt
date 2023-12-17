package com.kire.audio.screen

import android.content.res.Configuration
import android.net.Uri

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOne

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.exoplayer.ExoPlayer

import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.kire.audio.functional.ListSelector

import com.kire.audio.R
import com.kire.audio.functional.bounceClick
import com.kire.audio.functional.convertLongToTime
import com.kire.audio.functional.getContext

import com.kire.audio.mediaHandling.SkipTrackAction
import com.kire.audio.models.Track
import com.kire.audio.viewmodels.TrackListViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import java.util.concurrent.TimeUnit

import kotlin.time.Duration.Companion.seconds

@Composable
fun Screen(
    track: Track,
    skipTrack: (SkipTrackAction, Boolean, Boolean)->Unit,
    saveRepeatMode: (Int) -> Unit,
    repeatMode: StateFlow<Int>,
    changeRepeatMode: (Int) -> Unit,
    durationGet: () -> Float,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    selectList: ListSelector,
    updateIsLoved: (Track) -> Unit,
    currentTrackPlaying: Track?,
    currentTrackPlayingURI: String,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
    play: () -> Unit,
    exoPlayer: ExoPlayer,
    changeIsExpanded: (Boolean) -> Unit,
    sentInfoToBottomSheet: (Track, ListSelector, Int, String) -> Unit
){

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ){

        Background(imageUri = track.imageUri)

        Column(modifier = Modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.86f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {


            ShowImageAndText(
                track = track,
                changeIsExpanded = changeIsExpanded,
                sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter,
                selectListTracks = selectListTracks,
                selectList = selectList,
                updateIsLoved = updateIsLoved,
                skipTrack = skipTrack

            )

            FunctionalBlock(
                saveRepeatMode = saveRepeatMode,
                skipTrack = skipTrack,
                currentTrackPlaying = currentTrackPlaying,
                updateIsLoved = updateIsLoved,
                sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter,
                repeatMode = repeatMode,
                changeRepeatMode = changeRepeatMode,
                exoPlayer = exoPlayer,
                durationGet = durationGet,
                sentInfoToBottomSheet = sentInfoToBottomSheet,
                play = play,
                selectListTracks = selectListTracks,
                currentTrackPlayingURI = currentTrackPlayingURI
            )
        }
    }
}


@Composable
fun Background(
    imageUri: Uri?
){

    val context = getContext()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){}

    Crossfade(
        targetState = imageUri,
        label = "Background Image"
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(it)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .allowHardware(true)
                .diskCacheKey(it.toString())
                .memoryCacheKey(it.toString())
                .build(),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply{
                if (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                    setToScale(0.35f,0.35f,0.35f,1f)
                else setToScale(0.6f,0.6f,0.6f,1f)
            }),
            modifier = Modifier
                .fillMaxWidth()
                .blur(10.dp)
                .alpha(1f)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    track: Track,
    changeIsExpanded: (Boolean) -> Unit
){

    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var isBottomSheetOpened by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        Icon(
            Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Close",
            modifier = Modifier
                .size(30.dp)
                .alpha(0.8f)
                .bounceClick {
                    coroutineScope.launch(Dispatchers.IO) {
                        changeIsExpanded(false)
                    }
                },
            tint = Color.White
        )

        Icon(
            Icons.Rounded.MoreVert,
            contentDescription = "Settings",
            modifier = Modifier
                .size(30.dp)
                .alpha(0.8f)
                .bounceClick {
                    isBottomSheetOpened = true
                },
            tint = Color.White
        )

        if (isBottomSheetOpened){
            ModalBottomSheetTrackInfo(
                track = track,
                modalBottomSheetState = modalBottomSheetState,
                changeBottomSheetOpened = {isOpened ->
                    isBottomSheetOpened = isOpened
                }
            )
        }
    }
}

@Composable
fun GridElement(
    text: String,
    switcher: Boolean,
    isLastElement: Boolean
){

    if (switcher)
        Text(
            text = text,
            color =  MaterialTheme.colorScheme.onPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .padding(bottom = if (isLastElement) 14.dp else 0.dp)
        )
    else
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onTertiary,
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(bottom = if (isLastElement) 14.dp else 0.dp)
        )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetTrackInfo(
    track: Track,
    modalBottomSheetState: SheetState,
    changeBottomSheetOpened: (Boolean) -> Unit
){

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.5f),
        onDismissRequest = {
            changeBottomSheetOpened(false)
        },
        tonalElevation = 10.dp,
        containerColor = MaterialTheme.colorScheme.onBackground,
        sheetState = modalBottomSheetState,
        dragHandle = {}
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, start = 32.dp, end = 32.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.info_bottom_sheet_header),
                    fontWeight = FontWeight.W700,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.onPrimary
                )
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

            val minutesAll = TimeUnit.MILLISECONDS.toMinutes(track.duration)
            val secondsAll = TimeUnit.MILLISECONDS.toSeconds(track.duration) % 60

            val map = mapOf(
                stringResource(id = R.string.info_bottom_sheet_title) to track.title,
                stringResource(id = R.string.info_bottom_sheet_artist) to track.artist,
                stringResource(id = R.string.info_bottom_sheet_album) to (track.album ?: "0"),
                stringResource(id = R.string.info_bottom_sheet_duration) to "$minutesAll:$secondsAll",
                stringResource(id = R.string.info_bottom_sheet_favourite) to if (track.isFavourite) "Yes" else "No",
                stringResource(id = R.string.info_bottom_sheet_date_added) to convertLongToTime(track.date_added?.toLong() ?: 0),
                stringResource(id = R.string.info_bottom_sheet_album_id) to track.album_id.toString(),
                stringResource(id = R.string.info_bottom_sheet_image_uri) to track.imageUri.toString(),
                stringResource(id = R.string.info_bottom_sheet_path) to track.path
            )

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                columns = GridCells.Fixed(count = 2),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {


                for(element in map){
                    item {
                        GridElement(
                            text = element.key,
                            switcher = true,
                            isLastElement = element.key == stringResource(id = R.string.info_bottom_sheet_path)
                        )
                    }
                    item {
                        GridElement(
                            text = element.value,
                            switcher = false,
                            isLastElement = element.value == track.path
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShowImageAndText(
    track: Track,
    changeIsExpanded: (Boolean) -> Unit,
    skipTrack: (SkipTrackAction, Boolean, Boolean) -> Unit,
    selectList: ListSelector,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    updateIsLoved: (Track) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
){

    val imageUri = track.imageUri

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.68f)
            .padding(bottom = 22.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Header(
            track = track,
            changeIsExpanded = changeIsExpanded
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
                .wrapContentHeight(),
        ) {

            Crossfade(
                targetState = imageUri,
                label = "Track Image in foreground"
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .allowHardware(true)
                        .diskCacheKey(it.toString())
                        .memoryCacheKey(it.toString())
                        .build(),
                    contentDescription = "Track Image in foreground",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f / 1f)
                        .clip(RoundedCornerShape(25.dp))
                )
            }

            TextBlock(
                track = track,
                skipTrack = skipTrack,
                selectList = selectList,
                selectListTracks = selectListTracks,
                updateIsLoved = updateIsLoved,
                sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextBlock(
    track: Track,
    skipTrack: (SkipTrackAction, Boolean, Boolean)->Unit,
    selectList: ListSelector,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    updateIsLoved: (Track) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit
){
    val title = track.title
    val artist = track.artist

    val currentTrackList by selectListTracks(selectList).collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                ) {
                    append(title)
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.LightGray,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W300,
                        fontFamily = FontFamily.SansSerif
                    )
                ) {
                    append("\n" + artist)
                }
            },
            modifier = Modifier
                .padding(start = 6.dp)
                .fillMaxWidth(0.8f)
                .alpha(0.8f)
                .basicMarquee(
                    animationMode = MarqueeAnimationMode.Immediately,
                    delayMillis = 0
                )
        )

        Icon(
            if (track.isFavourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = "Favourite",
            tint = if (track.isFavourite) Color.Red else Color.White,
            modifier = Modifier
                .size(34.dp)
                .alpha(0.8f)
                .bounceClick {
                    if (selectList == ListSelector.FAVOURITE_LIST && track.isFavourite) {
                        TrackListViewModel.isRepeated.value = false

                        if (currentTrackList.size == 1) {
                            coroutineScope.launch(Dispatchers.IO) {
                                updateIsLoved(
                                    track
                                        .copy(isFavourite = !track.isFavourite)
                                        .also {
                                            sentInfoToBottomSheetOneParameter(it)
                                        }
                                )
                            }
                        } else {
                            coroutineScope.launch(Dispatchers.IO) {
                                updateIsLoved(
                                    track.copy(isFavourite = !track.isFavourite)
                                )
                            }

                            skipTrack(SkipTrackAction.NEXT, true, false)
                        }
                    } else {
                        coroutineScope.launch(Dispatchers.IO) {
                            updateIsLoved(
                                track
                                    .copy(isFavourite = !track.isFavourite)
                                    .also {
                                        sentInfoToBottomSheetOneParameter(it)
                                    }
                            )
                        }
                    }
                }
        )
    }
}




@Composable
fun SliderBlock(
    durationGet: () -> Float,
    exoPlayer: ExoPlayer
){

    var sliderPosition by remember {
        mutableFloatStateOf(exoPlayer.currentPosition.toFloat())
    }

    LaunchedEffect(Unit) {
        while(true) {
            sliderPosition = exoPlayer.currentPosition.toFloat()
            delay(1.seconds / 70)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-5).dp)
    ) {

        Slider(modifier = Modifier
            .fillMaxWidth(),
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                exoPlayer.seekTo(it.toLong())
                TrackListViewModel.reason.value = true
            },
            valueRange = 0f..durationGet(),
            colors = SliderDefaults.colors(
                inactiveTrackColor = Color.LightGray,
                activeTrackColor = Color.LightGray,
                thumbColor = Color.White
            )
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .width(323.dp)
                .wrapContentHeight()
        ) {

            val minutesCur = TimeUnit.MILLISECONDS.toMinutes(exoPlayer.currentPosition)
            val secondsCur = TimeUnit.MILLISECONDS.toSeconds(exoPlayer.currentPosition) % 60
            val minutesAll = TimeUnit.MILLISECONDS.toMinutes(durationGet().toLong())
            val secondsAll = TimeUnit.MILLISECONDS.toSeconds(durationGet().toLong()) % 60

            Text(
                text = "$minutesCur:$secondsCur",
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.alpha(0.7f)
            )
            Text(
                text = "${if (minutesAll >= 0) minutesAll else 0}:${if (secondsAll >= 0) secondsAll else 0}",
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.alpha(0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlButtons(
    skipTrack: (SkipTrackAction, Boolean, Boolean)->Unit,
    exoPlayer: ExoPlayer,
    saveRepeatMode: (Int) -> Unit,
    repeatMode: StateFlow<Int>,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    currentTrackPlaying: Track?,
    currentTrackPlayingURI: String,
    updateIsLoved: (Track) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
    changeRepeatMode: (Int) -> Unit,
    sentInfoToBottomSheet: (Track, ListSelector, Int, String) -> Unit,
    play: () -> Unit
) {
    var isBottomSheetOpened by rememberSaveable { mutableStateOf(false) }

    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val isPlaying by TrackListViewModel.reason.collectAsStateWithLifecycle()

    val repeatMode by repeatMode.collectAsStateWithLifecycle()

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        Icon(
            when (repeatMode) {
                0 -> Icons.Rounded.Repeat
                1 -> Icons.Rounded.RepeatOne
                else -> Icons.Rounded.RepeatOn
            },
            contentDescription = "RepeatMode",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .alpha(0.7f)
                .bounceClick {
                    changeRepeatMode(((repeatMode + 1) % 3).also { rep ->
                        saveRepeatMode(rep)
                    })
                }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            Icon(
                painter = painterResource(id = R.drawable.skip_previous_button),
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(0.78f)
                    .bounceClick {
                        skipTrack(SkipTrackAction.PREVIOUS, false, false)
                    }
            )

            Icon(
                painter = painterResource(id =
                if(isPlaying)
                    R.drawable.pause_button
                else
                    R.drawable.play_button
                ),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier
                    .size(63.dp)
                    .alpha(0.8f)
                    .bounceClick {
                        play()
                    }
            )

            Icon(
                painter = painterResource(id = R.drawable.skip_next_button),
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(0.78f)
                    .bounceClick {
                        skipTrack(SkipTrackAction.NEXT, false, false)
                    }
            )
        }

        Icon(
            Icons.AutoMirrored.Rounded.PlaylistPlay,
            contentDescription = "Playlist",
            modifier = Modifier
                .size(30.dp)
                .alpha(0.7f)
                .bounceClick {
                    isBottomSheetOpened = true
                },
            tint = Color.White,
        )

        if (isBottomSheetOpened){
            ModalBottomSheetFavouriteTracks(
                exoPlayer = exoPlayer,
                modalBottomSheetState = modalBottomSheetState,
                favouriteTracks = selectListTracks(ListSelector.FAVOURITE_LIST),
                sentInfoToBottomSheet = sentInfoToBottomSheet,
                currentTrackPlayingURI = currentTrackPlayingURI,
                currentTrackPlaying = currentTrackPlaying,
                updateIsLoved = updateIsLoved,
                sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter,
                changeBottomSheetOpened = {isOpened ->
                    isBottomSheetOpened = isOpened
                }
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ModalBottomSheetFavouriteTracks(
    exoPlayer: ExoPlayer,
    modalBottomSheetState: SheetState,
    favouriteTracks: StateFlow<List<Track>>,
    currentTrackPlaying: Track?,
    updateIsLoved: (Track) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
    sentInfoToBottomSheet: (Track, ListSelector, Int, String) -> Unit,
    currentTrackPlayingURI: String,
    changeBottomSheetOpened: (Boolean) -> Unit
){

    val favouriteTracks by favouriteTracks.collectAsStateWithLifecycle()

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.5f),
        onDismissRequest = {
            changeBottomSheetOpened(false)
        },
        tonalElevation = 10.dp,
        containerColor = MaterialTheme.colorScheme.onBackground,
        sheetState = modalBottomSheetState,
        dragHandle = { }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, start = 32.dp, end = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.favourite_bottom_sheet_header),
                    fontWeight = FontWeight.W700,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.onPrimary
                )
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = {

                    itemsIndexed(
                        favouriteTracks,
                        key = { _, track ->
                            track.id
                        }
                    ) { index, track ->
                        Item(
                            track = track,
                            selectList = ListSelector.FAVOURITE_LIST,
                            exoPlayer = exoPlayer,
                            currentUri = currentTrackPlayingURI,
                            trackINDEX = index,
                            currentTrackPlaying = currentTrackPlaying,
                            updateIsLoved = updateIsLoved,
                            sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter,
                            sentInfoToBottomSheet = sentInfoToBottomSheet,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = FastOutSlowInEasing
                                )
                            ),
                            imageSize = 60.dp,
                            textTitleSize = 19.sp,
                            textArtistSize = 15.sp,
                            startPadding = 17.dp
                        )
                    }
                }
            )
        }
    }
}


@Composable
fun FunctionalBlock(
    skipTrack: (SkipTrackAction, Boolean, Boolean)->Unit,
    currentTrackPlaying: Track?,
    currentTrackPlayingURI: String,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    updateIsLoved: (Track) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
    saveRepeatMode: (Int) -> Unit,
    repeatMode: StateFlow<Int>,
    changeRepeatMode: (Int) -> Unit,
    durationGet: () -> Float,
    exoPlayer: ExoPlayer,
    sentInfoToBottomSheet: (Track, ListSelector, Int, String) -> Unit,
    play: () -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        SliderBlock(
            durationGet = durationGet,
            exoPlayer = exoPlayer
        )

        ControlButtons(
            skipTrack = skipTrack,
            saveRepeatMode = saveRepeatMode,
            repeatMode = repeatMode,
            currentTrackPlaying = currentTrackPlaying,
            updateIsLoved = updateIsLoved,
            sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter,
            changeRepeatMode = changeRepeatMode,
            sentInfoToBottomSheet = sentInfoToBottomSheet,
            play = play,
            currentTrackPlayingURI = currentTrackPlayingURI,
            exoPlayer = exoPlayer,
            selectListTracks = selectListTracks
        )
    }
}