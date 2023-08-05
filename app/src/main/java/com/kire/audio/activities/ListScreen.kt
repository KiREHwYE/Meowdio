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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kire.audio.TrackRepository
import com.kire.audio.activities.destinations.ScreenDestination
import com.kire.audio.functional.getContext
import com.kire.audio.models.Track
import com.kire.audio.viewmodels.TrackListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Item(
    title: String,
    artist: String,
    uri: Uri?,
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
                        uri = uri
                    )
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                .build(),
            contentDescription = "",
            modifier = Modifier
                .size(50.dp, 50.dp)
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        contentPadding = PaddingValues(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            UpperBlock(tracks)
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
                    uri = track.imageUri,
                    navigator = navigator
                )
            }
        }
    }
    Counter(viewModel)
}

@Composable
fun UpperBlock(tracks: List<Track>){

    val coroutineScope = rememberCoroutineScope()
    val context = getContext()

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
            IconButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.Default) {
                        launch {
                            TrackRepository.get().loadTracksToDatabase(context)
                        }
                        launch {
                            TrackRepository.get().deleteTracksFromDatabase(tracks)
                        }
                    }
                }
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier
                        .size(30.dp)
                        .alpha(0.8f),
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun Counter(
    viewModel: TrackListViewModel
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
                viewModel.tracks.collectAsState().value.size.toString(),
                fontSize = 20.sp,
                color = Color.Cyan
            )
        }
    }
}