package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.local.LocalDataManager
import kotlinx.android.synthetic.main.dialog_hidden_mood.*

class DialogHiddenMood(private val context: Context) {

    var dialog: MaterialDialog? = null

    @SuppressLint("InflateParams")
    fun show(isActive: Boolean, localDataManager: LocalDataManager, onAction: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_hidden_mood, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog!!.icClose.setOnClickListener {
            checkDontShow(localDataManager)
            dialog?.dismiss()
        }

        dialog!!.btnSure.setOnClickListener {
            checkDontShow(localDataManager)
            onAction.invoke()
            dialog!!.dismiss()
        }

        dialog!!.moodTv.text = if(isActive) context.getString(R.string.hidden_mood_off) else context.getString(R.string.hidden_mood_on)
        dialog!!.moodDesc.text = if(isActive) context.getString(R.string.hidden_mood_off_text) else context.getString(R.string.hidden_mood_on_text)

        if(!localDataManager.getHiddenMoodDontAsk()){
            dialog!!.show()
        } else{
            onAction.invoke()
            dialog = null
        }
    }

    fun checkDontShow(localDataManager: LocalDataManager){
        if(dialog!!.moodCb.isChecked){
            localDataManager.setHiddenMoodDontAsk(true)
        }
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}