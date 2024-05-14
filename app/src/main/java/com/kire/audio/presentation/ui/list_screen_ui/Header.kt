package com.kire.audio.presentation.ui.list_screen_ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kire.audio.R
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

@Composable
fun Header(
    onTitleClick: () -> Unit
){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clickable {
            onTitleClick()
        },
        contentAlignment = Alignment.BottomStart
    ) {

        Text(
            text = stringResource(id = R.string.listscreen_header),
            fontSize = 52.sp,
            fontWeight = FontWeight.W700,
            fontFamily = FontFamily.SansSerif,
            color = AudioExtendedTheme.extendedColors.primaryText
        )
    }
}