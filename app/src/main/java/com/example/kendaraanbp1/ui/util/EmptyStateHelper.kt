package com.example.kendaraanbp1.ui.util

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.kendaraanbp1.databinding.LayoutEmptyStateBinding

/** Configures the icon/title/subtitle of an included [layout_empty_state]. */
fun LayoutEmptyStateBinding.bind(@DrawableRes iconRes: Int, @StringRes titleRes: Int, @StringRes subtitleRes: Int) {
    emptyStateIcon.setImageResource(iconRes)
    emptyStateTitle.setText(titleRes)
    emptyStateSubtitle.setText(subtitleRes)
}

/** Shows this empty state and hides [listView], or the reverse, based on [isEmpty]. */
fun LayoutEmptyStateBinding.setEmptyState(isEmpty: Boolean, listView: View) {
    emptyStateRoot.visibility = if (isEmpty) View.VISIBLE else View.GONE
    listView.visibility = if (isEmpty) View.GONE else View.VISIBLE
}
