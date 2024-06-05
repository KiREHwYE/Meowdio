package com.kire.audio.presentation.ui.list_screen_ui.top_block.action_bar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.kire.audio.R

@Composable
fun ActionBar(
    sortAndRefreshBar: @Composable () -> Unit,
    searchBar: @Composable () -> Unit,
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.column_and_row_universal_spacedby))
    ) {

        sortAndRefreshBar()

        searchBar()
    }
}
