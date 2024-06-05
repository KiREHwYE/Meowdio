package com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar.dropdown_menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.SortType
import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.util.nonScaledSp

@Composable
fun CustomDropDownMenuItem(
    sortOption: SortType,
    sortTypeASC: SortType,
    sortTypeDESC: SortType,
    text: String,
    sortOptionFunc: (String, SortType, SortType)->Unit
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .bounceClick {
                sortOptionFunc(
                    if (text.equals("date", true)) "data" else text,
                    sortTypeASC,
                    sortTypeDESC
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text,
            fontSize = 17.sp.nonScaledSp,
            fontWeight = FontWeight.W600,
            color = if (sortOption == sortTypeASC || sortOption == sortTypeDESC)
                Color.Red
            else
                AudioExtendedTheme.extendedColors.secondaryText,
            modifier = Modifier
                .padding(end = 16.dp)
        )

        DropdownMenuItemTrailingIcon(
            sortOption = sortOption,
            sortTypeASC = sortTypeASC,
            sortTypeDESC = sortTypeDESC
        )
    }
}