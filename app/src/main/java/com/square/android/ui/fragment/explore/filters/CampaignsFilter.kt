package com.square.android.ui.fragment.explore.filters

class CampaignsFilter(
        var type: Int = 0,
        var selectedCategories: MutableList<Int> = mutableListOf(),
        var availability: Int = 0,
        var typology: Int = 0
): BaseFilter() {

    override fun isEqualTo(filter: BaseFilter): Boolean {
        return if(filter is CampaignsFilter){
            (type == filter.type &&
                    (selectedCategories.containsAll(filter.selectedCategories) && filter.selectedCategories.containsAll(selectedCategories)) &&
                    availability == filter.availability &&
                    typology == filter.typology)
        } else{
            false
        }
    }

    override fun updateValues(filter: BaseFilter) {
        if(filter is CampaignsFilter){
            type = filter.type
            selectedCategories.clear()
            for(category in filter.selectedCategories){
                selectedCategories.add(category)
            }
            availability = filter.availability
            typology = filter.typology
        } else{
            throw IllegalArgumentException("Filter must be instance of CampaignsFilter")
        }
    }

    override fun isDefault(): Boolean = type == 0 && selectedCategories.isEmpty() && availability == 0  && typology == 0

    override fun clear() {
        type = 0
        selectedCategories.clear()
        availability = 0
        typology = 0
    }
}
