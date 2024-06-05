package com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar.dropdown_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.R
import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.model.SortOption
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.SortType
import com.kire.audio.presentation.util.nonScaledSp

import kotlinx.coroutines.flow.StateFlow

@Composable
fun DropDownMenu(
    isExpanded: () -> Boolean,
    onDismiss: () -> Unit,
    sortType: StateFlow<SortType>,
    saveSortOption: (SortType) -> Unit,
    onEvent: (SortOption) -> Unit
){
    val sortOption by sortType.collectAsStateWithLifecycle()

    var isSortOptionAsc by remember { mutableIntStateOf(1) }

    val sortOptionFunc: (String, SortType, SortType)->Unit = { text, sortTypeASC, sortTypeDESC ->

        isSortOptionAsc =
            if (!sortOption.toString().take(text.length).equals(text, true)) {
                isSortOptionAsc
            } else (isSortOptionAsc + 1) % 2

        onEvent(
            if (isSortOptionAsc == 0) {
                SortOption(sortTypeASC
                    .also { saveSortOption(it) })
            } else
                SortOption(sortTypeDESC
                    .also {saveSortOption(it) })
        )
    }


    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner)))
    ) {
        DropdownMenu(
            expanded = isExpanded(),
            onDismissRequest = { onDismiss() },
            offset = DpOffset(x = 0.dp, y = 16.dp),
            modifier = Modifier
                .wrapContentSize()
                .background(AudioExtendedTheme.extendedColors.controlElementsBackground)
                .padding(
                    PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    )
                )
        ) {

            CustomDropDownMenuItem(
                sortOption = sortOption,
                sortTypeASC = SortType.DATA_ASC_ORDER,
                sortTypeDESC = SortType.DATA_DESC_ORDER,
                text = stringResource(id = R.string.dropdown_date),
                sortOptionFunc
            )
            CustomDropDownMenuItem(
                sortOption = sortOption,
                sortTypeASC = SortType.TITLE_ASC_ORDER,
                sortTypeDESC = SortType.TITLE_DESC_ORDER,
                text = stringResource(id = R.string.dropdown_title),
                sortOptionFunc
            )
            CustomDropDownMenuItem(
                sortOption = sortOption,
                sortTypeASC = SortType.ARTIST_ASC_ORDER,
                sortTypeDESC = SortType.ARTIST_DESC_ORDER,
                text = stringResource(id = R.string.dropdown_artist),
                sortOptionFunc
            )

            CustomDropDownMenuItem(
                sortOption = sortOption,
                sortTypeASC = SortType.DURATION_ASC_ORDER,
                sortTypeDESC = SortType.DURATION_DESC_ORDER,
                text = stringResource(id = R.string.dropdown_duration),
                sortOptionFunc
            )
        }
    }
}