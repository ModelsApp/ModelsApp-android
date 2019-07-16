package com.square.android.ui.fragment.campaignNotApproved

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.square.android.R
import com.square.android.data.pojo.Campaign
import com.square.android.extensions.loadImage
import com.square.android.extensions.setTextColorRes
import org.jetbrains.anko.dimen

class RewardsAdapter (var rewards: List<Campaign.Reward>, private val handler: Handler?, var coloredText: Boolean = false) : RecyclerView.Adapter<RewardsAdapter.ViewHolder>(){

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView = v.findViewById(R.id.rewardImage) as ImageView
        var name: TextView = v.findViewById(R.id.rewardName)
        var container: ViewGroup = v.findViewById(R.id.rewardContainer) as ViewGroup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_reward, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (rewards[position].mainImage != null) {
            holder.image.visibility = View.VISIBLE
            holder.image.loadImage(rewards[position].mainImage!!, roundedCornersRadiusPx = holder.image.context!!.dimen(R.dimen.value_4dp))
        } else {
            holder.image.visibility = View.GONE
        }


        holder.name.text = rewards[position].description
        if (coloredText) holder.name.setTextColorRes(R.color.nice_pink)

        holder.container.setOnClickListener { handler?.itemClicked(holder.adapterPosition)}
    }

    override fun getItemCount(): Int {
        return if(rewards.isEmpty()) 0 else rewards.size
    }

    interface Handler {
        fun itemClicked(index: Int)
    }

}