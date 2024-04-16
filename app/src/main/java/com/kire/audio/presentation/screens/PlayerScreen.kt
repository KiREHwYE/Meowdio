package com.kire.audio.presentation.screens

import android.Manifest

import android.content.pm.PackageManager
import android.content.res.Configuration

import android.net.Uri

import android.os.Environment

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Save

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text

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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import coil.compose.AsyncImage
import com.kire.audio.BuildConfig

import com.kire.audio.R
import com.kire.audio.data.repositories.functional.createImageFile
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.functional.RepeatMode

import com.kire.audio.device.audio.functional.SkipTrackAction
import com.kire.audio.presentation.models.Track
import com.kire.audio.presentation.models.TrackUiState
import com.kire.audio.screen.functional.ListSelector
import com.kire.audio.screen.functional.convertLongToTime
import com.kire.audio.screen.functional.getContext
import com.kire.audio.presentation.functional.bounceClick

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import org.jsoup.Jsoup

import java.io.File
import java.io.IOException

import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale
import java.util.Objects

import java.util.concurrent.TimeUnit

import kotlin.time.Duration.Companion.seconds

@Composable
fun Screen(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    skipTrack: (SkipTrackAction)->Unit,
    saveRepeatMode: (Int) -> Unit,
    durationGet: () -> Float,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    play: () -> Unit,
    mediaController: MediaController,
){

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ){

        Background(imageUri = trackUiState.currentTrackPlaying?.imageUri)

        Column(modifier = Modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.86f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {


            ShowImageAndText(
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                upsertTrack = upsertTrack
            )

            FunctionalBlock(
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                upsertTrack = upsertTrack,
                saveRepeatMode = saveRepeatMode,
                skipTrack = skipTrack,
                mediaController = mediaController,
                durationGet = durationGet,
                play = play,
                selectListTracks = selectListTracks
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
            model = it,
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply{
                if (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                    setToScale(0.35f,0.35f,0.35f,1f)
                else setToScale(0.7f,0.7f,0.7f,1f)
            }),
            modifier = Modifier
                .fillMaxWidth()
                .blur(10.dp)
                .alpha(1f)
        )

    }
}

@Composable
fun Header(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit
){

    var openDialog by remember {
        mutableStateOf(false)
    }

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
                        changeTrackUiState(trackUiState.copy(isPlayerScreenExpanded = false))
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
                    openDialog = !openDialog
                },
            tint = Color.White
        )

        if (openDialog)
            DialogInfo(
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                changeOpenDialog = {isIt ->
                    openDialog = isIt
                },
                upsertTrack = upsertTrack
            )
    }
}


@Composable
fun GridElement(
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogGalleryOrPhoto(
    changeOpenDialog: (Boolean) -> Unit,
    updateUri: (Uri) -> Unit,
    imageUri: Uri?,
    defaultImageUri: Uri?
){

    val context = getContext()

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { newUri ->
            if (newUri == null) return@rememberLauncherForActivityResult

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_$timeStamp.jpg"

            val input = context.contentResolver.openInputStream(newUri) ?: return@rememberLauncherForActivityResult
            val outputFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName)

            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }

            input.close()

            val localUri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider",
                outputFile
            )

            updateUri(localUri)
        }
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { isTaken ->
                if (isTaken)
                    updateUri(uri)
            }
        )


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(uri)
        } else {  }
    }

    BasicAlertDialog(
        onDismissRequest = {
            changeOpenDialog(false)
        }
    ) {

        Row(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 24.dp)
                )
                .padding(
                    top = 28.dp,
                    bottom = 28.dp,
                    start = 32.dp,
                    end = 32.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Icon(
                imageVector = Icons.Rounded.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(52.dp)
                    .bounceClick {
                        val permissionCheckResult =
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
            )
            Icon(
                imageVector = Icons.Rounded.PhotoLibrary,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(52.dp)
                    .bounceClick {
                        galleryLauncher.launch("image/*")
                    }
            )

            if (imageUri != defaultImageUri)
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(52.dp)
                        .bounceClick {
                            defaultImageUri?.let {
                                updateUri(it)
                            }
                        }
                )
        }
    }
}


private fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}


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
                                    stringResource(id = R.string.info_dialog_image_uri)),
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

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}


@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit = {},
    back: @Composable (Modifier) -> Unit = {}
) {

    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing,
        ),
        label = "FlipCardRotation"
    )

    Card(
        onClick = { onClick(cardFace) },
        modifier = modifier
            .graphicsLayer {
                rotationY = -rotation.value
                cameraDistance = 12f * density
            },
    ) {

            if (rotation.value <= 90f)
                front()

            back(
                Modifier
                    .graphicsLayer { rotationY = 180f }
                    .alpha(1f)
            )
    }
}


private enum class LyricsFetcherState {
    DEFAULT,
    SELECTOR_IS_VISIBLE,
    BY_LINK,
    BY_TITLE_AND_ARTIST,
    EDIT_CURRENT_TEXT
}


@Composable
fun ShowImageAndText(
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

    var switcher by remember { mutableStateOf(LyricsFetcherState.DEFAULT) }


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

    val lyricsRequest: (mode: LyricsFetcherState) -> Unit = {

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val title =
                    trackUiState.currentTrackPlaying?.title?.toAllowedForm()

                val artist =
                    trackUiState.currentTrackPlaying?.artist?.toAllowedForm()?.replaceFirstChar(Char::titlecase)

                val url =
                    when(it) {
                        LyricsFetcherState.BY_LINK -> lyrics.value.also {
                            lyrics.value = ""
                            defaultMessage = waitingMessage
                        }
                        LyricsFetcherState.BY_TITLE_AND_ARTIST -> {

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

                if (it != LyricsFetcherState.BY_LINK)
                    lyrics.value = ""

                defaultMessage = unsuccessfulMessage
            }
        }

        switcher = LyricsFetcherState.DEFAULT
    }

    LaunchedEffect(trackUiState.currentTrackPlaying?.path) {
        if (trackUiState.currentTrackPlaying?.lyrics?.isEmpty() == true) {
            defaultMessage = waitingMessage
            lyrics.value = ""
            switcher = LyricsFetcherState.DEFAULT

            lyricsRequest(LyricsFetcherState.DEFAULT)
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
                                    if (isEnabled && switcher == LyricsFetcherState.EDIT_CURRENT_TEXT)
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
                                                    lyricsRequest(LyricsFetcherState.DEFAULT)
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
                                                    if (switcher == LyricsFetcherState.BY_LINK || switcher == LyricsFetcherState.BY_TITLE_AND_ARTIST || (lyrics.value?.isEmpty() == true && it))
                                                        lyricsRequest(switcher)
                                                    else
                                                        coroutineScope.launch(Dispatchers.Default) {
                                                            trackUiState.currentTrackPlaying?.let { track ->
                                                                upsertTrack(track.copy(lyrics = lyrics.value ?: "No lyrics"))
                                                            }

                                                        }

                                                switcher = if (!it)
                                                    LyricsFetcherState.SELECTOR_IS_VISIBLE
                                                else
                                                    LyricsFetcherState.DEFAULT
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

                        if (isEnabled && switcher == LyricsFetcherState.SELECTOR_IS_VISIBLE)

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
                                            switcher = LyricsFetcherState.BY_LINK
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
                                            switcher = LyricsFetcherState.BY_TITLE_AND_ARTIST
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
                                            switcher = LyricsFetcherState.EDIT_CURRENT_TEXT
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
                                    value = if (switcher != LyricsFetcherState.SELECTOR_IS_VISIBLE) lyrics.value ?: "No lyrics" else "",
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
                                                if (switcher != LyricsFetcherState.SELECTOR_IS_VISIBLE && lyrics.value?.isEmpty() == true) {
                                                    if (switcher == LyricsFetcherState.BY_LINK)
                                                        Text(
                                                            text = "Link example: https://genius.com/While-she-sleeps-feel-lyrics",
                                                            color = MaterialTheme.colorScheme.onPrimary,
                                                            fontSize = 15.sp,
                                                            fontFamily = FontFamily.SansSerif,
                                                            fontWeight = FontWeight.W300,
                                                        )
                                                    if (switcher == LyricsFetcherState.BY_TITLE_AND_ARTIST)
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

                                if (!isEnabled && lyrics.value?.isEmpty() == true && switcher == LyricsFetcherState.DEFAULT){
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

        TextBlock(
            trackUiState = trackUiState,
            changeTrackUiState = changeTrackUiState,
            upsertTrack = upsertTrack
        )
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextBlock(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
){
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                        append(trackUiState.currentTrackPlaying?.title)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.LightGray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W300,
                            fontFamily = FontFamily.SansSerif
                        )
                    ) {
                        append("\n" + trackUiState.currentTrackPlaying?.artist)
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
                if (trackUiState.currentTrackPlaying?.isFavourite == true) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Favourite",
                tint = if (trackUiState.currentTrackPlaying?.isFavourite == true) Color.Red else Color.White,
                modifier = Modifier
                    .size(34.dp)
                    .alpha(0.8f)
                    .bounceClick {
                        MediaCommands.isTrackRepeated.value = false

                        coroutineScope.launch(Dispatchers.IO) {
                            trackUiState.currentTrackPlaying?.let { track ->
                                upsertTrack(track.copy(isFavourite = !track.isFavourite)
                                        .also {
                                            changeTrackUiState(trackUiState.copy(currentTrackPlaying = it))
                                        }
                                )
                            }
                        }
                    }
            )
        }
    }
}


@Composable
fun SliderBlock(
    durationGet: () -> Float,
    mediaController: MediaController
){

    var sliderPosition by remember {
        mutableFloatStateOf(mediaController.currentPosition.toFloat())
    }

    LaunchedEffect(Unit) {
        while(true) {
            sliderPosition = mediaController.currentPosition.toFloat()
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
                mediaController.seekTo(it.toLong())
                mediaController.play()
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

            val minutesCur = TimeUnit.MILLISECONDS.toMinutes(mediaController.currentPosition)
            val secondsCur = TimeUnit.MILLISECONDS.toSeconds(mediaController.currentPosition) % 60
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

@Composable
fun ControlButtons(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    skipTrack: (SkipTrackAction) -> Unit,
    mediaController: MediaController,
    saveRepeatMode: (Int) -> Unit,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    play: () -> Unit
) {

    var openDialog by remember {
        mutableStateOf(false)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        Icon(
            when (trackUiState.trackRepeatMode) {
                RepeatMode.REPEAT_ONCE -> Icons.Rounded.Repeat
                RepeatMode.REPEAT_TWICE -> Icons.Rounded.RepeatOne
                RepeatMode.REPEAT_CYCLED -> Icons.Rounded.RepeatOn
            },
            contentDescription = "RepeatMode",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .alpha(0.7f)
                .bounceClick {
                    changeTrackUiState(trackUiState
                        .copy(
                            trackRepeatMode = RepeatMode
                                .entries[
                                    ((trackUiState.trackRepeatMode.ordinal + 1) % 3)
                                        .also { rep ->
                                            saveRepeatMode(rep)
                                        }
                                ]
                        )
                    )
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
                        skipTrack(SkipTrackAction.PREVIOUS)
                            .also { MediaCommands.isTrackRepeated.value = false }
                    }
            )

            Icon(
                painter = painterResource(id =
                if(trackUiState.isPlaying)
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
                        skipTrack(SkipTrackAction.NEXT)
                            .also { MediaCommands.isTrackRepeated.value = false }
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
                    openDialog = !openDialog
                },
            tint = Color.White,
        )


        if (openDialog)
            DialogFavourite(
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                mediaController = mediaController,
                favouriteTracks = selectListTracks(ListSelector.FAVOURITE_LIST),
                upsertTrack = upsertTrack,
                changeOpenDialog = {isIt ->
                    openDialog = isIt
                }
            )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DialogFavourite(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    mediaController: MediaController,
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
                    color = MaterialTheme.colorScheme.surface,
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
            }

            itemsIndexed(
                _favouriteTracks,
                key = { _, track ->
                    track.id
                }
            ) { listIndex, track ->
                Item(
                    track = track,
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

@Composable
fun FunctionalBlock(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    skipTrack: (SkipTrackAction) -> Unit,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    saveRepeatMode: (Int) -> Unit,
    durationGet: () -> Float,
    mediaController: MediaController,
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
            mediaController = mediaController
        )

        ControlButtons(
            trackUiState = trackUiState,
            changeTrackUiState = changeTrackUiState,
            upsertTrack = upsertTrack,
            skipTrack = skipTrack,
            saveRepeatMode = saveRepeatMode,
            play = play,
            mediaController = mediaController,
            selectListTracks = selectListTracks
        )
    }
}