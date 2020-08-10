package com.square.android.ui.activity.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.UserSocialChannel
import com.square.android.extensions.drawableFromRes
import kotlinx.android.synthetic.main.dialog_social_channel.view.*

class SocialChannelDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(item: UserSocialChannel, onAction: (newName: String) -> Unit) {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_social_channel, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.icClose.setOnClickListener { dialog.dismiss() }

        view.icon.drawableFromRes(when(item.name){
            //TODO change icons
            "Instagram" -> R.drawable.instagram_logo
            "Facebook" -> R.drawable.facebook_logo
            "Google" -> R.drawable.google_logo
            "VK" -> R.drawable.r_address
            "TicTok", "TikTok" -> R.drawable.r_address
            "Twitter" -> R.drawable.r_address
            "Snapchat" -> R.drawable.r_address
            "Pinterest" -> R.drawable.r_address
            "Yelp" -> R.drawable.r_address
            "Youtube" -> R.drawable.r_address
            "Zomato" -> R.drawable.r_address
            "Blogger" -> R.drawable.r_address
            "TripAdvisor" -> R.drawable.r_address
            "Models.com" -> R.drawable.r_address
            else -> 0
        })

        view.label.text = context.getString(R.string.add_social_format, item.name.replace("TicTok", "TikTok"))

        //TODO why not just leave account name in UserSocialChannel?
        val accountName = item.userChannel?.accountName ?: ""

        view.et_link.setText(accountName)

        view.btnDone.setOnClickListener{
            onAction.invoke(view.et_link.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

}