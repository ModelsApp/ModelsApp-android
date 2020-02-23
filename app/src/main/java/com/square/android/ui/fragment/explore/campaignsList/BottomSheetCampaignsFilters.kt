package com.square.android.ui.fragment.explore.campaignsList

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R
import com.square.android.ui.fragment.explore.AvailabilityAdapter
import com.square.android.ui.fragment.explore.AvailabilityItem
import com.square.android.ui.fragment.explore.CategoryDialog
import com.square.android.ui.fragment.explore.SimpleCheckableAdapter
import com.square.android.ui.fragment.explore.filters.BaseBottomSheetFilters
import com.square.android.ui.fragment.explore.filters.CampaignsFilter
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.bottom_sheet_filters_campaigns.*

class BottomSheetCampaignsFilters(var campaignFilter: CampaignsFilter, private val handler: Handler, mCancelable: Boolean = true ): BaseBottomSheetFilters(mCancelable) {

    override var layoutRes: Int = R.layout.bottom_sheet_filters_campaigns

    private var categoryDialog: CategoryDialog? = null

    var mFilter: CampaignsFilter? = null

    lateinit var typesList: List<String>

    lateinit var categoriesList: List<String>

    lateinit var availabilityList: List<AvailabilityItem>

    lateinit var typologyList: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typesList = listOf(getString(R.string.all), getString(R.string.influence), getString(R.string.asset))

        categoriesList = listOf(getString(R.string.category_1), getString(R.string.category_2), getString(R.string.category_3), getString(R.string.category_4), getString(R.string.category_5))

        availabilityList = listOf(AvailabilityItem(getString(R.string.all), R.drawable.r_address), AvailabilityItem(getString(R.string.girls_only), R.drawable.r_address), AvailabilityItem(getString(R.string.guys_only), R.drawable.r_address))

        typologyList = listOf(getString(R.string.all), getString(R.string.free), getString(R.string.paid))

        mFilter = CampaignsFilter().apply {updateValues(campaignFilter)}

        setClearVisibility(!campaignFilter.isDefault())

        categoryDialog = CategoryDialog(context!!)

        var selectedCategoriesString = if(campaignFilter.selectedCategories.isEmpty()) getString(R.string.all) else ""
        var isFirst = true

        for (categoryInt in campaignFilter.selectedCategories) {
            mFilter!!.selectedCategories.add(categoryInt)

            if (isFirst) {
                isFirst = false
                selectedCategoriesString += categoriesList[categoryInt]
            } else {
                selectedCategoriesString += ", " + categoriesList[categoryInt]
            }
        }

        categoryTv.text = selectedCategoriesString

        typesRv.adapter = SimpleCheckableAdapter(typesList.toMutableList(), mFilter!!.type, object:SimpleCheckableAdapter.Handler{
            override fun itemLongClicked(position: Int) { }

            override fun itemClicked(position: Int) {
                (typesRv.adapter as SimpleCheckableAdapter).setSelectedItem(position)
                mFilter!!.type = position
                checkIfDefault()
            }
        } )
        typesRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        typesRv.addItemDecoration(MarginItemDecorator(context!!.resources.getDimension(R.dimen.v_8dp).toInt(), false))

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

        btnApply.setOnClickListener {
            handler.filtersApplyClicked(mFilter!!)
            dialog!!.dismiss()
        }

        clearLl.setOnClickListener {
            if(!campaignFilter.isDefault()){
                handler.filtersClearClicked()
            }

            dialog!!.dismiss()
        }
    }

    private fun checkIfDefault(){
        clearLl.visibility = if(!mFilter!!.isDefault()) View.VISIBLE else View.INVISIBLE

        btnApply.isEnabled = (!mFilter!!.isEqualTo(campaignFilter))
    }

    private fun setClearVisibility(visible: Boolean){
        clearLl.visibility = if(visible) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        const val TAG = "BottomSheetCampaignsFilters"
    }
}