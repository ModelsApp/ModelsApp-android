package com.square.android.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.square.android.ui.activity.BaseTabActivity

class PreImeConstraintLayout: ConstraintLayout{

    var baseTabAc: BaseTabActivity? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int ) : super(context, attributeSet, defStyleAttr)

    fun setAc(baseTabActivity: BaseTabActivity){
        baseTabAc = baseTabActivity
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK ) {
            if (baseTabAc != null) {
                if(baseTabAc!!.isLastFragment() && baseTabAc!!.currentFocus is EditText){
                    return super.dispatchKeyEventPreIme(event)
                } else{
                    baseTabAc!!.onBackPressed()

                    return true
                }

            } else{
                return super.dispatchKeyEventPreIme(event)
            }

        } else{
            return super.dispatchKeyEventPreIme(event)
        }
    }
}