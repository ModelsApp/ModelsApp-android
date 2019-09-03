package com.square.android.ui.fragment.offersList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImage
import kotlinx.android.synthetic.main.select_offer_dialog.view.*
import android.graphics.Color

class SelectOfferDialog(private val context: Context) {
    var dialog: MaterialDialog? = null

    @SuppressLint("InflateParams")
    fun show(offer: OfferInfo, onAction: () -> Unit) {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.select_offer_dialog, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.offerDialogImg.loadImage((offer.mainImage ?: offer.photo) ?: "")

        view.offerDialogSubmit.setOnClickListener { cancel()
            onAction.invoke() }

        view.offerDialogName.text = offer.name
        view.offerDialogCredits.text = offer.price.toString()
        view.offerDialogComponents.text = offer.compositionAsStr()

        dialog!!.show()
    }

    fun cancel(){
        dialog?.cancel()
    }
}