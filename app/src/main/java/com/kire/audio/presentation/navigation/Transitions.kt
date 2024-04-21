package com.kire.audio.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

object PlayerScreenTransitions : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        return slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)) +
                fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
        return slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)) +
                fadeOut(animationSpec = tween(400, easing = FastOutSlowInEasing))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
        return slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing))+
                fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
        return slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)) +
                fadeOut(animationSpec = tween(400, easing = FastOutSlowInEasing))
    }
}

object ListScreenTransitions : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        return EnterTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
        return fadeOut(animationSpec = tween(5000))
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
        return EnterTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
        return fadeOut(animationSpec = tween(5000))
    }
}
