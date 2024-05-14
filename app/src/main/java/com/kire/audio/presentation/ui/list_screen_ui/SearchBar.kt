package com.kire.audio.presentation.ui.list_screen_ui

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search

import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.R
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.model.SearchUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.ListSelector

import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchBar(
    trackUiState: StateFlow<TrackUiState>,
    searchUiState: StateFlow<SearchUiState>,
    changeTrackUiState: (TrackUiState) -> Unit,
    changeSearchUiState: (SearchUiState) -> Unit,
    upsertTrack: suspend (Track) -> Unit,
    mediaController: MediaController?,
    selectListTracks: (ListSelector) -> StateFlow<List<Track>>,
    modifier: Modifier = Modifier
){
    val searchResult by selectListTracks(ListSelector.SEARCH_LIST).collectAsStateWithLifecycle()

    val trackUiState by trackUiState.collectAsStateWithLifecycle()
    val searchUiState by searchUiState.collectAsStateWithLifecycle()

    DockedSearchBar(
        query = searchUiState.searchText,
        onQueryChange = {
            changeSearchUiState(searchUiState.copy(searchText = it))
        },
        onSearch = {
            changeSearchUiState(searchUiState.copy(active = false))
        },
        active = searchUiState.active && !searchUiState.isExpanded,
        onActiveChange = {
            changeSearchUiState(searchUiState.copy(active = it))
        },
        colors = SearchBarDefaults.colors(
            containerColor = AudioExtendedTheme.extendedColors.controlElementsBackground,
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = AudioExtendedTheme.extendedColors.secondaryText,
                unfocusedTextColor = AudioExtendedTheme.extendedColors.secondaryText,
                disabledTextColor = AudioExtendedTheme.extendedColors.secondaryText
            )
        ),
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .size(24.dp),
                tint = AudioExtendedTheme.extendedColors.button
            )
        },
        trailingIcon = {
            if (searchUiState.active) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = AudioExtendedTheme.extendedColors.button,
                    modifier = Modifier
                        .size(24.dp)
                        .bounceClick {
                            if (searchUiState.searchText.isNotEmpty())
                                changeSearchUiState(searchUiState.copy(searchText = ""))
                            else
                                changeSearchUiState(searchUiState.copy(active = false))

                            changeTrackUiState(trackUiState.copy(currentListSelector = ListSelector.MAIN_LIST))
                        }
                )
            }
        },
        placeholder = {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    stringResource(id = R.string.listscreen_search),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = AudioExtendedTheme.extendedColors.secondaryText
                )
            }
        },
        modifier = modifier
            .shadow(
                elevation = 5.dp,
                spotColor = Color.DarkGray,
                shape = RoundedCornerShape(24.dp)
            )

    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            itemsIndexed(
                searchResult,
                key = { _, track ->
                    track.id
                }
            ) { listIndex, track ->
                TrackItem(
                    trackToShow = track,
                    trackUiState = trackUiState,
                    changeTrackUiState = changeTrackUiState,
                    selector = ListSelector.SEARCH_LIST,
                    mediaController = mediaController,
                    listINDEX = listIndex,
                    upsertTrack = upsertTrack,
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                    imageSize = 44.dp,
                    textTitleSize = 15.sp,
                    textArtistSize = 11.sp,
                    startPadding = 12.dp
                )
            }
        }
    }
}