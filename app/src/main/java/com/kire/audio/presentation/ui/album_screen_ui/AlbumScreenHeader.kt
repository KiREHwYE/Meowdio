package com.kire.audio.presentation.ui.album_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.kire.audio.R
import com.kire.audio.presentation.ui.album_screen_ui.dialog_album_info.DialogAlbumInfo

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.bounceClick

@Composable
fun AlbumScreenHeader(
    openDialog: () -> Unit,
    navigateBack: () -> Unit
){

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.app_universal_pad)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(
                    spotColor = AudioExtendedTheme.extendedColors.shadow,
                    elevation = dimensionResource(id = R.dimen.app_universal_shadow_elevation),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(AudioExtendedTheme.extendedColors.background)
                .bounceClick {
                    navigateBack()
                },
            contentAlignment = Alignment.Center
        ){
            Icon(
                Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Close",
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.app_universal_icon_size))
            )
        }

        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(
                    spotColor = AudioExtendedTheme.extendedColors.shadow,
                    elevation = dimensionResource(id = R.dimen.app_universal_shadow_elevation),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(AudioExtendedTheme.extendedColors.background)
                .bounceClick {
                    openDialog()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.MoreVert,
                contentDescription = "Info",
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.app_universal_icon_size))
            )
        }
    }
}