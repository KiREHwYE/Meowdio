package com.kire.audio.activities

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.kire.audio.activities.destinations.ScreenDestination
import com.kire.audio.functional.getContext
import com.kire.audio.models.Track
import com.kire.audio.viewmodels.TrackListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import com.kire.audio.R
import com.kire.audio.events.SortOptionEvent
import com.kire.audio.events.SortType
import com.kire.audio.functional.GetPermissions
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Item(
    title: String,
    artist: String,
    imageUri: Uri?,
    navigator: DestinationsNavigator
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigator.navigate(
                    ScreenDestination(
                        title = title,
                        artist = artist,
                        imageUri = imageUri
                    )
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Track Image",
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier
                    .padding(start = 14.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.W400)
                Text(artist, fontSize = 14.sp, fontWeight = FontWeight.W300, color = Color.Gray)
            }
        }
    }
}

@RootNavGraph(start = true)
@Destination
@Composable
fun ListOfTracks(
    navigator: DestinationsNavigator,
    viewModel: TrackListViewModel = hiltViewModel()
) {

    val tracks by viewModel.tracks.collectAsStateWithLifecycle()

    GetPermissions(
        lifecycleOwner = LocalLifecycleOwner.current,
        viewModel = viewModel
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        contentPadding = PaddingValues(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            UpperBlock()
        }

        item {
            UserActionBar(viewModel, tracks)
        }

        if (tracks.isNotEmpty()) {
            items(
                tracks,
                key = { listItem ->
                    listItem.id
                }
            ) { track ->
                Item(
                    title = track.title,
                    artist = track.artist,
                    imageUri = track.imageUri,
                    navigator = navigator
                )
            }
        }
    }

    Counter(viewModel.tracks)
}

@Composable
fun UpperBlock(){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
        .padding(bottom = 8.dp),
        contentAlignment = Alignment.BottomStart
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#All Tracks",
                fontSize = 30.sp,
                fontWeight = FontWeight.W700,
            )
        }
    }
}
@Composable
fun UserActionBar(
    viewModel: TrackListViewModel,
    tracks: List<Track>
){

    Box(modifier = Modifier
        .fillMaxWidth()
    ) {

        val coroutineScope = rememberCoroutineScope()
        val context = getContext()


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            DropDownMenu(viewModel)

            IconButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.Default) {
                        launch {
                            viewModel.loadTracksToDatabase(context)
                        }
                        launch {
                            viewModel.deleteTracksFromDatabase(tracks)
                        }
                    }
                }
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier
                        .size(30.dp),
                    tint = Color.Black
                )
            }
        }
    }
}


@Composable
fun DropDownMenu(
    viewModel: TrackListViewModel
){
    var expanded by remember { mutableStateOf(false) }

    var isSortOptionAsc by remember { mutableStateOf(1) }
    val sortOption = viewModel.sortType.collectAsStateWithLifecycle().value

    Box(Modifier.clickable { expanded = true }) {
        Icon(
            Icons.Filled.Menu,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
        )
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))
        ){
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 0.dp, y = 10.dp),
                modifier = Modifier.background(Color.White)
            ) {

                DropdownMenuItem(
                    text = {
                        Text(
                            "Date",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            color = if (sortOption == SortType.DATA_ACS_ORDER || sortOption == SortType.DATA_DESC_ORDER)
                                        Color.Red
                                    else
                                        Color.Black
                        ) },
                    onClick = {

                        isSortOptionAsc =
                            if (sortOption.toString().take(4) != "DATA"){
                                isSortOptionAsc
                            }
                            else (isSortOptionAsc + 1) % 2

                        viewModel.onEvent(
                            if (isSortOptionAsc == 0) {
                                SortOptionEvent.ListTrackSortOption(SortType.DATA_ACS_ORDER).also {
                                    viewModel.saveSortOption(SortType.DATA_ACS_ORDER)
                                }
                            }
                            else SortOptionEvent.ListTrackSortOption(SortType.DATA_DESC_ORDER).also {
                                viewModel.saveSortOption(SortType.DATA_DESC_ORDER)
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
                                modifier = Modifier.size(17.dp)
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (sortOption == SortType.DATA_DESC_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            "Title",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            color = if (sortOption == SortType.TITLE_ACS_ORDER || sortOption == SortType.TITLE_DESC_ORDER)
                                        Color.Red
                                    else
                                        Color.Black
                        ) },
                    onClick = {

                        isSortOptionAsc =
                            if (sortOption.toString().take(5) != "TITLE"){
                                isSortOptionAsc
                            }
                            else (isSortOptionAsc + 1) % 2

                        viewModel.onEvent(
                            if (isSortOptionAsc == 0) {
                                SortOptionEvent.ListTrackSortOption(SortType.TITLE_ACS_ORDER).also {
                                    viewModel.saveSortOption(SortType.TITLE_ACS_ORDER)
                                }
                            }
                            else SortOptionEvent.ListTrackSortOption(SortType.TITLE_DESC_ORDER).also {
                                viewModel.saveSortOption(SortType.TITLE_DESC_ORDER)
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
                                modifier = Modifier.size(17.dp)
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (sortOption == SortType.TITLE_DESC_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            "Artist",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500,
                            color = if (sortOption == SortType.ARTIST_ASC_ORDER || sortOption == SortType.ARTIST_DESC_ORDER)
                                        Color.Red
                                    else
                                        Color.Black
                        ) },
                    onClick = {

                        isSortOptionAsc =
                            if (sortOption.toString().take(6) != "ARTIST"){
                                isSortOptionAsc
                            }
                            else (isSortOptionAsc + 1) % 2

                        viewModel.onEvent(
                            if (isSortOptionAsc == 0) {
                                SortOptionEvent.ListTrackSortOption(SortType.ARTIST_ASC_ORDER).also {
                                    viewModel.saveSortOption(SortType.ARTIST_ASC_ORDER)
                                }
                            }
                            else SortOptionEvent.ListTrackSortOption(SortType.ARTIST_DESC_ORDER).also {
                                viewModel.saveSortOption(SortType.ARTIST_DESC_ORDER)
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
                                modifier = Modifier.size(17.dp)
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (sortOption == SortType.ARTIST_DESC_ORDER) Color.Red else Color.Black,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                )
            }
        }
    }

}


@Composable
fun Counter(
    tracks: StateFlow<List<Track>>
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .size(50.dp, 50.dp)
                .background(color = Color.Red), contentAlignment = Alignment.Center
        ) {
            Text(
                tracks.collectAsState().value.size.toString(),
                fontSize = 20.sp,
                color = Color.Cyan
            )
        }
    }
}