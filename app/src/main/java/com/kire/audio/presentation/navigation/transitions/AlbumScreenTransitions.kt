package com.kire.audio.presentation.navigation.transitions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.kire.audio.presentation.ui.screen.destinations.PlayerScreenDestination
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.utils.route

object AlbumScreenTransitions : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        return if (initialState.route() == PlayerScreenDestination)
            EnterTransition.None
        else
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)) +
                    fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
        return if (targetState.route() == PlayerScreenDestination)
            fadeOut(animationSpec = tween(300, easing = LinearOutSlowInEasing))
        else
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)) +
                    fadeOut(animationSpec = tween(300, easing = LinearOutSlowInEasing))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
        return if (initialState.route() == PlayerScreenDestination)
            EnterTransition.None
        else
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)) +
                    fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
        return if (targetState.route() == PlayerScreenDestination)
            fadeOut(animationSpec = tween(300, easing = LinearOutSlowInEasing))
        else
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)) +
                    fadeOut(animationSpec = tween(300, easing = LinearOutSlowInEasing))
    }
}