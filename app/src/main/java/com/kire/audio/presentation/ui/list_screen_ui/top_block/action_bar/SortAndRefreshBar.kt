package com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Refresh

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.kire.audio.R

import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

@Composable
fun SortAndRefreshBar(
    refreshAction: () -> Unit,
    dropDownMenu: @Composable (isExpanded: () -> Boolean, onDismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
){

    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.TopStart
    ) {
        Row(
            modifier = modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .shadow(
                    elevation = dimensionResource(id = R.dimen.app_universal_shadow_elevation),
                    spotColor = AudioExtendedTheme.extendedColors.shadow,
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner))
                )
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner)))
                .background(color = AudioExtendedTheme.extendedColors.controlElementsBackground)
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 15.dp,
                    bottom = 15.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.sort_and_refresh_bar_spacedby))

        ) {
            Icon(
                Icons.AutoMirrored.Rounded.Sort,
                contentDescription = null,
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.app_universal_icon_size))
                    .bounceClick {
                        expanded = !expanded
                    }
            )

            Icon(
                Icons.Rounded.Refresh,
                contentDescription = "Refresh",
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.app_universal_icon_size))
                    .bounceClick {
                        refreshAction()
                    }
            )
        }

        dropDownMenu(
            isExpanded = {
                expanded
            },
            onDismiss = {
                expanded = false
            }
        )
    }
}