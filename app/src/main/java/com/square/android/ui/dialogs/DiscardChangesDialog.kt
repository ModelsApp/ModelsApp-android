package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.ui.activity.BaseTabActivity
import kotlinx.android.synthetic.main.dialog_discard_changes.*

class DiscardChangesDialog(private val baseTabActivity: BaseTabActivity) {

    var dialog: MaterialDialog? = null

    @SuppressLint("InflateParams")
    fun show() {
        val inflater = LayoutInflater.from(baseTabActivity)
        val view = inflater.inflate(R.layout.dialog_discard_changes, null, false)

        dialog = MaterialDialog.Builder(baseTabActivity)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialog!!.discard.setOnClickListener {
            baseTabActivity.setIsEditing(false)
            baseTabActivity.onBackPressed()
            dialog!!.dismiss()
        }

        dialog!!.cancel.setOnClickListener { dialog!!.dismiss() }

        dialog!!.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}