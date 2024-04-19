package com.kire.audio.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kire.audio.R
import com.ramcosta.composedestinations.annotation.Destination


@Composable
fun AlbumItem(
    artist: String
){
    Box(
        modifier = Modifier
            .wrapContentSize()
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.music_icon),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
            )

            Text(
                text = artist
            )
        }
    }
}

@Destination
@Composable
fun AlbumScreen(
    albums: List<String> = listOf(
        "Ling Toshite Shigure",
        "ITZY",
        "G-IDLE",
        "BMTH",
        "SABATON",
        "AC/DC",
        "Gazmanov",
        "Ling Toshite Shigure",
        "ITZY",
        "G-IDLE",
        "BMTH",
        "SABATON",
        "AC/DC",
        "Gazmanov"
    )
){
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {

            items(
                albums
            ){ album ->
                AlbumItem(artist = album)
            }
        }
    }
}


@Preview
@Composable
fun Preview() {
    AlbumScreen(
        albums = listOf(
            "Ling Toshite Shigure",
            "ITZY",
            "G-IDLE",
            "BMTH",
            "SABATON",
            "AC/DC",
            "Gazmanov",
            "Ling Toshite Shigure",
            "ITZY",
            "G-IDLE",
            "BMTH",
            "SABATON",
            "AC/DC",
            "Gazmanov"
        )
    )
}