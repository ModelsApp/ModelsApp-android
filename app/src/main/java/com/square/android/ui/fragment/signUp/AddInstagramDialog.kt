package com.square.android.ui.fragment.signUp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.extensions.onTextChanged
import kotlinx.android.synthetic.main.dialog_add_instagram.view.*

class AddInstagramDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(oldValue: String? = null, onAction: (accountName: String) -> Unit) {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_add_instagram, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.icClose.setOnClickListener {dialog.dismiss()}

        view.et_link.onTextChanged {
            view.btnDone.isEnabled = !TextUtils.isEmpty(it.toString())
        }

        oldValue?.let {view.et_link.setText(it.replace("@",""))}

        view.btnDone.setOnClickListener{
            onAction.invoke(view.et_link.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

}