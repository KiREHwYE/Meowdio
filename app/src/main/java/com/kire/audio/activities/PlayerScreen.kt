package com.kire.audio.activities

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PauseCircleFilled
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun Screen(
    title: String,
    artist: String,
    imageUri: Uri?
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Background(imageUri)

        Column(modifier = Modifier
            .padding(10.dp)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header()
            ShowImage(imageUri)
            TextBlock(title = title, artist = artist)
            FunctionalBlock()
        }
    }
}

@Composable
fun Background(
    imageUri: Uri?
){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .build(),
        contentDescription = "Background image",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply{
            setToScale(0.5f,0.5f,0.5f,1f)
        }),
        modifier = Modifier
            .fillMaxSize()
            .blur(16.dp)
            .height(400.dp)
            .fillMaxWidth(0.9f)
    )
}

@Composable
fun Header(){

    Row(modifier = Modifier
        .padding(top = 40.dp)
        .width(340.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween)
    {


        Icon(
            Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Close",
            modifier = Modifier
                .size(30.dp)
                .alpha(0.8f),
            tint = Color.White
        )

        Icon(
            Icons.Rounded.MoreVert,
            contentDescription = "Settings",
            modifier = Modifier
                .size(30.dp)
                .alpha(0.8f),
            tint = Color.White
        )
    }
}

@Composable
fun ShowImage(
    imageUri: Uri?
){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri) 
            .build(),
        contentDescription = "Track Image in foreground",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(340.dp, 340.dp)
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(25.dp))
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextBlock(
    title: String,
    artist: String
){

    var isLoved = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = Modifier
        .width(340.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row(modifier = Modifier.
            fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(title)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W300
                        )
                    ) {
                        append("\n" + artist)
                    }
                },
                modifier = Modifier
                    .padding(start = 14.dp)
                    .fillMaxWidth(0.85f)
                    .alpha(0.8f)
                    .basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 0
                    )
            )

            Icon(
                if (isLoved.value) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Favourite",
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.8f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        if (!isLoved.value)
                            isLoved.value = true
                        else
                            isLoved.value = false
                    },
                tint = if (isLoved.value) Color.Red else Color.White
            )
        }

        Slider(
            value = 0.8f,
            onValueChange = {  },
            modifier = Modifier.padding(top = 15.dp),
        )
    }
}

@Composable
fun FunctionalBlock(){
    Row(modifier = Modifier
        .width(340.dp)
        .padding(bottom = 80.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        val isPlaying = remember { mutableStateOf(false) }
        val interactionSource = remember { MutableInteractionSource() }

        Icon(
            Icons.Rounded.Refresh,
            contentDescription = "RepeatOrShuffle",
            modifier = Modifier
                .size(30.dp)
                .alpha(0.7f),
            tint = Color.White
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Icon(
                Icons.Rounded.SkipPrevious,
                contentDescription = "Previous",
                modifier = Modifier
                    .size(40.dp)
                    .alpha(0.8f),
                tint = Color.White
            )

            Icon(
                if (isPlaying.value)
                    Icons.Rounded.PauseCircleFilled
                else
                    Icons.Rounded.PlayCircleFilled,
                contentDescription = "Play",
                modifier = Modifier
                    .size(70.dp)
                    .alpha(0.8f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        if (!isPlaying.value)
                            isPlaying.value = true
                        else
                            isPlaying.value = false
                    },
                tint = Color.White
            )


            Icon(
                Icons.Rounded.SkipNext,
                contentDescription = "Next",
                modifier = Modifier
                    .size(40.dp)
                    .alpha(0.8f),
                tint = Color.White
            )
        }

        Icon(
            Icons.Rounded.PlaylistPlay,
            contentDescription = "Playlist",
            modifier = Modifier
                .size(30.dp)
                .alpha(0.7f),
            tint = Color.White,
        )

    }
}