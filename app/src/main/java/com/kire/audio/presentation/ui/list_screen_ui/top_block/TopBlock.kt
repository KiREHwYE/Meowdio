package com.kire.audio.presentation.ui.list_screen_ui.top_block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import androidx.media3.session.MediaController
import com.kire.audio.R
import com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar.ActionBar
import com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar.dropdown_menu.DropDownMenu
import com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar.SearchBar
import com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar.SortAndRefreshBar

import com.kire.audio.presentation.viewmodel.TrackViewModel

@Composable
fun TopBlock(
    trackViewModel: TrackViewModel,
    onTitleClick: () -> Unit,
    mediaController: MediaController?
){

    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Header(
            text = stringResource(id = R.string.listscreen_header),
            onTitleClick = {
                onTitleClick()
            }
        )

        ActionBar(
            sortAndRefreshBar = {
                SortAndRefreshBar(
                    refreshAction = trackViewModel::updateTrackDataBase,
                    dropDownMenu = { isExpanded, onDismiss ->
                        DropDownMenu(
                            isExpanded = isExpanded,
                            onDismiss = onDismiss,
                            sortType = trackViewModel.sortType,
                            saveSortOption = trackViewModel::saveSortOption,
                            onEvent = trackViewModel::updateSortOption,
                        )
                    }
                )
            },
            searchBar = {
                SearchBar(
                    trackUiState = trackViewModel.trackUiState,
                    changeTrackUiState = trackViewModel::updateTrackUiState,
                    searchUiState = trackViewModel.searchUiState,
                    changeSearchUiState = trackViewModel::updateSearchUiState,
                    mediaController = mediaController,
                    upsertTrack = trackViewModel::upsertTrack,
                    selectListTracks = trackViewModel::selectListOfTracks,
                    modifier = Modifier.weight(1f)
                )
            }
        )

    }
}