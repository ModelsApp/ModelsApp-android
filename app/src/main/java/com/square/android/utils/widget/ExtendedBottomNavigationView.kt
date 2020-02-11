package com.square.android.utils.widget

import android.annotation.SuppressLint
import android.graphics.Typeface
import com.google.android.material.internal.BaselineLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.square.android.R
import java.lang.reflect.Field

class ExtendedBottomNavigationView(context: Context, attrs: AttributeSet) : BottomNavigationView(context, attrs) {
    private var fontFace: Typeface? = null

    @SuppressLint("RestrictedApi")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val bottomMenu = getChildAt(0) as ViewGroup
        val bottomMenuChildCount = bottomMenu.childCount
        var item: BottomNavigationItemView
        var itemTitle: View
        val shiftingMode: Field

        if (fontFace == null) {
            fontFace = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_poppins_medium))
        }
        try {
            //if you want to disable shiftingMode:
            //shiftingMode is a private member variable so you have to get access to it like this:
            shiftingMode = bottomMenu.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.setAccessible(true)
            shiftingMode.setBoolean(bottomMenu, false)
            shiftingMode.setAccessible(false)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        for (i in 0 until bottomMenuChildCount) {
            item = bottomMenu.getChildAt(i) as BottomNavigationItemView
            //this shows all titles of items
            item.setChecked(true)
            //every BottomNavigationItemView has two children, first is an itemIcon and second is an itemTitle
            itemTitle = item.getChildAt(1)
            //every itemTitle has two children, first is a smallLabel and second is a largeLabel. these two are type of AppCompatTextView
            ((itemTitle as BaselineLayout).getChildAt(0) as TextView).setTypeface(fontFace)
            ((itemTitle as BaselineLayout).getChildAt(0) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_13))
            ((itemTitle as BaselineLayout).getChildAt(1) as TextView).setTypeface(fontFace)
            ((itemTitle as BaselineLayout).getChildAt(1) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_13))
        }
    }
}