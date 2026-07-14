package com.example.kendaraanbp1.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A [BottomSheetDialogFragment] that always opens in the fully-expanded state.
 *
 * These form sheets place their primary action (the "Simpan"/save button) in a
 * footer *below* the scrollable content. When the sheet opens in its default
 * half-expanded/collapsed state, that footer is rendered below the bottom edge
 * of the screen, so the save button is unreachable and the form appears to do
 * nothing. Forcing the expanded state (and skipping the collapsed state) keeps
 * the footer on screen so the user can actually submit.
 */
open class ExpandedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val sheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) ?: return@setOnShowListener
            BottomSheetBehavior.from(sheet).apply {
                skipCollapsed = true
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }
}
