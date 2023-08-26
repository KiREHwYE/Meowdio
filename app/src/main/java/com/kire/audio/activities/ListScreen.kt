package com.kire.audio.activities

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kire.audio.functional.getContext
import com.kire.audio.models.Track
import com.kire.audio.viewmodels.TrackListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import coil.request.CachePolicy
import com.kire.audio.R
import com.kire.audio.events.SortOptionEvent
import com.kire.audio.events.SortType
import com.kire.audio.functional.GetPermissions
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun Item(
    title: String,
    artist: String,
    imageUri: Uri?,
    sentInfoToBottomSheet: (String, String, Uri?) -> Unit,
    imageSize: Dp = 56.dp,
    textTitleSize: TextUnit = 17.sp,
    textArtistSize: TextUnit = 13.sp
){
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                sentInfoToBottomSheet(
                    title,
                    artist,
                    imageUri
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

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
//                    .border(
//                        BorderStroke(1.dp, Color.Black),
//                        RoundedCornerShape(12.dp)
//                    )
            )

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontSize = textTitleSize,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    ) {
                        append(title)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.DarkGray,
                            fontSize = textArtistSize,
                            fontWeight = FontWeight.W300,
                            fontFamily = FontFamily.SansSerif
                        )
                    ) {
                        append("\n" + artist)
                    }
                },
                modifier = Modifier
                    .padding(start = 14.dp)
            )
        }
    }
}


@RootNavGraph(start = true)
@Destination
@Composable
fun ListScreen(
    viewModel: TrackListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = com.kire.audio.ui.theme.BackGroundLightGray)
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
                        searchText = viewModel.searchText,
                        active = viewModel.active,
                        searchResult = viewModel.searchResult,
                        onSearchTextChange = viewModel::onSearchTextChange,
                        onActiveChange = viewModel::onActiveChange,
                        loadTracksToDatabase = viewModel::loadTracksToDatabase,
                        deleteTracksFromDatabase = viewModel::deleteTracksFromDatabase,
                        sortType = viewModel.sortType,
                        onEvent = viewModel::onEvent,
                        saveSortOption = viewModel::saveSortOption,
                        sentInfoToBottomSheet = viewModel::sentInfoToBottomSheet
                    ) { tracks }
                }

                if (tracks.isNotEmpty()) {
                    items(
                        tracks,
                        key = { track ->
                            track.id
                        }
                    ) { track ->
                        Item(
                            title = track.title,
                            artist = track.artist,
                            imageUri = track.imageUri,
                            viewModel::sentInfoToBottomSheet
                        )
                    }
                }
            }
        )


        val imageUri = viewModel.bottomSheetTrackImageUri.collectAsStateWithLifecycle().value
        val title = viewModel.bottomSheetTrackTitle.collectAsStateWithLifecycle().value
        val artist = viewModel.bottomSheetTrackArtist.collectAsStateWithLifecycle().value


        BottomPlayer(
            imageUri = imageUri,
            title = title,
            artist = artist,
            isExpanded = viewModel.isExpanded,
            changeIsExpended = viewModel::changeIsExpended,
            isShown = viewModel::isShown
        )


        val itemSize = 65.dp
        val density = LocalDensity.current
        val itemSizePx = with(density) { itemSize.toPx() }
        val itemsScrollCount = tracks.size

        val isTrackScreenExpanded = viewModel.isExpanded.collectAsStateWithLifecycle().value

        AnimatedVisibility(
            visible = showButton && !isTrackScreenExpanded,
            enter = slideInHorizontally(initialOffsetX = { 60 }) + fadeIn(
                animationSpec = tween(
                    durationMillis = if (isTrackScreenExpanded) 0 else 250
                )
            ),
            exit = slideOutHorizontally(targetOffsetX = { 60 }) + fadeOut(
                animationSpec = tween(
                    durationMillis = if (isTrackScreenExpanded) 60 else 300
                )
            ),
        ) {

            ScrollToTopButton(onClick = {
                coroutineScope.launch {
                    listState.animateScrollBy(
                        value = -1 * itemSizePx * itemsScrollCount,
                        animationSpec = tween(durationMillis = 4000)
                    )
                }
            })
        }
    }
}

@Composable
fun ScrollToTopButton(
    onClick: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp, end = 20.dp), Alignment.BottomEnd
    ) {
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .shadow(10.dp, shape = CircleShape)
                .clip(shape = CircleShape)
                .size(60.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black
            )
        ) {
            Icon(
                Icons.Rounded.ArrowDropUp,
                "Scroll To Top",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
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
                text = "#All Tracks",
                fontSize = 48.sp,
                fontWeight = FontWeight.W900,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun UserActionBar(
    searchText: StateFlow<String>,
    active: StateFlow<Boolean>,
    searchResult: StateFlow<List<Track>>,
    onSearchTextChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    loadTracksToDatabase: (Context) -> Unit,
    deleteTracksFromDatabase: (List<Track>) -> Unit,
    sortType: StateFlow<SortType>,
    onEvent: (SortOptionEvent) -> Unit,
    saveSortOption: (SortType) -> Unit,
    sentInfoToBottomSheet: (String, String, Uri?) -> Unit,
    tracks: () -> List<Track>
){
    val coroutineScope = rememberCoroutineScope()
    val context = getContext()

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
                        .background(color = Color(0x99FF7F50)),
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
                                .size(28.dp)
                                .clickable {
                                    coroutineScope.launch(Dispatchers.Default) {
                                        launch {
                                            loadTracksToDatabase(context)
                                        }
                                        launch {
                                            deleteTracksFromDatabase(tracks())
                                        }
                                    }
                                },
                            tint = Color.Black
                        )
                    }
                }
            }

            SearchBar(
                searchText = searchText,
                active = active,
                searchResult = searchResult,
                onSearchTextChange = onSearchTextChange,
                onActiveChange = onActiveChange,
                sentInfoToBottomSheet = sentInfoToBottomSheet
            )
        }
    }
}


@Composable
fun DropDownMenu(
    sortType: StateFlow<SortType>,
    saveSortOption: (SortType)->Unit,
    onEvent: (SortOptionEvent) ->Unit
){
    var expanded by remember { mutableStateOf(false) }

    var isSortOptionAsc by remember { mutableIntStateOf(1) }
    val sortOption = sortType.collectAsStateWithLifecycle().value

    Box(Modifier.clickable { expanded = true }) {
        Icon(
            Icons.Rounded.Sort,
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
        )

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = -28.dp, y = 23.dp),
                modifier = Modifier.background(Color.White)
            ) {

                DropdownMenuItem(
                    text = {
                        Text(
                            "Date",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            color = if (sortOption == SortType.DATA_ACS_ORDER || sortOption == SortType.DATA_DESC_ORDER)
                                Color.Red
                            else
                                Color.Black
                        )
                    },
                    onClick = {

                        isSortOptionAsc =
                            if (sortOption.toString().take(4) != "DATA") {
                                isSortOptionAsc
                            } else (isSortOptionAsc + 1) % 2

                        onEvent(
                            if (isSortOptionAsc == 0) {
                                SortOptionEvent.ListTrackSortOption(SortType.DATA_ACS_ORDER)
                                    .also {
                                        saveSortOption(SortType.DATA_ACS_ORDER)
                                    }
                            } else SortOptionEvent.ListTrackSortOption(SortType.DATA_DESC_ORDER)
                                .also {
                                    saveSortOption(SortType.DATA_DESC_ORDER)
                                }
                        )
                    },
                    trailingIcon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                tint = if (sortOption == SortType.DATA_ACS_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (sortOption == SortType.DATA_DESC_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            "Title",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            color = if (sortOption == SortType.TITLE_ACS_ORDER || sortOption == SortType.TITLE_DESC_ORDER)
                                Color.Red
                            else
                                Color.Black
                        )
                    },

                    onClick = {
                        isSortOptionAsc =
                            if (sortOption.toString().take(5) != "TITLE") {
                                isSortOptionAsc
                            } else (isSortOptionAsc + 1) % 2

                        onEvent(
                            if (isSortOptionAsc == 0) {
                                SortOptionEvent.ListTrackSortOption(SortType.TITLE_ACS_ORDER)
                                    .also {
                                        saveSortOption(SortType.TITLE_ACS_ORDER)
                                    }
                            } else SortOptionEvent.ListTrackSortOption(SortType.TITLE_DESC_ORDER)
                                .also {
                                    saveSortOption(SortType.TITLE_DESC_ORDER)
                                }
                        )
                    },
                    trailingIcon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                tint = if (sortOption == SortType.TITLE_ACS_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (sortOption == SortType.TITLE_DESC_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            "Artist",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            color = if (sortOption == SortType.ARTIST_ASC_ORDER || sortOption == SortType.ARTIST_DESC_ORDER)
                                Color.Red
                            else
                                Color.Black
                        )
                    },
                    onClick = {

                        isSortOptionAsc =
                            if (sortOption.toString().take(6) != "ARTIST") {
                                isSortOptionAsc
                            } else (isSortOptionAsc + 1) % 2

                        onEvent(
                            if (isSortOptionAsc == 0) {
                                SortOptionEvent.ListTrackSortOption(SortType.ARTIST_ASC_ORDER)
                                    .also {
                                        saveSortOption(SortType.ARTIST_ASC_ORDER)
                                    }
                            } else SortOptionEvent.ListTrackSortOption(SortType.ARTIST_DESC_ORDER)
                                .also {
                                    saveSortOption(SortType.ARTIST_DESC_ORDER)
                                }
                        )
                    },
                    trailingIcon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                tint = if (sortOption == SortType.ARTIST_ASC_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (sortOption == SortType.ARTIST_DESC_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}


//@Composable
//fun Counter(
//    tracks: StateFlow<List<Track>>
//){
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.BottomStart
//    ) {
//        Box(
//            modifier = Modifier
//                .size(50.dp, 50.dp)
//                .background(color = Color.Red),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                tracks.collectAsState().value.size.toString(),
//                fontSize = 20.sp,
//                color = Color.Cyan
//            )
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: StateFlow<String>,
    active: StateFlow<Boolean>,
    searchResult: StateFlow<List<Track>>,
    onSearchTextChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    sentInfoToBottomSheet: (String, String, Uri?) -> Unit
){

    val searchText = searchText.collectAsStateWithLifecycle().value
    val active = active.collectAsStateWithLifecycle().value
    val searchResult = searchResult.collectAsStateWithLifecycle().value

    DockedSearchBar(
        query = searchText,
        onQueryChange = {
            onSearchTextChange(it)
        },
        onSearch = {
            onActiveChange(false)
        },
        active = active,
        onActiveChange = {
            onActiveChange(it)
        },
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (active) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            if (searchText.isNotEmpty())
                                onSearchTextChange("")
                            else
                                onActiveChange(false)
                        }
                )
            }
        },
        placeholder = {
            Text(
                "Search",
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif
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
                items(
                    searchResult
                ) { track ->
                    Item(
                        title = if (track.title.length > 12) track.title.take(12) + "..." else track.title,
                        artist = if (track.artist.length > 12) track.artist.take(12) + "..." else track.artist,
                        imageUri = track.imageUri,
                        sentInfoToBottomSheet = sentInfoToBottomSheet,
                        imageSize = 44.dp,
                        textTitleSize = 15.sp,
                        textArtistSize = 11.sp
                    )
                }
            }
        )
    }
}


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun BottomPlayer (
    imageUri: Uri?,
    title: String,
    artist: String,
    isExpanded: StateFlow<Boolean>,
    changeIsExpended: (Boolean) -> Unit,
    isShown: () -> Boolean
){

    val coroutineScope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState()
    val isExpanded = isExpanded.collectAsStateWithLifecycle().value

    val interactionSource = remember { MutableInteractionSource() }


    AnimatedVisibility(
        visible = isShown(),
        enter = slideInVertically(
            initialOffsetY = { 60 },
            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)) + fadeIn(animationSpec = tween(durationMillis = 100))
    ){
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier
                .fillMaxWidth(),
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetDragHandle = {  },
            sheetPeekHeight = 60.dp,
            sheetSwipeEnabled = false,
            sheetContainerColor = Color.White,
            sheetShadowElevation = 12.dp,
            sheetContent = {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            changeIsExpended(true)
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                ) {

                    this@BottomSheetScaffold.AnimatedVisibility(
                        visible = !isExpanded,
                        enter = fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = 100, easing = LinearOutSlowInEasing)),
                        exit = fadeOut(animationSpec = tween(0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(
                                modifier = Modifier
                                    .padding(start = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

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
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                Text(
                                    buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append(title)
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Gray,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.W300
                                            )
                                        ) {
                                            append("\n" + artist)
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .fillMaxWidth(0.7f)
                                        .basicMarquee(
                                            animationMode = MarqueeAnimationMode.Immediately,
                                            delayMillis = 0
                                        )
                                )
                            }

                            Icon(
                                Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(42.dp)
                                    .padding(end = 16.dp)
                            )
                        }

                    }

                    this@BottomSheetScaffold.AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        Box(
                            modifier = Modifier
                                .pointerInput(Unit){
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val (x, y) = dragAmount

                                        if (y > 50 && x < 40 && x > -40) {
                                            coroutineScope.launch {
                                                launch(Dispatchers.Main) {
                                                    scaffoldState.bottomSheetState.partialExpand()
                                                }
                                                launch(Dispatchers.IO) {
                                                    changeIsExpended(false)
                                                }
                                            }
                                        }
                                    }
                                }
                        ) {

                            BackHandler {
                                coroutineScope.launch {
                                    launch(Dispatchers.Main) {
                                        scaffoldState.bottomSheetState.partialExpand()
                                    }
                                    launch(Dispatchers.IO) {
                                        changeIsExpended(false)
                                    }
                                }
                                return@BackHandler
                            }

                            Screen(
                                title = title,
                                artist = artist,
                                imageUri = imageUri
                            )
                        }
                    }


                }
            }
        ) {

        }
    }
}