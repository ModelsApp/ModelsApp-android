package com.square.android.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.square.android.R
import kotlinx.android.synthetic.main.spinner_dropdown_checkmark_icon.view.*
import kotlinx.android.synthetic.main.spinner_dropdown_radio_button.view.*
import kotlinx.android.synthetic.main.spinner_simple_item.view.*
import android.app.Activity
import android.util.DisplayMetrics
import androidx.annotation.DimenRes

const val DROPDOWN_ITEM_TYPE_CHECKMARK = 0
const val DROPDOWN_ITEM_TYPE_RADIO_BUTTON = 1

class SimpleSpinnerAdapter(val activity: Activity, var dropdownItemType: Int, var items: List<String>, @DimenRes val startPadding: Int = R.dimen.v_24dp, @DimenRes val endPadding: Int = R.dimen.v_24dp, val halfSize: Boolean = false) : ArrayAdapter<String>(activity, 0, items) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {

        return getCustomView(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = LayoutInflater.from(context).inflate(R.layout.spinner_simple_item, null, false)
        v.tv.text = items[position]

        return v
    }

    private fun getCustomView(position: Int): View {
        val inflater = LayoutInflater.from(context)

        val view =  when(dropdownItemType){
            DROPDOWN_ITEM_TYPE_CHECKMARK -> {
                val view = inflater.inflate(R.layout.spinner_dropdown_checkmark_icon, null, false)
                view.checkableTv.text = items[position]

                view
            }

            DROPDOWN_ITEM_TYPE_RADIO_BUTTON -> {
                val view = inflater.inflate(R.layout.spinner_dropdown_radio_button, null, false)
                view.itemTv.text = items[position]

                view
            }

            else -> View(context)
        }

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        if(halfSize){
            view.minimumWidth = Math.round((displayMetrics.widthPixels/2) - ((activity.resources.getDimension(startPadding)/2) + activity.resources.getDimension(endPadding)))
        } else{
            view.minimumWidth = Math.round(displayMetrics.widthPixels - (activity.resources.getDimension(startPadding) + activity.resources.getDimension(endPadding)))
        }

        return view
    }

}