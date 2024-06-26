package com.kire.audio.presentation.ui.player_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import com.kire.audio.R

import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.player_screen_ui.dialog.dialog_info.DialogInfo
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

@Composable
fun Header(
    trackUiState: TrackUiState,
    changeTrackUiState: (TrackUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    navigateBack: () -> Unit
){

    var openDialog by remember {
        mutableStateOf(false)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        Icon(
            Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Close",
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.app_universal_icon_size))
                .alpha(0.8f)
                .bounceClick {
                    navigateBack()
                },
            tint = AudioExtendedTheme.extendedColors.playerScreenButton
        )

        Icon(
            Icons.Rounded.MoreVert,
            contentDescription = "Info",
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.app_universal_icon_size))
                .alpha(0.8f)
                .bounceClick {
                    openDialog = !openDialog
                },
            tint = AudioExtendedTheme.extendedColors.playerScreenButton
        )

        if (openDialog)
            DialogInfo(
                trackUiState = trackUiState,
                changeTrackUiState = changeTrackUiState,
                changeOpenDialog = {isIt ->
                    openDialog = isIt
                },
                upsertTrack = upsertTrack
            )
    }
}