package com.kire.audio.presentation.ui.list_screen_ui.top_block

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kire.audio.R
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.nonScaledSp

@Composable
fun Header(
    text: String,
    onTitleClick: () -> Unit,
){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(160.dp),
        contentAlignment = Alignment.BottomStart
    ) {

        Text(
            text = text,
            fontSize = 52.sp.nonScaledSp,
            fontWeight = FontWeight.W700,
            fontFamily = FontFamily.SansSerif,
            color = AudioExtendedTheme.extendedColors.primaryText,
            modifier = Modifier
                .pointerInput(Unit){
                    detectTapGestures {
                        onTitleClick()
                    }
                }
        )
    }
}