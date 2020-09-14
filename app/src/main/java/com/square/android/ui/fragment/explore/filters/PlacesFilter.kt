package com.square.android.ui.fragment.explore.filters

class PlacesFilter(
        private var actualMinHour: Int,

        var selectedCategories: MutableList<Int> = mutableListOf(),
        var availability: Int = 0,
        var showPlacesType: Int = 0,
        var offersTypology: Int = 0,
        var bookingType: Int = 0,
        var takeawayOption: Int = 0,
        var timeSlot: TimeSlot = TimeSlot(start = actualMinHour)
): BaseFilter() {

    override fun isEqualTo(filter: BaseFilter): Boolean {
        return if(filter is PlacesFilter){
            ((selectedCategories.containsAll(filter.selectedCategories) && filter.selectedCategories.containsAll(selectedCategories)) &&
                    availability == filter.availability &&
                    showPlacesType == filter.showPlacesType &&
                    offersTypology == filter.offersTypology &&
                    bookingType == filter.bookingType &&
                    takeawayOption == filter.takeawayOption &&
                    timeSlot.start == filter.timeSlot.start &&
                    timeSlot.end == filter.timeSlot.end
                    )
        } else{
            false
        }
    }

    fun getActualMinHour(): Int = actualMinHour

    override fun updateValues(filter: BaseFilter) {
        if(filter is PlacesFilter){
            selectedCategories.clear()
            for(category in filter.selectedCategories){
                selectedCategories.add(category)
            }
            availability = filter.availability
            showPlacesType = filter.showPlacesType
            offersTypology = filter.offersTypology
            bookingType = filter.bookingType
            takeawayOption = filter.takeawayOption
            setTimeSlotStartHour(filter.timeSlot.start)
            setTimeSlotEndHour(filter.timeSlot.end)
        } else{
            throw IllegalArgumentException("Filter must be instance of PlacesFilter")
        }
    }

    override fun isDefault(): Boolean = selectedCategories.isEmpty() && availability == 0 && showPlacesType == 0  &&
            offersTypology == 0 && bookingType == 0 && takeawayOption == 0 && timeSlot.isDefault(actualMinHour)

    override fun clear() {
        setTimeSlotStartHour(actualMinHour)
        setTimeSlotEndHour(24)
        selectedCategories.clear()
        availability = 0
        showPlacesType = 0
        offersTypology = 0
        bookingType = 0
        takeawayOption = 0
    }

    fun setTimeSlotStartHour(startHour: Int){ timeSlot.start = startHour}
    fun setTimeSlotEndHour(endHour: Int){ timeSlot.end = endHour }
}

class TimeSlot(var start: Int, var end: Int = 24) {
    fun isDefault(actualMinHour: Int): Boolean = (start == actualMinHour && end == 24)
}