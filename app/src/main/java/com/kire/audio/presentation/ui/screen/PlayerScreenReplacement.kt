package com.kire.audio.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreenReplacement(

){


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.LightGray,
                        Color.White
                    ),
                    startY = 0.0f,
                    endY = 550.0f
                )
            )
            .padding(
                start = 28.dp,
                end = 28.dp,
                top = 28.dp,
                bottom = 56.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        androidx.compose.material3.CenterAlignedTopAppBar(
            title = {
                Text(text = "Now playing")
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            },
            actions = {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                modifier = Modifier
                    .size(400.dp),
                tint = Color.LightGray
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "Corner Of My Sky", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, letterSpacing = 2.sp)
                Text(text = "Artist tralalal tralala tralllala trala", fontSize = 14.sp, color = Color.LightGray)
            }
        }

        Slider(state = SliderState(0F))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarViewDay,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
            )
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )
            Icon(
                imageVector = Icons.Default.PlayCircleFilled,
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
            )
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}

@Preview
@Composable
fun Test(){
    PlayerScreenReplacement()
}