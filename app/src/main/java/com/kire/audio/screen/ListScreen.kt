package com.kire.audio.screen

import java.util.concurrent.TimeUnit

import android.content.Context

import android.net.Uri

import androidx.activity.compose.BackHandler

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy

import com.kire.audio.functional.getContext
import com.kire.audio.models.Track
import com.kire.audio.viewmodels.TrackListViewModel
import com.kire.audio.R
import com.kire.audio.events.SortOptionEvent
import com.kire.audio.events.SortType
import com.kire.audio.functional.GetPermissions
import com.kire.audio.mediaHandling.SkipTrackAction
import com.kire.audio.functional.ListSelector
import com.kire.audio.functional.bounceClick

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay

import kotlin.time.Duration.Companion.seconds

import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem


@Composable
fun Item(
    track: Track,
    exoPlayer: ExoPlayer,
    selectList: ListSelector,
    currentUri: String,
    currentTrackPlaying: Track?,
    updateIsLoved: (Track) -> Unit,
    trackINDEX: Int,
    sentInfoToBottomSheet: (Track, ListSelector, Int, String) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
    imageSize: Dp = 56.dp,
    textTitleSize: TextUnit = 17.sp,
    textArtistSize: TextUnit = 13.sp,
    startPadding: Dp = 16.dp,
    modifier: Modifier
){

    val title by remember { mutableStateOf(track.title) }
    val artist by remember { mutableStateOf(track.artist) }
    val uri by remember { mutableStateOf(track.path) }
    val imageUri by remember { mutableStateOf(track.imageUri) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick {
                sentInfoToBottomSheet(
                    track,
                    selectList,
                    trackINDEX,
                    uri
                )

                exoPlayer.apply {

                    val isPlaying = TrackListViewModel.reason.value

                    if (isPlaying && currentUri == uri) {
                        pause()
                        TrackListViewModel.reason.value = false
                    } else if (!isPlaying && currentUri == uri) {
                        prepare()
                        play()
                    } else if (!isPlaying) {

                        val newMediaItem = MediaItem.fromUri(Uri.parse(uri))

                        setMediaItem(newMediaItem)
                        prepare()
                        play()
                    } else {
                        pause()

                        val newMediaItem = MediaItem.fromUri(Uri.parse(uri))

                        setMediaItem(newMediaItem)
                        prepare()
                        play()
                    }
                }
            }
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if(selectList != ListSelector.FAVOURITE_LIST) Arrangement.Start else Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .allowHardware(true)
                        .diskCacheKey(imageUri.toString())
                        .memoryCacheKey(imageUri.toString())
                        .build(),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Track Image",
                    modifier = Modifier
                        .height(imageSize)
                        .width(imageSize)
                        .clip(RoundedCornerShape(12.dp))
                )

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = textTitleSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        ) {
                            append(
                                with(title) {
                                    if (selectList == ListSelector.SEARCH_LIST)
                                        if (length > 12) take(12) + "..." else this

                                    else if (selectList == ListSelector.FAVOURITE_LIST)
                                        if (length > 16) take(16) + "..." else this

                                    else {
                                        if (length > 27) take(27) + "..." else this
                                    }
                                }
                            )
                        }
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = textArtistSize,
                                fontWeight = FontWeight.W300,
                                fontFamily = FontFamily.SansSerif
                            )
                        ) {
                            append("\n" +
                                    with(artist) {
                                        if (selectList == ListSelector.SEARCH_LIST)
                                            if (length > 12) take(12) + "..." else this

                                        else if (selectList == ListSelector.FAVOURITE_LIST)
                                            if (length > 16) take(16) + "..." else this

                                        else {
                                            if (length > 27) take(27) + "..." else this
                                        }
                                    }
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(start = startPadding)
                )
            }

            if (selectList == ListSelector.FAVOURITE_LIST) {
                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = "Favourite",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(26.dp)
                        .bounceClick {
                            updateIsLoved(
                                track
                                    .copy(isFavourite = !track.isFavourite)
                                    .also { track ->
                                        currentTrackPlaying?.let {
                                            if (it.title == title && it.artist == artist && it.path == uri)
                                                sentInfoToBottomSheetOneParameter(track)
                                        }
                                    }
                            )
                        }
                )
            }
        }
    }
}

@Composable
fun OnScrollListener(
    listState: LazyListState,
    currentTrackPlaying: Track?,
    changeIsShown: (Boolean) -> Unit
){
    var previousVisibleItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    listState.apply {
        LaunchedEffect(firstVisibleItemIndex){
            if (firstVisibleItemIndex - previousVisibleItemIndex > 1){
                changeIsShown(false)
                previousVisibleItemIndex = firstVisibleItemIndex
            }
            currentTrackPlaying?.let {
                if (firstVisibleItemIndex - previousVisibleItemIndex < -1){
                    changeIsShown(true)
                    previousVisibleItemIndex = firstVisibleItemIndex
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListScreen(
    viewModel: TrackListViewModel
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currentTrackPlaying by viewModel.currentTrackPlaying.collectAsStateWithLifecycle()
    val tracks by viewModel.selectListTracks(listSelect = ListSelector.MAIN_LIST).collectAsStateWithLifecycle()
    val trackINDEX by viewModel.bottomSheetTrackINDEX.collectAsStateWithLifecycle()

    val currentTrackPlayingURI by viewModel.currentTrackPlayingURI.collectAsStateWithLifecycle()


    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    OnScrollListener(
        listState = listState,
        currentTrackPlaying = currentTrackPlaying,
        changeIsShown = viewModel::changeIsShown
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        GetPermissions(
            lifecycleOwner = LocalLifecycleOwner.current,
            loadTracksToDatabase = viewModel::loadTracksToDatabase
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = {

                item {
                    UpperBlock()
                }

                item {
                    UserActionBar(
                        currentUri = currentTrackPlayingURI,
                        exoPlayer = viewModel.exoPlayer,
                        searchText = viewModel.searchText,
                        active = viewModel.active,
                        currentTrackPlaying = currentTrackPlaying,
                        updateIsLoved = viewModel::updateIsLoved,
                        sentInfoToBottomSheetOneParameter = viewModel::sentInfoToBottomSheet,
                        changeSelectList = viewModel::changeSelectList,
                        onSearchTextChange = viewModel::onSearchTextChange,
                        onActiveChange = viewModel::onActiveChange,
                        loadTracksToDatabase = viewModel::loadTracksToDatabase,
                        deleteTracksFromDatabase = viewModel::deleteTracksFromDatabase,
                        sortType = viewModel.sortType,
                        onEvent = viewModel::onEvent,
                        isExpanded = viewModel.isExpanded,
                        selectListTracks = viewModel::selectListTracks,
                        saveSortOption = viewModel::saveSortOption,
                        sentInfoToBottomSheet = viewModel::sentInfoToBottomSheet
                    ) { tracks }
                }

                if (tracks.isNotEmpty()) {
                    itemsIndexed(
                        tracks,
                        key = { _, track ->
                            track.id
                        }
                    ) { index, track ->
                        Item(
                            track = track,
                            currentUri = currentTrackPlayingURI,
                            exoPlayer = viewModel.exoPlayer,
                            selectList = ListSelector.MAIN_LIST,
                            currentTrackPlaying = currentTrackPlaying,
                            updateIsLoved = viewModel::updateIsLoved,
                            sentInfoToBottomSheetOneParameter = viewModel::sentInfoToBottomSheet,
                            trackINDEX = index,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )),
                            sentInfoToBottomSheet = viewModel::sentInfoToBottomSheet
                        )
                    }
                }
            }
        )

        BottomPlayer(
            currentTrackPlaying = viewModel.currentTrackPlaying,
            trackINDEX = trackINDEX,
            selectList = viewModel.selectList,
            selectListTracks = viewModel::selectListTracks,
            repeatMode = viewModel.repeatMode,
            changeRepeatMode = viewModel::changeRepeatMode,
            currentTrackPlayingURI = currentTrackPlayingURI,
            isExpanded = viewModel.isExpanded,
            saveRepeatMode = viewModel::saveRepeatMode,
            changeIsExpanded = viewModel::changeIsExpanded,
            isShown = viewModel.isShown,
            updateIsLoved = viewModel::updateIsLoved,
            sentInfoToBottomSheetOneParameter = viewModel::sentInfoToBottomSheet,
            changeIsShown = viewModel::changeIsShown,
            exoPlayer = viewModel.exoPlayer,
            changeSelectList = viewModel::changeSelectList,
            sentInfoToBottomSheet = viewModel::sentInfoToBottomSheet,
        )


        val itemSize = 70.dp
        val density = LocalDensity.current
        val itemSizePx = with(density) { itemSize.toPx() }
        val itemsScrollCount = tracks.size

        val isTrackScreenExpanded by viewModel.isExpanded.collectAsStateWithLifecycle()

        AnimatedVisibility(
            visible = showButton && !isTrackScreenExpanded,
            enter = slideInHorizontally(initialOffsetX = { 82 }) + fadeIn(
                animationSpec = tween(
                    durationMillis = 250,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutHorizontally(targetOffsetX = { 82 }) + fadeOut(
                animationSpec = tween(
                    durationMillis = if (isTrackScreenExpanded) 60 else 300
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
    }
}

@Composable
fun ScrollToTopButton(
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .padding(bottom = 128.dp, end = 20.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .bounceClick { onClick() }
                .wrapContentSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center

        ) {

            Icon(
                painter = painterResource(id = R.drawable.scroll_to_top_button),
                "Scroll To Top",
                tint = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier
                    .padding(6.dp)
                    .size(54.dp)
            )
        }
    }
}

@Composable
fun UpperBlock(){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(110.dp),
        contentAlignment = Alignment.BottomStart
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.listscreen_header),
                fontSize = 52.sp,
                fontWeight = FontWeight.W700,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun UserActionBar(
    currentUri: String,
    exoPlayer: ExoPlayer,
    searchText: StateFlow<String>,
    currentTrackPlaying: Track?,
    updateIsLoved: (Track) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
    active: StateFlow<Boolean>,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    onSearchTextChange: (String) -> Unit,
    changeSelectList: (ListSelector) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    loadTracksToDatabase: (Context) -> Unit,
    deleteTracksFromDatabase: (List<Track>) -> Unit,
    sortType: StateFlow<SortType>,
    isExpanded: StateFlow<Boolean>,
    onEvent: (SortOptionEvent) -> Unit,
    saveSortOption: (SortType) -> Unit,
    sentInfoToBottomSheet: (Track, ListSelector, Int, String) -> Unit,
    tracks: () -> List<Track>
){
    val coroutineScope = rememberCoroutineScope()
    val context = getContext()

    val updateTrackDatabase: ()->Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            launch {
                loadTracksToDatabase(context)
            }
            launch {
                deleteTracksFromDatabase(tracks())
            }
        }
    }

    LaunchedEffect(Unit){ updateTrackDatabase() }

    Box(
        modifier = Modifier
            .padding(bottom = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier
                .align(Alignment.Top)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .width(120.dp)
                        .height(56.dp)
                        .background(color = Color(0x88FF7F50)),
                    contentAlignment = Alignment.Center

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.65f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        DropDownMenu(sortType, saveSortOption, onEvent)

                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier
                                .bounceClick { updateTrackDatabase() },
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            SearchBar(
                currentUri = currentUri,
                searchText = searchText,
                active = active,
                currentTrackPlaying = currentTrackPlaying,
                updateIsLoved = updateIsLoved,
                sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter,
                selectListTracks = selectListTracks,
                exoPlayer = exoPlayer,
                isExpanded = isExpanded,
                onSearchTextChange = onSearchTextChange,
                onActiveChange = onActiveChange,
                changeSelectList = changeSelectList,
                sentInfoToBottomSheet = sentInfoToBottomSheet
            )
        }
    }
}





@Composable
fun DropDownMenu(
    sortType: StateFlow<SortType>,
    saveSortOption: (SortType) -> Unit,
    onEvent: (SortOptionEvent) -> Unit
){
    val sortOption by sortType.collectAsStateWithLifecycle()

    var isSortOptionAsc by remember { mutableIntStateOf(1) }

    var expanded by remember { mutableStateOf(false) }

    val sortOptionFunc: (String, SortType, SortType)->Unit = { text, sortTypeASC, sortTypeDESC ->

        isSortOptionAsc =
            if (!sortOption.toString().take(text.length).equals(text, true)) {
                isSortOptionAsc
            } else (isSortOptionAsc + 1) % 2

        onEvent(
            if (isSortOptionAsc == 0) {
                SortOptionEvent.ListTrackSortOption(
                    sortTypeASC.also {
                        saveSortOption(it)
                    }
                )

            } else SortOptionEvent.ListTrackSortOption(
                sortTypeDESC.also {
                    saveSortOption(it)
                }
            )
        )
    }

    Box(
        Modifier
            .bounceClick {
                expanded = true
            }
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.Sort,
            contentDescription = null,
            modifier = Modifier
                .size(28.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = (-28).dp, y = 23.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
            ) {

                CustomDropDownMenu(
                    sortOption = sortOption,
                    sortTypeASC = SortType.DATA_ASC_ORDER,
                    sortTypeDESC = SortType.DATA_DESC_ORDER,
                    text = stringResource(id = R.string.dropdown_date),
                    sortOptionFunc
                )
                CustomDropDownMenu(
                    sortOption = sortOption,
                    sortTypeASC = SortType.TITLE_ASC_ORDER,
                    sortTypeDESC = SortType.TITLE_DESC_ORDER,
                    text = stringResource(id = R.string.dropdown_title),
                    sortOptionFunc
                )
                CustomDropDownMenu(
                    sortOption = sortOption,
                    sortTypeASC = SortType.ARTIST_ASC_ORDER,
                    sortTypeDESC = SortType.ARTIST_DESC_ORDER,
                    text = stringResource(id = R.string.dropdown_artist),
                    sortOptionFunc
                )

                CustomDropDownMenu(
                    sortOption = sortOption,
                    sortTypeASC = SortType.DURATION_ASC_ORDER,
                    sortTypeDESC = SortType.DURATION_DESC_ORDER,
                    text = stringResource(id = R.string.dropdown_duration),
                    sortOptionFunc
                )
            }
        }
    }
}

@Composable
fun CustomDropDownMenu(
    sortOption: SortType,
    sortTypeASC: SortType,
    sortTypeDESC: SortType,
    text: String,
    sortOptionFunc: (String, SortType, SortType)->Unit
){
    DropdownMenuItem(
        text = {
            Text(
                text,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = if (sortOption == sortTypeASC || sortOption == sortTypeDESC)
                    Color.Red
                else
                    MaterialTheme.colorScheme.onPrimary
            )
        },
        onClick = {
            sortOptionFunc(
                if (text.equals("date", true)) "data" else text,
                sortTypeASC,
                sortTypeDESC
            )
        },
        trailingIcon = {
            DropdownMenuItemTrailingIcon(
                sortOption = sortOption,
                sortTypeASC = sortTypeASC,
                sortTypeDESC = sortTypeDESC
            )
        }
    )
}

@Composable
fun DropdownMenuItemTrailingIcon(
    sortOption: SortType,
    sortTypeASC: SortType,
    sortTypeDESC: SortType
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            tint = if (sortOption == sortTypeASC) Color.Red else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = if (sortOption == sortTypeDESC) Color.Red else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchBar(
    currentUri: String,
    searchText: StateFlow<String>,
    currentTrackPlaying: Track?,
    updateIsLoved: (Track) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
    active: StateFlow<Boolean>,
    exoPlayer: ExoPlayer,
    isExpanded: StateFlow<Boolean>,
    changeSelectList: (ListSelector) -> Unit,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    onSearchTextChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    sentInfoToBottomSheet: (Track, ListSelector, Int, String) -> Unit
){

    val searchText by searchText.collectAsStateWithLifecycle()
    val active by active.collectAsStateWithLifecycle()
    val searchResult by selectListTracks(ListSelector.SEARCH_LIST).collectAsStateWithLifecycle()
    val isExpanded by isExpanded.collectAsStateWithLifecycle()

    DockedSearchBar(
        query = searchText,
        onQueryChange = {
            onSearchTextChange(it)
        },
        onSearch = {
            onActiveChange(false)
        },
        active = active && !isExpanded,
        onActiveChange = {
            onActiveChange(it)
        },
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .size(24.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        },
        trailingIcon = {
            if (active) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .size(24.dp)
                        .bounceClick {
                            if (searchText.isNotEmpty())
                                onSearchTextChange("")
                            else
                                onActiveChange(false)

                            changeSelectList(ListSelector.MAIN_LIST)
                        }
                )
            }
        },
        placeholder = {
            Text(
                stringResource(id = R.string.listscreen_search),
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        modifier = Modifier
            .padding(start = 18.dp)

    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            contentPadding = PaddingValues(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                itemsIndexed(
                    searchResult,
                    key = { _, track ->
                        track.id
                    }
                ) { index, track ->
                    Item(
                        track = track,
                        selectList = ListSelector.SEARCH_LIST,
                        exoPlayer = exoPlayer,
                        currentUri = currentUri,
                        trackINDEX = index,
                        currentTrackPlaying = currentTrackPlaying,
                        updateIsLoved = updateIsLoved,
                        sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter,
                        sentInfoToBottomSheet = sentInfoToBottomSheet,
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )),
                        imageSize = 44.dp,
                        textTitleSize = 15.sp,
                        textArtistSize = 11.sp,
                        startPadding = 12.dp
                    )
                }
            }
        )
    }
}


@Composable
fun Updater(
    exoPlayer: ExoPlayer,
    minutesCur: (Long) -> Unit,
    minutesAll: (Long) -> Unit,
    secondsCur: (Long) -> Unit,
    secondsAll: (Long) -> Unit,
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

    LaunchedEffect(key1 = sliderPosition){
        minutesCur(TimeUnit.MILLISECONDS.toMinutes(exoPlayer.currentPosition))
        secondsCur(TimeUnit.MILLISECONDS.toSeconds(exoPlayer.currentPosition) % 60)
        minutesAll(TimeUnit.MILLISECONDS.toMinutes(exoPlayer.duration))
        secondsAll(TimeUnit.MILLISECONDS.toSeconds(exoPlayer.duration) % 60)
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPlayer(
    currentTrackPlaying: StateFlow<Track?>,
    selectList: StateFlow<ListSelector>,
    trackINDEX: Int,
    updateIsLoved: (Track) -> Unit,
    sentInfoToBottomSheetOneParameter: (Track) -> Unit,
    repeatMode: StateFlow<Int>,
    changeRepeatMode: (Int) -> Unit,
    saveRepeatMode: (Int) -> Unit,
    currentTrackPlayingURI: String,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    isExpanded: StateFlow<Boolean>,
    exoPlayer: ExoPlayer,
    changeIsExpanded: (Boolean) -> Unit,
    changeSelectList: (ListSelector) -> Unit,
    isShown: StateFlow<Boolean>,
    changeIsShown: (Boolean) -> Unit,
    sentInfoToBottomSheet: (Track, ListSelector, Int, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val track by currentTrackPlaying.collectAsStateWithLifecycle()

    val selectList by selectList.collectAsStateWithLifecycle()
    var currentTrackList = selectListTracks(selectList).collectAsStateWithLifecycle().value

    if (selectList == ListSelector.FAVOURITE_LIST && currentTrackList.isEmpty()) {
        changeSelectList(ListSelector.MAIN_LIST)
        currentTrackList = selectListTracks(ListSelector.MAIN_LIST).collectAsStateWithLifecycle().value
    }

    val isPlaying by TrackListViewModel.reason.collectAsStateWithLifecycle()
    val currentTrackPlaying by currentTrackPlaying.collectAsStateWithLifecycle()

    val isShown by isShown.collectAsStateWithLifecycle()

    val title = track?.title
    val artist = track?.artist
    val imageUri = track?.imageUri

    val _repeatMode by repeatMode.collectAsStateWithLifecycle()

    val isExpanded by isExpanded.collectAsStateWithLifecycle()

    var duration: Float by remember { mutableStateOf(0f) }

    track?.let {
        duration = it.duration.toFloat()
    } ?: 0f


    var minutesCur by remember { mutableStateOf(TimeUnit.MILLISECONDS.toMinutes(exoPlayer.currentPosition)) }
    var secondsCur by  remember{ mutableStateOf((TimeUnit.MILLISECONDS.toSeconds(exoPlayer.currentPosition) % 60)) }
    var minutesAll by remember { mutableStateOf(TimeUnit.MILLISECONDS.toMinutes(exoPlayer.duration)) }
    var secondsAll by remember{ mutableStateOf((TimeUnit.MILLISECONDS.toSeconds(exoPlayer.duration) % 60)) }

    Updater(
        exoPlayer = exoPlayer,
        minutesCur = { minCur ->
            minutesCur = minCur
        },
        minutesAll = { minAll ->
            minutesAll = minAll
        },
        secondsCur = { secCur ->
            secondsCur = secCur
        },
        secondsAll = { secAll ->
            secondsAll = secAll
        }
    )

    val skipTrack: (SkipTrackAction, Boolean, Boolean)->Unit = { skipTrackAction, isPlayerScreenTextBlock, isRepeated ->
        TrackListViewModel.isRepeated.value = isRepeated

        val newINDEX =
            skipTrackAction.action(trackINDEX, currentTrackList.size)

        duration = currentTrackList[newINDEX].duration.toFloat()

        sentInfoToBottomSheet(
            currentTrackList[newINDEX],
            selectList,
            if (!isPlayerScreenTextBlock) newINDEX else if (newINDEX == 0) 0 else trackINDEX,
            currentTrackList[newINDEX].path
        )

        val newMediaItem =
            MediaItem.fromUri(Uri.parse(currentTrackList[newINDEX].path))

        exoPlayer.apply {
            if (exoPlayer.isPlaying)
                stop()

            setMediaItem(newMediaItem)
            prepare()
            play()
        }
    }
    

    LaunchedEffect(minutesCur.toInt() == minutesAll.toInt()
        && secondsCur.toInt() == secondsAll.toInt() &&
        !(minutesAll.toInt() == 0 && secondsAll.toInt() == 0)
    ) {

        if (minutesCur.toInt() == minutesAll.toInt()
            && secondsCur.toInt() == secondsAll.toInt() &&
            !(minutesAll.toInt() == 0 && secondsAll.toInt() == 0)
            )
                when (_repeatMode) {
                    0 ->    skipTrack(SkipTrackAction.NEXT, false, false)

                    1 ->    if (!TrackListViewModel.isRepeated.value)
                                skipTrack(SkipTrackAction.REPEAT, false, true)
                            else
                                skipTrack(SkipTrackAction.NEXT, false, false)

                    2 ->    skipTrack(SkipTrackAction.REPEAT, false, false)
                }
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

    AnimatedVisibility(
        visible = isShown,
        enter = slideInVertically(
            initialOffsetY = { 120 },
            animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing)) + fadeIn(animationSpec = tween(durationMillis = 100)),
        exit = slideOutVertically(
            targetOffsetY = { 120 },
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(durationMillis = 90))
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val (x, y) = dragAmount

                        if (y > 10 && x < 60 && x > -60) {
                            coroutineScope.launch(Dispatchers.IO) {
                                changeIsShown(false)
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            changeIsExpanded(true)
                        }
                    )
                }
                .padding(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onBackground
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {

                this@Card.AnimatedVisibility(
                    visible = !isExpanded,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = 90,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                    exit = fadeOut()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Crossfade(
                            targetState = imageUri,
                            label = "BottomSheet Partial Expanded"
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
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
                                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                                    contentDescription = "Track Image",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .fillMaxHeight()
                                )

                                Text(
                                    buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append(title)
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                color = MaterialTheme.colorScheme.onSecondary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.W300
                                            )
                                        ) {
                                            append("\n" + artist)
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .fillMaxWidth(0.55f)
                                        .basicMarquee(
                                            animationMode = MarqueeAnimationMode.Immediately,
                                            delayMillis = 0
                                        )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.skip_previous_button_bottom_sheet),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .size(17.dp)
                                    .bounceClick {
                                        skipTrack(SkipTrackAction.PREVIOUS, false, false)
                                    }
                            )

                            Icon(
                                painter =
                                if (isPlaying)
                                    painterResource(id = R.drawable.pause_button_bottom_sheet)
                                else
                                    painterResource(id = R.drawable.play_button_bottom_sheet),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .size(if (isPlaying) 22.dp else 20.dp)
                                    .bounceClick {
                                        if (!isPlaying) {
                                            exoPlayer.apply {
                                                play()
                                            }
                                        } else {
                                            exoPlayer.apply {
                                                pause()
                                                TrackListViewModel.reason.value = false
                                            }
                                        }
                                    }
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.skip_next_button_bottom_sheet),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .size(17.dp)
                                    .bounceClick {
                                        skipTrack(SkipTrackAction.NEXT, false, false)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }


        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(250, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(250, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(280, easing = FastOutSlowInEasing))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount

                            if (y > 50 && x < 40 && x > -40) {
                                coroutineScope.launch(Dispatchers.IO) {
                                    changeIsExpanded(false)
                                }
                            }
                        }
                    }
            ) {

                BackHandler {
                    coroutineScope.launch(Dispatchers.IO) {
                        changeIsExpanded(false)
                    }
                    return@BackHandler
                }


                Screen(
                    track = track!!,
                    skipTrack = skipTrack,
                    saveRepeatMode = saveRepeatMode,
                    repeatMode = repeatMode,
                    changeRepeatMode = changeRepeatMode,
                    currentTrackPlaying = currentTrackPlaying,
                    updateIsLoved = updateIsLoved,
                    selectListTracks = selectListTracks,
                    currentTrackPlayingURI = currentTrackPlayingURI,
                    selectList = selectList,
                    sentInfoToBottomSheetOneParameter = sentInfoToBottomSheetOneParameter,
                    exoPlayer = exoPlayer,
                    durationGet = {
                        duration
                    },
                    changeIsExpanded = changeIsExpanded,
                    sentInfoToBottomSheet = sentInfoToBottomSheet,
                    play = {
                        exoPlayer.apply {
                            if (!isPlaying)
                                play()
                            else {
                                pause()
                                TrackListViewModel.reason.value = false
                            }
                        }
                    }
                )
            }
        }
    }
}