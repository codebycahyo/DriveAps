package com.example.kendaraanbp1.ui.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

/**
 * Applies the system bottom inset (navigation bar / gesture bar) as extra bottom padding,
 * on top of [extraPaddingPx], so edge-to-edge content isn't obscured by system bars.
 */
fun View.applyBottomSystemBarPadding(extraPaddingPx: Int = paddingBottom) {
    val basePaddingStart = paddingStart
    val basePaddingTop = paddingTop
    val basePaddingEnd = paddingEnd
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(basePaddingStart, basePaddingTop, basePaddingEnd, extraPaddingPx + systemBars.bottom)
        insets
    }
}

/**
 * Applies the system bottom inset as extra bottom *margin* (on top of the margin already set in
 * XML). Use for a floating dock so the inset opens a gap beneath the bar instead of inflating the
 * bar's own height, which would push its centered content off-centre.
 */
fun View.applyBottomSystemBarMargin() {
    val baseMarginBottom = (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = baseMarginBottom + systemBars.bottom
        }
        insets
    }
}

/** Applies the system top inset (status bar) as extra top padding, on top of [extraPaddingPx]. */
fun View.applyTopSystemBarPadding(extraPaddingPx: Int = paddingTop) {
    val basePaddingStart = paddingStart
    val basePaddingBottom = paddingBottom
    val basePaddingEnd = paddingEnd
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(basePaddingStart, extraPaddingPx + systemBars.top, basePaddingEnd, basePaddingBottom)
        insets
    }
}
