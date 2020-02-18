package com.square.android.ui.fragment.mainLists.filters

class EventsFilter(): BaseFilter() {
    override fun isDefault(): Boolean = true
    override fun clear() { }
    override fun updateValues(filter: BaseFilter) { }
    override fun isEqualTo(filter: BaseFilter): Boolean = false
}