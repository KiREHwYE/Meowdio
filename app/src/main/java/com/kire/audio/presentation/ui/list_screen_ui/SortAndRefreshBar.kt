package com.kire.audio.presentation.ui.list_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

@Composable
fun SortAndRefreshBar(
    refreshAction: () -> Unit,
    dropDownMenu: @Composable () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .shadow(
                elevation = 5.dp,
                spotColor = Color.DarkGray,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(color = AudioExtendedTheme.extendedColors.controlElementsBackground)
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 15.dp,
                bottom = 15.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        dropDownMenu()

        Icon(
            Icons.Rounded.Refresh,
            contentDescription = "Refresh",
            modifier = Modifier
                .size(24.dp)
                .bounceClick {
                    refreshAction()
                },
            tint = AudioExtendedTheme.extendedColors.button
        )
    }
}