package com.square.android.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.square.android.R
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatEditText

class ErrorEditText(context: Context, attributeSet: AttributeSet): AppCompatEditText(context, attributeSet){

    var errorShowing: Boolean = false
    var normalHint: String = ""
    var normalHintTextColor: Int = 0
    var normalTextSize: Float = 0f

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        if(errorShowing){
            setTextSize(TypedValue.COMPLEX_UNIT_PX, normalTextSize)
            errorShowing = false
            setHintTextColor(ContextCompat.getColor(context, normalHintTextColor))
            hint = normalHint
        }
    }

    fun showCustomError(errorText: String, errorTextColor: Int = R.color.nice_red, normalText: String = if(hint == null) "" else hint.toString(), normalTextColor: Int = R.color.grey_dark){
        normalTextSize = textSize

        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.error_text_size))
        normalHint = normalText
        normalHintTextColor = normalTextColor
        setHintTextColor(ContextCompat.getColor(context, errorTextColor))
        hint = errorText
        errorShowing = true
    }

    fun setCustomIconError(error: String){
        setError(error, context.getDrawable(R.drawable.ic_info_error))
    }

    override fun setError(error: CharSequence?, icon: Drawable) {
        icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight);

        super.setError(error, icon)
//        setCompoundDrawables(null, null, icon, null)
//
//
//
//        if (error == null) {
//
//        } else if (error.toString() == "")
//            setCompoundDrawables(null, null, icon, null)
//        else

    }

}
