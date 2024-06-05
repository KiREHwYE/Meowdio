package com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar.dropdown_menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.kire.audio.R
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.SortType

@Composable
fun DropdownMenuItemTrailingIcon(
    sortOption: SortType,
    sortTypeASC: SortType,
    sortTypeDESC: SortType
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-4).dp)
    ) {
        Icon(
            Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            tint = if (sortOption == sortTypeASC) Color.Red else AudioExtendedTheme.extendedColors.button,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.app_universal_icon_size))
        )
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = if (sortOption == sortTypeDESC) Color.Red else AudioExtendedTheme.extendedColors.button,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.app_universal_icon_size))
        )
    }
}