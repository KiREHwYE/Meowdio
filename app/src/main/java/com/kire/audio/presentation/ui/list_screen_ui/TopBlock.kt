package com.kire.audio.presentation.ui.list_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.media3.session.MediaController

import com.kire.audio.presentation.viewmodel.TrackViewModel

@Composable
fun TopBlock(
    trackViewModel: TrackViewModel,
    onTitleClick: () -> Unit,
    mediaController: MediaController?,
    modifier: Modifier
){
    Column(
        modifier = modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Header(
            onTitleClick = {
                onTitleClick()
            }
        )

        ActionBar(
            sortAndRefreshBar = {
                SortAndRefreshBar(
                    refreshAction = trackViewModel::updateTrackDataBase,
                    dropDownMenu = {
                        DropDownMenu(
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