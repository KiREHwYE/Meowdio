package com.kire.audio.presentation.navigation

import androidx.compose.runtime.Composable

import androidx.media3.session.MediaController

import androidx.navigation.NavHostController

import com.kire.audio.presentation.screen.AlbumScreen
import com.kire.audio.presentation.screen.ListScreen
import com.kire.audio.presentation.screen.NavGraphs
import com.kire.audio.presentation.screen.PlayerScreen
import com.kire.audio.presentation.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.screen.destinations.ListScreenDestination
import com.kire.audio.presentation.screen.destinations.PlayerScreenDestination

import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.spec.NavHostEngine

@Composable
fun NavigationUI(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navController: NavHostController,
    navHostEngine: NavHostEngine
){

    DestinationsNavHost(navGraph = NavGraphs.root, engine = navHostEngine, navController = navController) {
        composable(ListScreenDestination) {
            ListScreen(
                viewModel = trackViewModel,
                navigator = destinationsNavigator,
                mediaController = mediaController
            )
        }
        composable(PlayerScreenDestination) {
            PlayerScreen(
                viewModel = trackViewModel,
                navigateBack = {
                    destinationsNavigator.popBackStack(route = PlayerScreenDestination.route, inclusive = true)
                    trackViewModel.changeTrackUiState(trackViewModel.trackUiState.value.copy(isPlayerBottomCardShown = true))
                },
                mediaController = mediaController
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