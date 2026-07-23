package com.example.kendaraanbp1.ui.util

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Starts a subtle, infinitely-looping vertical "float" on this view so the onboarding hero
 * feels alive and premium. Purely decorative — returns the [ObjectAnimator] so the caller can
 * cancel it in onDestroyView to avoid leaking the view.
 */
fun View.startFloatingAnimation(distanceDp: Float = 12f, durationMs: Long = 2400L): ObjectAnimator {
    val dy = distanceDp * resources.displayMetrics.density
    return ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f, -dy).apply {
        duration = durationMs
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        interpolator = AccelerateDecelerateInterpolator()
        start()
    }
}
