package com.square.android.ui.activity.place

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Day
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_day.*
import kotlinx.android.synthetic.main.item_day_adjustable.*

private const val TYPE_DAY = R.layout.item_day
private const val TYPE_DAY_ADJUSTABLE = R.layout.item_day_adjustable

class DaysAdapter(data: List<Day>,
                   private val handler: Handler?, private val itemSize: Int? = null) : BaseAdapter<Day, DaysAdapter.DayHolder>(data) {

    var selectedItemPosition: Int? = null
    var selectedMonth: Int = 0

    override fun getLayoutId(viewType: Int) = viewType

    override fun instantiateHolder(view: View): DayHolder = DayHolder(view, handler, itemSize)

    override fun getViewType(position: Int): Int {
        return if(itemSize == null) TYPE_DAY else TYPE_DAY_ADJUSTABLE
    }

    override fun getItemCount() = data.size

    override fun bindHolder(holder: DayHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition, selectedMonth)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: DayHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is SelectedPayload }
                .forEach {
                    if(itemSize == null){
                        holder.bindSelected(data[position], selectedItemPosition, selectedMonth)
                    }
                    else{
                        holder.bindAdjustableSelected(data[position], selectedItemPosition, selectedMonth)
                    }
                }
    }

    fun setSelectedItem(position: Int?) {
        if (position == null) return

        val previousPosition = selectedItemPosition
        selectedItemPosition = position

        previousPosition?.let { notifyItemChanged(it, SelectedPayload) }

        notifyItemChanged(position)
    }

    class DayHolder(containerView: View, var handler: Handler?,var itemSize: Int? ) : BaseHolder<Day>(containerView) {

        override fun bind(item: Day, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int
            val selectedMonth = extras[1] as Int

            if(itemSize == null){
                bindDay(item, selectedPosition, selectedMonth)
            } else{
                bindDayAdjustable(item, selectedPosition, selectedMonth)
            }
        }

        private fun bindDay(item: Day, selectedPosition: Int?, selectedMonth: Int?) {
            bindSelected(item, selectedPosition, selectedMonth)

            itemDayName.text = item.dayName

            itemDayValue.text = item.dayValue.toString()

            itemDayValue.checkMarkDrawable = null

            itemDayValueContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        private fun bindDayAdjustable(item: Day, selectedPosition: Int?, selectedMonth: Int?) {
            bindAdjustableSelected(item, selectedPosition, selectedMonth)

            itemDayAdjustableName.text = item.dayName

            itemDayAdjustableValue.text = item.dayValue.toString()

            itemDayAdjustableValue.checkMarkDrawable = null

            itemDayAdjustableValueContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            itemSize?.let { size -> itemDayAdjustableValueContainer.layoutParams.also {
                it.width = size
                it.height = size
            } }
        }

        fun bindSelected(item: Day,selectedPosition: Int?, selectedMonth: Int?) {
            itemDayValue.isChecked = (selectedPosition == adapterPosition)
            itemDayValue.isEnabled = true
        }

        fun bindAdjustableSelected(item: Day,selectedPosition: Int?, selectedMonth: Int?) {
            itemDayAdjustableValue.isChecked = (selectedPosition == adapterPosition)
            itemDayAdjustableValue.isEnabled = true
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}