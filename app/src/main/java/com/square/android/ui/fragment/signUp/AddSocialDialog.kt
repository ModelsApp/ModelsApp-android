package com.square.android.ui.fragment.signUp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.extensions.drawableFromRes
import com.square.android.extensions.onTextChanged
import kotlinx.android.synthetic.main.dialog_add_social.view.*

enum class SocialType{
    Instagram,
    Google,
    VK,
    TikTok,
    Pinterest,
    Yelp,
    Youtube,
    Snapchat,
    Blogger,
    TripAdvisor,
    Modelscom
}

class AddSocialDialog(private val context: Context) {
    @SuppressLint("InflateParams")
    fun show(type: SocialType, oldValue: String? = null, onAction: (accountName: String) -> Unit) {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_add_social, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.icClose.setOnClickListener {dialog.dismiss()}

        view.et_link.onTextChanged {
            view.btnDone.isEnabled = !TextUtils.isEmpty(it.toString())
        }

        view.icon.drawableFromRes(when(type){
            SocialType.Instagram -> R.drawable.instagram_logo
            SocialType.Google -> R.drawable.r_address
            SocialType.VK -> R.drawable.r_address
            SocialType.TikTok -> R.drawable.r_address
            SocialType.Pinterest -> R.drawable.r_address
            SocialType.Yelp -> R.drawable.r_address
            SocialType.Youtube -> R.drawable.r_address
            SocialType.Snapchat -> R.drawable.r_address
            SocialType.Blogger -> R.drawable.r_address
            SocialType.TripAdvisor -> R.drawable.r_address
            SocialType.Modelscom -> R.drawable.r_address
        })

        val socialName = when(type){
            SocialType.Instagram -> "Instagram"
            SocialType.Google -> "Google"
            SocialType.VK -> "VK"
            SocialType.TikTok -> "TikTok"
            SocialType.Pinterest -> "Pinterest"
            SocialType.Yelp -> "Yelp"
            SocialType.Youtube -> "Youtube"
            SocialType.Snapchat -> "Snapchat"
            SocialType.Blogger -> "Blogger"
            SocialType.TripAdvisor -> "TripAdvisor"
            SocialType.Modelscom -> "Models.com"
        }

        view.label.text = context.getString(R.string.add_social_format, socialName)

        oldValue?.let {view.et_link.setText(it.replace("@",""))}

        view.btnDone.setOnClickListener{
            onAction.invoke(view.et_link.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

}