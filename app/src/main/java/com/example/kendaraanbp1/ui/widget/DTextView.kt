package com.example.kendaraanbp1.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import com.example.kendaraanbp1.R
import kotlin.math.roundToInt

class DTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.DTextView, defStyleAttr, 0)
            val lineHeightSp = a.getFloat(R.styleable.DTextView_lineHeightSp, 0f)
            if (lineHeightSp > 0f) {
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    lineHeightSp,
                    resources.displayMetrics,
                ).roundToInt()
                TextViewCompat.setLineHeight(this, px)
            }
            a.recycle()
        }
    }
}
