package com.kire.audio.activities

import android.net.Uri
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
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

        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Close",
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.8f),
                tint = Color.White
            )
        }

        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "Settings",
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.8f),
                tint = Color.White
            )
        }

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

@Composable
fun TextBlock(
    title: String,
    artist: String
){

    Column(modifier = Modifier
        .width(340.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row(modifier = Modifier.
            fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.W500,
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier
                        .alpha(0.8f)
                )

                Text(
                    artist,
                    fontWeight = FontWeight.W300,
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    modifier = Modifier
                        .alpha(0.8f)
                        .padding(top = 4.dp)
                )
            }

            IconButton(onClick = { /*TODO*/ },
                modifier = Modifier.alpha(0.8f)
            ) {
                Icon(
                    Icons.Filled.FavoriteBorder,
                    contentDescription = "Favourite",
                    modifier = Modifier
                        .size(30.dp)
                        .alpha(0.8f),
                    tint = Color.White
                )
            }
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
        .padding(bottom = 90.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = "RepeatOrShuffle",
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.7f),
                tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Previous",
                modifier = Modifier
                    .size(40.dp)
                    .alpha(0.8f),
                tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier
                    .size(60.dp)
                    .alpha(0.8f),
                tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                modifier = Modifier
                    .size(40.dp)
                    .alpha(0.8f),
                tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Filled.List,
                contentDescription = "Playlist",
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.7f),
                tint = Color.White,
            )
        }
    }
}