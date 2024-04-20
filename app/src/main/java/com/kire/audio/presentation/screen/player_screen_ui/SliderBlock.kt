package com.kire.audio.presentation.screen.player_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.session.MediaController
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

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