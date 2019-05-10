package com.square.android.ui.fragment.offer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImage
import kotlinx.android.synthetic.main.offer_dialog_info.view.*
import android.graphics.Color
import com.square.android.data.pojo.Place

class OfferDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(offer: OfferInfo, place: Place?) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.offer_dialog_info, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.offerDialogImg.loadImage(offer.photo)
        view.offerDialogPlace.text = place?.name
        view.offerDialogCredits.text = context.getString(R.string.credits_format_lowercase, offer.price)
        view.offerDialogAddress.text = place?.address
        view.offerDialogComponents.text = offer.compositionAsStr()
        view.offerDialogName.text = offer.name

        dialog.show()
    }
}