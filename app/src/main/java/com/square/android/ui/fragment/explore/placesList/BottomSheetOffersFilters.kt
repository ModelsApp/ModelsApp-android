package com.square.android.ui.fragment.explore.placesList

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_sheet_filters_offers.*
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.square.android.R
import com.square.android.extensions.toHourStr
import com.square.android.ui.fragment.explore.*
import com.square.android.ui.fragment.explore.filters.BaseBottomSheetFilters
import com.square.android.ui.fragment.explore.filters.PlacesFilter
import com.square.android.ui.fragment.map.MarginItemDecorator

class BottomSheetOffersFilters(var placeFilter: PlacesFilter, private val handler: Handler, mCancelable: Boolean = true ): BaseBottomSheetFilters(mCancelable) {

    override var layoutRes: Int = R.layout.bottom_sheet_filters_offers

    private var categoryDialog: CategoryDialog? = null

    var mFilter: PlacesFilter? = null

    lateinit var categoriesList: List<String>

    lateinit var availabilityList: List<AvailabilityItem>

    lateinit var showMePlacesList: List<String>

    lateinit var offersTypologyList: List<String>

    lateinit var offersLevelList: List<IconItem>

    lateinit var bookingTypeList: List<String>

    lateinit var takeawayList: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesList = listOf(getString(R.string.category_1), getString(R.string.category_2), getString(R.string.category_3), getString(R.string.category_4), getString(R.string.category_5))

        availabilityList = listOf(AvailabilityItem(getString(R.string.all), R.drawable.r_address), AvailabilityItem(getString(R.string.girls_only), R.drawable.r_address), AvailabilityItem(getString(R.string.guys_only), R.drawable.r_address))

        showMePlacesList = listOf(getString(R.string.all), getString(R.string.not_full))

        offersTypologyList = listOf(getString(R.string.complimentary), getString(R.string.discounted))

        //TODO change to strings from res
        offersLevelList = listOf(IconItem(getString(R.string.all), null), IconItem("Welcome", "25"), IconItem("Basic", "100"), IconItem("Premium", "250"))

        bookingTypeList = listOf(getString(R.string.all), getString(R.string.reservation_needed), getString(R.string.walk_in))

        takeawayList = listOf(getString(R.string.all), getString(R.string.accepted), getString(R.string.unavailable))

        mFilter = PlacesFilter(placeFilter.getActualMinHour()).apply {updateValues(placeFilter)}

        setClearVisibility(!placeFilter.isDefault())

        categoryDialog = CategoryDialog(context!!)

        reservationStart.text = mFilter!!.timeSlot.start.toHourStr()
        reservationEnd.text = mFilter!!.timeSlot.end.toHourStr()
        reservationTimeslotSeekBar.setProgress(mFilter!!.timeSlot.start.toFloat(), mFilter!!.timeSlot.end.toFloat())

        var selectedCategoriesString = if(placeFilter.selectedCategories.isEmpty()) getString(R.string.all) else ""
        var isFirst = true

        for (categoryInt in placeFilter.selectedCategories) {
            mFilter!!.selectedCategories.add(categoryInt)

            if (isFirst) {
                isFirst = false
                selectedCategoriesString += categoriesList[categoryInt]
            } else {
                selectedCategoriesString += ", " + categoriesList[categoryInt]
            }
        }

        categoryTv.text = selectedCategoriesString

        reservationTimeslotSeekBar.setOnRangeChangedListener(object: OnRangeChangedListener{
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) { }

            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                reservationStart.text = leftValue.toInt().toHourStr()
                reservationEnd.text = rightValue.toInt().toHourStr()

                mFilter!!.timeSlot.start = leftValue.toInt()
                mFilter!!.timeSlot.end = rightValue.toInt()

                checkIfDefault()
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) { }
        })

        categoryIc.setOnClickListener {
            categoryDialog?.show(categoriesList, mFilter!!.selectedCategories) {

                checkIfDefault()

                selectedCategoriesString = if(mFilter!!.selectedCategories.isEmpty()) getString(R.string.all) else ""

                isFirst = true
                for(categoryInt in mFilter!!.selectedCategories){
                    if(isFirst){
                        isFirst = false
                        selectedCategoriesString += categoriesList[categoryInt]
                    } else{
                        selectedCategoriesString += ", "+categoriesList[categoryInt]
                    }
                }

                categoryTv.text = selectedCategoriesString
            }
        }

        availabilityRv.adapter = AvailabilityAdapter(availabilityList, mFilter!!.availability, object:AvailabilityAdapter.Handler{
            override fun itemClicked(position: Int) {
                (availabilityRv.adapter as AvailabilityAdapter).setSelectedItem(position)
                mFilter!!.availability = position
                checkIfDefault()
            }
        } )
        availabilityRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        availabilityRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        showMePlacesRv.adapter = SimpleCheckableAdapter(showMePlacesList.toMutableList(), mFilter!!.showPlacesType, object:SimpleCheckableAdapter.Handler{
            override fun itemLongClicked(position: Int) { }

            override fun itemClicked(position: Int) {
                (showMePlacesRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                mFilter!!.showPlacesType = position
                checkIfDefault()
            }
        } )
        showMePlacesRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        showMePlacesRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        offersTypologyRv.adapter = SimpleCheckableAdapter(offersTypologyList.toMutableList(), mFilter!!.offersTypology, object:SimpleCheckableAdapter.Handler{
            override fun itemLongClicked(position: Int) { }

            override fun itemClicked(position: Int) {
                (offersTypologyRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                mFilter!!.offersTypology = position
                checkIfDefault()
            }
        } )
        offersTypologyRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        offersTypologyRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        offersLevelRv.adapter = IconCheckableAdapter(offersLevelList, mFilter!!.selectedOffersLevel, object:IconCheckableAdapter.Handler{
            override fun itemClicked(position: Int) {
                (offersLevelRv.adapter as IconCheckableAdapter).setSelectedItem(position)
                mFilter!!.selectedOffersLevel = position
                checkIfDefault()
            }
        } )
        offersLevelRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        offersLevelRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        bookingTypeRv.adapter = SimpleCheckableAdapter(bookingTypeList.toMutableList(), mFilter!!.bookingType, object:SimpleCheckableAdapter.Handler{
            override fun itemLongClicked(position: Int) { }

            override fun itemClicked(position: Int) {
                (bookingTypeRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                mFilter!!.bookingType = position
                checkIfDefault()
            }
        } )
        bookingTypeRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        bookingTypeRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        takeawayOptionRv.adapter = SimpleCheckableAdapter(takeawayList.toMutableList(), mFilter!!.takeawayOption, object:SimpleCheckableAdapter.Handler{
            override fun itemLongClicked(position: Int) { }

            override fun itemClicked(position: Int) {
                (takeawayOptionRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                mFilter!!.takeawayOption = position
                checkIfDefault()
            }
        } )
        takeawayOptionRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        takeawayOptionRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        btnApply.setOnClickListener {
            handler.filtersApplyClicked(mFilter!!)
            dialog.dismiss()
        }

        clearLl.setOnClickListener {
            if(!placeFilter.isDefault()){
                handler.filtersClearClicked()
            }

            dialog.dismiss()
        }
    }

    private fun checkIfDefault(){
        clearLl.visibility = if(!mFilter!!.isDefault()) View.VISIBLE else View.INVISIBLE

        btnApply.isEnabled = (!mFilter!!.isEqualTo(placeFilter))
    }

    private fun setClearVisibility(visible: Boolean){
        clearLl.visibility = if(visible) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        const val TAG = "BottomSheetOffersFilters"
    }
}