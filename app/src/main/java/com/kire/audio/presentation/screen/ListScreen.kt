package com.kire.audio.presentation.screen

import java.util.concurrent.TimeUnit

import androidx.activity.compose.BackHandler

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import coil.compose.AsyncImage

import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.viewmodel.TrackViewModel
import com.kire.audio.R
import com.kire.audio.presentation.functional.events.SortOptionEvent
import com.kire.audio.presentation.util.SortType
import com.kire.audio.device.audio.functional.SkipTrackAction

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource

import androidx.media3.session.MediaController
import com.kire.audio.device.audio.functional.MediaCommands
import com.kire.audio.device.audio.functional.PlayerState
import com.kire.audio.device.audio.functional.RepeatMode
import com.kire.audio.device.audio.performPlayMedia
import com.kire.audio.device.audio.rememberManagedMediaController
import com.kire.audio.device.audio.functional.state
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.screen.functional.GetPermissions
import com.kire.audio.screen.functional.ListSelector
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.SearchUiState

@Composable
fun Item(
    trackToShow: Track,
    listINDEX: Int,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    mediaController: MediaController,
    upsertTrack: suspend (Track) -> Unit,
    selector: ListSelector = ListSelector.MAIN_LIST,
    imageSize: Dp = 56.dp,
    textTitleSize: TextUnit = 17.sp,
    textArtistSize: TextUnit = 13.sp,
    startPadding: Dp = 16.dp,
    heartIconSize: Dp = 24.dp,
    modifier: Modifier
){

    val coroutineScope = rememberCoroutineScope()

    var _track by remember {
        mutableStateOf(trackToShow)
    }

    val currentTrackPlaying = trackUiState.currentTrackPlaying.also {
        it?.let {
            if (it.id == _track.id)
                _track = it
        }
    }

    _track.apply {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .bounceClick {

                    changeTrackUiState(
                        trackUiState
                            .copy(
                                isPlaying = if (_track.path == currentTrackPlaying?.path) !trackUiState.isPlaying else true,
                                currentTrackPlaying = _track,
                                currentListSelector = selector,
                                currentTrackPlayingIndex = listINDEX,
                                currentTrackPlayingURI = path,
                                isPlayerBottomCardShown = true
                            )
                    )

                    mediaController.apply {

                        if (trackUiState.isPlaying && trackUiState.currentTrackPlayingURI == path)
                            pause()
                        else if (!trackUiState.isPlaying && trackUiState.currentTrackPlayingURI == path) {
                            prepare()
                            play()

                        } else
                            performPlayMedia(_track)
                    }
                }
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if(selector != ListSelector.FAVOURITE_LIST) Arrangement.Start else Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    AsyncImage(
                        model = imageUri,
                        placeholder = painterResource(R.drawable.ic_launcher_foreground),
                        contentScale = ContentScale.Crop,
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
                                        if (selector == ListSelector.SEARCH_LIST)
                                            if (length > 11) take(11) + "..." else this
                                        else if (selector == ListSelector.FAVOURITE_LIST)
                                            if (length > 13) take(13) + "..." else this
                                        else
                                            if (length > 23) take(23) + "..." else this
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
                                            if (selector == ListSelector.SEARCH_LIST)
                                                if (length > 11) take(11) + "..." else this
                                            else if (selector == ListSelector.FAVOURITE_LIST)
                                                if (length > 13) take(13) + "..." else this
                                            else
                                                if (length > 23) take(23) + "..." else this
                                        }
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(start = startPadding)
                    )
                }

                if (selector == ListSelector.FAVOURITE_LIST) {
                    Icon(
                        Icons.Rounded.Favorite,
                        contentDescription = "Favourite",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(heartIconSize)
                            .bounceClick {
                                coroutineScope.launch(Dispatchers.IO) {
                                    upsertTrack(
                                        _track
                                            .copy(isFavourite = !_track.isFavourite)
                                            .also { thisTrack ->
                                                currentTrackPlaying?.let {
                                                    if (it.title == title && it.artist == artist && it.path == path)
                                                        changeTrackUiState(
                                                            trackUiState.copy(
                                                                currentTrackPlaying = thisTrack
                                                            )
                                                        )
                                                }
                                            }
                                    )
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun OnScrollListener(
    listState: LazyListState,
    currentTrackPlaying: Track?,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit
){
    var previousVisibleItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    listState.apply {
        LaunchedEffect(firstVisibleItemIndex){
            if (firstVisibleItemIndex - previousVisibleItemIndex > 1){
                changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = false))
                previousVisibleItemIndex = firstVisibleItemIndex
            }
            currentTrackPlaying?.let {
                if (firstVisibleItemIndex - previousVisibleItemIndex < -1){
                    changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = true))
                    previousVisibleItemIndex = firstVisibleItemIndex
                }
            }
        }
    }
}

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

    mediaController?.let {
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            UpperBlock()
                            UserActionBar(
                                trackUiState = trackUiState,
                                changeTrackUiState = viewModel::changeTrackUiState,
                                searchUiState = searchUiState,
                                changeSearchUiState = viewModel::changeSearchUiState,
                                mediaController = mediaController!!,
                                upsertTrack = viewModel::upsertTrack,
                                loadTracksToDatabase = viewModel::loadTracksToDatabase,
                                deleteTracksFromDatabase = viewModel::deleteTracksFromDatabase,
                                sortType = viewModel.sortType,
                                onEvent = viewModel::onEvent,
                                selectListTracks = viewModel::selectListOfTracks,
                                saveSortOption = viewModel::saveSortOption
                            ) { tracks }
                        }
                    }

                    itemsIndexed(
                        tracks,
                        key = { _, track ->
                            track.id
                        }
                    ) { listIndex, track ->

                        Item(
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
                .background(MaterialTheme.colorScheme.onBackground),
            contentAlignment = Alignment.Center

        ) {

            Icon(
                painter = painterResource(id = R.drawable.scroll_to_top_button),
                "Scroll To Top",
                tint = MaterialTheme.colorScheme.outline,
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
    searchUiState: SearchUiState,
    changeSearchUiState: (SearchUiState) -> Unit,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    mediaController: MediaController,
    upsertTrack: suspend (Track) -> Unit,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    loadTracksToDatabase: suspend () -> Unit,
    deleteTracksFromDatabase: suspend (List<Track>) -> Unit,
    sortType: StateFlow<SortType>,
    onEvent: (SortOptionEvent) -> Unit,
    saveSortOption: (SortType) -> Unit,
    tracks: () -> List<Track>
){
    val coroutineScope = rememberCoroutineScope()

    val updateTrackDatabase: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            launch {
                loadTracksToDatabase()
            }
            launch {
                deleteTracksFromDatabase(tracks())
            }
        }
    }

    LaunchedEffect(Unit){
        updateTrackDatabase()
    }

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
                        .background(color = MaterialTheme.colorScheme.onBackground),
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
                                .bounceClick {
                                    updateTrackDatabase()
                                             },
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            SearchBar(
                searchUiState = searchUiState,
                changeSearchUiState = changeSearchUiState,
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                upsertTrack = upsertTrack,
                selectListTracks = selectListTracks,
                mediaController = mediaController,
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
    searchUiState: SearchUiState,
    changeSearchUiState: (SearchUiState) -> Unit,
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    mediaController: MediaController,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
){

    val searchResult by selectListTracks(ListSelector.SEARCH_LIST).collectAsStateWithLifecycle()

    DockedSearchBar(
        query = searchUiState.searchText,
        onQueryChange = {
            changeSearchUiState(searchUiState.copy(searchText = it))
        },
        onSearch = {
            changeSearchUiState(searchUiState.copy(active = false))
        },
        active = searchUiState.active && !searchUiState.isExpanded,
        onActiveChange = {
            changeSearchUiState(searchUiState.copy(active = it))
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                disabledTextColor = MaterialTheme.colorScheme.onPrimary
            )
        ),
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
            if (searchUiState.active) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .size(24.dp)
                        .bounceClick {
                            if (searchUiState.searchText.isNotEmpty())
                                changeSearchUiState(searchUiState.copy(searchText = ""))
                            else
                                changeSearchUiState(searchUiState.copy(active = false))

                            changeTrackUiState(trackUiState.copy(currentListSelector = ListSelector.MAIN_LIST))
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
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                itemsIndexed(
                    searchResult,
                    key = { _, track ->
                        track.id
                    }
                ) { listIndex, track ->
                    Item(
                        trackToShow = track,
                        trackUiState = trackUiState,
                        changeTrackUiState = changeTrackUiState,
                        selector = ListSelector.SEARCH_LIST,
                        mediaController = mediaController,
                        listINDEX = listIndex,
                        upsertTrack = upsertTrack,
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
    mediaController: MediaController,
    minutesCur: (Long) -> Unit,
    minutesAll: (Long) -> Unit,
    secondsCur: (Long) -> Unit,
    secondsAll: (Long) -> Unit,
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

    LaunchedEffect(key1 = sliderPosition){
        minutesCur(TimeUnit.MILLISECONDS.toMinutes(mediaController.currentPosition))
        secondsCur(TimeUnit.MILLISECONDS.toSeconds(mediaController.currentPosition) % 60)
        minutesAll(TimeUnit.MILLISECONDS.toMinutes(mediaController.duration))
        secondsAll(TimeUnit.MILLISECONDS.toSeconds(mediaController.duration) % 60)
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPlayer(
    _trackUiState: StateFlow<TrackUiState>,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    saveRepeatMode: (Int) -> Unit,
    selectListOfTracks: (ListSelector) -> StateFlow<List<Track>>,
    mediaController: MediaController,
) {
    val coroutineScope = rememberCoroutineScope()

    val trackUiState by _trackUiState.collectAsStateWithLifecycle()

    var currentTrackList = selectListOfTracks(trackUiState.currentListSelector).collectAsStateWithLifecycle().value

    if (currentTrackList.isEmpty() && (trackUiState.currentListSelector != ListSelector.MAIN_LIST)) {
        changeTrackUiState(trackUiState.copy(currentListSelector = ListSelector.MAIN_LIST))
        currentTrackList = selectListOfTracks(ListSelector.MAIN_LIST).collectAsStateWithLifecycle().value
    }

    var duration: Float by remember { mutableFloatStateOf(0f) }

    trackUiState.currentTrackPlaying?.let {
        duration = it.duration.toFloat()
    } ?: 0f


    var minutesCur by remember { mutableLongStateOf(TimeUnit.MILLISECONDS.toMinutes(mediaController.currentPosition)) }
    var secondsCur by  remember { mutableLongStateOf((TimeUnit.MILLISECONDS.toSeconds(mediaController.currentPosition) % 60)) }
    var minutesAll by remember { mutableLongStateOf(TimeUnit.MILLISECONDS.toMinutes(mediaController.duration)) }
    var secondsAll by remember { mutableLongStateOf((TimeUnit.MILLISECONDS.toSeconds(mediaController.duration) % 60)) }

    Updater(
        mediaController = mediaController,
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

    val skipTrack: (SkipTrackAction) -> Unit = { skipTrackAction ->

        val newINDEX = trackUiState.currentTrackPlayingIndex?.let { index ->
            skipTrackAction.action(index, currentTrackList.size)
        } ?: 0

        duration = currentTrackList[newINDEX].duration.toFloat()

        changeTrackUiState(
            trackUiState.copy(
                currentTrackPlaying = currentTrackList[newINDEX],
                currentTrackPlayingIndex = newINDEX,
                currentTrackPlayingURI = currentTrackList[newINDEX].path
            )
        )

        mediaController.apply {
            if (mediaController.isPlaying)
                stop()

            performPlayMedia(currentTrackList[newINDEX])
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
                when (trackUiState.trackRepeatMode) {
                    RepeatMode.REPEAT_ONCE -> skipTrack(SkipTrackAction.NEXT)
                        .also { MediaCommands.isTrackRepeated.value = false }

                    RepeatMode.REPEAT_TWICE -> {
                        if (!MediaCommands.isTrackRepeated.value)
                            skipTrack(SkipTrackAction.REPEAT)
                                .also { MediaCommands.isTrackRepeated.value = true }
                        else
                            skipTrack(SkipTrackAction.NEXT)
                                .also { MediaCommands.isTrackRepeated.value = false }
                    }
                    RepeatMode.REPEAT_CYCLED -> skipTrack(SkipTrackAction.REPEAT)
                        .also { MediaCommands.isTrackRepeated.value = false }
                }
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

    AnimatedVisibility(
        visible = trackUiState.isPlayerBottomCardShown,
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
                    detectDragGestures { _, dragAmount ->
                        val (x, y) = dragAmount

                        if (y > 10 && x < 60 && x > -60) {
                            coroutineScope.launch(Dispatchers.IO) {
                                changeTrackUiState(trackUiState.copy(isPlayerBottomCardShown = false))
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            changeTrackUiState(trackUiState.copy(isPlayerScreenExpanded = true))
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
                    visible = !trackUiState.isPlayerScreenExpanded,
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
                            targetState = trackUiState.currentTrackPlaying?.imageUri,
                            label = "BottomSheet Partial Expanded"
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = it,
                                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "Track Image",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .aspectRatio(ratio = 1f)
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
                                            append(trackUiState.currentTrackPlaying?.title)
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                color = MaterialTheme.colorScheme.onSecondary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.W300
                                            )
                                        ) {
                                            append("\n" + trackUiState.currentTrackPlaying?.artist)
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
                                        skipTrack(SkipTrackAction.PREVIOUS)
                                            .also { MediaCommands.isTrackRepeated.value = false }
                                    }
                            )

                            Icon(
                                painter =
                                if (trackUiState.isPlaying)
                                    painterResource(id = R.drawable.pause_button_bottom_sheet)
                                else
                                    painterResource(id = R.drawable.play_button_bottom_sheet),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .size(if (trackUiState.isPlaying) 22.dp else 20.dp)
                                    .bounceClick {
                                        if (!trackUiState.isPlaying) {
                                            mediaController.play()
                                            changeTrackUiState(trackUiState.copy(isPlaying = true))
                                        } else {
                                            mediaController.pause()
                                            changeTrackUiState(trackUiState.copy(isPlaying = false))
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
                                        skipTrack(SkipTrackAction.NEXT)
                                            .also { MediaCommands.isTrackRepeated.value = false }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }


        AnimatedVisibility(
            visible = trackUiState.isPlayerScreenExpanded,
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
                                    changeTrackUiState(trackUiState.copy(isPlayerScreenExpanded = false))
                                }
                            }
                        }
                    }
            ) {

                BackHandler {
                    coroutineScope.launch(Dispatchers.IO) {
                        changeTrackUiState(trackUiState.copy(isPlayerScreenExpanded = false))
                    }
                    return@BackHandler
                }


                Screen(
                    trackUiState = trackUiState,
                    changeTrackUiState = changeTrackUiState,
                    upsertTrack = upsertTrack,
                    skipTrack = skipTrack,
                    saveRepeatMode = saveRepeatMode,
                    selectListTracks = selectListOfTracks,
                    mediaController = mediaController,
                    durationGet = {
                        duration
                    },
                    play = {
                        mediaController.apply {
                            if (!trackUiState.isPlaying) {
                                play()
                                changeTrackUiState(trackUiState.copy(isPlaying = true))
                            }
                            else {
                                pause()
                                changeTrackUiState(trackUiState.copy(isPlaying = false))
                            }
                        }
                    }
                )
            }
        }
    }
}