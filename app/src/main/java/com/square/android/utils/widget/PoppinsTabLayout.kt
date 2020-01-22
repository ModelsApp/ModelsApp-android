package com.square.android.utils.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.square.android.R

class PoppinsTabLayout: TabLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun setupWithViewPager(viewPager: ViewPager?) {
        super.setupWithViewPager(viewPager)

        val viewGroup: ViewGroup = this.getChildAt(0) as ViewGroup
        val tabsCount: Int = viewGroup.childCount
        for (j in 0 until tabsCount) {
            val viewGroupTab: ViewGroup = viewGroup.getChildAt(j) as ViewGroup
            val tabChildCount: Int = viewGroupTab.childCount
            for (i in 0 until tabChildCount) {
                val tabViewChild: View = viewGroupTab.getChildAt(i) as View
                if (tabViewChild is TextView) {
                    val typeface = Typeface.createFromAsset(context.assets, context.getString(R.string.font_poppins_medium))
                    tabViewChild.typeface = typeface
                    tabViewChild.isAllCaps = false
                }
            }
        }
    }
}