package com.square.android.ui.fragment.explore.filters

import android.text.TextUtils

class EventsFilter(
        var datePeriod: Int = 1,
        var selectedCategories: MutableList<Int> = mutableListOf(),
        var availability: Int = 0,
        var showEventsType: Int = 0,
        var typology: Int = 0,
        var timeRange: TimeRange = TimeRange(),
        var dateFrom: String = "",
        var dateTo: String = ""
): BaseFilter() {

    override fun isEqualTo(filter: BaseFilter): Boolean {
        return if(filter is EventsFilter){
            (datePeriod == filter.datePeriod &&
                    (selectedCategories.containsAll(filter.selectedCategories) && filter.selectedCategories.containsAll(selectedCategories)) &&
                    availability == filter.availability &&
                    showEventsType == filter.showEventsType &&
                    typology == filter.typology &&
                    timeRange.min == filter.timeRange.min &&
                    timeRange.max == filter.timeRange.max &&
                    dateFrom == filter.dateFrom &&
                    dateTo == filter.dateTo
                    )
        } else{
            false
        }
    }

    override fun updateValues(filter: BaseFilter) {
        if(filter is EventsFilter){
            datePeriod = filter.datePeriod
            selectedCategories.clear()
            for(category in filter.selectedCategories){
                selectedCategories.add(category)
            }
            availability = filter.availability
            showEventsType = filter.showEventsType
            typology = filter.typology
            setTimeRangeMin(filter.timeRange.min)
            setTimeRangeMax(filter.timeRange.max)
            dateFrom = filter.dateFrom
            dateTo = filter.dateTo
        } else{
            throw IllegalArgumentException("Filter must be instance of EventsFilter")
        }
    }

    override fun isDefault(): Boolean = datePeriod == 1 && selectedCategories.isEmpty() && availability == 0 && showEventsType == 0  &&
            typology == 0 && timeRange.isDefault() && TextUtils.isEmpty(dateFrom) && TextUtils.isEmpty(dateTo)

    override fun clear() {
        datePeriod = 1
        setTimeRangeMin(1)
        setTimeRangeMax(12)
        selectedCategories.clear()
        availability = 0
        showEventsType = 0
        typology = 0
        dateFrom = ""
        dateTo = ""
    }

    fun setTimeRangeMin(startHour: Int){ timeRange.min = startHour}
    fun setTimeRangeMax(endHour: Int){ timeRange.max = endHour }
}

class TimeRange(var min: Int = 1, var max: Int = 12) {
    fun isDefault(): Boolean = (min == 1 && max == 12)
}