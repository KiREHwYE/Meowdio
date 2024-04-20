package com.kire.audio.presentation.screen.list_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kire.audio.R
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.functional.events.SortOptionEvent
import com.kire.audio.presentation.util.SortType
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DropDownMenu(
    sortType: StateFlow<SortType>,
    saveSortOption: (SortType) -> Unit,
    onEvent: (SortOptionEvent) -> Unit
){
    val sortOption by sortType.collectAsStateWithLifecycle()

    var isSortOptionAsc by remember { mutableIntStateOf(1) }

    var expanded by remember { mutableStateOf(false) }

    val sortOptionFunc: (String, SortType, SortType)->Unit = { text, sortTypeASC, sortTypeDESC ->

        isSortOptionAsc =
            if (!sortOption.toString().take(text.length).equals(text, true)) {
                isSortOptionAsc
            } else (isSortOptionAsc + 1) % 2

        onEvent(
            if (isSortOptionAsc == 0) {
                SortOptionEvent.ListTrackSortOption(
                    sortTypeASC.also {
                        saveSortOption(it)
                    }
                )

            } else SortOptionEvent.ListTrackSortOption(
                sortTypeDESC.also {
                    saveSortOption(it)
                }
            )
        )
    }

    Box(
        Modifier
            .bounceClick {
                expanded = true
            }
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.Sort,
            contentDescription = null,
            modifier = Modifier
                .size(28.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = (-28).dp, y = 23.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
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
}

@Composable
fun CustomDropDownMenuItem(
    sortOption: SortType,
    sortTypeASC: SortType,
    sortTypeDESC: SortType,
    text: String,
    sortOptionFunc: (String, SortType, SortType)->Unit
){
    DropdownMenuItem(
        text = {
            Text(
                text,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = if (sortOption == sortTypeASC || sortOption == sortTypeDESC)
                    Color.Red
                else
                    MaterialTheme.colorScheme.onPrimary
            )
        },
        onClick = {
            sortOptionFunc(
                if (text.equals("date", true)) "data" else text,
                sortTypeASC,
                sortTypeDESC
            )
        },
        trailingIcon = {
            DropdownMenuItemTrailingIcon(
                sortOption = sortOption,
                sortTypeASC = sortTypeASC,
                sortTypeDESC = sortTypeDESC
            )
        }
    )
}

@Composable
fun DropdownMenuItemTrailingIcon(
    sortOption: SortType,
    sortTypeASC: SortType,
    sortTypeDESC: SortType
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            tint = if (sortOption == sortTypeASC) Color.Red else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = if (sortOption == sortTypeDESC) Color.Red else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
    }
}