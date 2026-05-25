package com.apulum.tenis.ui.theme

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/** Padding sub bara de status; fără inset-uri orizontale asimetrice de la sistem. */
@Composable
fun Modifier.apulumStatusBarPadding(): Modifier =
    windowInsetsPadding(WindowInsets.statusBars)

/**
 * Top offset for overlays (toasts) on edge-to-edge screens, including display cutout.
 * Falls back when inset values are not yet available to the composition.
 */
@Composable
fun Modifier.apulumTopSafeAreaPadding(extra: Dp = 12.dp): Modifier {
    val density = LocalDensity.current
    val topInsetPx = max(
        WindowInsets.statusBars.getTop(density),
        WindowInsets.displayCutout.getTop(density)
    )
    val topPadding = if (topInsetPx > 0) {
        with(density) { topInsetPx.toDp() } + extra
    } else {
        48.dp + extra
    }
    return padding(top = topPadding)
}
