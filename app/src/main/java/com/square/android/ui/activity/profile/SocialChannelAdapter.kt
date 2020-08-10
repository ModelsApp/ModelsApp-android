package com.square.android.ui.activity.profile

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.UserSocialChannel
import com.square.android.extensions.drawableFromRes
import com.square.android.extensions.getColorFromRes
import com.square.android.extensions.setVisible
import com.square.android.extensions.textIsEmpty
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_social_channel.*

class SocialChannelsAdapter(data: List<UserSocialChannel>, private val handler: Handler?) : BaseAdapter<UserSocialChannel, SocialChannelsAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_social_channel

    override fun getItemCount() = data.size

    override fun bindHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        onBindViewHolder(holder, position)
        return
    }

    override fun instantiateHolder(view: View): ViewHolder = ViewHolder(view, handler)

    class ViewHolder(containerView: View,
                     var handler: Handler?) : BaseHolder<UserSocialChannel>(containerView) {

        override fun bind(item: UserSocialChannel, vararg extras: Any? ) {

            itemContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            val context = itemImg.context

            if(adapterPosition != 0){
                itemDivider.setVisible(true)
            } else{
                itemDivider.setVisible(false)
            }

            itemImg.drawableFromRes(when(item.name){
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

            itemLabel.text = item.name.replace("TicTok", "TikTok")

            //TODO why not just leave account name in UserSocialChannel?
            val accountName = item.userChannel?.accountName ?: ""

            itemTv.text = if(accountName.textIsEmpty()) "" else "@${accountName}"

            if(!accountName.textIsEmpty()){
                itemTv.setVisible(true)

                val d = context.getDrawable(R.drawable.checkmark)
                d.setTint(context.getColorFromRes(R.color.text_gray))

                itemIc.setImageDrawable(d)
            }else{
                itemTv.setVisible(false)

                val d = context.getDrawable(R.drawable.add)
                d.setTint(context.getColorFromRes(android.R.color.black))

                itemIc.setImageDrawable(d)
            }
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

}