package com.kire.audio.presentation.navigation

import androidx.compose.runtime.Composable

import androidx.media3.session.MediaController

import androidx.navigation.NavHostController
import com.kire.audio.presentation.ui.screen.AlbumScreen

import com.kire.audio.presentation.ui.screen.ListAlbumScreen
import com.kire.audio.presentation.ui.screen.ListScreen
import com.kire.audio.presentation.ui.screen.PlayerScreen
import com.kire.audio.presentation.ui.screen.NavGraphs
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.ListAlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.ListScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.PlayerScreenDestination

import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.spec.NavHostEngine

@Composable
fun NavigationUI(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navHostController: NavHostController,
    navHostEngine: NavHostEngine
){

    DestinationsNavHost(navGraph = NavGraphs.root, engine = navHostEngine, navController = navHostController) {
        composable(ListScreenDestination) {
            ListScreen(
                trackViewModel = trackViewModel,
                navigator = destinationsNavigator,
                mediaController = mediaController
            )
        }
        composable(PlayerScreenDestination) {
            PlayerScreen(
                trackViewModel = trackViewModel,
                navigateBack = {
                    destinationsNavigator.popBackStack(route = PlayerScreenDestination, inclusive = true)
                    trackViewModel.updateTrackUiState(trackViewModel.trackUiState.value.copy(isPlayerBottomCardShown = true))
                },
                mediaController = mediaController
            )
        }
        composable(ListAlbumScreenDestination) {
            ListAlbumScreen(
                trackViewModel = trackViewModel,
                mediaController = mediaController,
                navigator = destinationsNavigator
            )
        }
        composable(AlbumScreenDestination) {
            AlbumScreen(
                trackViewModel = trackViewModel,
                mediaController = mediaController,
                navigator = destinationsNavigator
            )
        }
    }
}