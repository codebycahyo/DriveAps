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

/**
 * Bottom padding that follows the keyboard (IME) OR the navigation bar, whichever is taller.
 * Because the app is edge-to-edge (decorFitsSystemWindows = false), the window does NOT resize
 * when the keyboard opens; this makes a scroll container leave room above the keyboard so its
 * fields can scroll into view instead of being covered.
 */
fun View.applyImeBottomPadding(extraPaddingPx: Int = paddingBottom) {
    val baseStart = paddingStart
    val baseTop = paddingTop
    val baseEnd = paddingEnd
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        view.setPadding(baseStart, baseTop, baseEnd, extraPaddingPx + maxOf(bars.bottom, ime.bottom))
        insets
    }
}

/**
 * Single listener that applies BOTH the status-bar top inset and the keyboard/nav-bar bottom inset.
 * Use on a full-screen scroll container (e.g. login/register) so content sits below the status bar
 * and scrolls clear of the keyboard. (A View can only hold one insets listener, so top and bottom
 * must be handled together here.)
 */
fun View.applyTopBarAndImeInsets() {
    val baseStart = paddingStart
    val baseTop = paddingTop
    val baseEnd = paddingEnd
    val baseBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        view.setPadding(baseStart, baseTop + bars.top, baseEnd, baseBottom + maxOf(bars.bottom, ime.bottom))
        insets
    }
}
