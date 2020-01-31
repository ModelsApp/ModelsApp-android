package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import kotlinx.android.synthetic.main.dialog_log_out.*

class LogOutDialog(private val context: Context) {

    var dialog: MaterialDialog? = null

    @SuppressLint("InflateParams")
    fun show(onAction: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_log_out, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialog!!.logOut.setOnClickListener { onAction.invoke()
            dialog?.dismiss()
        }

        dialog!!.cancel.setOnClickListener { dialog!!.dismiss() }

        dialog!!.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}