package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.extensions.toDate
import kotlinx.android.synthetic.main.dialog_date_range.*
import java.util.*
import com.square.android.R
import com.square.android.extensions.getStringDate

class DateRangeDialog(private val context: Context) {

    var dialog: MaterialDialog? = null

    @SuppressLint("InflateParams")
    fun show(startDate: String? = null, endDate: String? = null, onSave: (startDate: String?, endDate: String?) -> Unit, onClear: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_date_range, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .build()

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog!!.calendar.setFonts(Typeface.createFromAsset(context.assets, context.getString(com.square.android.R.string.font_poppins_regular)))

        if(!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)){
            if(startDate!!.toDate().time < Calendar.getInstance().timeInMillis){
                val sDate = Calendar.getInstance().apply { timeInMillis = startDate!!.toDate().time  }
                val rangeEndDate = Calendar.getInstance().apply{ timeInMillis = startDate!!.toDate().time  }.apply { add(Calendar.YEAR, 10) }
                val eDate = Calendar.getInstance().apply { timeInMillis = endDate!!.toDate().time  }

                dialog!!.calendar.setVisibleMonthRange(sDate, rangeEndDate)
                dialog!!.calendar.setSelectedDateRange(sDate, eDate)
            } else{
                val startCalDate = Calendar.getInstance()
                val endCalDate = Calendar.getInstance().apply { add(Calendar.YEAR, 10) }

                val sDate = Calendar.getInstance().apply { timeInMillis = startDate!!.toDate().time  }
                val eDate = Calendar.getInstance().apply { timeInMillis = endDate!!.toDate().time  }

                dialog!!.calendar.setVisibleMonthRange(startCalDate, endCalDate)
                dialog!!.calendar.setSelectedDateRange(sDate, eDate)
            }
        } else{
            val sDate = Calendar.getInstance()
            val eDate = Calendar.getInstance().apply { add(Calendar.YEAR, 10) }
            dialog!!.calendar.setVisibleMonthRange(sDate, eDate)
        }

        dialog!!.btnSave.setOnClickListener {
            val calS: Calendar? = dialog!!.calendar.startDate
            val calE: Calendar? = dialog!!.calendar.endDate

            if (calS != null && calE != null) {
                onSave.invoke(calS.getStringDate(), calE.getStringDate())
            } else if (calS != null) {
                onSave.invoke(calS.getStringDate(), calS.getStringDate())
            }

            dialog!!.dismiss()
        }

        dialog!!.icClose.setOnClickListener {
            dialog!!.dismiss()
        }

        dialog!!.btnClear.setOnClickListener {
            onClear.invoke()

            dialog!!.dismiss()
        }

        dialog!!.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

}