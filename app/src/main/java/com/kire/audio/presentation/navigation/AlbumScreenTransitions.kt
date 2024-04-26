package com.kire.audio.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.kire.audio.presentation.screen.appDestination
import com.kire.audio.presentation.screen.destinations.PlayerScreenDestination
import com.ramcosta.composedestinations.spec.DestinationStyle

object AlbumScreenTransitions : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        return when (initialState.appDestination()) {
            PlayerScreenDestination ->
                EnterTransition.None

            else -> slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
        return when (targetState.appDestination()) {
            PlayerScreenDestination ->
                fadeOut(animationSpec = tween(5000))

            else -> slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
        return when (initialState.appDestination()) {
            PlayerScreenDestination ->
                EnterTransition.None

            else -> slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
        return when (targetState.appDestination()) {
            PlayerScreenDestination ->
                fadeOut(animationSpec = tween(5000))

            else -> slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
    }
}