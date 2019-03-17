package com.b2blogist.vkcommviewer.customViews

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView


class LayoutedTextView : TextView {

    private var mOnLayoutListener: OnLayoutListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    interface OnLayoutListener {
        fun onLayouted(view: LayoutedTextView)
    }

    fun setOnLayoutListener(listener: OnLayoutListener) {
        mOnLayoutListener = listener
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mOnLayoutListener?.onLayouted(this)
    }

}