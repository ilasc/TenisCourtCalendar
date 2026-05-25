package com.apulum.tenis.ui.components

import androidx.annotation.DrawableRes
import com.apulum.tenis.R

@DrawableRes
fun courtDrawableRes(courtId: String): Int = when (courtId) {
    "exterior" -> R.drawable.court_outdoor
    "acoperit" -> R.drawable.court_indoor
    else -> R.drawable.court_outdoor
}
