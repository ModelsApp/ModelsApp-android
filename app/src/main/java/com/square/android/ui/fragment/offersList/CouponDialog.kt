package com.square.android.ui.fragment.offersList

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.newPojo.OfferInfo
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.extensions.loadImage
import kotlinx.android.synthetic.main.dialog_coupon.view.*
import java.util.*

class CouponDialog(private val context: Context, var cancelable: Boolean = true) {

    lateinit var dialog: MaterialDialog

    @SuppressLint("InflateParams")
    fun show(redemptionFull: RedemptionFull, userData: Profile.User, offerInfo: OfferInfo, cancelHandler: Handler? = null) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_coupon, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(cancelable)
                .canceledOnTouchOutside(true)
                .cancelListener { DialogInterface.OnCancelListener {cancelHandler?.dialogCancelled()} }
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)))

        view.dateTv.text = redemptionFull.redemption.date
        view.timeTv.text = redemptionFull.redemption.startTime +" - " +redemptionFull.redemption.endTime
        view.placeTv.text = redemptionFull.redemption.place.name
        view.addressTv.text = redemptionFull.redemption.place.address




        //TODO no offerInfo in redemptionFull
//        val numberList: MutableList<Int> = mutableListOf()
//        val names = redemptionFull.offer.compositionAsStr()
//
//        val p = Pattern.compile("\\d+")
//        val m = p.matcher(redemptionFull.offer.compositionAsString())
//        while (m.find()) {
//            numberList.add(m.group().toInt())
//        }
//        view.offerNames.text = names
//        view.offerNumbers.text = numberList.joinToString(separator = "\n")

        //TODO what should be here?
//      view.notesTv.text =

        view.userImg.loadImage(url = (userData.mainImage ?: userData.photo)?: "", roundedCornersRadiusPx = 10)
        view.userName.text = userData.name +" "+ userData.surname.get(0)+"."

        //TODO offer img and name

        val calendar = Calendar.getInstance()
        val hour: String = if(calendar.get(Calendar.HOUR_OF_DAY) < 10) "0"+calendar.get(Calendar.HOUR_OF_DAY) else calendar.get(Calendar.HOUR_OF_DAY).toString()
        val minute: String = if(calendar.get(Calendar.MINUTE) < 10) "0"+calendar.get(Calendar.MINUTE) else calendar.get(Calendar.MINUTE).toString()

        view.checkedAt.text = "$hour:$minute"

        dialog.show()
    }

    fun close(){
        dialog.cancel()
    }

    interface Handler {
        fun dialogCancelled()
    }

}