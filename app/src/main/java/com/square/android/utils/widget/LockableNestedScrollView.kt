package com.square.android.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class LockableScrollView: NestedScrollView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    var isScrollable = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (ev.action) {
                MotionEvent.ACTION_DOWN  ->{
                    if (isScrollable){
                        return super.onTouchEvent(ev);
                    }
                    return isScrollable
                }
                else -> {
                    return super.onTouchEvent(ev)
                }
            }
        } ?: return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!isScrollable) return false
        else return super.onInterceptTouchEvent(ev);
    }
}