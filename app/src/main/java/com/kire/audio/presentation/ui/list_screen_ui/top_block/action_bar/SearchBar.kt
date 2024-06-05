package com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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

import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.R
import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.model.SearchUiState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.TrackUiState
import com.kire.audio.presentation.ui.list_screen_ui.TrackItem
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.ListSelector
import com.kire.audio.presentation.util.nonScaledSp

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
            dividerColor = AudioExtendedTheme.extendedColors.secondaryText,
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = AudioExtendedTheme.extendedColors.secondaryText,
                unfocusedTextColor = AudioExtendedTheme.extendedColors.secondaryText,
                disabledTextColor = AudioExtendedTheme.extendedColors.secondaryText,
                cursorColor = AudioExtendedTheme.extendedColors.secondaryText,
            )
        ),
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Search",
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.app_universal_icon_size))
            )
        },
        trailingIcon = {
            if (searchUiState.active) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = AudioExtendedTheme.extendedColors.button,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.app_universal_icon_size))
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
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    stringResource(id = R.string.listscreen_search),
                    fontSize = 14.sp.nonScaledSp,
                    fontFamily = FontFamily.SansSerif,
                    color = AudioExtendedTheme.extendedColors.secondaryText,
                    lineHeight = 14.sp.nonScaledSp
                )
            }
        },
        modifier = modifier
            .shadow(
                elevation = dimensionResource(id = R.dimen.app_universal_shadow_elevation),
                spotColor = AudioExtendedTheme.extendedColors.shadow,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.app_universal_rounded_corner))
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
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.search_list_spacedby))
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
                    updateTrackUiState = changeTrackUiState,
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
                    textTitleSize = 15.sp.nonScaledSp,
                    textArtistSize = 11.sp.nonScaledSp,
                    startPadding = 12.dp
                )
            }
        }
    }
}