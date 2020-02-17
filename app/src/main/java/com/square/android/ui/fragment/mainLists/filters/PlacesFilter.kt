package com.square.android.ui.fragment.mainLists.filters

class PlacesFilter(
        private var actualMinHour: Int,

        // multiple selection in rv
        var selectedCategories: MutableList<Int> = mutableListOf(),
        // 0 - all, 1 - girls only, 2 - guys only
        var availability: Int = 0,
        // 0 - all, 1 - not full
        var showPlacesType: Int = 0,
        // 0 - complimentary, 1 - discounted
        var offersTypology: Int = 0,
        // 0 - all, 1 - welcome 25, etc.
        var selectedOffersLevel: Int = 0,
        // 0 - all, 1 - reservation needed, 2 - walk-in
        var bookingType: Int = 0,
        // 0 - all, 1 - accepted, 2 - unavailable
        var takeawayOption: Int = 0,
        var timeSlot: TimeSlot = TimeSlot(start = actualMinHour)
): BaseFilter() {

    override fun updateValues(filter: BaseFilter) {
        selectedCategories = (filter as  PlacesFilter).selectedCategories
        availability = (filter as  PlacesFilter).availability
        showPlacesType = (filter as  PlacesFilter).showPlacesType
        offersTypology = (filter as  PlacesFilter).offersTypology
        selectedOffersLevel = (filter as  PlacesFilter).selectedOffersLevel
        bookingType = (filter as  PlacesFilter).bookingType
        takeawayOption = (filter as  PlacesFilter).takeawayOption
        setTimeSlotStartHour((filter as  PlacesFilter).timeSlot.start)
        setTimeSlotEndHour((filter as  PlacesFilter).timeSlot.end)
    }

    override fun isDefault(): Boolean = selectedCategories.isEmpty() && availability == 0 && showPlacesType == 0  &&
            offersTypology == 0 && selectedOffersLevel == 0 && bookingType == 0 && takeawayOption == 0 && timeSlot.isDefault(actualMinHour)

    override fun clear() {
        setTimeSlotStartHour(actualMinHour)
        setTimeSlotEndHour(24)
        selectedCategories.clear()
        availability = 0
        showPlacesType = 0
        offersTypology = 0
        selectedOffersLevel = 0
        bookingType = 0
        takeawayOption = 0
    }

    fun setTimeSlotStartHour(startHour: Int){ timeSlot.start = startHour}
    fun setTimeSlotEndHour(endHour: Int){ timeSlot.end = endHour }
}

class TimeSlot(var start: Int, var end: Int = 24) {
    fun isDefault(actualMinHour: Int): Boolean = (start == actualMinHour && end == 24)
}