package com.square.android.ui.fragment.explore.eventsList

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_sheet_filters_events.*
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.square.android.R
import com.square.android.ui.dialogs.DateRangeDialog
import com.square.android.ui.fragment.explore.*
import com.square.android.ui.fragment.explore.filters.BaseBottomSheetFilters
import com.square.android.ui.fragment.explore.filters.EventsFilter
import com.square.android.ui.fragment.map.MarginItemDecorator

class BottomSheetEventsFilters(var eventFilter: EventsFilter, private val handler: Handler, mCancelable: Boolean = true ): BaseBottomSheetFilters(mCancelable) {

    override var layoutRes: Int = R.layout.bottom_sheet_filters_events

    private var categoryDialog: CategoryDialog? = null

    var mFilter: EventsFilter? = null

    lateinit var datePeriodList: MutableList<String>

    lateinit var categoriesList: List<String>

    lateinit var availabilityList: List<AvailabilityItem>

    lateinit var showMeEventsList: List<String>

    lateinit var typologyList: List<String>

    var backupDateFrom: String = ""
    var backupDateTo: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datePeriodList = mutableListOf(getString(R.string.period_1), getString(R.string.period_2), getString(R.string.period_3))

        categoriesList = listOf(getString(R.string.category_1), getString(R.string.category_2), getString(R.string.category_3), getString(R.string.category_4), getString(R.string.category_5))

        availabilityList = listOf(AvailabilityItem(getString(R.string.all), R.drawable.r_address), AvailabilityItem(getString(R.string.girls_only), R.drawable.r_address), AvailabilityItem(getString(R.string.guys_only), R.drawable.r_address))

        showMeEventsList = listOf(getString(R.string.all), getString(R.string.not_full))

        typologyList = listOf(getString(R.string.all), getString(R.string.free), getString(R.string.paid))

        mFilter = EventsFilter().apply {updateValues(eventFilter)}

        if(mFilter!!.datePeriod == 0){

            if(mFilter!!.dateFrom == mFilter!!.dateTo){
                datePeriodList[0] = mFilter!!.dateFrom
            } else{
                datePeriodList[0] = mFilter!!.dateFrom+" - "+mFilter!!.dateTo
            }

            backupDateFrom = mFilter!!.dateFrom
            backupDateTo = mFilter!!.dateTo
        }

        setClearVisibility(!eventFilter.isDefault())

        categoryDialog = CategoryDialog(context!!)

        minimumStayIntervalTv.text = mFilter!!.timeRange.min.toString()+"h - "+mFilter!!.timeRange.max+"h"

        minimumStayTimeslotSeekBar.setProgress(mFilter!!.timeRange.min.toFloat(), mFilter!!.timeRange.max.toFloat())

        var selectedCategoriesString = if(eventFilter.selectedCategories.isEmpty()) getString(R.string.all) else ""
        var isFirst = true

        for (categoryInt in eventFilter.selectedCategories) {
            mFilter!!.selectedCategories.add(categoryInt)

            if (isFirst) {
                isFirst = false
                selectedCategoriesString += categoriesList[categoryInt]
            } else {
                selectedCategoriesString += ", " + categoriesList[categoryInt]
            }
        }

        categoryTv.text = selectedCategoriesString

        minimumStayTimeslotSeekBar.setOnRangeChangedListener(object: OnRangeChangedListener{
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) { }

            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                minimumStayIntervalTv.text = leftValue.toInt().toString()+"h - "+rightValue.toInt().toString()+"h"

                mFilter!!.timeRange.min = leftValue.toInt()
                mFilter!!.timeRange.max = rightValue.toInt()

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

        showMeEventsRv.adapter = SimpleCheckableAdapter(showMeEventsList.toMutableList(), mFilter!!.showEventsType, object:SimpleCheckableAdapter.Handler{
            override fun itemLongClicked(position: Int) { }

            override fun itemClicked(position: Int) {
                (showMeEventsRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                mFilter!!.showEventsType = position
                checkIfDefault()
            }
        } )
        showMeEventsRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        showMeEventsRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        typologyRv.adapter = SimpleCheckableAdapter(typologyList.toMutableList(), mFilter!!.typology, object:SimpleCheckableAdapter.Handler{
            override fun itemLongClicked(position: Int) { }

            override fun itemClicked(position: Int) {
                (typologyRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                mFilter!!.typology = position
                checkIfDefault()
            }
        } )
        typologyRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        typologyRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        datePeriodRv.adapter = SimpleCheckableAdapter(datePeriodList, mFilter!!.datePeriod, object:SimpleCheckableAdapter.Handler{
            override fun itemLongClicked(position: Int) {
                if(position == 0){
                    if(!TextUtils.isEmpty(backupDateFrom) && !TextUtils.isEmpty(backupDateTo)){
                        val dateRangeDialog = DateRangeDialog(context!!)
                        dateRangeDialog.show(mFilter!!.dateFrom, mFilter!!.dateTo, { start: String?, end: String? ->

                            if(!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)){
                                mFilter!!.dateFrom = start!!
                                mFilter!!.dateTo = end!!
                                backupDateFrom = start!!
                                backupDateTo = end!!

                                if(start!! == end!!){
                                    datePeriodList[0] = mFilter!!.dateFrom
                                } else{
                                    datePeriodList[0] = mFilter!!.dateFrom+" - "+mFilter!!.dateTo
                                }

                                (datePeriodRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                                mFilter!!.datePeriod = position
                                checkIfDefault()
                            }
                        }, { clearDate() })
                    }
                }
            }

            override fun itemClicked(position: Int) {
                if(position == 0){
                    if(!TextUtils.isEmpty(backupDateFrom) && !TextUtils.isEmpty(backupDateTo)){
                        mFilter!!.dateFrom = backupDateFrom
                        mFilter!!.dateTo = backupDateTo

                        if(mFilter!!.dateFrom == mFilter!!.dateTo){
                            datePeriodList[0] = mFilter!!.dateFrom
                        } else{
                            datePeriodList[0] = mFilter!!.dateFrom+" - "+mFilter!!.dateTo
                        }

                        (datePeriodRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                        mFilter!!.datePeriod = position

                        checkIfDefault()
                    } else{
                        val dateRangeDialog = DateRangeDialog(context!!)
                        dateRangeDialog.show(mFilter!!.dateFrom, mFilter!!.dateTo, { start: String?, end: String? ->

                            if(!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)){
                                mFilter!!.dateFrom = start!!
                                mFilter!!.dateTo = end!!
                                backupDateFrom = start!!
                                backupDateTo = end!!

                                if(start!! == end!!){
                                    datePeriodList[0] = mFilter!!.dateFrom
                                } else{
                                    datePeriodList[0] = mFilter!!.dateFrom+" - "+mFilter!!.dateTo
                                }

                                (datePeriodRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                                mFilter!!.datePeriod = position
                                checkIfDefault()
                            }
                        }, { clearDate() })
                    }

                } else{
                    mFilter!!.dateFrom = ""
                    mFilter!!.dateTo = ""
                    datePeriodList[0] = getString(R.string.period_1)
                    (datePeriodRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                    mFilter!!.datePeriod = position
                    checkIfDefault()
                }
            }
        } )
        datePeriodRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        datePeriodRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        btnApply.setOnClickListener {
            handler.filtersApplyClicked(mFilter!!)
            dialog.dismiss()
        }

        clearLl.setOnClickListener {
            if(!eventFilter.isDefault()){
                handler.filtersClearClicked()
            }

            dialog.dismiss()
        }
    }

    fun clearDate(){
        mFilter!!.dateFrom = ""
        mFilter!!.dateTo = ""
        backupDateFrom = ""
        backupDateTo = ""

        datePeriodList[0] = getString(R.string.period_1)

        (datePeriodRv.adapter as SimpleCheckableAdapter).setSelectedItem1(getString(R.string.period_1))

        mFilter!!.datePeriod = 1
        checkIfDefault()
    }

    private fun checkIfDefault(){
        clearLl.visibility = if(!mFilter!!.isDefault()) View.VISIBLE else View.INVISIBLE

        btnApply.isEnabled = (!mFilter!!.isEqualTo(eventFilter))
    }

    private fun setClearVisibility(visible: Boolean){
        clearLl.visibility = if(visible) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        const val TAG = "BottomSheetEventsFilters"
    }
}