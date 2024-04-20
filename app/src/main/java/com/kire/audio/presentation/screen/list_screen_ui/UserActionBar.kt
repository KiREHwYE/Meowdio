package com.kire.audio.presentation.screen.list_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kire.audio.presentation.functional.bounceClick

@Composable
fun UserActionBar(
    updateTrackDataBase: () -> Unit,
    dropDownMenu: @Composable () -> Unit,
    searchBar: @Composable () -> Unit,
){

    LaunchedEffect(key1 = true) {
        updateTrackDataBase()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .padding(bottom = 18.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .width(120.dp)
                    .height(56.dp)
                    .background(color = MaterialTheme.colorScheme.onBackground)
                    .align(Alignment.Top),
                contentAlignment = Alignment.Center

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.65f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    dropDownMenu()

                    Icon(
                        Icons.Rounded.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier
                            .bounceClick {
                                updateTrackDataBase()
                            },
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            searchBar()
        }
    }
}