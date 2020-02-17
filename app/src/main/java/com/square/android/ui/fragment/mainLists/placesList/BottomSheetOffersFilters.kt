package com.square.android.ui.fragment.mainLists.placesList

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.data.pojo.City
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.bottom_sheet_filters_offers.*
import android.app.Dialog
import android.os.Build
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import android.graphics.Color
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.square.android.R
import com.square.android.extensions.toHourInt
import com.square.android.extensions.toHourStr
import com.square.android.ui.fragment.mainLists.filters.BaseBottomSheetFilters
import com.square.android.ui.fragment.mainLists.filters.BaseFilter
import com.square.android.ui.fragment.mainLists.filters.PlacesFilter
import java.util.*

class BottomSheetOffersFilters(var placeFilter: PlacesFilter, private val handler: Handler, var mCancelable: Boolean = true ): BaseBottomSheetFilters(mCancelable)
//        , CitiesAdapter.Handler
{

//    private var adapter: CitiesAdapter? = null

    override var layoutRes: Int = R.layout.bottom_sheet_filters_offers

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reservationStart.text = placeFilter.timeSlot.start.toHourStr()
        reservationEnd.text = placeFilter.timeSlot.end.toHourStr()
        reservationTimeslotSeekBar.setProgress(placeFilter.timeSlot.start.toFloat(), placeFilter.timeSlot.end.toFloat())

        reservationTimeslotSeekBar.setOnRangeChangedListener(object: OnRangeChangedListener{
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) { }

            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                reservationStart.text = leftValue.toInt().toHourStr()
                reservationEnd.text = rightValue.toInt().toHourStr()
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) { }
        })

        btnApply.setOnClickListener {
            handler.filtersApplyClicked(placeFilter.apply {
                timeSlot.start = reservationStart.text.toString().toHourInt()
                timeSlot.end = reservationEnd.text.toString().toHourInt()
            })

            dialog.dismiss()
        }

        clearLl.setOnClickListener {
            handler.filtersClearClicked()
            dialog.dismiss()
        }

//        adapter = CitiesAdapter(cities, this)
//        citiesRv.adapter = adapter
//        citiesRv.layoutManager = LinearLayoutManager(citiesRv.context, RecyclerView.HORIZONTAL,false)
//        citiesRv.addItemDecoration(MarginItemDecorator(citiesRv.context.resources.getDimension(R.dimen.v_8dp).toInt(), false))
//
//        selectedCity?.let {
//            val city: City? = cities.firstOrNull{it.name == selectedCity!!.name}
//            city?.let {
//                selectedCityIndex = cities.indexOf(it)
//                adapter?.setSelectedItem(selectedCityIndex)
//            }
//        }
    }

//    override fun itemClicked(position: Int) {
//        selectedCityIndex = position
//        adapter?.setSelectedItem(selectedCityIndex)
//
//        handler?.cityClicked(cities[selectedCityIndex])
//
//        dialog.dismiss()
//    }


    companion object {
        const val TAG = "BottomSheetOffersFilters"
    }

}