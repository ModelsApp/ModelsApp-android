package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.App
import com.square.android.R
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.extensions.loadImage
import com.square.android.ui.fragment.agenda.schedule.ScheduleAvailabilityAdapter
import kotlinx.android.synthetic.main.dialog_schedule_pending_action.view.*

class SchedulePendingActionDialog(private val context: Context) {

    var selectedItem: String? = null
    var adapter: ScheduleAvailabilityAdapter? = null

    @SuppressLint("InflateParams")
    fun show(redemptionInfo: RedemptionInfo, onAction: (optionSelected: String) -> Unit, onDismiss: () -> Unit ) {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_schedule_pending_action, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val items: MutableList<String> = redemptionInfo.availabilityItems.toMutableList()
        items.add(App.getString(R.string.i_wont_go_anymore))

        adapter = ScheduleAvailabilityAdapter(items, 0, object: ScheduleAvailabilityAdapter.Handler{
            override fun itemClicked(position: Int) {
                adapter!!.setSelectedItem(position)

                selectedItem = items[position]

                if(position == items.size -1){
                    view.button.text = context.getString(R.string.cancel_reservation)

                } else{
                    view.button.text = context.getString(R.string.confirm_reservation)
                }
            }
        })

        view.button.text = context.getString(R.string.confirm_reservation)
        selectedItem = items[0]
        adapter!!.setSelectedItem(0)

        //TODO rv height is wrong in dialog !!!
        view.rv.adapter = adapter
        view.rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)

        view.icClose.setOnClickListener {
            dialog.dismiss()
            onDismiss.invoke()
        }

        view.button.setOnClickListener {
            selectedItem?.let {
                onDismiss.invoke()
                onAction.invoke(it)
                dialog.dismiss()
            }
        }

        //TODO
        // change topIc AND bottomIc

        view.image.loadImage(redemptionInfo.place.mainImage, roundedCornersRadiusPx = Math.round(context.resources.getDimension(R.dimen.v_16dp)))

        view.title.text = redemptionInfo.place.name

        view.topText.text = redemptionInfo.topList.joinToString(separator = ", ")
        view.bottomText.text = redemptionInfo.bottomList.joinToString(separator = ", ")

        dialog.show()
    }

}