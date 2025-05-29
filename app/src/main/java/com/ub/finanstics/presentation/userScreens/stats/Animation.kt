package com.ub.finanstics.presentation.stats

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp

@Suppress("MagicNumber")
@Composable
fun rememberAnimationPlayed(): Boolean {
    var animationPlayed by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    return animationPlayed
}

@Suppress("MagicNumber")
@Composable
fun animateChartSize(
    animationPlayed: Boolean,
    radiusOuter: Dp,
    animDuration: Int
): Float {
    return animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            easing = LinearOutSlowInEasing
        )
    ).value
}

@Suppress("MagicNumber")
@Composable
fun animateChartRotation(
    animationPlayed: Boolean,
    animDuration: Int
): Float {
    return animateFloatAsState(
        targetValue = if (animationPlayed) 270f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            easing = LinearOutSlowInEasing
        )
    ).value
}

@Suppress("MagicNumber")
@Composable
fun animateTextSize(
    animationPlayed: Boolean,
    animDuration: Int
): Float {
    return animateFloatAsState(
        targetValue = if (animationPlayed) 24f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            easing = LinearOutSlowInEasing
        )
    ).value
}

@Suppress("MagicNumber")
@Composable
fun animateDp(
    animationPlayed: Boolean,
    start: Dp,
    end: Dp,
    animDuration: Int
): Dp {
    return animateDpAsState(
        targetValue = if (animationPlayed) end else start,
        animationSpec = tween(
            durationMillis = animDuration,
            easing = LinearOutSlowInEasing
        )
    ).value
}
